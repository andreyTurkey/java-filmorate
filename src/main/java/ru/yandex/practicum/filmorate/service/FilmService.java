package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmService implements FilmStorage {

    final FilmStorage filmStorage;

    List<Film> filmsRating;

    int count;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        count = 1;
        filmsRating = new ArrayList<>();
    }

    protected Comparator<Film> comparator = new Comparator<Film>() {
        @Override
        public int compare(Film o1, Film o2) {
            return o2.getLikes().size() - o1.getLikes().size();
        }
    };

    public void setFilmsRating(List<Film> filmsRating) {
        this.filmsRating = filmsRating;
    }

    public void addRatingFilm(Film film) {
        filmsRating.add(film);
        updateRating();
    }

    public void deleteFilmsRating() {
        filmsRating.clear();
    }

    public List<Film> getFilmByRating(Integer count) {
        return filmsRating.stream().limit(count).collect(Collectors.toList());
    }

    public void deleteFilmRating(Film film) {
        setFilmsRating(filmsRating.stream().filter(i -> !(i.getId() == film.getId())).collect(Collectors.toList()));
    }

    public void updateRating() {
        filmsRating.sort(comparator);
        log.info(String.format("%s", filmsRating));
    }

    public Film addLike(Integer filmId, Integer userId) {
        if (userId <= 0) throw new UserNotFoundException(String.format("Пользователя %d  не существует", userId));
        Film film = filmStorage.getFilmById(filmId);

        film.addLike(userId);

        update(film);
        return film;
    }

    @Override
    public Film create(Film film) {
        film.setId(count);
        filmStorage.create(film);
        addRatingFilm(film);
        count++;
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!filmExistsById(film.getId())) {
            log.error(film.getName() + " film doesn't exist.");
            throw new UserNotFoundException("Check ID field.");
        }
        deleteFilmRating(film);

        filmStorage.update(film);

        addRatingFilm(film);

        updateRating();
        log.info(film.getName() + " was updated");
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Integer id) {
        if (filmExistsById(id)) {
            return filmStorage.getFilmById(id);
        } else {
            throw new UserNotFoundException(
                    String.format("Фильм %d не существует.", id));
        }
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        if (userId <= 0) throw new UserNotFoundException(String.format("Пользователя %d  не существует", userId));
        Film film = filmStorage.getFilmById(filmId);

        film.deleteLike(userId);

        update(film);
        return film;
    }

    @Override
    public Film deleteFilmById(Integer id) {
        if (!filmExistsById(id)) {
            throw new UserNotFoundException(
                    String.format("Фильм  %d не существует.", id));
        }
        Film film = filmStorage.deleteFilmById(id);
        deleteFilmRating(film);
        log.info(film.getName() + " was deleted");
        return film;
    }

    @Override
    public List<Film> deleteFilms() {
        log.info("Films were deleted");
        deleteFilmsRating();
        return filmStorage.deleteFilms();
    }

    @Override
    public boolean filmExistsById(Integer filmId) {
        return filmStorage.filmExistsById(filmId);
    }
}
