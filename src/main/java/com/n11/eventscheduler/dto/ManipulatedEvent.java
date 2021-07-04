package com.n11.eventscheduler.dto;

import java.time.LocalTime;

/**
 * @author cihan.bulut
 */
public class ManipulatedEvent {

    private int order;

    private String track;

    private Long eventDuration;

    private LocalTime eventStartTime;

    private String eventStartTimeAsString;

    private LocalTime eventFinishTime;

    private Boolean isContactAction;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public LocalTime getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(LocalTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public LocalTime getEventFinishTime() {
        return eventFinishTime;
    }

    public void setEventFinishTime(LocalTime eventFinishTime) {
        this.eventFinishTime = eventFinishTime;
    }

    public Long getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(Long eventDuration) {
        this.eventDuration = eventDuration;
    }

    public String getEventStartTimeAsString() {
        return eventStartTimeAsString;
    }

    public void setEventStartTimeAsString(String eventStartTimeAsString) {
        this.eventStartTimeAsString = eventStartTimeAsString;
    }

    public Boolean getContactAction() {
        return isContactAction;
    }

    public void setContactAction(Boolean contactAction) {
        isContactAction = contactAction;
    }
}
