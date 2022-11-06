package ru.practicum.shareit.user;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    private String email;
}
