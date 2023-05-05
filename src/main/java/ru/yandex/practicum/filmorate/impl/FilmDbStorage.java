package ru.yandex.practicum.filmorate.impl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.extractor.FilmsExtractor;
import ru.yandex.practicum.filmorate.extractor.SingleFilmExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    final SingleFilmExtractor singleFilmExtractor;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("UserDbStorage") UserStorage userStorage,
    MpaStorage mpaStorage, GenreStorage genreStorage, FilmsExtractor filmsExtractor
    , SingleFilmExtractor singleFilmExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.filmsExtractor = filmsExtractor;
        this.singleFilmExtractor = singleFilmExtractor;
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
        String sqlQuery = "SELECT DISTINCT f.id," +
                "       f.name , " +
                "       f.description, " +
                "       f.duration, " +
                "       f.releaseDate, " +
                "       f.mpa, " +
                "       gs.id AS genre_id, " +
                "       gs.name AS genre_name " +
                "FROM film AS f " +
                "LEFT OUTER JOIN genre AS g ON  f.id = g.film_id " +
                "LEFT OUTER JOIN genres AS gs ON g.genre_id = gs.id";
        return jdbcTemplate.query(sqlQuery, filmsExtractor);
    }

    /*@Override
    public Film getFilmById(Integer id) {
        filmExistsById(id);
        *//*String sqlQuery = "SELECT id, name, description, duration, releaseDate, mpa " +
                "FROM film WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);*//*
        String sqlQuery = "SELECT DISTINCT f.id," +
                "       f.name , " +
                "       f.description, " +
                "       f.duration, " +
                "       f.releaseDate, " +
                "       f.mpa, " +
                "       gs.id AS genre_id, " +
                "       gs.name AS genre_name " +
                "FROM film AS f " +
                "LEFT OUTER JOIN genre AS g ON  f.id = g.film_id " +
                "LEFT OUTER JOIN genres AS gs ON g.genre_id = gs.id " +
                "WHERE f.id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }*/

    @Override
    public Film getFilmById(Integer id) {
        filmExistsById(id);
        /*String sqlQuery = "SELECT id, name, description, duration, releaseDate, mpa " +
                "FROM film WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);*/
        String sqlQuery = "SELECT DISTINCT f.id," +
                "       f.name , " +
                "       f.description, " +
                "       f.duration, " +
                "       f.releaseDate, " +
                "       f.mpa, " +
                "       gs.id AS genre_id, " +
                "       gs.name AS genre_name " +
                "FROM film AS f " +
                "LEFT OUTER JOIN genre AS g ON  f.id = g.film_id " +
                "LEFT OUTER JOIN genres AS gs ON g.genre_id = gs.id " +
                "WHERE f.id = ?";
        return jdbcTemplate.query(sqlQuery, singleFilmExtractor, id);

    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Integer ratingId = resultSet.getInt("mpa");
        Rating rating = mpaStorage.getRatingById(ratingId);

        List<Genre> genres = new ArrayList<>();
        if (!resultSet.getString("name").equals("")) {
            genres.add(Genre.builder()
                    .id(resultSet.getInt("genre_id"))
                    .name(resultSet.getString("genre_name"))
                    .build());
        }
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .releaseDate(resultSet.getDate("releaseDate").toLocalDate())
                .mpa(rating)
                .genres(genres)
                .build();
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
}
