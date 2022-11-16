package ru.yandex.practicum.filmorate.storages.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmLikeDbStorageImpl implements FilmLikeDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addLike(Long filmId, Long userId) {
        isExistFilm(filmId);
        isExistUser(userId);
        String sqlQuery = "MERGE INTO LIKES key(FILM_ID,USER_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return true;
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        isExistFilm(filmId);
        isExistUser(userId);
        String sqlQuery = "DELETE from LIKES WHERE FILM_ID=? AND USER_ID=?";
        int row = jdbcTemplate.update(sqlQuery, filmId, userId);
        if (row == 0) {
            throw new NotFoundException("User like for film not found. filmId:" + filmId + " userId:" + userId);
        }
        return true;
    }

    @Override
    public List<Film> showMostLikedFilms(Integer count) {
        String sqlQuery = "SELECT ID, NAME, RELEASE_DATE, DESCRIPTION, DURATION, RATE, MPA, GENRES " +
                "From FILMS f LEFT OUTER JOIN LIKES on F.ID = LIKES.FILM_ID " +
                "GROUP BY f.ID " +
                "ORDER BY count(DISTINCT LIKES.USER_ID) DESC LIMIT ? ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private void isExistFilm(Long id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) From FILMS where id = ?", Integer.class, id);
        if (cnt == null || cnt <= 0) {
            throw new NotFoundException("Film with ID=" + id + " not found");
        }
    }

    private void isExistUser(Long id) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(*) From USERS where id = ?", Integer.class, id);
        if (cnt == null || cnt <= 0) {
            throw new NotFoundException("User with ID=" + id + " not found");
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name((resultSet.getString("name")))
                .releaseDate((resultSet.getDate("releaseDate")).toLocalDate())
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .mpa(Mpa.builder()
                        .id(resultSet.getLong("mpa.id"))
                        .name(resultSet.getString("mpa.name"))
                        .build())
                .genres(filmGenres.get(resultSet.getLong(("id"))))
                .build();
    }


}
