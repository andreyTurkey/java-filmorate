package ru.yandex.practicum.filmorate.extractor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserFriendsExtractor implements ResultSetExtractor<List<User>> {

    @Override
    public List<User> extractData(ResultSet resultSet)
            throws SQLException, DataAccessException {

        List<User> friends = new ArrayList<>();

        while (resultSet.next()) {

            User user = User.builder()
                    .id(resultSet.getInt("friend_id"))
                    .name(resultSet.getString("friend_name"))
                    .email(resultSet.getString("friend_email"))
                    .login(resultSet.getString("friend_login"))
                    .birthday(resultSet.getDate("friend_birthday").toLocalDate())
                    .build();
            friends.add(user);
        }
        return friends;
    }
}
