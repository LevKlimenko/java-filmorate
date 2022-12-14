package ru.yandex.practicum.filmorate.services.genre;

import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;


public interface GenreService {

    Genre findById(Long id);

    List<Genre> getAll();
}
