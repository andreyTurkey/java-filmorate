package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users;

    public InMemoryUserStorage() {
        users = new HashMap<>();
    }

    public Map<Integer, User> getUsers() {
        return users;
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
        List<User> usersFromMap = new ArrayList<>(users.values());
        return usersFromMap;
    }

    @Override
    public User getUserById(Integer id) {
            return users.get(id);
    }

    @Override
    public User deleteUserById(Integer id) {
            User user = users.get(id);
            users.remove(id);
            return user;
    }

    @Override
    public List<User> deleteUsers() {
        users.clear();
        List<User> deletedUsers = new ArrayList<>(users.values());
        return deletedUsers;
    }
}
