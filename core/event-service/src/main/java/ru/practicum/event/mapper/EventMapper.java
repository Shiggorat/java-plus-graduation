package ru.practicum.event.mapper;

import org.mapstruct.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.dto.event.EventForRequestDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiatorId", source = "initiatorId")
    Event toEvent(EventNewDto eventNewDto, Category category, Long initiatorId);

    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "id", source = "event.id")
    EventFullDto toFullDto(Event event, UserShortDto initiator);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event toEventFromEventUserUpdateDto(EventUserUpdateDto dto, @MappingTarget Event event);

    default List<EventShortDto> toEventShortDtoList(List<Event> events, Map<Long, UserShortDto> users) {
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            EventShortDto dto = new EventShortDto();
            dto.setEventDate(event.getEventDate());
            dto.setId(event.getId());
            dto.setPaid(event.isPaid());
            dto.setAnnotation(event.getAnnotation());
            dto.setConfirmedRequests(event.getConfirmedRequests());
            dto.setTitle(event.getTitle());
            dto.setInitiator(users.get(event.getInitiatorId()));
            CategoryDto categoryDto = new CategoryDto(event.getCategory().getId(), event.getCategory().getName());
            dto.setCategory(categoryDto);
            eventShortDtos.add(dto);
        }
        return eventShortDtos;
    }

    @Mapping(target = "initiator", source = "initiatorId")
    List<EventFullDto> toEventFullDtoList(List<Event> events);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event toEventFromEventAdminUpdateDto(EventAdminUpdateDto dto, @MappingTarget Event event);

    EventForRequestDto toEventForRequestDto(Event event);
}
