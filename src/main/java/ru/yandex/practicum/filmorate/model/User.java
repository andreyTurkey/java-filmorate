package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.enumCatalog.FriendStatus;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    final Set<Integer> friends = new HashSet<>();

    final Map<Integer, FriendStatus> friendStatus = new HashMap<>();

    public void addFriend(Integer friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(Integer friendId) {
        friends.remove(friendId);
    }
}
