package com.cth.academy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RecentActivityVO {
    private String activityId;
    private String activityType;
    private String activityTypeId;
    private String activityTypeInfo;
    private Timestamp activityCreatedDate;
    private String action;
}
