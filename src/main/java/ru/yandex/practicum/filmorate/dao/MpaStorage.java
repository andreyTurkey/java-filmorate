package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface MpaStorage {

    List<Rating> getAllRating();

    Rating getRatingById(Integer id);

    void ratingExistsById(Integer ratingId);
}
