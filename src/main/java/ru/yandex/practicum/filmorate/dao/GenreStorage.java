package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    void updateFilmGenre(Film film);

    List<Genre> getGenresByFilmId(Integer id);

    List<Genre> getAllGenres();

    Genre getGenreById(Integer id);

    void genreExistsById(Integer genreId);
}
