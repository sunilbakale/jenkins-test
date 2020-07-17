package com.cth.academy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionVO {
    private String academyId;
    private String TransactionId;
    private Timestamp createdStamp;
}
