package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Integer id);

    Genre getGenreById(Integer id);

    List<Genre> getAllGenres();

    Rating getRatingById(Integer id);

    List<Rating> getAllRating();

    void filmExistsById(Integer filmId);

    void genreExistsById(Integer genreId);

    void ratingExistsById(Integer ratingId);

    Film addLike(Integer filmId, Integer userId);

    Film deleteLike(Integer filmId, Integer userId);

    List<Film> getFilmByRating(Integer count);

    void updateFilmGenre(Film film);

    List<Genre> getGenresByFilmId(Integer id);
}
