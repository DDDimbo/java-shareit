package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markerinterface.Create;
import ru.practicum.shareit.markerinterface.Update;


@Controller
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Create user validation");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable(value = "userId") Long id, @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Update user id={} validation", id);
        return userClient.updateUser(id, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable(value = "userId", required = false) Long id) {
        log.info("Get user id={} validation", id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Get all users validation");
        return userClient.getUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteById(@PathVariable(value = "userId", required = false) Long id) {
        log.info("Delete user id={}", id);
        return userClient.deleteUser(id);
    }
}
