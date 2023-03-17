package ru.yandex.practicum.filmorate.usersTest;

import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exceptions.InvalidDatesException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserLogicTest {
    private int count = 1;

    private final Map<Integer, User> users = new HashMap();

    User rightUser = User.builder()
            .email("mail@mail.ru")
            .login("feodor")
            .birthday(LocalDate.of(2000, 01, 01))
            .build();

    User userFailMail = User.builder()
            .email("mailmail.ru")
            .name("Fedor")
            .login("feodor")
            .birthday(LocalDate.of(2000, 01, 01))
            .build();

    User userFailLogin = User.builder()
            .email("mail@mail.ru")
            .login("feodor feodor")
            .birthday(LocalDate.of(2000, 01, 01))
            .build();

    User userFailBirthday = User.builder()
            .email("mail@mail.ru")
            .login("feodor")
            .birthday(LocalDate.of(2025, 01, 01))
            .build();

    @Test
    void create() throws InvalidDatesException {
        create(rightUser);
        assertEquals(users.get(1), rightUser);
    }

    @Test
    void createFailMail() throws InvalidDatesException {
        try {
            create(userFailMail);
        } catch (InvalidDatesException e) {
            assertEquals(users.size(), 0);
            assertEquals(e.getMessage(), "Check email address.");
        }
    }

    @Test
    void createFailLogin() throws InvalidDatesException {
        try {
            create(userFailLogin);
        } catch (InvalidDatesException e) {
            assertEquals(users.size(), 0);
            assertEquals(e.getMessage(), "Check login.");
        }
    }

    @Test
    void createFailBirthday() throws InvalidDatesException {
        try {
            create(userFailBirthday);
        } catch (InvalidDatesException e) {
            assertEquals(users.size(), 0);
            assertEquals(e.getMessage(), "Check date of birth.");
        }
    }

    @Test
    void getUsersFromMap() throws InvalidDatesException {
        create(rightUser);
        List<User> users1 = getUsers();
        assertEquals(users1.size(), 1);
    }

    @Test
    void putUser() throws InvalidDatesException {
        create(rightUser);
        User user = User.builder()
                .id(1)
                .email("mail@yandex.ru")
                .login("terminator")
                .birthday(LocalDate.of(2020, 01, 01))
                .build();
        update(user);
        assertEquals(users.get(1), user);
    }

    @Test
    void putUserFailMail() throws InvalidDatesException {
        create(rightUser);
        User user = User.builder()
                .id(1)
                .email("mailyandex.ru")
                .login("terminator")
                .birthday(LocalDate.of(2020, 01, 01))
                .build();
        try {
            update(user);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Check  email address.");
        }
    }

    @Test
    void putUserFailLogin() throws InvalidDatesException {
        create(rightUser);
        User user = User.builder()
                .id(1)
                .email("mail@yandex.ru")
                .login("terminator ha ha")
                .birthday(LocalDate.of(2020, 01, 01))
                .build();
        try {
            update(user);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Check  login.");
        }
    }

    @Test
    void putUserFailBirthday() throws InvalidDatesException {
        create(rightUser);
        User user = User.builder()
                .id(1)
                .email("mail@yandex.ru")
                .login("terminator")
                .birthday(LocalDate.of(2025, 01, 01))
                .build();
        try {
            update(user);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Check date of birth.");
        }
    }

    @Test
    void putUserFailFieldBirthday() throws InvalidDatesException {
        create(rightUser);
        User user = User.builder()
                .id(1)
                .email("mail@yandex.ru")
                .login("terminator")
                .build();
        try {
            update(user);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Check all request fields.");
        }
    }

    public User create(User user) throws InvalidDatesException {
        try {
            if (users.containsKey(user.getId())) {
                throw new InvalidDatesException("User already exists.");
            } else if ((user.getEmail() == null || user.getEmail().isBlank()) || !user.getEmail().contains("@")) {
                throw new InvalidDatesException("Check email address.");
            } else if ((user.getLogin() == null || user.getLogin().isBlank()) || user.getLogin().contains(" ")) {
                throw new InvalidDatesException("Check login.");
            } else if (!user.getBirthday().isBefore(LocalDate.now())) {
                throw new InvalidDatesException("Check date of birth.");
            } else {
                if (user.getName() == null) {
                    user.setName(user.getLogin());
                }
                user.setId(count++);
                users.put(user.getId(), user);
                return user;
            }
        } catch (NullPointerException e) {
            throw new InvalidDatesException("Check all request fields.");
        }
    }

    public List<User> getUsers() {
        List<User> usersList = new ArrayList<>(users.values());
        return usersList;
    }

    public User update(User user) throws InvalidDatesException {
        try {
            for (User userFromMap : users.values()) {
                if (userFromMap.getId() != user.getId() || userFromMap.getId() == 0) {
                    throw new InvalidDatesException("The user has not been updated. Check user data.");
                } else if ((user.getEmail() == null || user.getEmail().isBlank()) || !user.getEmail().contains("@")) {
                    throw new InvalidDatesException("Check  email address.");
                } else if ((user.getLogin() == null || user.getLogin().isBlank()) || user.getLogin().contains(" ")) {
                    throw new InvalidDatesException("Check  login.");
                } else if (!user.getBirthday().isBefore(LocalDate.now())) {
                    throw new InvalidDatesException("Check date of birth.");
                } else {
                    users.put(user.getId(), user);
                }
            }
        } catch (NullPointerException e) {
            throw new InvalidDatesException("Check all request fields.");
        }
        return user;
    }
}
