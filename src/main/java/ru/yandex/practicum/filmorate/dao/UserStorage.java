package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user);

    List<User> getAllUsers();

    User getUserById(Integer id);

    void userExistsById(Integer id);

    User addFriend(Integer userId, Integer friendId);

    List<User> deleteFriend(Integer userId, Integer friendId);

    List<User> getUserFriends(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer friendId);
}
