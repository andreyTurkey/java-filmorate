package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MpaService {

    final MpaStorage mpaStorage;

    final UserService userService;

    @Autowired
    public MpaService(MpaStorage mpaStorage, UserService userService) {
        this.mpaStorage = mpaStorage;
        this.userService = userService;
    }

    public Rating getRatingById(Integer id) {
        return mpaStorage.getRatingById(id);
    }

    public List<Rating> getAllRatings() {
        return mpaStorage.getAllRating();
    }
}

