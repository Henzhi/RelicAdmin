package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFavorite {
    private Integer userId;
    private Integer artifactId;
    private String groupName;
    private LocalDateTime createdAt;
}
