package com.relic.service.impl;

import com.relic.mapper.*;
import com.relic.service.UserBehaviorAdminService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserBehaviorAdminServiceImpl implements UserBehaviorAdminService {

    private final UserFavoriteMapper userFavoriteMapper;
    private final ArtifactLikeMapper artifactLikeMapper;
    private final PostLikeMapper postLikeMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final UserPostMapper userPostMapper;
    private final UserCommentMapper userCommentMapper;
    private final UserFollowMapper userFollowMapper;
    private final UserUploadMapper userUploadMapper;
    private final UserBrowseHistoryMapper userBrowseHistoryMapper;
    private final UserBehaviorMapper userBehaviorMapper;

    @Override
    public PageResultVO<Map<String, Object>> listFavorites(String username, String artifactName, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = userFavoriteMapper.selectAllByPage(username, artifactName, startTime, endTime, offset, pageSize);
        long total = userFavoriteMapper.countAll(username, artifactName, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public PageResultVO<Map<String, Object>> listArtifactLikes(Integer userId, String username, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = artifactLikeMapper.selectAllByPage(userId, username, startTime, endTime, offset, pageSize);
        long total = artifactLikeMapper.countAll(userId, username, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public PageResultVO<Map<String, Object>> listPostLikes(Integer userId, String username, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = postLikeMapper.selectAllByPage(userId, username, startTime, endTime, offset, pageSize);
        long total = postLikeMapper.countAll(userId, username, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public PageResultVO<Map<String, Object>> listCommentLikes(Integer userId, String username, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = commentLikeMapper.selectAllByPage(userId, username, startTime, endTime, offset, pageSize);
        long total = commentLikeMapper.countAll(userId, username, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public PageResultVO<Map<String, Object>> listPosts(String keyword, Integer userId, String username, String status, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = userPostMapper.selectByPage(keyword, userId, username, status, startTime, endTime, offset, pageSize);
        long total = userPostMapper.countByPage(keyword, userId, username, status, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public PageResultVO<Map<String, Object>> listComments(String keyword, Integer userId, String username, Integer artifactId, String status, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = userCommentMapper.selectByPage(keyword, userId, username, artifactId, status, startTime, endTime, offset, pageSize);
        long total = userCommentMapper.countByPage(keyword, userId, username, artifactId, status, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public PageResultVO<Map<String, Object>> listFollows(Integer followerId, Integer followeeId, String username, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = userFollowMapper.selectByPage(followerId, followeeId, username, startTime, endTime, offset, pageSize);
        long total = userFollowMapper.countByPage(followerId, followeeId, username, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public PageResultVO<Map<String, Object>> listUploads(Integer userId, String username, String mediaType, String status, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = userUploadMapper.selectByPage(userId, username, mediaType, status, startTime, endTime, offset, pageSize);
        long total = userUploadMapper.countByPage(userId, username, mediaType, status, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public PageResultVO<Map<String, Object>> listBrowseHistory(Integer userId, String username, Integer artifactId, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = userBrowseHistoryMapper.selectByPage(userId, username, artifactId, startTime, endTime, offset, pageSize);
        long total = userBrowseHistoryMapper.countByPage(userId, username, artifactId, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public PageResultVO<Map<String, Object>> listBehaviorLogs(Integer userId, String username, String behaviorType, String keyword, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = userBehaviorMapper.selectByPage(userId, username, behaviorType, keyword, startTime, endTime, offset, pageSize);
        long total = userBehaviorMapper.countByPage(userId, username, behaviorType, keyword, startTime, endTime);
        return new PageResultVO<>(total, records, page, pageSize);
    }
}