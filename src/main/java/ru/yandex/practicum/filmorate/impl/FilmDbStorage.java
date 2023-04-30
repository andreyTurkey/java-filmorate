package ru.yandex.practicum.filmorate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
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
        updateFilmGenre(film);
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

        updateFilmGenre(film);
        return getFilmById(film.getId());
    }

    @Override
    public void updateFilmGenre(Film film) {
        String gernreDeleteSqlQuery = "DELETE FROM genre WHERE film_id = ?";
        jdbcTemplate.update(gernreDeleteSqlQuery,
                film.getId());
        List<Genre> genres = film.getGenres();
        if (genres != null) {
            for (int i = 0; i < genres.size(); i++) {
                Genre genre = genres.get(i);
                String genreInsertSqlQuery = "INSERT INTO genre(film_id, genre_id) " +
                        "values (?, ?)";
                jdbcTemplate.update(genreInsertSqlQuery,
                        film.getId(),
                        genre.getId());
            }
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT id, name, description, duration, releaseDate, mpa FROM film";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
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

    @Override
    public Film getFilmById(Integer id) {
        filmExistsById(id);
        String sqlQuery = "SELECT id, name, description, duration, releaseDate, mpa " +
                "FROM film WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
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

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Integer ratingId = resultSet.getInt("mpa");
        Rating rating = getRatingById(ratingId);

        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .releaseDate(resultSet.getDate("releaseDate").toLocalDate())
                .mpa(rating)
                .genres(getGenresByFilmId(resultSet.getInt("id")))
                .build();
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .build();
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
    public void filmExistsById(Integer filmId) {
        List<Integer> ids = this.jdbcTemplate.query(
                "SELECT id FROM film",
                (resultSet, rowNum) -> {
                    Integer id;
                    id = resultSet.getInt("id");
                    return id;
                });
        if (!ids.contains(filmId))
            throw new FilmNotFoundException(String.format("Фильма с ID = %d не существует. Проверьте ID.", filmId));
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
        String sqlQuery = "SELECT  id " +
                "FROM film AS f " +
                "         LEFT OUTER JOIN ( " +
                "    SELECT film_id, " +
                "           COUNT(film_id)  AS count " +
                "    FROM likes " +
                "    GROUP BY film_id) " +
                "    AS l ON f.id = l.film_id ORDER BY count DESC LIMIT ?";
        List<Integer> ids = this.jdbcTemplate.query(sqlQuery,
                (resultSet, rowNum) -> {
                    Integer id;
                    id = resultSet.getInt("id");
                    return id;
                },
                count);
        List<Film> mostPopular = new ArrayList<>();
        for (Integer id : ids) {
            Film film = getFilmById(id);
            mostPopular.add(film);
        }
        return mostPopular;
    }

    public void clearTablesFilm() {
        jdbcTemplate.update("DROP TABLE IF EXISTS film CASCADE");
        jdbcTemplate.update("DROP TABLE IF EXISTS likes CASCADE");
    }

    public void createTablesFilm() {
        jdbcTemplate.update("create table IF NOT EXISTS film " +
                "( " +
                "    id INTEGER  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                "    name varchar NOT NULL, " +
                "    description varchar NOT NULL, " +
                "    duration INTEGER NOT NULL, " +
                "    releaseDate date NOT NULL, " +
                "    mpa INTEGER REFERENCES mpa (id) " +
                ")");
        jdbcTemplate.update("create table IF NOT EXISTS likes " +
                "( " +
                "    film_id INTEGER NOT NULL REFERENCES film (id), " +
                "    user_id INTEGER NOT NULL REFERENCES users (id) " +
                ")");
    }
}
