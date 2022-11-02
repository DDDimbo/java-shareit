package ru.practicum.shareit.user;

import lombok.*;
import ru.practicum.shareit.markerinterface.Create;
import ru.practicum.shareit.markerinterface.Update;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class UserDto {

    @Positive(groups = {Create.class, Update.class})
    private Long id;

    @NotBlank(message = "Имя не должно быть пустым", groups = {Create.class})
    @Pattern(regexp = "^\\S+$", groups = {Create.class, Update.class})
    private String name;

    @Email(message = "Некорректный адрес электронной почты", groups = {Create.class, Update.class})
    @NotNull(message = "Email не может быть null", groups = {Create.class})
    private String email;
}
