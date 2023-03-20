package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidDataException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private int count = 0;
    private Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public ResponseEntity<Film> add(@Valid @RequestBody Film film) {
        count++;
        film.setId(count);
        films.put(film.getId(), film);
        log.debug(film.getName() + " was added");
        return ResponseEntity.ok(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        List<Film> films1 = new ArrayList<>(films.values());
        log.debug("Request all films was executed.");
        return films1;
    }

    @PutMapping
    public ResponseEntity<Film> update(@Valid @RequestBody Film film) throws InvalidDataException {
        if (!films.containsKey(film.getId())) {
            log.error(film.getName() + " film doesn't exist.");
            throw new InvalidDataException("Check ID field.");
        }
        film.setId(count);
        films.put(film.getId(), film);
        return ResponseEntity.ok(film);
    }

    @ControllerAdvice
    public class AwesomeExceptionHandler {

        @ResponseBody
        @ExceptionHandler(MethodArgumentNotValidException.class)
        protected ResponseEntity<AwesomeException> handleThereIsNoSuchValidationFilm(MethodArgumentNotValidException ex) {
            log.error("Invalid fields");
            return new ResponseEntity<>(new FilmController
                    .AwesomeExceptionHandler
                    .AwesomeException(ex.getFieldError().getDefaultMessage())
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ResponseBody
        @ExceptionHandler(InvalidDataException.class)
        protected ResponseEntity<AwesomeException> handleThereIsNoSuchExceptionInController(InvalidDataException e) {
            return new ResponseEntity<>(new AwesomeException(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Data
        @AllArgsConstructor
        private class AwesomeException {
            private String message;
        }
    }
}
