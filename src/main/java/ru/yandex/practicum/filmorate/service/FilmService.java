package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmService implements FilmStorage {

    final FilmStorage filmStorage;

    int count;

    final FilmRating filmRating;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, FilmRating filmRating) {
        this.filmStorage = filmStorage;
        count = 1;
        this.filmRating = filmRating;
    }

    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);

        film.getLikes().add(userId);

        update(film);
        return film;
    }

    @Override
    public Film create(Film film) {
        film.setId(count);
        film.setLikes(new HashSet<>());
        filmStorage.create(film);
        filmRating.add(film);
        count++;
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!ifFilmExists(film.getId())) {
            log.error(film.getName() + " film doesn't exist.");
            throw new UserNotFoundException("Check ID field.");
        }
        if (film.getLikes() == null) film.setLikes(new HashSet<>());

        filmRating.deleteFilm(film);

        filmStorage.update(film);

        filmRating.add(film);

        filmRating.updateRating();
        log.info(film.getName() + " was updated");
        return film;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Integer id) {
        if (ifFilmExists(id)) {
            return filmStorage.getFilmById(id);
        } else {
            throw new UserNotFoundException(
                    String.format("Фильм %d не существует.", id));
        }
    }

    public List<Film> getFilmByRating(Integer count) {
        return filmRating.getFilmRating(count);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        if (userId <= 0) throw new UserNotFoundException(String.format("Пользователя %d  не существует", userId));
        Film film = filmStorage.getFilmById(filmId);

        film.getLikes().remove(userId);

        update(film);
        return film;
    }

    @Override
    public Film deleteFilmById(Integer id) {
        if (ifFilmExists(id)) {
            Film film = filmStorage.deleteFilmById(id);
            filmRating.deleteFilm(film);
            log.info(film.getName() + " was deleted");
            return film;
        } else {
            throw new UserNotFoundException(
                    String.format("Фильм  %d не существует.", id));
        }
    }

    @Override
    public List<Film> deleteFilms() {
        log.info("Films were deleted");
        filmRating.deleteFilms();
        return filmStorage.deleteFilms();
    }

    public boolean ifFilmExists(Integer id) {
        return filmStorage.getFilms().containsKey(id);
    }
}
