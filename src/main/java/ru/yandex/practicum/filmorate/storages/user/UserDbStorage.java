package ru.yandex.practicum.filmorate.storages.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConflictException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.sql.*;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Qualifier("userDbStorage")
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getUser() {
        String sqlQuery = "SELECT * from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        checkBlankName(user);
       String sqlQuery = "insert into users(login, name,email, birthday) values (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return findById(id);
    }


    @Override
    public User update(User user) {
        checkBlankName(user);
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday =?" +
                "where id = ?";
        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
        return findById(user.getId());
    }

    @Override
    public User findById(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE  id = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", id));
        }
        return user;
    }

    @Override
    public Map<Long, User> getMap() {
        String sqlQuery = "SELECT * from users";
        return (Map<Long, User>) jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public long getGeneratorId() {
        return 0;
    }

    @Override
    public boolean isExist(Long id) {
        return false;
    }


    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email((resultSet.getString("email")))
                .login((resultSet.getString("login")))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    private void checkBlankName(User user){
        if (user.getName().isBlank()){
            user.setName(user.getLogin());
        }
    }
}
