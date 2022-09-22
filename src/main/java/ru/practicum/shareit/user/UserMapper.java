package ru.practicum.shareit.user;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.markerinterface.Create;

public class UserMapper {

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static @Validated({Create.class}) UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
