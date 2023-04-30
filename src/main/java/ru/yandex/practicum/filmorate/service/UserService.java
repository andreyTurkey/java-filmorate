package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class UserService {

    final UserStorage userStorage;

    int count;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
        count = 1;
    }

    public User addFriend(Integer userId, Integer friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public List<User> getUserFriends(Integer userId) {
        return userStorage.getUserFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        return userStorage.getCommonFriends(userId, friendId);
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(count);
        userStorage.create(user);
        count++;
        return user;
    }

    public User update(User user) {
        userExistsById(user.getId());
        userStorage.update(user);
        log.debug(user.getEmail() + " was updated");
        return user;
    }

    public List<User> getAllUsers() {
        List<User> usersFromMap = userStorage.getAllUsers();
        return usersFromMap;
    }

    public User getUserById(Integer id) {
        userExistsById(id);
        return userStorage.getUserById(id);
    }

    public void userExistsById(Integer id) {
        userStorage.userExistsById(id);
    }
}
