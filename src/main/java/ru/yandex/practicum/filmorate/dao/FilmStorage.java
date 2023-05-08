package ru.yandex.practicum.filmorate.dao;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Integer id);

    void filmExistsById(Integer filmId);

    Film addLike(Integer filmId, Integer userId);

    Film deleteLike(Integer filmId, Integer userId);

    List<Film> getFilmByRating(Integer count);
}
