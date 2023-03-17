package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.InvalidDatesException;
import ru.yandex.practicum.filmorate.model.User;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int count = 1;

    private final Map<Integer, User> users = new HashMap();

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public User create(@RequestBody User user) throws InvalidDatesException {
        try {
            if (users.containsKey(user.getId())) {
                log.error(user.getEmail() + " already exists.");
                throw new InvalidDatesException("User already exists.");
            } else if ((user.getEmail() == null || user.getEmail().isBlank()) || !user.getEmail().contains("@")) {
                log.error(user.getEmail() + " -  invalid email address entered.");
                throw new InvalidDatesException("Check email address.");
            } else if ((user.getLogin() == null || user.getLogin().isBlank()) || user.getLogin().contains(" ")) {
                log.error(user.getEmail() + " -  invalid login entered.");
                throw new InvalidDatesException("Check login.");
            } else if (!user.getBirthday().isBefore(LocalDate.now())) {
                log.error(user.getEmail() + " -  invalid date of birth entered.");
                throw new InvalidDatesException("Check date of birth.");
            } else {
                if (user.getName() == null) {
                    user.setName(user.getLogin());
                }
                user.setId(count++);
                users.put(user.getId(), user);
                log.debug(user.getEmail() + " added.");
                return user;
            }
        } catch (NullPointerException e) {
            throw new InvalidDatesException("Check all request fields.");
        }
    }

    @GetMapping
    public List<User> getUsers(HttpServletRequest request) {
        List<User> usersList = new ArrayList<>(users.values());
        log.info("Request received to endpoint : '{} {}', Query parameter string: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return usersList;
    }

    @PutMapping
    public User update(@RequestBody User user) throws InvalidDatesException {
        try {
            for (User userFromMap : users.values()) {
                if (userFromMap.getId() != user.getId() || userFromMap.getId() == 0) {
                    log.error("User doesn't exist.");
                    throw new InvalidDatesException("The user has not been updated. Check user data.");
                } else if ((user.getEmail() == null || user.getEmail().isBlank()) || !user.getEmail().contains("@")) {
                    log.error(user.getEmail() + " -  invalid email address entered.");
                    throw new InvalidDatesException("Check  email address.");
                } else if ((user.getLogin() == null || user.getLogin().isBlank()) || user.getLogin().contains(" ")) {
                    log.error(user.getEmail() + " -  invalid login entered.");
                    throw new InvalidDatesException("Check  login.");
                } else if (!user.getBirthday().isBefore(LocalDate.now())) {
                    log.error(user.getEmail() + " -  invalid date of birth entered.");
                    throw new InvalidDatesException("Check date of birth.");
                } else {
                    users.put(user.getId(), user);
                    log.debug(user.getEmail() + " is updated.");
                }
            }
        } catch (NullPointerException e) {
            throw new InvalidDatesException("Check all request fields.");
        }
        return user;
    }

    @ControllerAdvice
    public class AwesomeExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(InvalidDatesException.class)
        protected ResponseEntity<AwesomeException> handleThereIsNoSuchUserException(InvalidDatesException e) {
            return new ResponseEntity<>(new AwesomeException(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Data
        @AllArgsConstructor
        private class AwesomeException {
            private String message;
        }
    }
}
