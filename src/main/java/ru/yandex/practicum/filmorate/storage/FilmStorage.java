package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Integer id);

    Film deleteFilmById(Integer id);

    List<Film> deleteFilms();

    void filmExistsById(Integer filmId);
}
