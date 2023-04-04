package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    public Film create(Film film);

    public Film update(Film film);

    public Map<Integer, Film> getFilms();

    public List<Film> getAllFilms();

    public Film getFilmById(Integer id);

    public Film deleteFilmById(Integer id);

    public List<Film> deleteFilms();
}
