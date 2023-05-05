package ru.yandex.practicum.filmorate.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MpaDbStorage implements MpaStorage {

    final JdbcTemplate jdbcTemplate;
    UserStorage userStorage;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public List<Rating> getAllRating() {
        String sqlQuery = "SELECT id, name, description FROM mpa";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    @Override
    public Rating getRatingById(Integer id) {
        ratingExistsById(id);
        String sqlQuery = "SELECT id, name, description " +
                " FROM mpa WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .build();
    }

    @Override
    public void ratingExistsById(Integer ratingId) {
        List<Integer> ids = this.jdbcTemplate.query(
                "SELECT id FROM mpa",
                (resultSet, rowNum) -> {
                    Integer id;
                    id = resultSet.getInt("id");
                    return id;
                });
        if (!ids.contains(ratingId))
            throw new FilmNotFoundException(String.format("Рейтинга с ID = %d не существует. Проверьте ID.", ratingId));
    }
}
