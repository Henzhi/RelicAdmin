package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    private Integer id;
    private String nameZh;
    private String nameEn;
    private Integer parentId;
    private String type;
    private LocalDateTime createdAt;
}
