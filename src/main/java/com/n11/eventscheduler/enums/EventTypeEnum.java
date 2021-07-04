package com.n11.eventscheduler.enums;

/**
 * @author cihan.bulut on 7/1/2021
 */
public enum  EventTypeEnum {

    STANDART("Standart"),
    LIGHTNING("Lightning");

    private String label;

    private EventTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
