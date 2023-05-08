package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> add(@Valid @RequestBody User user) {
        userService.create(user);
        log.debug(user.getEmail() + " was added");
        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "/{userId}")
    public User getUserById(@PathVariable int userId) {
        return userService.getUserById(userId);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id,
                                       @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.debug("Request all users was executed.");
        return userService.getAllUsers();
    }

    @GetMapping(value = "{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Integer id) {
        log.debug("Request all friends was executed.");
        return userService.getUserFriends(id);
    }

    @PutMapping(value = "/{userId}/friends/{friendId}") // добавление друга
    public List<User> addFriend(@PathVariable Integer userId,
                                @PathVariable Integer friendId) {
        userService.addFriend(userId, friendId);
        List<User> friends = new ArrayList<>(List.of(userService.getUserById(userId),
                userService.getUserById(friendId)));
        return friends;
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        userService.update(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping(value = "{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable Integer id,
                                   @PathVariable Integer friendId) {
        return userService.deleteFriend(id, friendId);
    }
}
