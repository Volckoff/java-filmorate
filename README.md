# java-filmorate
Template repository for Filmorate project.

## ER-Диаграмма

![ER-Диаграмма](./ER-diagram.png)

## Описание
**Filmorate** — это приложение для хранения и управления данными о фильмах и пользователях. Оно позволяет пользователям:
- Добавлять фильмы и оценивать их.
- Добавлять друзей и управлять статусом дружбы.
- Просматривать топ популярных фильмов и общие друзья с другими пользователями.

Диаграмма отображает структуру базы данных приложения. Она включает следующие сущности:
- **User**: Пользователи приложения.
- **Film**: Фильмы, которые пользователи могут оценивать.
- **Genre**: Жанры фильмов.
- **MPA_Rating**: Рейтинги возрастных ограничений (например, G, PG, R).
- **Friendship**: Связи между пользователями (дружба).
- **Like**: Лайки, которые пользователи ставят фильмам.

## Описание таблиц

### User
Таблица содержит информацию о пользователях приложения:
- `user_id`: Уникальный идентификатор пользователя.
- `email`: Электронная почта пользователя.
- `login`: Логин пользователя.
- `name`: Имя пользователя (если не указано, используется логин).
- `birthday`: Дата рождения пользователя.

### Film
Таблица содержит информацию о фильмах:
- `film_id`: Уникальный идентификатор фильма.
- `name`: Название фильма.
- `description`: Описание фильма (до 200 символов).
- `release_date`: Дата выхода фильма.
- `duration`: Продолжительность фильма (в минутах).
- `mpa_id`: Внешний ключ, ссылается на таблицу `MPA_Rating`.

### MPA_Rating
Таблица содержит рейтинги возрастных ограничений:
- `mpa_id`: Уникальный идентификатор рейтинга.
- `name`: Название рейтинга (например, G, PG, R).

### Genre
Таблица содержит жанры фильмов:
- `genre_id`: Уникальный идентификатор жанра.
- `name`: Название жанра (например, Комедия, Драма).

### Friendship
Таблица описывает связи между пользователями (дружбу):
- `user_id`: Идентификатор пользователя.
- `friend_id`: Идентификатор друга.
- `status`: Статус дружбы (`pending` — запрос отправлен, `confirmed` — подтверждено).

### Like
Таблица описывает лайки, которые пользователи ставят фильмам:
- `user_id`: Идентификатор пользователя.
- `film_id`: Идентификатор фильма.

## Примеры SQL-Запросов

### Получение топ N популярных фильмов:
 ```sql
SELECT f.film_id, f.name, COUNT(l.user_id) AS like_count
FROM Film f
LEFT JOIN Like l ON f.film_id = l.film_id
GROUP BY f.film_id, f.name
ORDER BY like_count DESC
LIMIT N;

### Получение списка друзей пользователя
 ```sql
SELECT u.*
FROM User u
JOIN Friendship f ON u.user_id = f.friend_id
WHERE f.user_id = :user_id AND f.status = 'confirmed';

### Получение списка общих друзей с другим пользователем:
 ```sql
SELECT u.*
FROM User u
JOIN Friendship f1 ON u.user_id = f1.friend_id
JOIN Friendship f2 ON u.user_id = f2.friend_id
WHERE f1.user_id = :user_id_1 AND f2.user_id = :user_id_2
  AND f1.status = 'confirmed' AND f2.status = 'confirmed';

### Получение всех фильмов с их жанрами
 ```sql
SELECT f.film_id, f.name, g.name AS genre_name
FROM Film f
JOIN Film_Genre fg ON f.film_id = fg.film_id
JOIN Genre g ON fg.genre_id = g.genre_id;

### Проверка рейтинга MPA для фильма
 ```sql
SELECT f.film_id, f.name, m.name AS mpa_rating
FROM Film f
JOIN MPA_Rating m ON f.mpa_id = m.mpa_id;

