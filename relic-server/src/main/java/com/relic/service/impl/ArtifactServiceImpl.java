package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.ArtifactCreateDTO;
import com.relic.dto.ArtifactUpdateDTO;
import com.relic.entity.Artifact;
import com.relic.entity.ArtifactImage;
import com.relic.mapper.ArtifactImageMapper;
import com.relic.mapper.ArtifactMapper;
import com.relic.service.ArtifactService;
import com.relic.vo.ArtifactDetailVO;
import com.relic.vo.ArtifactImageVO;
import com.relic.vo.ArtifactVO;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtifactServiceImpl implements ArtifactService {

    private final ArtifactMapper artifactMapper;
    private final ArtifactImageMapper artifactImageMapper;

    @Override
    public PageResultVO<ArtifactVO> page(String titleZh, String titleEn, String type, Integer dynastyId,
                                         Integer museumId, String material, String keyword,
                                         String sortBy, String sortOrder, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Artifact> entities = artifactMapper.selectByPage(titleZh, titleEn, type, dynastyId,
                museumId, material, keyword, sortBy, sortOrder, offset, pageSize);
        long total = artifactMapper.countByPage(titleZh, titleEn, type, dynastyId, museumId, material, keyword);
        List<ArtifactVO> records = entities.stream().map(VoConverter::toArtifactVO).collect(Collectors.toList());
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public ArtifactDetailVO getById(Integer id) {
        Artifact artifact = artifactMapper.selectById(id);
        if (artifact == null) {
            throw new RuntimeException("文物不存在");
        }
        ArtifactDetailVO detail = new ArtifactDetailVO();
        detail.setId(artifact.getId());
        detail.setObjectId(artifact.getObjectId());
        detail.setTitleZh(artifact.getTitleZh());
        detail.setTitleEn(artifact.getTitleEn());
        detail.setTimePeriod(artifact.getTimePeriod());
        detail.setDynastyId(artifact.getDynastyId());
        detail.setType(artifact.getType());
        detail.setMaterial(artifact.getMaterial());
        detail.setDescription(artifact.getDescription());
        detail.setDimensions(artifact.getDimensions());
        detail.setMuseumId(artifact.getMuseumId());
        detail.setLocationId(artifact.getLocationId());
        detail.setDetailUrl(artifact.getDetailUrl());
        detail.setImageUrl(artifact.getImageUrl());
        detail.setImagePath(artifact.getImagePath());
        detail.setCreditLine(artifact.getCreditLine());
        detail.setAccessionNumber(artifact.getAccessionNumber());
        detail.setCrawlDate(artifact.getCrawlDate());
        detail.setImageValidated(artifact.getImageValidated());
        detail.setLastUpdated(artifact.getLastUpdated());
        detail.setCreatedAt(artifact.getCreatedAt());
        detail.setImages(new ArrayList<>());
        detail.setArtists(new ArrayList<>());
        detail.setRelicLocations(new ArrayList<>());
        List<ArtifactImage> images = artifactImageMapper.selectByArtifactId(id);
        if (images != null) {
            detail.setImages(images.stream().map(VoConverter::toArtifactImageVO).collect(Collectors.toList()));
        }
        return detail;
    }

    @Override
    public void create(ArtifactCreateDTO dto) {
        Artifact artifact = new Artifact();
        artifact.setObjectId(dto.getObjectId());
        artifact.setTitleZh(dto.getTitleZh());
        artifact.setTitleEn(dto.getTitleEn());
        artifact.setTimePeriod(dto.getTimePeriod());
        artifact.setDynastyId(dto.getDynastyId());
        artifact.setType(dto.getType());
        artifact.setMaterial(dto.getMaterial());
        artifact.setDescription(dto.getDescription());
        artifact.setDimensions(dto.getDimensions());
        artifact.setMuseumId(dto.getMuseumId());
        artifact.setLocationId(dto.getLocationId());
        artifact.setDetailUrl(dto.getDetailUrl());
        artifact.setImageUrl(dto.getImageUrl());
        artifact.setCreditLine(dto.getCreditLine());
        artifact.setAccessionNumber(dto.getAccessionNumber());
        artifact.setCrawlDate(dto.getCrawlDate());
        LocalDateTime now = LocalDateTime.now();
        artifact.setLastUpdated(now);
        artifact.setCreatedAt(now);
        artifactMapper.insert(artifact);
    }

    @Override
    public void update(Integer id, ArtifactUpdateDTO dto) {
        Artifact artifact = new Artifact();
        artifact.setId(id);
        artifact.setObjectId(dto.getObjectId());
        artifact.setTitleZh(dto.getTitleZh());
        artifact.setTitleEn(dto.getTitleEn());
        artifact.setTimePeriod(dto.getTimePeriod());
        artifact.setDynastyId(dto.getDynastyId());
        artifact.setType(dto.getType());
        artifact.setMaterial(dto.getMaterial());
        artifact.setDescription(dto.getDescription());
        artifact.setDimensions(dto.getDimensions());
        artifact.setMuseumId(dto.getMuseumId());
        artifact.setLocationId(dto.getLocationId());
        artifact.setDetailUrl(dto.getDetailUrl());
        artifact.setImageUrl(dto.getImageUrl());
        artifact.setImagePath(dto.getImagePath());
        artifact.setCreditLine(dto.getCreditLine());
        artifact.setAccessionNumber(dto.getAccessionNumber());
        artifact.setCrawlDate(dto.getCrawlDate());
        artifact.setImageValidated(dto.getImageValidated());
        artifact.setLastUpdated(LocalDateTime.now());
        artifactMapper.update(artifact);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        artifactImageMapper.clearPrimary(id);
        artifactMapper.deleteById(id);
    }
}