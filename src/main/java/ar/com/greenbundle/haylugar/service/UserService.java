package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dao.UserDao;
import ar.com.greenbundle.haylugar.dao.UserProfileDao;
import ar.com.greenbundle.haylugar.dto.UserDto;
import ar.com.greenbundle.haylugar.dto.UserProfileDto;
import ar.com.greenbundle.haylugar.exceptions.CreateUserException;
import ar.com.greenbundle.haylugar.pojo.constants.Nationality;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static ar.com.greenbundle.haylugar.pojo.constants.Nationality.ARGENTINEAN;
@Slf4j
@Service
public class UserService {
    private final List<Nationality> ALLOWED_NATIONALITIES = List.of(ARGENTINEAN);
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private PaymentService paymentService;

    public Mono<UserDto> findUserByEmail(String email) {
        return userDao.getUserByEmail(email)
                .flatMap(user -> userProfileDao.getProfileByUser(user.getId())
                        .doOnNext(user::setProfile).then(Mono.just(user)));
    }

    public Mono<UserDto> findUser(String userId) {
        return userDao.getUser(userId)
                .flatMap(user -> userProfileDao.getProfileByUser(user.getId())
                        .doOnNext(user::setProfile).then(Mono.just(user)));
    }

    @Transactional
    public Mono<String> registerUser(UserDto user) {
        try {

            final int LEGAL_AGE = 18;
            final String DATE_FORMAT = "dd/MM/yyyy";

            if(!ALLOWED_NATIONALITIES.contains(user.getProfile().getNationality()))
                return Mono.error(new CreateUserException(String.format("Nationality %s is not allowed",
                        user.getProfile().getNationality())));

            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date userBirthDate = dateFormat.parse(user.getProfile().getBirthDate());
            Date today = dateFormat.parse(dateFormat.format(new Date()));

            if(userBirthDate.after(today) || userBirthDate.equals(today))
                return Mono.error(new CreateUserException("User birth date is invalid"));

            Calendar calendar = GregorianCalendar.getInstance();

            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - LEGAL_AGE);

            boolean isAgeLegal = calendar.getTime().after(userBirthDate);

            if(!isAgeLegal)
                return Mono.error(new CreateUserException("User age is less than legal years old"));

            return userDao.saveUser(user)
                    .flatMap(savedUser -> {

                        UserProfileDto userProfile = UserProfileDto.builder()
                                .userId(savedUser.getId())
                                .name(user.getProfile().getName())
                                .lastName(user.getProfile().getLastName())
                                .dni(user.getProfile().getDni())
                                .birthDate(user.getProfile().getBirthDate())
                                .gender(user.getProfile().getGender())
                                .nationality(user.getProfile().getNationality())
                                .build();

                        return userProfileDao.saveProfile(userProfile)
                                .doOnNext(profile -> {
                                    savedUser.setProfile(profile);
                                    paymentService.createPaymentProfileForUser(savedUser)
                                            .subscribeOn(Schedulers.boundedElastic())
                                            .doOnError(throwable -> log.warn("Payment profile for userId[{}] could not be created [{}]",
                                                    savedUser.getId(), throwable.getMessage()))
                                            .subscribe(result -> log.info("Payment profile for userId[{}] created with id[{}]",
                                                    savedUser.getId(), result), throwable -> {});
                                })
                                .then(Mono.just(savedUser.getId()));
                    });

        } catch (ParseException e) {
            return Mono.error(e);
        }
    }
}
