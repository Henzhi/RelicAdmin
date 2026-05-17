package com.relic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestoreRecord {
    private Long id;
    private Long backupId;
    private String backupName;
    private Integer operatorId;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
}