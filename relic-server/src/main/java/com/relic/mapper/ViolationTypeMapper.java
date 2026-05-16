package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ViolationTypeMapper {
    List<Map<String, Object>> selectAll();

    List<Map<String, Object>> selectByPage(@Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);

    long countByPage(@Param("status") Integer status);

    Map<String, Object> selectById(@Param("id") Integer id);

    int insert(@Param("typeCode") String typeCode, @Param("typeName") String typeName,
               @Param("severityLevel") Integer severityLevel, @Param("defaultPenalty") String defaultPenalty,
               @Param("description") String description);

    int update(@Param("id") Integer id, @Param("typeName") String typeName,
               @Param("severityLevel") Integer severityLevel, @Param("defaultPenalty") String defaultPenalty,
               @Param("description") String description, @Param("status") Integer status);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    int deleteById(@Param("id") Integer id);

    int checkTypeCodeExists(@Param("typeCode") String typeCode);
}