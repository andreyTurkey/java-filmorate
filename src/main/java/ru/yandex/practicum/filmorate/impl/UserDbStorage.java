package ru.yandex.practicum.filmorate.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
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
        String getFriendsSqlQuery = "SELECT DISTINCT f.user_id, " +
                "                f.friend_id, " +
                "                u.name AS friend_name, " +
                "                u.email AS friend_email, " +
                "                u.login AS friend_login, " +
                "                u.birthday AS friend_birthday, " +
                "                us.name AS user_name, " +
                "                us.email AS user_email, " +
                "                us.login AS user_login, " +
                "                us.birthday AS user_birthday " +
                "FROM friend AS f " +
                "LEFT OUTER JOIN users AS u ON  f.friend_id = u.id " +
                "LEFT OUTER JOIN users AS us ON f.user_id = us.id" +
                " WHERE f.user_id = ?";
        return jdbcTemplate.query(getFriendsSqlQuery, userFriendsExtractor, userId);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        userExistsById(userId);
        userExistsById(friendId);
        String getCommonFriendSqlQuery = "SELECT * " +
                "FROM users " +
                "WHERE id IN " +
                "(SELECT u.friend_id " +
                "FROM " +
                "    (SELECT user_id, " +
                "            friend_id " +
                "     FROM friend " +
                "     WHERE user_id = ?) AS u " +
                "INNER JOIN ( " +
                "    SELECT user_id, " +
                "           friend_id " +
                "    FROM friend " +
                "WHERE user_id = ? " +
                "    ) as f ON u.friend_id = f.friend_id);";
        return jdbcTemplate.query(getCommonFriendSqlQuery, this::mapRowToUser, userId, friendId);
    }
}
