package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpload {
    private Integer id;
    private Integer userId;
    private Integer artifactId;
    private String mediaType;
    private String fileUrl;
    private String filePath;
    private String caption;
    private String locationTaken;
    private String status;
    private Integer auditBy;
    private LocalDateTime auditTime;
    private String rejectReason;
    private Integer likeCount;
    private LocalDateTime createdAt;
}
