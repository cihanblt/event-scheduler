package com.n11.eventscheduler.services;

import com.n11.eventscheduler.EventSchedulerApplication;
import com.n11.eventscheduler.dto.ManipulatedEvent;
import com.n11.eventscheduler.enums.EventTypeEnum;
import com.n11.eventscheduler.enums.ResultEnum;
import com.n11.eventscheduler.models.Event;
import com.n11.eventscheduler.util.ResultObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cihan.bulut
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration
class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Test
    void addEvent() {
        Event event = new Event();
        event.setContactAction(Boolean.FALSE);
        event.setEventDate(LocalDate.now());
        event.setEventName("test");
        event.setEventTypeEnum(EventTypeEnum.STANDART);
        event.setEventDuration(45l);
        ResultObject resultObject = eventService.addEvent(event);
        Event added = (Event) resultObject.fetchAndRemove(ResultEnum.SUCCESS);
        assertEquals(event.getEventDate(),added.getEventDate());
    }

    @Test
    void fetchAll() {
        ResultObject resultObject = eventService.fetchAll();
        List<Event> eventList = (List<Event>)resultObject.fetchAndRemove(ResultEnum.SUCCESS);
        assertFalse(eventList.isEmpty());
    }

    @Test
    void fetchEventsByEventDate() {
        ResultObject resultObject = eventService.fetchEventsByEventDate(LocalDate.now());
        List<Event> eventList = (List<Event>) resultObject.fetchAndRemove(ResultEnum.SUCCESS);
        assertEquals(eventList.get(0).getEventDate(),LocalDate.now());
    }

    @Test
    void adjustEvents() {
        Event e1 = new Event();
        e1.setEventDuration(30l);
        e1.setEventTypeEnum(EventTypeEnum.STANDART);
        e1.setEventDate(LocalDate.now());
        e1.setContactAction(Boolean.FALSE);
        e1.setEventName("test1");
        eventService.addEvent(e1);

        Event e2 = new Event();
        e2.setEventDuration(55l);
        e2.setEventTypeEnum(EventTypeEnum.STANDART);
        e2.setEventDate(LocalDate.now());
        e2.setContactAction(Boolean.FALSE);
        e2.setEventName("test2");
        eventService.addEvent(e2);


        Event e3 = new Event();
        e3.setEventDuration(48l);
        e3.setEventTypeEnum(EventTypeEnum.STANDART);
        e3.setEventDate(LocalDate.now());
        e3.setContactAction(Boolean.FALSE);
        e3.setEventName("test3");
        eventService.addEvent(e3);

        ResultObject resultObject = eventService.adjustEvents(LocalDate.now());
        List<ManipulatedEvent> manipulatedEventList = (List<ManipulatedEvent>) resultObject.fetchAndRemove(ResultEnum.SUCCESS);
        assertEquals(manipulatedEventList.size(),3);
    }
}