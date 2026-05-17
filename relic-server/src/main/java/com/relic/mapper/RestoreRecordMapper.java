package com.relic.mapper;

import com.relic.dto.RestoreRecordCreateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RestoreRecordMapper {
    List<Map<String, Object>> selectByPage(@Param("status") Integer status,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    long countByPage(@Param("status") Integer status);

    int insert(RestoreRecordCreateDTO dto);

    @Select("SELECT LAST_INSERT_ID()")
    Long selectLastInsertId();

    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status,
                     @Param("remark") String remark);

    Map<String, Object> selectById(@Param("id") Long id);
}
