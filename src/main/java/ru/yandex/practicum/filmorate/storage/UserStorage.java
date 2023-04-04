package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    Map<Integer, User> getUsers();

    User create(User user);

    User update(User user);

    List<User> getAllUsers();

    User getUserById(Integer id);

    User deleteUserById(Integer id);

    List<User> deleteUsers();
}
