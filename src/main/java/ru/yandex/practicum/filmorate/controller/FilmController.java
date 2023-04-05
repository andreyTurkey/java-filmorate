package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<Film> add(@Valid @RequestBody Film film) {
        filmService.create(film);
        log.debug(film.getName() + " was added");
        return ResponseEntity.ok(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Request all films was executed.");
        return filmService.getAllFilms();
    }

    @GetMapping(value = "{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.debug("Request film was executed.");
        return filmService.getFilmById(id);
    }

    @GetMapping("popular")
    public List<Film> getFilmByRating(@RequestParam(defaultValue = "10") Integer count) {
        log.debug("Request films was executed.");
        return filmService.getFilmByRating(count);
    }

    @PutMapping
    public ResponseEntity<Film> update(@Valid @RequestBody Film film) {
        filmService.update(film);
        return ResponseEntity.ok(film);
    }

    @DeleteMapping(value = "{id}/like/{userId}")
    public ResponseEntity<Film> deleteLike(@PathVariable Integer id,
                                           @PathVariable Integer userId) {
        filmService.deleteLike(id, userId);
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    @DeleteMapping("delete")
    public List<Film> deleteFilms() {
        filmService.deleteFilms();
        return new ArrayList<>(filmService.getFilms().values());
    }

    @DeleteMapping("delete/{id}")
    public List<Film> deleteFilmById(@PathVariable Integer id) {
        filmService.deleteFilmById(id);
        return new ArrayList<>(filmService.getFilms().values());
    }

    @PutMapping(value = "{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable Integer id,
                                        @PathVariable Integer userId) {
        filmService.addLike(id, userId);
        return ResponseEntity.ok(filmService.getFilmById(id));
    }
}
