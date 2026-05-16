package com.relic.dto;

import lombok.Data;

@Data
public class SensitiveWordUpdateDTO {
    private String word;
    private String category;
    private Integer status;
}