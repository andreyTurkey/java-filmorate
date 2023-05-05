package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;

import java.util.*;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmService {

    final FilmStorage filmStorage;

    final UserService userService;

    int count;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        count = 1;
    }

    public List<Film> getFilmByRating(Integer count) {
        return filmStorage.getFilmByRating(count);
    }

    public Film addLike(Integer filmId, Integer userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public Film create(Film film) {
        film.setId(count);
        filmStorage.create(film);
        count++;
        return film;
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        return filmStorage.deleteLike(filmId, userId);
    }
}
