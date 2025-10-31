package ru.practicum.contract;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;
import java.util.Map;

public interface UserOperations {
    @PostMapping
    Map<Long, UserShortDto> getAllUsersByIds(@RequestBody List<Long> userIds);

    @GetMapping("/{id}")
    UserShortDto getById(@PathVariable Long id);

    @GetMapping("/check/{id}")
    void checkUserExists(@PathVariable Long id);
}
