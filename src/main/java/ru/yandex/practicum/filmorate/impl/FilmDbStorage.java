package ru.yandex.practicum.filmorate.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.extractor.FilmsExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Slf4j
@Component
@Qualifier("FilmDbStorage")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDbStorage implements FilmStorage {

    final JdbcTemplate jdbcTemplate;

    final UserStorage userStorage;

    final MpaStorage mpaStorage;

    final GenreStorage genreStorage;

    final FilmsExtractor filmsExtractor;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("UserDbStorage") UserStorage userStorage,
                         MpaStorage mpaStorage, GenreStorage genreStorage, FilmsExtractor filmsExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.filmsExtractor = filmsExtractor;
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO film(name, description, duration, releaseDate, mpa) " +
                "values (?, ?, ?, ?, ?)";
        Rating rating = film.getMpa();
        int ratingId = rating.getId();

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                ratingId
        );
        genreStorage.updateFilmGenre(film);
        return getFilmById(film.getId());
    }

    @Override
    public Film update(Film film) {
        filmExistsById(film.getId());
        String sqlQuery = "UPDATE film SET " +
                " name = ?, description = ?, duration = ?, releaseDate = ?, mpa = ?" +
                " WHERE id = ?";

        Rating rating = film.getMpa();
        int ratingId = rating.getId();

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                ratingId,
                film.getId());

        genreStorage.updateFilmGenre(film);
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT DISTINCT f.id, " +
                "       f.name, " +
                "       f.description, " +
                "       f.duration, " +
                "       f.releaseDate, " +
                "       f.mpa, " +
                "       m.name AS mpa_name, " +
                "       m.description AS mpa_description, " +
                "       gs.id AS genre_id, " +
                "       gs.name AS genre_name " +
                "FROM film AS f " +
                "LEFT OUTER JOIN genre AS g ON  f.id = g.film_id " +
                "LEFT OUTER JOIN genres AS gs ON g.genre_id = gs.id " +
                "LEFT OUTER JOIN mpa AS m ON  f.mpa  = m.id ";
        return jdbcTemplate.query(sqlQuery, filmsExtractor);
    }

    @Override
    public Film getFilmById(Integer id) {
        filmExistsById(id);
        String sqlQuery = "SELECT DISTINCT f.id, " +
                "       f.name, " +
                "       f.description, " +
                "       f.duration, " +
                "       f.releaseDate, " +
                "       f.mpa, " +
                "       m.name AS mpa_name, " +
                "       m.description AS mpa_description, " +
                "       gs.id AS genre_id, " +
                "       gs.name AS genre_name " +
                "FROM film AS f " +
                "LEFT OUTER JOIN genre AS g ON  f.id = g.film_id " +
                "LEFT OUTER JOIN genres AS gs ON g.genre_id = gs.id " +
                "LEFT OUTER JOIN mpa AS m ON  f.mpa  = m.id " +
                "WHERE f.id = ?;";
        List<Film> film = jdbcTemplate.query(sqlQuery, filmsExtractor, id);
        return film.get(0);
    }

    @Override
    public void filmExistsById(Integer filmId) {
        SqlRowSet filmIdRows = jdbcTemplate.queryForRowSet("SELECT * FROM film " +
                "WHERE id = ?", filmId);
        if (!filmIdRows.next()) {
            throw new FilmNotFoundException(String.format("Фильма с ID = %d не существует. Проверьте ID.", filmId));
        }
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        userStorage.userExistsById(userId);
        filmExistsById(filmId);
        String sqlQuery = "INSERT INTO likes(film_id, user_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        userStorage.userExistsById(userId);
        filmExistsById(filmId);
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getFilmByRating(Integer count) {
        String sqlQuery = "SELECT DISTINCT f.id,\n" +
                "                f.name,\n" +
                "                f.description,\n" +
                "                f.duration,\n" +
                "                f.releaseDate,\n" +
                "                f.mpa,\n" +
                "                m.name AS mpa_name,\n" +
                "                m.description AS mpa_description,\n" +
                "                gs.id AS genre_id,\n" +
                "                gs.name AS genre_name\n" +
                "                FROM film AS f\n" +
                "                LEFT OUTER JOIN genre AS g ON  f.id = g.film_id\n" +
                "                LEFT OUTER JOIN genres AS gs ON g.genre_id = gs.id\n" +
                "                LEFT OUTER JOIN mpa AS m ON  f.mpa  = m.id\n" +
                "WHERE f.id IN (SELECT  f.id\n" +
                "               FROM film AS f\n" +
                "                        LEFT OUTER JOIN (\n" +
                "                   SELECT film_id,\n" +
                "                          COUNT(film_id)  AS count\n" +
                "                   FROM likes\n" +
                "                   GROUP BY film_id)\n" +
                "                   AS l ON f.id = l.film_id ORDER BY count DESC LIMIT ?)";
        return jdbcTemplate.query(sqlQuery, filmsExtractor, count);
    }
}
