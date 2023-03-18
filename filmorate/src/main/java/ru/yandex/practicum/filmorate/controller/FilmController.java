package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.exception.InvalidDataException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private int count = 0;
    private Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film add(@RequestBody Film film) throws InvalidDataException {
        if (film.getName().isBlank() || film.getName() == null) {
            log.error(film.getName() + " invalid field NAME");
            throw new InvalidDataException("Check NAME film.");
        } else if (film.getDescription().length() > 200) {
            log.error(film.getName() + " invalid field DESCRIPTION");
            throw new InvalidDataException("Check DESCRIPTION length.");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error(film.getName() + " invalid field RELEASE");
            throw new InvalidDataException("Check RELEASE field.");
        } else if (film.getDuration() <= 0) {
            log.error(film.getName() + " invalid field DURATION");
            throw new InvalidDataException("Check DURATION field.");
        } else {
            count++;
            film.setId(count);
            films.put(film.getId(), film);
            log.debug(film.getName() + " was added");
            return film;
        }
    }

    @GetMapping
    public List<Film> getFilms(HttpServletRequest request) {
        List<Film> films1 = new ArrayList<>(films.values());
        return films1;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws InvalidDataException {
        if (!films.containsKey(film.getId())) {
            log.error(film.getName() + " film doesn't exist.");
            throw new InvalidDataException("Check ID field.");
        }
        if (film.getName().isBlank() || film.getName() == null) {
            log.error(film.getName() + " invalid field NAME");
            throw new InvalidDataException("Check NAME film.");
        } else if (film.getDescription().length() > 200) {
            log.error(film.getName() + " invalid field DESCRIPTION");
            throw new InvalidDataException("Check DESCRIPTION length.");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error(film.getName() + " invalid field RELEASE");
            throw new InvalidDataException("Check RELEASE field.");
        } else if (film.getDuration() <= 0) {
            log.error(film.getName() + " invalid field DURATION");
            throw new InvalidDataException("Check DURATION field.");
        } else {
            films.put(film.getId(), film);
            log.debug(film.getName() + " was updated");
        }
        return film;
    }

    @ControllerAdvice
    public class AwesomeExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(InvalidDataException.class)
        protected ResponseEntity<AwesomeException> handleThereIsNoSuchUserException(InvalidDataException e) {
            return new ResponseEntity<>(new AwesomeException(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Data
        @AllArgsConstructor
        private class AwesomeException {
            private String message;
        }
    }
}
