package com.cth.academy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcademyVO {

    private String academyId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String mobile;
    private String countryName;
    private String countryGeoId;
    private String product;
    private String productValidity;
}
