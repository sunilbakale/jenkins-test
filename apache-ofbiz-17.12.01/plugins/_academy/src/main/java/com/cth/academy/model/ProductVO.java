package com.cth.academy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductVO {

    String productId;
    String productTypeId;
    String productName;
    String longDescription;
    ProductPriceVO productPrice;

}
