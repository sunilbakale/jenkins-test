package com.cth.academy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductPriceVO {

    String productId;
    String productPricePurposeId;
    String productPriceTypeId;
    String currencyUomId;
    String productStoreGroupId;
    String price;
    String fromDate;

}
