package com.cth.academy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SearchEventVO {

    private String eventId;
    private String title;
    private Timestamp startDate;
    private Timestamp endDate;
    private String partyId;
    private String isPrivateEvent;

    @Override
    public String toString() {
        return "for party: "+ partyId + ", Date: " + startDate + " : " + endDate  +" privateEvent "+isPrivateEvent;
    }
}
