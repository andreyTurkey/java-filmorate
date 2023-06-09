create table IF NOT EXISTS mpa
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL,
    description varchar NOT NULL
);

create table IF NOT EXISTS users
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar NOT NULL,
    login varchar NOT NULL,
    name varchar NOT NULL,
    birthday date NOT NULL
);

create table IF NOT EXISTS friend
(
    user_id INTEGER NOT NULL REFERENCES users (id),
    friend_id INTEGER NOT NULL REFERENCES users (id)
);

create table IF NOT EXISTS film
(
    id INTEGER  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL,
    description varchar NOT NULL,
    duration INTEGER NOT NULL,
    releaseDate date NOT NULL,
    mpa INTEGER REFERENCES mpa (id)
);

create table IF NOT EXISTS likes
(
    film_id INTEGER NOT NULL REFERENCES film (id),
    user_id INTEGER NOT NULL REFERENCES users (id)
);

create table IF NOT EXISTS genres
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL
);

create table IF NOT EXISTS genre
(
    film_id INTEGER NOT NULL REFERENCES film (id),
    genre_id INTEGER NOT NULL REFERENCES genres (id)
);

