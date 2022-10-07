package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {
    Set<Film> getCompareFilm();

    public Collection<Film> getAllFilms();
    public Film addFilm(Film film);
    public Film updateFilm(Film film);
    public Film findFilmById(Integer filmId);
    public Map<Integer,Film> getFilms();

}
