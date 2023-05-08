-- Данные для таблицы МРА (рейтинг)
INSERT INTO mpa (name, description) VALUES ('G', 'у фильма нет возрастных ограничений');
INSERT INTO mpa (name, description) VALUES ('PG', 'детям рекомендуется смотреть фильм с родителями');
INSERT INTO mpa (name, description) VALUES ('PG-13', 'детям до 13 лет просмотр не желателен');
INSERT INTO mpa (name, description) VALUES ('R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
INSERT INTO mpa (name, description) VALUES ('NC-17', 'лицам до 18 лет просмотр запрещён');

-- Данные для таблицы GENRES (жанры)
INSERT INTO genres (name) VALUES ('Комедия');
INSERT INTO genres (name) VALUES ('Драма');
INSERT INTO genres (name) VALUES ('Мультфильм');
INSERT INTO genres (name) VALUES ('Триллер');
INSERT INTO genres (name) VALUES ('Документальный');
INSERT INTO genres (name) VALUES ('Боевик');