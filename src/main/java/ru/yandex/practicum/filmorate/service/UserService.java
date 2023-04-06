package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class UserService implements UserStorage {

    final UserStorage userStorage;

    int count;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
        count = 1;
    }

    public User addFriend(Integer userId, Integer friendId) {
        if (!(userExistsById(userId) && userExistsById(friendId))) {
            throw new UserNotFoundException
                    (String.format("Пользователя %d не существует.", friendId));
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.addFriend(friendId);
        friend.addFriend(userId);

        userStorage.update(user);
        userStorage.update(friend);

        return user;
    }

    public List<User> getUserFriends(Integer userId) {
        if (!userExistsById(userId)) {
            throw new UserNotFoundException(String.format("Проверьте ID пользователей"));
        }
        User user = userStorage.getUserById(userId);
        List<User> userFriends = new ArrayList<>();

        Set<Integer> userFreindsId = user.getFriends();
        for (Integer id : userFreindsId) {
            userFriends.add(userStorage.getUserById(id));
        }
        return userFriends;
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        if (!userExistsById(userId) && !userExistsById(friendId)) {
            throw new UserNotFoundException(String.format("Проверьте ID пользователей"));
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        List<Integer> commonFreindsId;
        List<User> commonFreinds = new ArrayList<>();

        commonFreindsId = user.getFriends().stream().filter(t -> friend.getFriends().contains(t))
                .collect(Collectors.toList());

        for (Integer id : commonFreindsId) {
            commonFreinds.add(userStorage.getUserById(id));
        }
        return commonFreinds;
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        if (!userExistsById(userId) && !userExistsById(friendId)) {
            throw new UserNotFoundException(String.format("Проверьте ID пользователей"));
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        List<User> users = new ArrayList<>();

        user.deleteFriend(friendId);

        userStorage.update(user);

        users.add(userStorage.getUserById(userId));

        friend.deleteFriend(userId);

        userStorage.update(friend);

        users.add(userStorage.getUserById(friendId));

        return users;
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(count);
        userStorage.create(user);
        count++;
        return user;
    }

    @Override
    public User update(User user) {
        if (!userExistsById(user.getId())) {
            log.error(user.getName() + " user doesn't exist.");
            throw new UserNotFoundException("Check ID field.");
        }
        userStorage.update(user);
        log.debug(user.getEmail() + " was updated");
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> usersFromMap = userStorage.getAllUsers();
        return usersFromMap;
    }

    @Override
    public User getUserById(Integer id) {
        if (userExistsById(id)) {
            return userStorage.getUserById(id);
        } else {
            throw new UserNotFoundException(
                    String.format("Пользователя %d не существует.", id));
        }
    }

    @Override
    public User deleteUserById(Integer id) {
        if (!userExistsById(id)) {
            throw new UserNotFoundException(
                    String.format("Пользователя  %d не существует.", id));
        }
        User user = userStorage.getUserById(id);
        userStorage.deleteUserById(id);
        return user;
    }

    @Override
    public List<User> deleteUsers() {
        List<User> deletedUsers = userStorage.deleteUsers();
        return deletedUsers;
    }

    @Override
    public boolean userExistsById(Integer id) {
        return userStorage.userExistsById(id);
    }
}
