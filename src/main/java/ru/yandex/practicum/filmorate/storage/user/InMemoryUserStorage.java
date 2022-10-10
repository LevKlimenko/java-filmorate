package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException.UserBadLoginException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException.UserIdException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException.UserWithoutIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usersEmailInBase = new HashSet<>();
    private final Set<String> usersLoginInBase = new HashSet<>();
    private long generatorId;

    @Override
    public Collection<User> getAllUser() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        checkSpaceInLogin(user);
        checkAlreadyExistUser(user);
        checkValidateName(user);
        ++generatorId;
        user.setId(generatorId);
        users.put(user.getId(), user);
        usersEmailInBase.add(user.getEmail());
        usersLoginInBase.add(user.getLogin());
        log.info("Пользователь '{}' с электронной почтой '{}' сохранен. ID={}", user.getLogin(), user.getEmail(),
                user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new UserWithoutIdException("Нельзя обновить пользователя, если не указан ID");
        }
        if (!users.containsKey(user.getId())) {
            throw new UserIdException("Нет пользователя с ID=" + user.getId());
        }
        checkSpaceInLogin(user);
        checkValidateName(user);
        users.put(user.getId(), user);
        log.info("Пользователь c ID={} обновлен", user.getId());
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserIdException(String.format("Пользователь № %d не найден", userId));
        }
        return users.get(userId);
    }

    private void checkSpaceInLogin(User user) {
        if (user.getLogin().contains(" "))
            throw new UserBadLoginException("Логин не может содержать пробелы");
    }

    private void checkValidateName(User user) {
        if (user.getLogin() != null || !user.getLogin().isBlank()) {
            if (user.getName() == null || user.getName().isBlank())
                user.setName(user.getLogin());
        } else {
            throw new UserBadLoginException("Логин не может быть пустым");
        }
    }

    private void checkAlreadyExistUser(User user) {
        if (usersEmailInBase.contains(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с электронной почтой " + user.getEmail() +
                    " уже существует");
        }
        if (usersLoginInBase.contains(user.getLogin())) {
            throw new UserAlreadyExistException("Пользователь с логином " + user.getLogin() + " уже существует");
        }
    }

    @Override
    public Map<Long, User> getUsers() {
        return new HashMap<>(users);
    }

    @Override
    public long getGeneratorId() {
        return generatorId;
    }


}
