package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {

    final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        log.debug("Request genres was executed.");
        return genreService.getAllGenres();
    }

    @GetMapping("{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        log.debug("Request genreById was executed.");
        return genreService.getGenreById(id);
    }
}
