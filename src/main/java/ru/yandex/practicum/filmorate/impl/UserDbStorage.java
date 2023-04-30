package ru.yandex.practicum.filmorate.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users(email, login, name, birthday) " +
                "values (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());

        return null;
    }

    @Override
    public User update(User user) {
        userExistsById(user.getId());
        String sqlQuery = "UPDATE users SET " +
                " email = ?, login = ?, name = ?, birthday = ?" +
                " WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return getUserById(user.getId());
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        userExistsById(userId);
        userExistsById(friendId);
        jdbcTemplate.update("INSERT INTO friend(user_id, friend_id) " +
                "values (?, ?)", userId, friendId);

        return getUserById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT id, email, login, name, birthday  FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(Integer id) {
        userExistsById(id);
        String sqlQuery = "SELECT id, email, login, name, birthday " +
                "FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public void userExistsById(Integer id) {
        List<Integer> ids = this.jdbcTemplate.query(
                "SELECT id FROM users",
                (resultSet, rowNum) -> {
                    Integer userId;
                    userId = resultSet.getInt("id");
                    return userId;
                });
        if (!ids.contains(id))
            throw new UserNotFoundException(String.format("Пользователя с ID = %d не существует. Проверьте ID.", id));
    }

    @Override
    public List<User> deleteFriend(Integer userId, Integer friendId) {
        String sqlQuery = "DELETE FROM friend WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery,
                userId, friendId);
        return getUserFriends(userId);
    }

    @Override
    public List<User> getUserFriends(Integer userId) {
        String getFriendsSqlQuery = "SELECT DISTINCT friend_id FROM friend " +
                "WHERE user_id = ?";
        List<Integer> ids = this.jdbcTemplate.query(
                getFriendsSqlQuery,
                (resultSet, rowNum) -> {
                    Integer id;
                    id = resultSet.getInt("friend_id");
                    return id;
                }, userId);
        List<User> users = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            users.add(getUserById(ids.get(i)));
        }
        return users;
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        userExistsById(userId);
        userExistsById(friendId);
        return getUserFriends(userId).stream().filter(
                t -> getUserFriends(friendId).contains(t)).collect(Collectors.toList());
    }

    public void clearTablesUser() {
        jdbcTemplate.update("DROP TABLE IF EXISTS users CASCADE");
        jdbcTemplate.update("DROP TABLE IF EXISTS friend CASCADE;");
    }

    public void createTablesUser() {
        jdbcTemplate.update("create table IF NOT EXISTS users " +
                "(" +
                "    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                "    email varchar NOT NULL, " +
                "    login varchar NOT NULL, " +
                "    name varchar NOT NULL, " +
                "    birthday date NOT NULL " +
                ")");
        jdbcTemplate.update("create table IF NOT EXISTS friend " +
                "( " +
                "    user_id INTEGER NOT NULL REFERENCES users (id), " +
                "    friend_id INTEGER NOT NULL REFERENCES users (id) " +
                ")");
    }
}
