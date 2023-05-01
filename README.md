# java-filmorate
Template repository for Filmorate project.
![Diagramm](src/main/resources/diagram.png)

### Описание диаграммы

>В диаграмме показаны два блока. Первый работа с фильмами. Второй с пользователями. Пересечение двух блоков в 
> таблице likes. Таблица с названием user в финальной редакции будет переименована на users, чтобы избежать конфликта
> в вызове команд SQL.
> БД нормализована до 3-го уровня. 
> Во всех таблицах простые ключи, кроме таблицы friend_status - применен композитный статус.

### Примеры запросов

#### Получение таблицы всех пользователей
>SELECT*
> FROM user
> LIMIT 5;

#### Получение таблицы всех фильмов

>SELECT*
> FROM film
> LIMIT 10;

#### Получение таблицы статуcов на добавление в друзья

>SELECT*
> FROM friend_status
> LIMIT 10;

#### Получение таблицы статуов (только подтверденная дружба)

>SELECT*
> FROM friend_status
> WHERE status_id = 2 (CONFIRMED)
> LIMIT 10;
