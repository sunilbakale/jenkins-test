package com.cth.academy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class InvoiceVO {

    private String toParty;
    private ArrayList<ItemVO> items;
    private BigDecimal discount;
    private BigDecimal outstandingAmount;
    private Timestamp dueDate;
    private String invoiceTypeId = "SALES_INVOICE";
    private  String invoiceId;
    private  String statusId;
    private Timestamp createdDate;
}
