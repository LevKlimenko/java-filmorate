/*package ru.yandex.practicum.filmorate.storages.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre findGenreById(Long id){
       String sqlQuery = " select * from genres where id = ?";
        Genre genre;
        try{
            genre = jdbcTemplate.queryForObject(sqlQuery,this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Genre с id " + id +  " не найден");
        }
        return genre;
    }

    public List<Genre> findAllGenre(){
        String sqlQuery = "SELECT * from genres";
        return jdbcTemplate.query(sqlQuery,this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
*/