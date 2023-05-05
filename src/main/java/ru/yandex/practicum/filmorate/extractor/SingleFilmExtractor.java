package ru.yandex.practicum.filmorate.extractor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SingleFilmExtractor implements ResultSetExtractor<Film> {

    final MpaStorage mpaStorage;

    @Autowired
    public SingleFilmExtractor(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Film extractData(ResultSet resultSet)
            throws SQLException, DataAccessException {

        Map<Film, List<Genre>> data = new LinkedHashMap<>();

        while (resultSet.next()) {
            Integer ratingId = resultSet.getInt("mpa");
            Rating rating = mpaStorage.getRatingById(ratingId);
            Film film = Film.builder()
                    .id(resultSet.getInt("id"))
                    .name(resultSet.getString("name"))
                    .description(resultSet.getString("description"))
                    .duration(resultSet.getInt("duration"))
                    .releaseDate(resultSet.getDate("releaseDate").toLocalDate())
                    .mpa(rating)
                    .build();
            if (data.containsKey(film)) {
                data.get(film).add((Genre.builder()
                        .id(resultSet.getInt("genre_id"))
                        .name(resultSet.getString("genre_name"))
                        .build()));
            } else {
                data.put(film, new ArrayList<>());
                if (resultSet.getInt("genre_id") != 0) {
                    data.get(film).add(Genre.builder()
                            .id(resultSet.getInt("genre_id"))
                            .name(resultSet.getString("genre_name"))
                            .build());
                }
            }
        }
        Film newFilm = null;
        for (Film oldFilm : data.keySet()) {
            if (data.get(oldFilm).size() == 0) {
                oldFilm.setGenres(new ArrayList<>());
                newFilm = oldFilm;
            } else {
                oldFilm.setGenres(data.get(oldFilm));
                newFilm = oldFilm;
            }
        }
        return newFilm;
    }
}
