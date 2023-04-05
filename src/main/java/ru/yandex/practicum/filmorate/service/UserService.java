package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserStorage {

    private UserStorage userStorage;

    private int count;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
        count = 1;
    }

    public User addFriend(Integer userId, Integer friendId) {
        if (userStorage.getUsers().containsKey(userId) && userStorage.getUsers().containsKey(friendId)) {
            User user = userStorage.getUserById(userId);
            User friend = userStorage.getUserById(friendId);
            try {
                user.getFriends().add(friendId);
            } catch (NullPointerException ex) {
                Set<Integer> friends = new HashSet<>();

                friends.add(friendId);

                user.setFriends(friends);
            }
            try {
                friend.getFriends().add(userId);
            } catch (NullPointerException ex) {
                Set<Integer> friends = new HashSet<>();

                friends.add(userId);

                friend.setFriends(friends);
            }
            userStorage.update(user);

            userStorage.update(friend);
            return user;
        } else {
            throw new UserNotFoundException(String.format("Пользователя %d не существует.", friendId));
        }
    }

    public List<User> getUserFriends(Integer userId) {
        if (ifUserExists(userId)) {
            User user = userStorage.getUserById(userId);
            List<User> userFriends = new ArrayList<>();
            try {
                Set<Integer> userFreindsId = user.getFriends();
                for (Integer id : userFreindsId) {
                    userFriends.add(userStorage.getUsers().get(id));
                }
                return userFriends;
            } catch (NullPointerException ex) {
                return userFriends;
            }
        } else {
            throw new UserNotFoundException(String.format("Проверьте ID пользователей"));
        }
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        if (ifUserExists(userId) && ifUserExists(friendId)) {
            User user = userStorage.getUserById(userId);
            User friend = userStorage.getUserById(friendId);
            List<Integer> commonFreindsId;
            List<User> commonFreinds = new ArrayList<>();
            try {
                commonFreindsId = user.getFriends().stream().filter(t -> friend.getFriends().contains(t))
                        .collect(Collectors.toList());
                for (Integer id : commonFreindsId) {
                    commonFreinds.add(userStorage.getUsers().get(id));
                }
                return commonFreinds;
            } catch (NullPointerException ex) {
                return commonFreinds;
            }
        } else {
            throw new UserNotFoundException(String.format("Проверьте ID пользователей"));
        }
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        if (ifUserExists(userId) && ifUserExists(friendId)) {
            User user = userStorage.getUserById(userId);
            User friend = userStorage.getUserById(friendId);
            List<User> users = new ArrayList<>();
            try {
                Set<Integer> friendsId = user.getFriends();

                friendsId.remove(friendId);

                user.setFriends(friendsId);

                userStorage.update(user);

                users.add(userStorage.getUserById(userId));

                Set<Integer> otherFriendsId = friend.getFriends();

                otherFriendsId.remove(userId);

                friend.setFriends(otherFriendsId);

                userStorage.update(friend);

                users.add(userStorage.getUserById(friendId));

                return users;
            } catch (NullPointerException ex) {
                return users;
            }
        } else {
            throw new UserNotFoundException(String.format("Проверьте ID пользователей"));
        }
    }

    @Override
    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
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
        if (!userStorage.getUsers().containsKey(user.getId())) {
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
        if (ifUserExists(id)) {
            return userStorage.getUserById(id);
        } else {
            throw new UserNotFoundException(
                    String.format("Пользователя %d не существует.", id));
        }
    }

    @Override
    public User deleteUserById(Integer id) {
        if (ifUserExists(id)) {
            User user = userStorage.getUserById(id);
            userStorage.deleteUserById(id);
            return user;
        } else {
            throw new UserNotFoundException(
                    String.format("Пользователя  %d не существует.", id));
        }
    }

    @Override
    public List<User> deleteUsers() {
        List<User> deletedUsers = userStorage.deleteUsers();
        return deletedUsers;
    }

    public boolean ifUserExists(Integer userId) {
        return userStorage.getUsers().containsKey(userId);
    }
}
