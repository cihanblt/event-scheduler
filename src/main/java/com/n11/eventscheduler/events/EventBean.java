package com.n11.eventscheduler.events;

import com.n11.eventscheduler.constans.EventConstants;
import com.n11.eventscheduler.dto.ManipulatedEvent;
import com.n11.eventscheduler.enums.EventTypeEnum;
import com.n11.eventscheduler.enums.ResultEnum;
import com.n11.eventscheduler.models.Event;
import com.n11.eventscheduler.services.EventService;
import com.n11.eventscheduler.util.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @author cihan.bulut
 */

@Component
@ManagedBean(name = "eventBean")
@ViewScoped
public class EventBean implements Serializable {

    private static final long serialVersionUID = 4085285875380136779L;

    @Autowired
    private EventService eventService;

    private Event event;

    private List<Event> eventList;

    private List<ManipulatedEvent> manipulatedEventList;

    private LocalDate selectedDate;

    private Boolean isDurationDisable;

    @PostConstruct
    public void init() {
        event = new Event();
    }

    public EventTypeEnum[] getEventTypeEnums(){
        return EventTypeEnum.values();
    }

    public void acrionSaveEvent(){
        ResultObject resultObject = eventService.addEvent(event);
        if (resultObject.fetchAndRemove(ResultEnum.SUCCESS) != null) {
            refresh();
        }else if (resultObject.fetchAndRemove(ResultEnum.WARNING)!= null){
            addMessage(FacesMessage.SEVERITY_WARN,"Warning","");
        }
    }

    public void actionFetchAll(){
        ResultObject resultObject = eventService.fetchAll();
        eventList = (List<Event>) resultObject.fetchAndRemove(ResultEnum.SUCCESS);
        if(resultObject.fetchAndRemove(ResultEnum.EMPTY) != null){
            addMessage(FacesMessage.SEVERITY_INFO,"Info","Record not found");
        }
    }

    public void actionFetchEventsByDate(){
        ResultObject resultObject = eventService.fetchEventsByEventDate(selectedDate);
        eventList = (List<Event>) resultObject.fetchAndRemove(ResultEnum.SUCCESS);
        if(resultObject.fetchAndRemove(ResultEnum.EMPTY) != null){
            addMessage(FacesMessage.SEVERITY_INFO,"Info","Record not found");
        }
    }

    public void actionFetchManipulatedEventsByDate(){
        ResultObject resultObject = eventService.adjustEvents(selectedDate);
        manipulatedEventList = (List<ManipulatedEvent>) resultObject.fetchAndRemove(ResultEnum.SUCCESS);
        if(resultObject.fetchAndRemove(ResultEnum.EMPTY) != null){
            addMessage(FacesMessage.SEVERITY_INFO,"Info","Record not found");
        }
    }

    public void changeEventType(){
        if(event.getEventTypeEnum().equals(EventTypeEnum.LIGHTNING)){
            event.setEventDuration(EventConstants.LIGTNING_EVENT_DURATION);
            isDurationDisable = Boolean.TRUE;
        }else {
            event.setEventDuration(null);
            isDurationDisable = Boolean.FALSE;
        }
    }

    public void refresh(){
        event = new Event();
    }

    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public void showSticky() {
        FacesContext.getCurrentInstance().addMessage("sticky-key", new FacesMessage(FacesMessage.SEVERITY_INFO, "Sticky Message", "Message Content"));
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public List<ManipulatedEvent> getManipulatedEventList() {
        return manipulatedEventList;
    }

    public void setManipulatedEventList(List<ManipulatedEvent> manipulatedEventList) {
        this.manipulatedEventList = manipulatedEventList;
    }

    public Boolean getDurationDisable() {
        return isDurationDisable;
    }

    public void setDurationDisable(Boolean durationDisable) {
        isDurationDisable = durationDisable;
    }
}
