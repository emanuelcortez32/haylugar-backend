package ar.com.greenbundle.haylugar.dao;

import ar.com.greenbundle.haylugar.dto.UserDto;
import ar.com.greenbundle.haylugar.entities.UserEntity;
import ar.com.greenbundle.haylugar.exceptions.CreateUserException;
import ar.com.greenbundle.haylugar.exceptions.ResourceNotFoundException;
import ar.com.greenbundle.haylugar.repositories.UserRepository;
import ar.com.greenbundle.haylugar.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class UserDao {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileDao userProfileDao;

    public Mono<UserDto> getUserByEmail(String userEmail) {
        return findUserByCriteria(Map.of("email", userEmail));
    }

    public Mono<UserDto> getUser(String userId) {
        return findUserByCriteria(Map.of("id", userId));
    }

    public Mono<UserDto> saveUser(UserDto userData) {
        return userRepository.findByEmail(userData.getEmail())
                .flatMap(__ -> Mono.error(new CreateUserException("User already exists")))
                .switchIfEmpty(userRepository.save(new UserDto().dtoToEntity(userData)))
                .cast(UserEntity.class)
                .map(user -> new UserDto().dtoFromEntity(user));
    }

    private Mono<UserDto> findUserByCriteria(Map<String, String> criteria) {

        Mono<UserEntity> initialOperation = null;

        if(criteria.containsKey("email") && !StringUtils.isNullOrEmpty(criteria.get("email")))
            initialOperation = userRepository.findByEmail(criteria.get("email"));

        if(criteria.containsKey("id") && !StringUtils.isNullOrEmpty(criteria.get("id")))
            initialOperation = userRepository.findById(criteria.get("id"));

        if(initialOperation == null)
            return Mono.error(new IllegalStateException("Criteria is invalid"));

        return initialOperation
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")))
                .map(user -> new UserDto().dtoFromEntity(user))
                .flatMap(user -> userProfileDao.getProfileByUser(user.getId())
                        .map(userProfile -> {
                            user.setProfile(userProfile);
                            return user;
                        }));
    }
}
