package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AppealRecordMapper {
    int insert(@Param("penaltyId") Long penaltyId,
               @Param("userId") Integer userId,
               @Param("appealReason") String appealReason,
               @Param("evidence") String evidence);

    List<Map<String, Object>> selectByUserId(@Param("userId") Integer userId,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);

    long countByUserId(@Param("userId") Integer userId);

    List<Map<String, Object>> selectAdminPage(@Param("status") String status,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);

    long countAdminPage(@Param("status") String status);

    int updateReview(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("reviewResult") String reviewResult,
                     @Param("reviewerId") Long reviewerId,
                     @Param("reviewRemark") String reviewRemark);
}