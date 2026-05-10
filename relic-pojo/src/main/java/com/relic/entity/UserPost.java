package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPost {
    private Integer id;
    private Integer userId;
    private String content;
    private Integer artifactId;
    private Integer museumId;
    private String imageUrls;
    private Integer likeCount;
    private Integer commentCount;
    private String status;
    private Integer auditBy;
    private LocalDateTime auditTime;
    private String rejectReason;
    private LocalDateTime createdAt;
}
