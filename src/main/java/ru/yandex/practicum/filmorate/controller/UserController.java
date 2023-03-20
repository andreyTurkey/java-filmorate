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
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int count = 0;

    @PostMapping
    public ResponseEntity<User> add(@Valid @RequestBody User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        count++;
        user.setId(count);
        users.put(user.getId(), user);
        log.debug(user.getEmail() + " was added");
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public List<User> getUsers(HttpServletRequest request) {
        List<User> users1 = new ArrayList<>(users.values());
        log.debug("Request all films was executed.");
        return users1;
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) throws InvalidDataException {
        if (!users.containsKey(user.getId())) {
            log.error(user.getName() + " user doesn't exist.");
            throw new InvalidDataException("Check ID field.");
        }
        users.put(user.getId(), user);
        log.debug(user.getEmail() + " was updated");
        return ResponseEntity.ok(user);
    }

    @ControllerAdvice
    public class AwesomeExceptionHandler {

        @ResponseBody
        @ExceptionHandler(MethodArgumentNotValidException.class)
        protected ResponseEntity<AwesomeException> handleThereIsNoSuchUserException(MethodArgumentNotValidException ex) {
            log.error("Invalid fields");
            return new ResponseEntity<>(new UserController
                    .AwesomeExceptionHandler
                    .AwesomeException(ex.getFieldError().getDefaultMessage())
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ResponseBody
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
