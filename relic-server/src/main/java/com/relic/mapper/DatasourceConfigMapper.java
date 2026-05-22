package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DatasourceConfigMapper {
    List<Map<String, Object>> selectByPage(@Param("dsType") String dsType, @Param("status") String status,
                                           @Param("offset") int offset, @Param("limit") int limit);

    long countByPage(@Param("dsType") String dsType, @Param("status") String status);

    Map<String, Object> selectById(@Param("id") Integer id);

    int insert(@Param("dsName") String dsName, @Param("dsKey") String dsKey,
               @Param("dsType") String dsType, @Param("host") String host,
               @Param("port") Integer port, @Param("databaseName") String databaseName,
               @Param("username") String username, @Param("passwordEncrypted") String passwordEncrypted,
               @Param("extraParams") String extraParams, @Param("maxPoolSize") Integer maxPoolSize);

    int update(@Param("id") Integer id, @Param("dsName") String dsName,
               @Param("dsType") String dsType, @Param("host") String host,
               @Param("port") Integer port, @Param("databaseName") String databaseName,
               @Param("username") String username, @Param("passwordEncrypted") String passwordEncrypted,
               @Param("extraParams") String extraParams, @Param("maxPoolSize") Integer maxPoolSize);

    int updateStatus(@Param("id") Integer id, @Param("status") String status,
                     @Param("lastTestResult") String lastTestResult);

    int deleteById(@Param("id") Integer id);

    int checkDsKeyExists(@Param("dsKey") String dsKey);
}