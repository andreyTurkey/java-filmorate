package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;

    private final FilmDbStorage filmStorage;

    @BeforeEach
    public void createTables() {
        userStorage.createTablesUser();
        filmStorage.createTablesFilm();
    }

    @AfterEach
    public void deleteTables() {
        filmStorage.clearTablesFilm();
        userStorage.clearTablesUser();
    }

    public void createUserForTest() {
        userStorage.create(
                User.builder()
                        .id(1)
                        .email("mail@mail.ru")
                        .login("Example 1")
                        .name("Example 1")
                        .birthday(LocalDate.parse("2000-08-19"))
                        .build()
        );
    }

    public void createFilmForTest() {
        filmStorage.create(
                Film.builder()
                        .id(1)
                        .name("nisi eiusmod")
                        .description("adipisicing")
                        .releaseDate(LocalDate.parse("2000-08-19"))
                        .mpa(Rating.builder()
                                .id(1)
                                .build())
                        .build()
        );
    }

    @Test
    public void createUser() {
        createUserForTest();
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "Example 1")
                );
        assertEquals(1, userStorage.getAllUsers().size(), "Неверное количество пользователей.");
    }

    @Test
    public void testFindUserById() {
        createUserForTest();
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void updateUser() {
        createUserForTest();
        userStorage.update(
                User.builder()
                        .id(1)
                        .email("mail@yandex.ru")
                        .login("Example 1")
                        .name("Example 1")
                        .birthday(LocalDate.parse("2000-08-19"))
                        .build()
        );
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "mail@yandex.ru")
                );
    }

    @Test
    public void getAllUsers() {
        createUserForTest();
        List<User> allUsers = userStorage.getAllUsers();

        assertEquals(1, allUsers.size(), "Неверное количество пользователей.");
    }

    @Test
    public void addFriend() {
        createUserForTest();
        userStorage.create(
                User.builder()
                        .id(2)
                        .email("Friend@yandex.ru")
                        .login("Friend 1")
                        .name("Friend 1")
                        .birthday(LocalDate.parse("2000-08-19"))
                        .build()
        );

        userStorage.addFriend(1, 2);
        List<User> friends = userStorage.getUserFriends(1);
        assertEquals(1, friends.size(), "Друг не добавлен.");

    }

    @Test
    public void getUserFriend() {
        addFriend();
        List<User> friends = userStorage.getUserFriends(1);
        assertEquals(1, friends.size(), "Неверное количество добавленных друзей.");
    }

    @Test
    public void userExistById() {
        createUserForTest();
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "Example 1")
                );
    }

    @Test
    public void getCommonFriend() {
        addFriend();
        userStorage.create(
                User.builder()
                        .id(3)
                        .email("Friend2@yandex.ru")
                        .login("Friend 2")
                        .name("Friend 2")
                        .birthday(LocalDate.parse("2000-08-19"))
                        .build()
        );

        userStorage.addFriend(3, 2);
        List<User> commonFriends = userStorage.getCommonFriends(1, 3);

        Optional<User> userOptional = Optional.ofNullable(commonFriends.get(0));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2)
                );

        assertEquals(1, commonFriends.size(), "Неверное количество добавленных друзей.");
    }

    @Test
    public void deleteFriend() {
        userStorage.deleteFriend(1, 2);
        List<User> commonFriends = userStorage.getUserFriends(1);

        assertEquals(0, commonFriends.size(), "Пользователь не удален из друзей.");
    }

    @Test
    public void creatFilm() {
        createFilmForTest();

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void getAllFilm() {
        createFilmForTest();
        List<Film> films = filmStorage.getAllFilms();
        assertEquals(1, films.size(), "Неверное количество полученных фильмов.");
    }

    @Test
    public void updateFilm() {
        createFilmForTest();
        filmStorage.update(
                Film.builder()
                        .id(1)
                        .name("Film Updated")
                        .description("adipisicing")
                        .releaseDate(LocalDate.parse("2000-08-19"))
                        .mpa(Rating.builder()
                                .id(1)
                                .build())
                        .build()
        );

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "Film Updated")
                );
    }

    @Test
    public void getAllGenres() {
        List<Genre> genres = filmStorage.getAllGenres();
        assertEquals(6, genres.size(), "Неверное количество полученных жанров.");
    }

    @Test
    public void getAllRating() {
        List<Rating> ratings = filmStorage.getAllRating();
        assertEquals(5, ratings.size(), "Неверное количество полученных рейтингов.");
    }

    @Test
    public void getFilmById() {
        createFilmForTest();
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "nisi eiusmod")
                );
    }

    @Test
    public void getGenreById() {
        Optional<Genre> genreOptional = Optional.ofNullable(filmStorage.getGenreById(1));

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void getRatingById() {
        Optional<Rating> ratingOptional = Optional.ofNullable(filmStorage.getRatingById(1));

        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(rating ->
                        assertThat(rating).hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    public void updateFilmGenre() {
        createFilmForTest();

        filmStorage.updateFilmGenre(Film.builder()
                .id(1)
                .name("Film Updated")
                .description("adipisicing")
                .releaseDate(LocalDate.parse("2000-08-19"))
                .genres(List.of(filmStorage.getGenreById(1)))
                .mpa(Rating.builder()
                        .id(1)
                        .build())

                .build());
        assertEquals("Комедия", filmStorage.getFilmById(1).getGenres().get(0).getName(), "Неверное значение жанра.");
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));
    }

    @Test
    public void getGenresByFilmId() {
        updateFilmGenre();
        List<Genre> genres = filmStorage.getGenresByFilmId(1);
        assertEquals("Комедия", genres.get(0).getName(), "Неверное значение жанра.");
        assertEquals(1, genres.size(), "Неверное количество жанров.");
    }

    @Test
    public void filmExistsById() {
        createFilmForTest();
        filmStorage.filmExistsById(1);
        try {
            filmStorage.filmExistsById(999);
        } catch (FilmNotFoundException ex) {
            assertEquals("Фильма с ID = 999 не существует. Проверьте ID.", ex.getMessage());
        }
    }

    @Test
    public void genreExistsById() {
        filmStorage.genreExistsById(1);
        try {
            filmStorage.genreExistsById(999);
        } catch (FilmNotFoundException ex) {
            assertEquals("Жанра с ID = 999 не существует. Проверьте ID.", ex.getMessage());
        }
    }

    @Test
    public void ratingExistsById() {
        filmStorage.ratingExistsById(1);
        try {
            filmStorage.ratingExistsById(999);
        } catch (FilmNotFoundException ex) {
            assertEquals("Рейтинга с ID = 999 не существует. Проверьте ID.", ex.getMessage());
        }
    }

    @Test
    public void addLike() {
        createFilmForTest();
        createFilmForTest();
        createUserForTest();
        filmStorage.addLike(2, 1);
        filmStorage.getFilmByRating(1);
        assertEquals(2, filmStorage.getFilmByRating(1).get(0).getId(), "Неверное значение жанра.");
    }

    @Test
    public void deleteLike() {
        addLike();
        filmStorage.deleteLike(2, 1);
        log.debug(filmStorage.getFilmById(2) + " количество лайков");
        assertEquals(1, filmStorage.getFilmByRating(1).get(0).getId(), "Неверное количество likes.");
    }

    @Test
    public void getRatingByFilmId() {
        addLike();
        filmStorage.getFilmByRating(1);
        assertEquals(2, filmStorage.getFilmByRating(1).get(0).getId(), "Неверное количество likes.");
    }
}
