package ru.yandex.practicum.filmorate.usersTest;

import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exceptions.InvalidDatesException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmLogicTest {
    private int count = 1;
    private final LocalDate EARLIEST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap();

    Film rightFilm = Film.builder()
            .name("Titanic")
            .description("About love two young persons")
            .duration(120)
            .releaseDate(LocalDate.of(1995, 01, 01))
            .build();

    Film filmFailName = Film.builder()
            .description("About love two young persons")
            .duration(120)
            .releaseDate(LocalDate.of(1995, 01, 01))
            .build();

    Film filmFailDescription = Film.builder()
            .name("Spring")
            .description("Spring is, by far, the most popular framework for application development " +
                    "in the Java ecosystem. By a wide margin. The reason is actually quite simple " +
                    "– it does a lot of things right, and it's getting better and better with each release.")
            .duration(120)
            .releaseDate(LocalDate.of(1995, 01, 01))
            .build();

    Film filmFailDurationZero = Film.builder()
            .name("Titanic")
            .description("About love two young persons")
            .duration(0)
            .releaseDate(LocalDate.of(1995, 01, 01))
            .build();

    Film filmFailDurationMinus = Film.builder()
            .name("Titanic")
            .description("About love two young persons")
            .duration(-1)
            .releaseDate(LocalDate.of(1995, 01, 01))
            .build();

    Film filmFailReleaseDate = Film.builder()
            .name("Titanic")
            .description("About love two young persons")
            .duration(120)
            .releaseDate(LocalDate.of(1800, 01, 01))
            .build();

    @Test
    void createRightFilm() throws InvalidDatesException {
        create(rightFilm);
        assertEquals(films.size(), 1);
    }

    @Test
    void createFailName() {
        try {
            create(filmFailName);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Сheck film name.");
            assertEquals(films.size(), 0);
        }
    }

    @Test
    void createFailDescription() {
        try {
            create(filmFailDescription);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Сheck length film name.");
            assertEquals(films.size(), 0);
        }
    }

    @Test
    void createFailDurationZero() {
        try {
            create(filmFailDurationZero);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Check duration.");
            assertEquals(films.size(), 0);
        }
    }

    @Test
    void createFailDurationMinus() {
        try {
            create(filmFailDurationMinus);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Check duration.");
            assertEquals(films.size(), 0);
        }
    }

    @Test
    void createFailReleaseDate() {
        try {
            create(filmFailReleaseDate);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Check RELEASE_DATE.");
            assertEquals(films.size(), 0);
        }
    }

    @Test
    void createFailRelease() throws InvalidDatesException {

        Film film = Film.builder()
                .name("Titanic")
                .description("About mechanical person")
                .duration(100)
                .build();
        try {
            create(film);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Check all request fields.");
        }
    }

    @Test
    void getFilmsList() throws InvalidDatesException {
        create(rightFilm);
        List<Film> films = getFilms();

        assertEquals(films.size(), 1);
    }

    @Test
    void putFilmKnownAndUnknown() throws InvalidDatesException {
        create(rightFilm);

        Film filmIsKnown = Film.builder()
                .id(1)
                .name("Terminator")
                .description("About mechanical person")
                .duration(120)
                .releaseDate(LocalDate.of(1995, 01, 01))
                .build();

        update(filmIsKnown);

        assertEquals(films.get(1), filmIsKnown);
        assertEquals(films.size(), 1);

        Film filmIsUnknown = Film.builder()
                .id(1001)
                .name("Terminator")
                .description("About mechanical person")
                .duration(120)
                .releaseDate(LocalDate.of(1995, 01, 01))
                .build();
        try {
            update(filmIsUnknown);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "The film has not been updated. Film is unknown");
        }
        assertEquals(films.size(), 1);
    }

    @Test
    void putFailName() throws InvalidDatesException {
        create(rightFilm);

        Film filmIsKnown = Film.builder()
                .id(1)
                .name("")
                .description("About mechanical person")
                .duration(120)
                .releaseDate(LocalDate.of(1995, 01, 01))
                .build();
        try {
            update(filmIsKnown);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Сheck film name.");
        }
    }

    @Test
    void putFailDescription() throws InvalidDatesException {
        create(rightFilm);

        Film filmIsKnown = Film.builder()
                .id(1)
                .name("Spring")
                .description("Spring is, by far, the most popular framework for application development " +
                        "in the Java ecosystem. By a wide margin. The reason is actually quite simple " +
                        "– it does a lot of things right, and it's getting better and better with each release.")
                .duration(120)
                .releaseDate(LocalDate.of(1995, 01, 01))
                .build();
        try {
            update(filmIsKnown);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Сheck length film description.");
        }
    }

    @Test
    void putFailDuration() throws InvalidDatesException {
        create(rightFilm);

        Film filmIsKnown = Film.builder()
                .id(1)
                .name("Titanic")
                .description("About mechanical person")
                .duration(-1)
                .releaseDate(LocalDate.of(1995, 01, 01))
                .build();
        try {
            update(filmIsKnown);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Check duration.");
        }
    }

    @Test
    void putFailRelease() throws InvalidDatesException {
        create(rightFilm);

        Film filmIsKnown = Film.builder()
                .id(1)
                .name("Titanic")
                .description("About mechanical person")
                .duration(120)
                .releaseDate(LocalDate.of(1880, 01, 01))
                .build();
        try {
            update(filmIsKnown);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Check RELEASE_DATE.");
        }
    }

    @Test
    void putFailReleaseOrDuration() throws InvalidDatesException {
        create(rightFilm);

        Film filmIsKnown = Film.builder()
                .id(1)
                .name("Titanic")
                .description("About mechanical person")
                .releaseDate(LocalDate.of(1980, 01, 01))
                .build();
        try {
            update(filmIsKnown);
        } catch (InvalidDatesException e) {
            assertEquals(e.getMessage(), "Check all request fields.");
        }
    }

    public Film create(Film film) throws InvalidDatesException {
        try {
            if (films.containsKey(film.getId())) {
                throw new InvalidDatesException("Film already exists.");
            } else if (film.getName() == null || film.getName().isBlank()) {
                throw new InvalidDatesException("Сheck film name.");
            } else if (film.getDescription().length() > 200) {
                throw new InvalidDatesException("Сheck length film name.");
            } else if (film.getReleaseDate().isBefore(EARLIEST_FILM_RELEASE_DATE)) {
                throw new InvalidDatesException("Check RELEASE_DATE.");
            } else if (film.getDuration() == 0 || film.getDuration() < 0) {
                throw new InvalidDatesException("Check duration.");
            } else {
                film.setId(count++);
                films.put(film.getId(), film);
                return film;
            }
        } catch (NullPointerException e) {
            throw new InvalidDatesException("Check all request fields.");
        }
    }

    public List<Film> getFilms() {
        List<Film> filmsList = new ArrayList<>(films.values());
        return filmsList;
    }

    public Film update(Film film) throws InvalidDatesException {
            try {
                for (Integer filmsId : films.keySet()) {
                    if (filmsId != film.getId()) {
                        throw new InvalidDatesException("The film has not been updated. Film is unknown");
                    } else if (film.getName() == null || film.getName().isBlank()) {
                        throw new InvalidDatesException("Сheck film name.");
                    } else if (film.getDescription().length() > 200) {
                        throw new InvalidDatesException("Сheck length film description.");
                    } else if (film.getReleaseDate().isBefore(EARLIEST_FILM_RELEASE_DATE)) {
                        throw new InvalidDatesException("Check RELEASE_DATE.");
                    } else if (film.getDuration() < 0) {
                        throw new InvalidDatesException("Check duration.");
                    } else {
                        films.put(film.getId(), film);
                    }
                }
            } catch (NullPointerException e) {
                throw new InvalidDatesException("Check all request fields.");
            }
            return film;
        }
}
