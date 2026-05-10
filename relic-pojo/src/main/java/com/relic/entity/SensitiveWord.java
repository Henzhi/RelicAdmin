package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensitiveWord {
    private Long id;
    private String word;
    private String category;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
