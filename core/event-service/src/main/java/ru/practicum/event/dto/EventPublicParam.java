package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventPublicParam {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private EventSort sort;
    private int from;
    private int size;

    public enum EventSort {
        EVENT_DATE("eventDate"),
        VIEWS("views");

        private final String field;

        EventSort(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }
}
