package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.ArtifactImageCreateDTO;
import com.relic.entity.ArtifactImage;
import com.relic.mapper.ArtifactImageMapper;
import com.relic.service.ArtifactImageService;
import com.relic.vo.ArtifactImageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtifactImageServiceImpl implements ArtifactImageService {

    private final ArtifactImageMapper artifactImageMapper;

    @Override
    public List<ArtifactImageVO> getByArtifactId(Integer artifactId) {
        List<ArtifactImage> images = artifactImageMapper.selectByArtifactId(artifactId);
        return images.stream().map(VoConverter::toArtifactImageVO).collect(Collectors.toList());
    }

    @Override
    public void create(Integer artifactId, ArtifactImageCreateDTO dto) {
        ArtifactImage image = new ArtifactImage();
        image.setArtifactId(artifactId);
        image.setImageUrl(dto.getImageUrl());
        image.setImagePath(dto.getImagePath());
        image.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : 0);
        image.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        artifactImageMapper.insert(image);
    }

    @Override
    public void update(Integer artifactId, Integer imageId, ArtifactImageCreateDTO dto) {
        ArtifactImage image = new ArtifactImage();
        image.setId(imageId);
        image.setImageUrl(dto.getImageUrl());
        image.setImagePath(dto.getImagePath());
        image.setIsPrimary(dto.getIsPrimary());
        image.setSortOrder(dto.getSortOrder());
        artifactImageMapper.update(image);
    }

    @Override
    public void delete(Integer artifactId, Integer imageId) {
        artifactImageMapper.deleteById(imageId);
    }

    @Override
    @Transactional
    public void setPrimary(Integer artifactId, Integer imageId) {
        artifactImageMapper.clearPrimary(artifactId);
        artifactImageMapper.setPrimary(imageId);
    }
}