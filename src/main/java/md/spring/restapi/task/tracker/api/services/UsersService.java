package md.spring.restapi.task.tracker.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.AckDto;
import md.spring.restapi.task.tracker.api.dto.UserDto;
import md.spring.restapi.task.tracker.api.exceptions.BadRequestException;
import md.spring.restapi.task.tracker.api.factories.UserDtoFactory;
import md.spring.restapi.task.tracker.api.services.helpers.ServiceHelper;
import md.spring.restapi.task.tracker.api.websecurityconfig.SecurityConfig;
import md.spring.restapi.task.tracker.store.entities.UserEntity;
import md.spring.restapi.task.tracker.store.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UsersService {

    public static final String USER_ROLE = "USER";

    UserRepository userRepository;
    UserDtoFactory userDtoFactory;
    ServiceHelper serviceHelper;
    SecurityConfig securityConfig;
    public List<UserDto> fetchUsers(){
        return userRepository.findAll().stream()
                .map(userDtoFactory::makeUserDto)
                .collect(Collectors.toList());
    }


    public UserDto createUser(String userName,String password){
        if(userName.trim().isEmpty()){
            throw new BadRequestException("UserName can't be empty");
        }
        if(password.trim().isEmpty()){
            throw new BadRequestException("Password can't be empty");
        }
        userRepository
                .findByUsername(userName)
                .ifPresent(user -> {
                    throw new BadRequestException(String.format("User \"%s\" already exists.", userName));
                });
        UserEntity user = userRepository.save(
                UserEntity.builder()
                        .username(userName)
                        .password(securityConfig.passwordEncoder().encode(password))
//                        .password(password)
                        .role(USER_ROLE)
                        .build()
        );
        return  userDtoFactory.makeUserDto(user);
    }

    public UserDto editUser(Long userId,
                              Optional<String> userName,
                              Optional<String> password) {
        UserEntity user = serviceHelper.getUserOrThrowException(userId);

        if (userName.isPresent() && !userName.get().trim().isEmpty()) {
            String newUserName = userName.get();
            userRepository.findByUsername(newUserName)
                    .ifPresent(anotherTaskState -> {
                        throw new BadRequestException(String.format("Task \"%s\" already exists.", newUserName));
                    });

            user.setUsername(newUserName);
        }
        if (password.isPresent() && !password.get().trim().isEmpty()) {
            String newUserPassword = password.get();
            user.setPassword(securityConfig.passwordEncoder().encode(newUserPassword));
//            user.setPassword(newUserPassword);
        }
        user = userRepository.saveAndFlush(user);
        return userDtoFactory.makeUserDto(user);
    }
    public AckDto deleteUser(Long userId){
        serviceHelper.getUserOrThrowException(userId);
        userRepository.deleteById(userId);
        return AckDto.makeDefault(true);
    }

    public void createDefaultAdmin() {
        if (!userRepository.findByUsername("admin").isPresent()) {
            UserEntity admin = UserEntity.builder()
                    .username("admin")
                    .password(securityConfig.passwordEncoder().encode("123456"))
//                    .password("123456")
                    .role("ADMIN")
                    .build();
            userRepository.save(admin);
        }
    }

}
