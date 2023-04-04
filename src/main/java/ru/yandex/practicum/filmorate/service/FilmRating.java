package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmRating {

    private List<Film> filmsRating;

    public FilmRating() {
        this.filmsRating = new ArrayList<>();
    }

    protected Comparator<Film> comparator = new Comparator<Film>() {
        @Override
        public int compare(Film o1, Film o2) {
            return o2.getLikes().size() - o1.getLikes().size();
        }
    };

    public void setFilmsRating(List<Film> filmsRating) {
        this.filmsRating = filmsRating;
    }

    public void add(Film film) {
        filmsRating.add(film);
        updateRating();
    }

    public void deleteFilms() {
        filmsRating.clear();
    }

    public List<Film> getFilmRating(Integer count) {
        return filmsRating.stream().limit(count).collect(Collectors.toList());
    }

    public void deleteFilm(Film film) {
        setFilmsRating(filmsRating.stream().filter(i -> !(i.getId() == film.getId())).collect(Collectors.toList()));
    }

    public void updateRating() {
        filmsRating.sort(comparator);
        log.info(String.format("%s", filmsRating));
    }
}
