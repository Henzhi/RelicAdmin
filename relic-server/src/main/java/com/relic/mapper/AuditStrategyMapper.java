package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AuditStrategyMapper {
    List<Map<String, Object>> selectAll();

    Map<String, Object> selectByContentType(@Param("contentType") String contentType);

    int insertDefault(@Param("contentType") String contentType,
                      @Param("autoMode") String autoMode,
                      @Param("enableSensitiveCheck") Integer enableSensitiveCheck,
                      @Param("enableImageCheck") Integer enableImageCheck,
                      @Param("riskThreshold") Integer riskThreshold);

    int update(@Param("id") Integer id,
               @Param("autoMode") String autoMode,
               @Param("enableSensitiveCheck") Integer enableSensitiveCheck,
               @Param("enableImageCheck") Integer enableImageCheck,
               @Param("riskThreshold") Integer riskThreshold,
               @Param("status") Integer status);
}