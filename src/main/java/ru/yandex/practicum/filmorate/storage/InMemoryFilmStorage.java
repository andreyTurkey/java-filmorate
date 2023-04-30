package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    @Override
    public Film create(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        filmExistsById(id);
        return films.get(id);
    }

    @Override
    public void filmExistsById(Integer filmId) {
        if (!films.containsKey(filmId))
            throw new FilmNotFoundException(String.format("Фильма с ID = %d не существует. Проверьте ID.", filmId));
    }

    @Override
    public void genreExistsById(Integer genreId) {

    }

    @Override
    public void ratingExistsById(Integer ratingId) {

    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        return null;
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        return null;
    }

    @Override
    public List<Film> getFilmByRating(Integer count) {
        return null;
    }

    @Override
    public void updateFilmGenre(Film film) {

    }

    @Override
    public List<Genre> getGenresByFilmId(Integer id) {
        return null;
    }

    @Override
    public Genre getGenreById(Integer id) {
        return null;
    }

    @Override
    public List<Genre> getAllGenres() {
        return null;
    }

    @Override
    public Rating getRatingById(Integer id) {
        return null;
    }

    @Override
    public List<Rating> getAllRating() {
        return null;
    }
}
