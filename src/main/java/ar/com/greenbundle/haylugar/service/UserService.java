package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dto.CreateUserData;
import ar.com.greenbundle.haylugar.dto.UpdateUserData;
import ar.com.greenbundle.haylugar.dto.constants.Nationality;
import ar.com.greenbundle.haylugar.entities.UserItem;
import ar.com.greenbundle.haylugar.exceptions.CreateUserException;
import ar.com.greenbundle.haylugar.exceptions.ResourceNotFoundException;
import ar.com.greenbundle.haylugar.repositories.UserRepository;
import ar.com.greenbundle.haylugar.util.EncodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static ar.com.greenbundle.haylugar.dto.constants.Nationality.ARGENTINEAN;
import static ar.com.greenbundle.haylugar.dto.constants.UserState.RIGHT;

@Service
public class UserService {
    private final List<Nationality> ALLOWED_NATIONALITIES = List.of(
            ARGENTINEAN
    );
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService;

    public Mono<UserItem> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Mono<UserItem> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Mono<UserItem> createUser(CreateUserData userData) {
        try {

            int LEGAL_AGE = 18;

            if(!ALLOWED_NATIONALITIES.contains(userData.getProfile().getNationality()))
                return Mono.error(new CreateUserException(String.format("Nationality %s is not allowed",
                        userData.getProfile().getNationality())));

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date userBirthDate = dateFormat.parse(userData.getProfile().getBirthDate());
            Date today = dateFormat.parse(dateFormat.format(new Date()));

            if(userBirthDate.after(today) || userBirthDate.equals(today))
                return Mono.error(new CreateUserException("User birth date is invalid"));

            Calendar calendar = GregorianCalendar.getInstance();

            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - LEGAL_AGE);

            boolean isAgeLegal = calendar.getTime().after(userBirthDate);

            if(!isAgeLegal)
                return Mono.error(new CreateUserException("User age is less than legal years old"));

            UserItem user = UserItem.builder()
                    .email(userData.getEmail())
                    .passwordSalt(EncodeUtil.encodeBase64AsString(userData.getPassword().getSalt()))
                    .passwordHash(EncodeUtil.encodeBase64AsString(userData.getPassword().getHash()))
                    .profile(userData.getProfile())
                    .state(RIGHT)
                    .build();

            return userRepository.insert(user)
                    .flatMap(userSaved -> paymentService.addCardForUser(userSaved.getId(), "985bb7da7bd9faf833447dba14fa6de1", "123")
                            .then(Mono.just(userSaved)));

        } catch (ParseException e) {
            return Mono.error(e);
        }
    }

    public Mono<UserItem> updateUser(String userEmail, UpdateUserData userData) {
        try {

            int LEGAL_AGE = 18;

            if(!ALLOWED_NATIONALITIES.contains(userData.getProfile().getNationality()))
                return Mono.error(new CreateUserException(String.format("Nationality %s is not allowed",
                        userData.getProfile().getNationality())));

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date userBirthDate = dateFormat.parse(userData.getProfile().getBirthDate());
            Date today = dateFormat.parse(dateFormat.format(new Date()));

            if(userBirthDate.after(today) || userBirthDate.equals(today))
                return Mono.error(new CreateUserException("User birth date is invalid"));

            Calendar calendar = GregorianCalendar.getInstance();

            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - LEGAL_AGE);

            boolean isAgeLegal = calendar.getTime().after(userBirthDate);

            if(!isAgeLegal)
                return Mono.error(new CreateUserException("User age is less than legal years old"));

            return userRepository.findUserByEmail(userEmail)
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")))
                    .flatMap(user -> {
                        if(userData.getUserState() != null)
                            user.setState(user.getState());

                        if(userData.getProfile() != null) {
                            if(userData.getProfile().getNationality() != null)
                                user.getProfile().setNationality(userData.getProfile().getNationality());

                            if(userData.getProfile().getName() != null)
                                user.getProfile().setName(userData.getProfile().getName());

                            if(userData.getProfile().getSurname() != null)
                                user.getProfile().setSurname(userData.getProfile().getSurname());

                            if(userData.getProfile().getGender() != null)
                                user.getProfile().setGender(userData.getProfile().getGender());

                            if(userData.getProfile().getDni() != null)
                                user.getProfile().setDni(userData.getProfile().getDni());

                            if(userData.getProfile().getBirthDate() != null)
                                user.getProfile().setBirthDate(userData.getProfile().getBirthDate());
                        }

                        return userRepository.save(user);
                    });

        } catch (ParseException e) {
            return Mono.error(e);
        }
    }
}
