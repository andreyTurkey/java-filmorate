package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users;

    public InMemoryUserStorage() {
        users = new HashMap<>();
    }

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public void userExistsById(Integer id) {
        if (!users.containsKey(id))
            throw new UserNotFoundException(String.format("Пользователя ID = %d не существует.", id));
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public List<User> deleteFriend(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public List<User> getUserFriends(Integer userId) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        return null;
    }
}
