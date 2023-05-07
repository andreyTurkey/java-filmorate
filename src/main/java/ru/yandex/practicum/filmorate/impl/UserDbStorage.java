package ru.yandex.practicum.filmorate.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.extractor.UserFriendsExtractor;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Qualifier("UserDbStorage")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDbStorage implements UserStorage {

    final JdbcTemplate jdbcTemplate;

    final UserFriendsExtractor userFriendsExtractor;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserFriendsExtractor userFriendsExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.userFriendsExtractor = userFriendsExtractor;
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
        SqlRowSet usersIdRows = jdbcTemplate.queryForRowSet("SELECT * FROM users " +
                "WHERE id = ?", id);
        if (!usersIdRows.next()) {
            throw new FilmNotFoundException(String.format("Жанра с ID = %d не существует. Проверьте ID.", id));
        }
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
        String getFriendsSqlQuery = "SELECT DISTINCT f.user_id, " +
                "                f.friend_id, " +
                "                u.name AS friend_name, " +
                "                u.email AS friend_email, " +
                "                u.login AS friend_login, " +
                "                u.birthday AS friend_birthday " +
                "FROM friend AS f " +
                "LEFT OUTER JOIN users AS u ON  f.friend_id = u.id " +
                " WHERE f.user_id = ?";
        return jdbcTemplate.query(getFriendsSqlQuery, userFriendsExtractor, userId);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        userExistsById(userId);
        userExistsById(friendId);
        String getCommonFriendSqlQuery = "SELECT *\n" +
                "FROM users\n" +
                "WHERE id IN\n" +
                "      (SELECT f.friend_id\n" +
                "       FROM friend AS f\n" +
                "    INNER JOIN friend  AS fr ON f.friend_id = fr.friend_id\n" +
                "    WHERE f.user_id =? AND fr.user_id =?);";
        return jdbcTemplate.query(getCommonFriendSqlQuery, this::mapRowToUser, userId, friendId);
    }
}
