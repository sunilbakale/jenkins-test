package com.cth.academy.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ExpenseVO {
    private String expenseId;
    private String title;
    private Timestamp date;
    private BigDecimal amount;
    private String paymentMode;
    private String description;

    @Override
    public String toString() {return expenseId + " : " + title; }
}
