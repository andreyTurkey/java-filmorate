package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Film;

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

    public Map<Integer, Film> getFilms() {
        return films;
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
        List<Film> allFilms = new ArrayList<>(films.values());
        return allFilms;
    }

    @Override
    public Film getFilmById(Integer id) {
        return films.get(id);
    }

    @Override
    public Film deleteFilmById(Integer id) {
            Film film = films.get(id);
            films.remove(id);
            return film;
    }

    @Override
    public List<Film> deleteFilms() {
        films.clear();
        List<Film> emptyFilms = new ArrayList<>(films.values());
        return emptyFilms;
    }
}
