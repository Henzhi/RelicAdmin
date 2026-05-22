package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SystemConfigMapper {
    List<Map<String, Object>> selectByPage(@Param("configGroup") String configGroup, @Param("offset") int offset, @Param("limit") int limit);

    long countByPage(@Param("configGroup") String configGroup);

    Map<String, Object> selectById(@Param("id") Integer id);

    Map<String, Object> selectByKey(@Param("configKey") String configKey);

    List<Map<String, Object>> selectByGroup(@Param("configGroup") String configGroup);

    int insert(@Param("configKey") String configKey, @Param("configName") String configName,
               @Param("configValue") String configValue, @Param("configType") String configType,
               @Param("configGroup") String configGroup, @Param("description") String description,
               @Param("sortOrder") Integer sortOrder);

    int update(@Param("id") Integer id, @Param("configName") String configName,
               @Param("configValue") String configValue, @Param("configType") String configType,
               @Param("configGroup") String configGroup, @Param("description") String description,
               @Param("sortOrder") Integer sortOrder, @Param("status") Integer status);

    int updateValue(@Param("id") Integer id, @Param("configValue") String configValue);

    int deleteById(@Param("id") Integer id);

    int checkConfigKeyExists(@Param("configKey") String configKey);
}