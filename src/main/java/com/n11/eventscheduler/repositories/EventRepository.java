package com.n11.eventscheduler.repositories;

import com.n11.eventscheduler.models.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author cihan.bulut on 7/1/2021
 */
public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findEventsByEventDate(LocalDate eventDate);

    Short countByEventDate(LocalDate eventDate);

}
