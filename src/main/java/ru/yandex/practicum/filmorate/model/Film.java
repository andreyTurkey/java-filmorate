package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.customValidator.MinimumDate;

import javax.validation.constraints.*;
import java.time.*;

import java.util.Set;
import java.util.TreeSet;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    static final LocalDate EARLY_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    int id;

    @NonNull @NotBlank(message = "NAME can't be empty or consist of spaces.") String name;

    @NonNull @Size(max = 200, message = "Size a description should be <= 200 characters.") String description;

    @NonNull
    @MinimumDate
    LocalDate releaseDate;

    @NonNull @Positive(message = "DURATION should be positive.") int duration;

    final Set<Integer> likes = new TreeSet<>();

    public void addLike(Integer userId) {
        likes.add(userId);
    }

    public void deleteLike(Integer userId) {
        likes.remove(userId);
    }
}
