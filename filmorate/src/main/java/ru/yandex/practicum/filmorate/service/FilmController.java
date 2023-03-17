package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.InvalidDatesException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int count = 1;
    private final LocalDate EARLIEST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap();

    @PostMapping
    public Film create(@RequestBody Film film) throws InvalidDatesException {
        try {
            if (films.containsKey(film.getId())) {
                log.error(film.getName() + " already exists.");
                throw new InvalidDatesException("Film already exists.");
            } else if (film.getName() == null || film.getName().isBlank()) {
                log.error(film.getName() + " -  invalid name entered.");
                throw new InvalidDatesException("Сheck film name.");
            } else if (film.getDescription().length() > 200) {
                log.error(film.getName() + " -  invalid length name entered.");
                throw new InvalidDatesException("Сheck length film name.");
            } else if (film.getReleaseDate().isBefore(EARLIEST_FILM_RELEASE_DATE)) {
                log.error(film.getName() + " -  invalid RELEASE_DATE.");
                throw new InvalidDatesException("Check RELEASE_DATE.");
            } else if (film.getDuration() == 0 || film.getDuration() < 0) {
                log.error(film.getName() + " -  invalid duration.");
                throw new InvalidDatesException("Check duration.");
            } else {
                film.setId(count++);
                films.put(film.getId(), film);
                log.debug(film.getName() + " added.");
                return film;
            }
        } catch (NullPointerException e) {
            throw new InvalidDatesException("Check all request fields.");
        }
    }

    @GetMapping
    public List<Film> getFilms(HttpServletRequest request) {
        List<Film> filmsList = new ArrayList<>(films.values());
        log.info("Request received to endpoint : '{} {}', Query parameter string: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return filmsList;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws InvalidDatesException {
        try {
            for (Integer filmsId : films.keySet()) {
                if (filmsId != film.getId()) {
                    log.error("Film doesn't exist.");
                    throw new InvalidDatesException("The film has not been updated. Film is unknown");
                } else if (film.getName() == null || film.getName().isBlank()) {
                    log.error(film.getName() + " -  invalid name entered.");
                    throw new InvalidDatesException("Сheck film name.");
                } else if (film.getDescription().length() > 200) {
                    log.error(film.getName() + " -  invalid length description.");
                    throw new InvalidDatesException("Сheck length film description.");
                } else if (film.getReleaseDate().isBefore(EARLIEST_FILM_RELEASE_DATE)) {
                    log.error(film.getName() + " -  invalid RELEASE_DATE.");
                    throw new InvalidDatesException("Check RELEASE_DATE.");
                } else if (film.getDuration() < 0) {
                    log.error(film.getName() + " -  invalid duration.");
                    throw new InvalidDatesException("Check duration.");
                } else {
                    films.put(film.getId(), film);
                    log.debug(film.getName() + " updated.");
                }
            }
        } catch (NullPointerException e) {
            throw new InvalidDatesException("Check all request fields.");
        }
        return film;
    }

    @ControllerAdvice
    class AwesomeExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(InvalidDatesException.class)
        protected ResponseEntity<FilmController.AwesomeExceptionHandler.AwesomeException> handleThereIsNoSuchUserException(InvalidDatesException e) {
            return new ResponseEntity<>(new FilmController.AwesomeExceptionHandler.AwesomeException(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Data
        @AllArgsConstructor
        private class AwesomeException {
            private String message;
        }
    }
}
