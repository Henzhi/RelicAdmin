package com.relic.service;

import com.relic.vo.PageResultVO;

import java.util.Map;

public interface UserBehaviorAdminService {
    PageResultVO<Map<String, Object>> listFavorites(String username, String artifactName,
                                                    String startTime, String endTime, int page, int pageSize);
    PageResultVO<Map<String, Object>> listArtifactLikes(Integer userId, String username,
                                                        String startTime, String endTime, int page, int pageSize);
    PageResultVO<Map<String, Object>> listPostLikes(Integer userId, String username,
                                                    String startTime, String endTime, int page, int pageSize);
    PageResultVO<Map<String, Object>> listCommentLikes(Integer userId, String username,
                                                       String startTime, String endTime, int page, int pageSize);
    PageResultVO<Map<String, Object>> listPosts(String keyword, Integer userId, String username, String status,
                                                String startTime, String endTime, int page, int pageSize);
    PageResultVO<Map<String, Object>> listComments(String keyword, Integer userId, String username,
                                                   Integer artifactId, String status,
                                                   String startTime, String endTime, int page, int pageSize);
    PageResultVO<Map<String, Object>> listFollows(Integer followerId, Integer followeeId, String username,
                                                  String startTime, String endTime, int page, int pageSize);
    PageResultVO<Map<String, Object>> listUploads(Integer userId, String username, String mediaType, String status,
                                                  String startTime, String endTime, int page, int pageSize);
    PageResultVO<Map<String, Object>> listBrowseHistory(Integer userId, String username, Integer artifactId,
                                                        String startTime, String endTime, int page, int pageSize);
    PageResultVO<Map<String, Object>> listBehaviorLogs(Integer userId, String username, String behaviorType,
                                                       String keyword, String startTime, String endTime,
                                                       int page, int pageSize);
}