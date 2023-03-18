package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.exception.InvalidDataException;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int count = 0;

    @PostMapping
    public User add(@RequestBody User user) throws InvalidDataException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error(user.getEmail() + " invalid field EMAIL");
            throw new InvalidDataException("Check field EMAIL.");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error(user.getEmail() + " invalid field LOGIN");
            throw new InvalidDataException("Check field LOGIN.");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error(user.getEmail() + " invalid field BIRTHDAY");
            throw new InvalidDataException("Check BIRTHDAY field.");
        } else {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            count++;
            user.setId(count);
            users.put(user.getId(), user);
            log.debug(user.getEmail() + " was added");
            return user;
        }
    }

    @GetMapping
    public List<User> getUsers(HttpServletRequest request) {
        List<User> users1 = new ArrayList<>(users.values());
        return users1;
    }

    @PutMapping
    public User update(@RequestBody User user) throws InvalidDataException {
        for (Integer id : users.keySet()) {
            if (id != user.getId()) {
                log.error(user.getEmail() + " user doesn't exist.");
                throw new InvalidDataException("Check ID field.");
            }
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error(user.getEmail() + " invalid field EMAIL");
            throw new InvalidDataException("Check field EMAIL.");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error(user.getEmail() + " invalid field LOGIN");
            throw new InvalidDataException("Check field LOGIN.");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error(user.getEmail() + " invalid field BIRTHDAY");
            throw new InvalidDataException("Check BIRTHDAY field.");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error(user.getEmail() + " invalid field BIRTHDAY");
            throw new InvalidDataException("Check BIRTHDAY field.");
        } else {
            if (user.getName().isBlank() || user.getName() == null) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.debug(user.getEmail() + " was added");
            return user;
        }
    }

    @ControllerAdvice
    public class AwesomeExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(InvalidDataException.class)
        protected ResponseEntity<UserController.AwesomeExceptionHandler.AwesomeException> handleThereIsNoSuchUserException(InvalidDataException e) {
            return new ResponseEntity<>(new UserController.AwesomeExceptionHandler.AwesomeException(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Data
        @AllArgsConstructor
        private class AwesomeException {
            private String message;
        }
    }
}

