package com.relic.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationVO {
    private Integer id;
    private String nameZh;
    private String nameEn;
    private String type;
    private Integer parentId;
    private String parentName;
    private List<LocationVO> children;
    private LocalDateTime createdAt;
}