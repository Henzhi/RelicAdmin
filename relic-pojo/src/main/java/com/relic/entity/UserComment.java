package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserComment {
    private Integer id;
    private Integer userId;
    private Integer artifactId;
    private Integer parentId;
    private String content;
    private Integer likeCount;
    private String status;
    private Integer auditBy;
    private LocalDateTime auditTime;
    private String rejectReason;
    private LocalDateTime createdAt;
}
