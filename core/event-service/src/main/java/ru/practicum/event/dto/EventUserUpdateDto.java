package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Location;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUserUpdateDto {
    @Size(max = 2000, min = 20, message = "Длина аннотации должна быть от 20 до 2000 символов")
    private String annotation;
    private Long category;
    @Size(max = 7000, min = 20, message = "Длина описания должна быть от 20 до 7000 символов")
    private String description;
    @Future(message = "Дата события должна быть позже настоящего момента")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    @PositiveOrZero(message = "Число участников должно быть больше или равно нулю")
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    @Size(max = 120, min = 3, message = "Длина заголовка должна быть от 3 до 120 символов")
    private String title;

    public enum StateAction {
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }
}
