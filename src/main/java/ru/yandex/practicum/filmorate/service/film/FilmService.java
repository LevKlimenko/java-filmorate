package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.userServiceException.UserNullException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Создайте FilmService, который будет отвечать за операции с фильмами,
 * — добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков.
 * Пусть пока каждый пользователь может поставить лайк фильму только один раз.
 **/
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    //InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();


    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.findFilmById(filmId);
        if (userStorage.getUsers().containsKey(userId)) {
            film.getLikesId().add(userId);
            filmStorage.getCompareFilm().add(filmStorage.getFilms().get(filmId));
        } else {
            throw new UserNullException("Пользователя не существует");
        }
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.findFilmById(filmId);
        if (userStorage.getUsers().containsKey(userId)) {
            film.getLikesId().remove(userId);
            filmStorage.getCompareFilm().add(filmStorage.getFilms().get(filmId));
        } else {
            throw new UserNullException("Пользователя не существует");
        }
    }

    public List<Film> showMostLikedFilms(Integer count) {
       /* ArrayList<Film> sortFilm = new ArrayList<>(compareFilm);
        List<Film> likedFilms = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            likedFilms.add(sortFilm.get(i));
        }*/
        return filmStorage.getCompareFilm().stream()
                .limit(count)
                .collect(Collectors.toList());
    }

}
