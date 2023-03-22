package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = {"id"})
public class User {

    int id;

    @NonNull
    @Email(message = "EMAIL should be valid")
    String email;

    @NonNull
    @Pattern(regexp="^\\S+$", message="LOGIN can't consist of spaces.")
    String login;

    String name;

    @NonNull
    @Past(message = "BIRTHDAY can't be in the future.")
    LocalDate birthday;
}
