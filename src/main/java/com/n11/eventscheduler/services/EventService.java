package com.n11.eventscheduler.services;

import com.n11.eventscheduler.constans.EventConstants;
import com.n11.eventscheduler.dto.ManipulatedEvent;
import com.n11.eventscheduler.enums.ResultEnum;
import com.n11.eventscheduler.models.Event;
import com.n11.eventscheduler.repositories.EventRepository;
import com.n11.eventscheduler.util.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author cihan.bulut
 */
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Transactional
    public ResultObject addEvent(Event event) {
        ResultObject resultObject = new ResultObject();
        setOrderInEvent(event);
        ResultObject r = adjustEvents(event.getEventDate());
        List<ManipulatedEvent> manipulatedEventList = (List<ManipulatedEvent>) r.fetchAndRemove(ResultEnum.SUCCESS);
        if (!manipulatedEventList.isEmpty()) {
            if (!event.getContactAction()) {
                List<ManipulatedEvent> manipulatedEvents = manipulatedEventList.stream().filter(e -> !e.getContactAction()).collect(Collectors.toList());
                ManipulatedEvent lastManipulatedEvent = manipulatedEvents.get(manipulatedEvents.size() - 1);
                if (lastManipulatedEvent.getEventFinishTime().plusMinutes(event.getEventDuration()).isAfter(LocalTime.of(16, 0))) {
                    resultObject.fillResult(ResultEnum.WARNING, "Daily Total Time Exceeded!");
                }
            }else{
                List<ManipulatedEvent> manipulatedEvents = manipulatedEventList.stream().filter(e -> e.getContactAction()).collect(Collectors.toList());
                ManipulatedEvent lastManipulatedEvent = manipulatedEvents.get(manipulatedEvents.size() - 1);
                if (lastManipulatedEvent.getEventFinishTime().plusMinutes(event.getEventDuration()).isAfter(LocalTime.of(17, 0))) {
                    resultObject.fillResult(ResultEnum.WARNING, "Daily Total Time Exceeded!");
                }
            }
        } else {
            Event e = eventRepository.save(event);
            resultObject.fillResult(ResultEnum.SUCCESS, e);
        }
        return resultObject;
    }

    private void setOrderInEvent(Event event) {
        Short eventCount = eventRepository.countByEventDate(event.getEventDate());
        if (eventCount.intValue() > 0) {
            event.setEventOrder(++eventCount);
        } else {
            event.setEventOrder((short) 1);
        }
    }

    public ResultObject fetchAll() {
        ResultObject resultObject = new ResultObject();
        List<Event> eventList = eventRepository.findAll();
        if (eventList != null && !eventList.isEmpty()) {
            resultObject.fillResult(ResultEnum.SUCCESS, eventList);
        } else {
            resultObject.fillResult(ResultEnum.EMPTY, Boolean.TRUE);
        }
        return resultObject;
    }

    public ResultObject fetchEventsByEventDate(LocalDate eventDate) {
        ResultObject resultObject = new ResultObject();
        List<Event> eventList = eventRepository.findEventsByEventDate(eventDate);
        if (eventList != null && !eventList.isEmpty()) {
            resultObject.fillResult(ResultEnum.SUCCESS, eventList);
        } else {
            resultObject.fillResult(ResultEnum.EMPTY, Boolean.TRUE);
        }
        return resultObject;
    }

    public ResultObject adjustEvents(LocalDate eventDate) {
        ResultObject resultObject = new ResultObject();
        List<Event> eventList = eventRepository.findEventsByEventDate(eventDate);
        List<ManipulatedEvent> manipulatedEventList = new ArrayList<>();
        List<ManipulatedEvent> contactActionList = new ArrayList<>();
        AtomicLong totalDuration = new AtomicLong(0);
        if (eventList != null && !eventList.isEmpty()) {
            eventList.stream().forEach(event -> {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                if (event.getContactAction() != Boolean.TRUE) {
                    long finalDuration = totalDuration.addAndGet(event.getEventDuration());
                    ManipulatedEvent lastManipulatedEvent = null;
                    LocalTime eventStartTime = null;
                    LocalTime beforeStartTime = null;
                    LocalTime afterStartTime = null;
                    if (!manipulatedEventList.isEmpty()) {
                        lastManipulatedEvent = manipulatedEventList.get(manipulatedEventList.size() - 1);
                        if (finalDuration < EventConstants.HALY_DAY_TIME_LIMIT) {
                            addEventInManipulatedList(manipulatedEventList, event, dateTimeFormatter, lastManipulatedEvent);
                        } else if (lastManipulatedEvent.getEventStartTime().plusMinutes(event.getEventDuration()).isBefore(LocalTime.of(12, 0))
                                && lastManipulatedEvent.getEventFinishTime().plusMinutes(event.getEventDuration()).isAfter(LocalTime.of(12, 0))) {
                            long diff = finalDuration - 180;
                            //before lunch
                            addManipulatedEventLunch(manipulatedEventList, event, dateTimeFormatter, lastManipulatedEvent, event.getEventDuration() - diff);
                            //add lunch
                            addLunch(manipulatedEventList, dateTimeFormatter);
                            //after lunch
                            lastManipulatedEvent = manipulatedEventList.get(manipulatedEventList.size() - 1);
                            addManipulatedEventLunch(manipulatedEventList, event, dateTimeFormatter, lastManipulatedEvent, diff);

                        } else if (lastManipulatedEvent.getEventFinishTime().plusMinutes(event.getEventDuration()).isAfter(LocalTime.of(13, 0))
                                && lastManipulatedEvent.getEventFinishTime().plusMinutes(event.getEventDuration()).isBefore(LocalTime.of(16, 0))) {
                            addEventInManipulatedList(manipulatedEventList, event, dateTimeFormatter, lastManipulatedEvent);
                        }
                    } else {
                        eventStartTime = LocalTime.of(9, 0);
                        addEventManipulated(manipulatedEventList, event, dateTimeFormatter, eventStartTime);
                    }
                } else {
                    ManipulatedEvent lastContactAction = null;
                    LocalTime eventStartTime = null;
                    if (!contactActionList.isEmpty()) {
                        lastContactAction = contactActionList.get(contactActionList.size() - 1);
                        if (lastContactAction.getEventFinishTime().plusMinutes(event.getEventDuration()).isAfter(LocalTime.of(16, 0))
                                && lastContactAction.getEventFinishTime().plusMinutes(event.getEventDuration()).isBefore(LocalTime.of(17, 0))) {
                            addEventInManipulatedList(contactActionList, event, dateTimeFormatter, lastContactAction);
                        }
                    } else {
                        eventStartTime = LocalTime.of(16, 0);
                        addEventManipulated(contactActionList, event, dateTimeFormatter, eventStartTime);
                    }
                    manipulatedEventList.addAll(contactActionList);
                }
            });
            resultObject.fillResult(ResultEnum.SUCCESS, manipulatedEventList);
        } else {
            resultObject.fillResult(ResultEnum.EMPTY, Boolean.TRUE);
        }
        return resultObject;
    }

    private void addManipulatedEventLunch(List<ManipulatedEvent> manipulatedEventList, Event event, DateTimeFormatter dateTimeFormatter, ManipulatedEvent lastManipulatedEvent, long diff) {
        LocalTime afterStartTime;
        afterStartTime = LocalTime.of(lastManipulatedEvent.getEventFinishTime().getHour(), lastManipulatedEvent.getEventFinishTime().getMinute());
        LocalTime eventFinishTimeAfter = afterStartTime.plusMinutes(diff);
        ManipulatedEvent manipulatedEventAfter = new ManipulatedEvent();
        manipulatedEventAfter.setOrder(event.getEventOrder());
        manipulatedEventAfter.setTrack(event.getEventName());
        manipulatedEventAfter.setEventDuration(diff);
        manipulatedEventAfter.setEventStartTime(afterStartTime);
        manipulatedEventAfter.setEventStartTimeAsString(afterStartTime.format(dateTimeFormatter));
        manipulatedEventAfter.setEventFinishTime(eventFinishTimeAfter);
        manipulatedEventList.add(manipulatedEventAfter);
    }

    private void addLunch(List<ManipulatedEvent> manipulatedEventList, DateTimeFormatter dateTimeFormatter) {
        ManipulatedEvent manipulatedEventLunch = new ManipulatedEvent();
        manipulatedEventLunch.setOrder(0);
        manipulatedEventLunch.setTrack("LUNCH");
        manipulatedEventLunch.setEventDuration(60l);
        manipulatedEventLunch.setEventStartTime(LocalTime.of(12, 0));
        manipulatedEventLunch.setEventStartTimeAsString(LocalTime.of(12, 0).format(dateTimeFormatter));
        manipulatedEventLunch.setEventFinishTime(LocalTime.of(13, 0));
        manipulatedEventList.add(manipulatedEventLunch);
    }

    private void addEventManipulated(List<ManipulatedEvent> manipulatedEventList, Event event, DateTimeFormatter dateTimeFormatter, LocalTime eventStartTime) {
        LocalTime eventFininshTime = eventStartTime.plusMinutes(event.getEventDuration());
        ManipulatedEvent manipulatedEvent = new ManipulatedEvent();
        manipulatedEvent.setOrder(event.getEventOrder());
        manipulatedEvent.setTrack(event.getEventName());
        manipulatedEvent.setEventDuration(event.getEventDuration());
        manipulatedEvent.setEventStartTime(eventStartTime);
        manipulatedEvent.setEventStartTimeAsString(eventStartTime.format(dateTimeFormatter));
        manipulatedEvent.setEventFinishTime(eventFininshTime);
        manipulatedEventList.add(manipulatedEvent);
    }

    private void addEventInManipulatedList(List<ManipulatedEvent> contactActionList, Event event, DateTimeFormatter dateTimeFormatter, ManipulatedEvent lastContactAction) {
        LocalTime eventStartTime;
        eventStartTime = LocalTime.of(lastContactAction.getEventFinishTime().getHour(), lastContactAction.getEventFinishTime().getMinute());
        addEventManipulated(contactActionList, event, dateTimeFormatter, eventStartTime);
    }

}
