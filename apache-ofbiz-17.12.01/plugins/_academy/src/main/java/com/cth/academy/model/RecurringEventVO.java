package com.cth.academy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RecurringEventVO {

    private String recurringType;
    private Timestamp startDate;
    private Timestamp endDate;
    private String until;
    private Long numberOfOccurrence;
    private String onSunday = "N";
    private String onMonday = "N";
    private String onTuesday = "N";
    private String onWednesday = "N";
    private String onThursday = "N";
    private String onFriday = "N";
    private String onSaturday = "N";
    private Long onMonthDay;

}
