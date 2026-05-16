package com.relic.mapper;

import com.relic.entity.UserFavorite;
import com.relic.vo.FavoriteVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFavoriteMapper {
    void insert(UserFavorite favorite);
    void delete(@Param("userId") Integer userId, @Param("artifactId") Integer artifactId);
    UserFavorite selectByUserAndArtifact(@Param("userId") Integer userId, @Param("artifactId") Integer artifactId);
    List<FavoriteVO> selectByUserId(@Param("userId") Integer userId,
                                       @Param("groupName") String groupName,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);
    int countByUserId(@Param("userId") Integer userId, @Param("groupName") String groupName);
    List<UserFavorite> selectAllByPage(@Param("userId") Integer userId, @Param("offset") int offset, @Param("limit") int limit);
    long countAllByPage(@Param("userId") Integer userId);
}