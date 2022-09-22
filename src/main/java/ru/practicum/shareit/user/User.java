package ru.practicum.shareit.user;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class User {

    private Long id;

    private String name;

    private String email;
}
