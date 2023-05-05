package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    Integer id;

    @NonNull
    @Email(message = "EMAIL should be valid")
    String email;

    @NonNull
    @Pattern(regexp = "^\\S+$", message = "LOGIN can't consist of spaces.")
    String login;

    String name;

    @NonNull
    @Past(message = "BIRTHDAY can't be in the future.")
    LocalDate birthday;

    List<User> friends;
}
