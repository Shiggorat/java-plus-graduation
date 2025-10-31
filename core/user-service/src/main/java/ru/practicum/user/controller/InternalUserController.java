package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.contract.UserOperations;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.user.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/users")
public class InternalUserController implements UserOperations {

    private final UserService userService;

    @PostMapping
    public Map<Long, UserShortDto> getAllUsersByIds(@RequestBody List<Long> userIds) {
        return userService.getAllUsersByIds(userIds);
    }

    @GetMapping("/{id}")
    public UserShortDto getById(@RequestBody Long id) {
        return userService.getById(id);
    }

    @GetMapping("/check/{id}")
    public void checkUserExists(Long id) {
        userService.checkUserExists(id);
    }
}
