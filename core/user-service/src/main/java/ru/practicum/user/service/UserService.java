package ru.practicum.user.service;

import ru.practicum.dto.user.UserShortDto;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserAdminParam;
import ru.practicum.user.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    void deleteUser(long userId);

    UserDto createUser(NewUserRequest newUserRequest);

    List<UserDto> getAllUsers(UserAdminParam params);

    Map<Long, UserShortDto> getAllUsersByIds(List<Long> userIds);

    UserShortDto getById(Long id);

    void checkUserExists(Long id);
}
