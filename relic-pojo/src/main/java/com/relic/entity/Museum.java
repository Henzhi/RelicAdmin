package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Museum {
    private Integer id;
    private String name;
    private String shortName;
    private String country;
    private String city;
    private String website;
    private String collectionUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
