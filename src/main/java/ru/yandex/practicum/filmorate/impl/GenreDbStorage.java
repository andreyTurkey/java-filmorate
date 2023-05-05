package ru.yandex.practicum.filmorate.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenreDbStorage implements GenreStorage {

    final JdbcTemplate jdbcTemplate;
    UserStorage userStorage;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public void updateFilmGenre(Film film) {
        String gernreDeleteSqlQuery = "DELETE FROM genre WHERE film_id = ?";
        jdbcTemplate.update(gernreDeleteSqlQuery, film.getId());
        List<Genre> genres = film.getGenres();
        if (genres == null) return;

        jdbcTemplate.batchUpdate("INSERT INTO genre (film_id, genre_id) VALUES(?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, film.getId());
                preparedStatement.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });

    }

    @Override
    public List<Genre> getGenresByFilmId(Integer id) {
        String sqlQuery = "SELECT DISTINCT genre_id, name FROM genre AS g " +
                "INNER JOIN genres AS gs ON g.genre_id = gs.id  " +
                "WHERE film_id = ?";
        return this.jdbcTemplate.query(
                sqlQuery,
                (resultSet, rowNum) -> {
                    return Genre.builder()
                            .id(resultSet.getInt("genre_id"))
                            .name(resultSet.getString("name"))
                            .build();

                }, id);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT id, name FROM genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        genreExistsById(id);
        String sqlQuery = "SELECT id, name " +
                " FROM genres WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    @Override
    public void genreExistsById(Integer genreId) {
        List<Integer> ids = this.jdbcTemplate.query(
                "SELECT id FROM genres",
                (resultSet, rowNum) -> {
                    Integer id;
                    id = resultSet.getInt("id");
                    return id;
                });
        if (!ids.contains(genreId))
            throw new FilmNotFoundException(String.format("Жанра с ID = %d не существует. Проверьте ID.", genreId));
    }
}
