package com.relic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestoreRecordCreateDTO {
    private Long id;
    private Long backupId;
    private String backupName;
    private Integer operatorId;
    private Integer status;
    private String remark;
}
