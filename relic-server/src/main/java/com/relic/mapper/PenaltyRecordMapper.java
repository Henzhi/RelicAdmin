package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PenaltyRecordMapper {
    int insert(@Param("userId") Integer userId,
               @Param("penaltyType") String penaltyType,
               @Param("reason") String reason,
               @Param("operatorId") Long operatorId,
               @Param("expireTime") String expireTime,
               @Param("remark") String remark);

    List<Map<String, Object>> selectByPage(@Param("userId") Integer userId,
                                           @Param("penaltyType") String penaltyType,
                                           @Param("status") Integer status,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    long countByPage(@Param("userId") Integer userId,
                     @Param("penaltyType") String penaltyType,
                     @Param("status") Integer status);

    int updateRevoke(@Param("id") Long id, @Param("remark") String remark);
}