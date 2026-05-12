package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.ArtifactImageCreateDTO;
import com.relic.entity.Artifact;
import com.relic.entity.ArtifactImage;
import com.relic.mapper.ArtifactImageMapper;
import com.relic.mapper.ArtifactMapper;
import com.relic.service.ArtifactImageService;
import com.relic.utils.AliOssUtil;
import com.relic.vo.ArtifactImageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtifactImageServiceImpl implements ArtifactImageService {

    private final ArtifactImageMapper artifactImageMapper;
    private final ArtifactMapper artifactMapper;
    private final AliOssUtil aliOssUtil;

    @Override
    public List<ArtifactImageVO> getByArtifactId(Integer artifactId) {
        List<ArtifactImage> images = artifactImageMapper.selectByArtifactId(artifactId);
        return images.stream().map(VoConverter::toArtifactImageVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void create(Integer artifactId, ArtifactImageCreateDTO dto) {
        ArtifactImage image = new ArtifactImage();
        image.setArtifactId(artifactId);
        image.setImageUrl(dto.getImageUrl());
        image.setImagePath(dto.getImagePath());
        image.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : 0);
        image.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        artifactImageMapper.insert(image);

        if (image.getIsPrimary() == 1) {
            artifactImageMapper.clearPrimary(artifactId);
            artifactImageMapper.setPrimary(image.getId());
            updateArtifactImageUrl(artifactId, dto.getImageUrl());
        }
    }

    @Override
    public ArtifactImageVO ossUpload(Integer artifactId, MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String objectName = "artifact/" + artifactId + "/" + UUID.randomUUID() + ext;
            byte[] bytes = file.getBytes();
            String imageUrl = aliOssUtil.upload(bytes, objectName);

            // 仅返回URL，不写入数据库（由调用方在点击"确定"后统一保存）
            ArtifactImageVO vo = new ArtifactImageVO();
            vo.setImageUrl(imageUrl);
            return vo;
        } catch (IOException e) {
            log.error("OSS上传失败", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(Integer artifactId, Integer imageId, ArtifactImageCreateDTO dto) {
        ArtifactImage image = new ArtifactImage();
        image.setId(imageId);
        image.setImageUrl(dto.getImageUrl());
        image.setImagePath(dto.getImagePath());
        image.setIsPrimary(dto.getIsPrimary());
        image.setSortOrder(dto.getSortOrder());
        artifactImageMapper.update(image);

        if (dto.getIsPrimary() != null && dto.getIsPrimary() == 1) {
            artifactImageMapper.clearPrimary(artifactId);
            artifactImageMapper.setPrimary(imageId);
            updateArtifactImageUrl(artifactId, dto.getImageUrl());
        }
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

        ArtifactImage image = artifactImageMapper.selectById(imageId);
        if (image != null && image.getImageUrl() != null) {
            updateArtifactImageUrl(artifactId, image.getImageUrl());
        }
    }

    private void updateArtifactImageUrl(Integer artifactId, String imageUrl) {
        Artifact artifact = new Artifact();
        artifact.setId(artifactId);
        artifact.setImageUrl(imageUrl);
        artifact.setLastUpdated(java.time.LocalDateTime.now());
        artifactMapper.update(artifact);
    }
}