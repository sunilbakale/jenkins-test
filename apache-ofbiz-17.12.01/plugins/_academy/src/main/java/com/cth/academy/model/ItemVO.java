package com.cth.academy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ItemVO {

    private String description;
    private String itemType = "ITM_FEE";
    private BigDecimal quantity = BigDecimal.ZERO;
    private BigDecimal unitPrice = BigDecimal.ZERO;

}
