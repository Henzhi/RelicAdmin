package com.relic.service.impl;

import com.relic.dto.FavoriteCreateDTO;
import com.relic.entity.Artifact;
import com.relic.entity.UserFavorite;
import com.relic.mapper.ArtifactMapper;
import com.relic.mapper.UserFavoriteMapper;
import com.relic.service.FavoriteService;
import com.relic.vo.FavoriteVO;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserFavoriteMapper userFavoriteMapper;
    private final ArtifactMapper artifactMapper;

    @Override
    @Transactional
    public void add(Integer userId, FavoriteCreateDTO dto) {
        Artifact artifact = artifactMapper.selectById(dto.getArtifactId());
        if (artifact == null) {
            throw new IllegalArgumentException("文物不存在");
        }
        UserFavorite exist = userFavoriteMapper.selectByUserAndArtifact(userId, dto.getArtifactId());
        if (exist != null) {
            throw new IllegalArgumentException("该文物已在收藏列表中");
        }
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setArtifactId(dto.getArtifactId());
        favorite.setGroupName(dto.getGroupName());
        favorite.setCreatedAt(LocalDateTime.now());
        userFavoriteMapper.insert(favorite);
    }

    @Override
    @Transactional
    public void remove(Integer userId, Integer artifactId) {
        userFavoriteMapper.delete(userId, artifactId);
    }

    @Override
    public PageResultVO<FavoriteVO> list(Integer userId, String groupName, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<FavoriteVO> records = userFavoriteMapper.selectByUserId(userId, groupName, offset, pageSize);
        int total = userFavoriteMapper.countByUserId(userId, groupName);
        PageResultVO<FavoriteVO> result = new PageResultVO<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public boolean isFavorited(Integer userId, Integer artifactId) {
        UserFavorite exist = userFavoriteMapper.selectByUserAndArtifact(userId, artifactId);
        return exist != null;
    }
}