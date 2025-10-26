package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.enums.request.RequestStatus;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(long userId);

    List<Request> findAllByEventId(long eventId);

    @Query("select count(r) from Request r where r.eventId = :eventId and r.status = 'CONFIRMED'")
    int findCountOfConfirmedRequestsByEventId(long eventId);

    @Query("select r.eventId as eventId, count(r) as count " +
            "from Request r " +
            "where r.eventId in :eventsIds and r.status = 'CONFIRMED' " +
            "group by r.eventId")
    List<EventRequestCount> findCountConfirmedByEventIds(List<Long> eventsIds);

    @Modifying
    @Transactional
    @Query("UPDATE Request r SET r.status = :status WHERE r.id IN :ids")
    void updateStatus(RequestStatus status, List<Long> ids);

    boolean existsByEventIdAndRequesterId(long eventId, long requesterId);

    interface EventRequestCount {
        Long getEventId();

        Integer getCount();
    }

}
