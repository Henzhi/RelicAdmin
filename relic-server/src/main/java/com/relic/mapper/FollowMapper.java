package com.relic.mapper;

import com.relic.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowMapper {
    UserFollow selectByFollowerAndFollowee(@Param("followerId") Integer followerId, @Param("followeeId") Integer followeeId);
    List<UserFollow> selectByPage(@Param("followerName") String followerName,
                                  @Param("followeeName") String followeeName,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);
    long countByPage(@Param("followerName") String followerName,
                     @Param("followeeName") String followeeName);
    int insert(UserFollow follow);
    int delete(@Param("followerId") Integer followerId, @Param("followeeId") Integer followeeId);
}