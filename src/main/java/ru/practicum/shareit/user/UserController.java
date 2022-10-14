package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markerinterface.Create;
import ru.practicum.shareit.markerinterface.Update;

import java.util.Collection;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto create(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Create user");
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@PathVariable(value = "userId") Long id, @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Update user id={}", id);
        return userService.update(id, userDto);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findById(@PathVariable(value = "userId", required = false) Long id) {
        log.info("Get user id={}", id);
        return userService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> findAll() {
        log.info("Get all users");
        return userService.findAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable(value = "userId", required = false) Long id) {
        log.info("Delete user id={}", id);
        userService.deleteById(id);
    }
}
