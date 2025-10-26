package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationParam;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventList = eventService.getAllByIds(newCompilationDto.getEvents());
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(eventList);
        compilationRepository.save(compilation);
        return mapToDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id = " + compId + " not found.");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationFromTable = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id = " + compId + " not found."));

        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            compilationFromTable.setEvents(eventService.getAllByIds(updateCompilationRequest.getEvents()));
        }
        if (updateCompilationRequest.getPinned() != null)
            compilationFromTable.setPinned(updateCompilationRequest.getPinned());
        if (updateCompilationRequest.getTitle() != null)
            compilationFromTable.setTitle(updateCompilationRequest.getTitle());
        compilationFromTable = compilationRepository.save(compilationFromTable);
        return mapToDto(compilationFromTable);
    }

    public List<CompilationDto> getAllCompilations(CompilationParam param) {
        Boolean isPinned = param.getIsPinned();
        int from = param.getFrom();
        int size = param.getSize();

        List<Compilation> compilations = compilationRepository.findAllByPinned(isPinned, PageRequest.of(from / size, size));
        return mapToDtoList(compilations);
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id = " + compId + " not found."));
        return mapToDto(compilation);
    }

    private List<CompilationDto> mapToDtoList(List<Compilation> compilations) {

        Set<Event> events = compilations.stream()
                .flatMap(c -> c.getEvents().stream())
                .collect(Collectors.toSet());

        List<EventShortDto> eventsDto = eventService.getShortEvents(events.stream().toList());

        Map<Long, EventShortDto> eventDtoById = eventsDto.stream()
                .collect(Collectors.toMap(EventShortDto::getId, dto -> dto));


        return compilations.stream()
                .map(compilation -> {
                    List<EventShortDto> compilationEvents = compilation.getEvents().stream()
                            .map(Event::getId)
                            .map(eventDtoById::get)
                            .toList();

                    CompilationDto dto = compilationMapper.toCompilationDto(compilation);
                    dto.setEvents(compilationEvents);
                    return dto;
                })
                .toList();
    }

    private CompilationDto mapToDto(Compilation compilation) {
        List<EventShortDto> eventsToSet = eventService.getShortEvents(compilation.getEvents());
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        compilationDto.setEvents(eventsToSet);
        return compilationDto;
    }
}
