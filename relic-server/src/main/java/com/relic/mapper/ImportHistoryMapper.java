package com.relic.mapper;

import com.relic.entity.ImportHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImportHistoryMapper {

    @Insert("INSERT INTO import_history (file_name, file_size, file_type, total_count, success_count, fail_count, " +
            "status, error_report, operator_id, operator_name, started_at, completed_at) " +
            "VALUES (#{fileName}, #{fileSize}, #{fileType}, #{totalCount}, #{successCount}, #{failCount}, " +
            "#{status}, #{errorReport}, #{operatorId}, #{operatorName}, #{startedAt}, #{completedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ImportHistory importHistory);

    @Select("SELECT * FROM import_history ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<ImportHistory> selectByPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM import_history")
    long count();

    @Select("SELECT * FROM import_history WHERE id = #{id}")
    ImportHistory selectById(@Param("id") Integer id);

    @Update("UPDATE import_history SET total_count=#{totalCount}, success_count=#{successCount}, " +
            "fail_count=#{failCount}, status=#{status}, error_report=#{errorReport}, " +
            "completed_at=#{completedAt} WHERE id = #{id}")
    int updateResult(ImportHistory importHistory);
}
