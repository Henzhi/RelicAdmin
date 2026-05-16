package com.relic.dto;

import lombok.Data;

@Data
public class SensitiveWordCreateDTO {
    private String word;
    private String category;
}