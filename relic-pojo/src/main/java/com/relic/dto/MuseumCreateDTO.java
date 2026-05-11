package com.relic.dto;

import lombok.Data;

@Data
public class MuseumCreateDTO {
    private String name;
    private String shortName;
    private String country;
    private String city;
    private String website;
    private String collectionUrl;
}