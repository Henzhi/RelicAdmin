package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemLog {
    private Long id;
    private String logLevel;
    private String module;
    private String message;
    private String detail;
    private LocalDateTime createdAt;
}
