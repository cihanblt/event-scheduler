package com.n11.eventscheduler.models;

import com.n11.eventscheduler.enums.EventTypeEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

/**
 * @author cihan.bulut on 7/1/2021
 */

@Document
//@CompoundIndexes({ @CompoundIndex(name = "email_age", def = "{'email.id' : 1, 'age': 1}") })
public class Event {

    @Id
    private String id;

    private LocalDate eventDate;

    private Long eventDuration;

    private String eventName;

    private EventTypeEnum eventTypeEnum;

    private Boolean isContactAction;

    private Short eventOrder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public EventTypeEnum getEventTypeEnum() {
        return eventTypeEnum;
    }

    public void setEventTypeEnum(EventTypeEnum eventTypeEnum) {
        this.eventTypeEnum = eventTypeEnum;
    }

    public Boolean getContactAction() {
        return isContactAction;
    }

    public void setContactAction(Boolean contactAction) {
        isContactAction = contactAction;
    }

    public Long getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(Long eventDuration) {
        this.eventDuration = eventDuration;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public Short getEventOrder() {
        return eventOrder;
    }

    public void setEventOrder(Short eventOrder) {
        this.eventOrder = eventOrder;
    }
}
