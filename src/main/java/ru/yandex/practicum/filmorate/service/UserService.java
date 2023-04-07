package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class UserService {

    final UserStorage userStorage;

    int count;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
        count = 1;
    }

    public User addFriend(Integer userId, Integer friendId) {
        userExistsById(userId);
        userExistsById(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.addFriend(friendId);
        friend.addFriend(userId);

        userStorage.update(user);
        userStorage.update(friend);

        return user;
    }

    public List<User> getUserFriends(Integer userId) {
        userExistsById(userId);

        User user = userStorage.getUserById(userId);
        List<User> userFriends = new ArrayList<>();

        Set<Integer> userFreindsId = user.getFriends();
        for (Integer id : userFreindsId) {
            userFriends.add(userStorage.getUserById(id));
        }
        return userFriends;
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        userExistsById(userId);
        userExistsById(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        return user.getFriends().stream()
                .filter(t -> friend.getFriends().contains(t))
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        userExistsById(userId);
        userExistsById(friendId);

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

    public User deleteUserById(Integer id) {
        userExistsById(id);
        User user = userStorage.getUserById(id);
        userStorage.deleteUserById(id);
        return user;
    }

    public List<User> deleteUsers() {
        List<User> deletedUsers = userStorage.deleteUsers();
        return deletedUsers;
    }

    public void userExistsById(Integer id) {
        userStorage.userExistsById(id);
    }

}
