package md.spring.restapi.task.tracker.api.controllers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.AckDto;
import md.spring.restapi.task.tracker.api.dto.UserDto;
import md.spring.restapi.task.tracker.api.services.UsersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
public class UsersController {

    UsersService userService;
    public static final String FETCH_USERS = "/api/admin/users";
    public static final String CREATE_USER = "/api/admin/user";
    public static final String EDIT_USER = "/api/admin/users/{user_id}";
    public static final String DELETE_USER = "/api/admin/users/{user_id}";


    @GetMapping(FETCH_USERS)
    public List<UserDto> fetchUsers() {
        return userService.fetchUsers();
    }

    @PutMapping(CREATE_USER)
    public UserDto createUser(@RequestParam(name = "user_name") String userName,
                              @RequestParam String password) {
        return userService.createUser(userName, password);
    }

    @PatchMapping(EDIT_USER)
    public UserDto updateUser(@PathVariable("user_id") Long userId,
                  @RequestParam(name = "user_name",required = false) Optional<String> userName,
                  @RequestParam(name = "password",required = false) Optional<String> password
                             ) {
        return userService.editUser(userId,userName,password);
    }

    @DeleteMapping(DELETE_USER)
    public AckDto deleteUser(@PathVariable("user_id") Long userId) {
        return userService.deleteUser(userId);
    }
}