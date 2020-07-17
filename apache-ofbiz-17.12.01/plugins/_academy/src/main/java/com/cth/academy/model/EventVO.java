package com.cth.academy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EventVO implements Comparable {

    private String eventId;
    private String type;
    private String title;
    private String description;
    private String eventLocation;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private String isPrivateEvent = "N";
    private String isRecurringEvent = "N";
    private String notifyGuests = "N";
    private String isDeleted = "N";
    private ArrayList<String> guestList;
    private RecurringEventVO recurringEventVO;
    private String parentEventId;

    @Override
    public String toString() {
        return eventId + " : " + title ;
    }

    @Override
    public int compareTo(Object o) {
        EventVO other = (EventVO) o;
        return this.startDateTime.compareTo(other.getStartDateTime());
    }
}
