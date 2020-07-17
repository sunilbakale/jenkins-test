package com.cth.academy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class InvoicePaymentVO {

    private BigDecimal amount;
    private String invoiceId;
    private String fromParty;
    private String comments;
}
