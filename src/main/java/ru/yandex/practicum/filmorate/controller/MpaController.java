package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {

    private FilmService filmService;

    @Autowired
    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Rating> getAllRating() {
        log.debug("Request rating was executed.");
        return filmService.getAllRatings();
    }

    @GetMapping("{id}")
    public Rating getRatingByIdg(@PathVariable Integer id) {
        log.debug("Request ratingById was executed.");
        return filmService.getRatingByIdById(id);
    }
}
