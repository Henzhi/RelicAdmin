package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private Long id;
    private Integer userId;
    private String type;
    private String title;
    private String content;
    private Boolean isRead;
    private String extraData;
    private LocalDateTime createdAt;
}
