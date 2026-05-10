package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityLog {
    private Long id;
    private Integer userId;
    private String eventType;
    private String ip;
    private String detail;
    private LocalDateTime createdAt;
}
