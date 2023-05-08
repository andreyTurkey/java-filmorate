package ru.yandex.practicum.filmorate.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UsersExtractor implements ResultSetExtractor<List<User>> {

    @Override
    public List<User> extractData(ResultSet resultSet)
            throws SQLException, DataAccessException {

        List<User> friends = new ArrayList<>();
        Map<User, List<User>> data = new LinkedHashMap<>();

        while (resultSet.next()) {
            User user = User.builder()
                    .id(resultSet.getInt("user_id"))
                    .name(resultSet.getString("user_name"))
                    .email(resultSet.getString("user_email"))
                    .login(resultSet.getString("user_login"))
                    .birthday(resultSet.getDate("birthday").toLocalDate())
                    .build();
            if (data.containsKey(user)) {
                data.get(user).add((User.builder()
                        .id(resultSet.getInt("friend_id"))
                        .name(resultSet.getString("friend_name"))
                        .email(resultSet.getString("friend_email"))
                        .login(resultSet.getString("friend_login"))
                        .birthday(resultSet.getDate("friend_birthday").toLocalDate())
                        .build()));
            } else {
                data.put(user, new ArrayList<>());
                if (resultSet.getInt("friend_id") != 0) {
                    data.get(user).add(User.builder()
                            .id(resultSet.getInt("friend_id"))
                            .name(resultSet.getString("friend_name"))
                            .email(resultSet.getString("friend_email"))
                            .login(resultSet.getString("friend_login"))
                            .birthday(resultSet.getDate("friend_birthday").toLocalDate())
                            .build());
                }
            }
        }
        for (User newUser : data.keySet()) {
            if (data.get(newUser).size() == 0) {
                newUser.setFriends(new ArrayList<>());
                friends.add(newUser);
            } else {
                newUser.setFriends(data.get(newUser));
                friends.add(newUser);
            }
        }
        return friends;
    }
}
