package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.enumCatalog.Genre;
import ru.yandex.practicum.filmorate.enumCatalog.Rating;
import ru.yandex.practicum.filmorate.exception.InvalidDataException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmService {

    final FilmStorage filmStorage;

    final UserService userService;

    final List<Film> filmsRating;

    int count;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        count = 1;
        filmsRating = new ArrayList<>();
    }

    private Comparator<Film> comparator = new Comparator<Film>() {
        @Override
        public int compare(Film o1, Film o2) {
            return o2.getLikes().size() - o1.getLikes().size();
        }
    };

    private void addRatingFilm(Film film) {
        filmsRating.add(film);
        updateRating();
    }

    private void deleteFilmsRating() {
        filmsRating.clear();
    }

    public List<Film> getFilmByRating(Integer count) {
        return filmsRating.stream().limit(count).collect(Collectors.toList());
    }

    private void deleteFilmRating(Film film) {
        filmsRating.remove(film);
    }

    private void updateRating() {
        filmsRating.sort(comparator);
        log.info(String.format("%s", filmsRating));
    }

    public Film addLike(Integer filmId, Integer userId) {
        userService.userExistsById(userId);
        Film film = filmStorage.getFilmById(filmId);

        film.addLike(userId);

        update(film);
        return film;
    }

    public Film create(Film film) {
        film.setId(count);
        filmStorage.create(film);
        addRatingFilm(film);
        count++;
        return film;
    }

    public Film update(Film film) {
        filmExistsById(film.getId());

        deleteFilmRating(getFilmById(film.getId()));

        filmStorage.update(film);

        addRatingFilm(film);

        updateRating();
        log.info(film.getName() + " was updated");
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        filmExistsById(filmId);
        userService.userExistsById(userId);

        Film film = filmStorage.getFilmById(filmId);
        if (!film.getLikes().contains(userId))
            throw new UserNotFoundException(String.format("Пользователя %d  не существует", userId));

        film.deleteLike(userId);

        update(film);
        return film;
    }

    public Film deleteFilmById(Integer id) {
        filmExistsById(id);
        Film film = filmStorage.deleteFilmById(id);
        deleteFilmRating(film);
        log.info(film.getName() + " was deleted");
        return film;
    }

    public List<Film> deleteFilms() {
        log.info("Films were deleted");
        deleteFilmsRating();
        return filmStorage.deleteFilms();
    }

    public Genre getGenre(String filmGenre) throws InvalidDataException {
        switch (filmGenre) {
            case "comedy":
                return Genre.COMEDY;
            case "drama":
                return Genre.DRAMA;
            case "cartoon":
                return Genre.CARTOON;
            case "thriller":
                return Genre.THRILLER;
            case "documentary":
                return Genre.DOCUMENTARY;
            case "action":
                return Genre.ACTION;
            default:
                throw  new InvalidDataException("Жанр не существует.");
        }
    }

    public Rating getRating(String filmRating) throws InvalidDataException {
        switch (filmRating) {
            case "g":
                return Rating.G;
            case "pg":
                return Rating.PG;
            case "pg-13":
                return Rating.PG_13;
            case "r":
                return Rating.R;
            case "nc-17":
                return Rating.NC_17;
            default:
                throw  new InvalidDataException("Жанр не существует.");
        }
    }

    private void filmExistsById(Integer filmId) {
        filmStorage.filmExistsById(filmId);
    }
}
