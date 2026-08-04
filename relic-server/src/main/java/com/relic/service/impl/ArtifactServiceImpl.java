package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.ArtifactCreateDTO;
import com.relic.dto.ArtifactUpdateDTO;
import com.relic.entity.Artifact;
import com.relic.entity.ArtifactImage;
import com.relic.entity.Dynasty;
import com.relic.entity.Location;
import com.relic.entity.Museum;
import com.relic.mapper.ArtifactImageMapper;
import com.relic.mapper.ArtifactMapper;
import com.relic.mapper.DynastyMapper;
import com.relic.mapper.LocationMapper;
import com.relic.mapper.MuseumMapper;
import com.relic.service.ArtifactService;
import com.relic.vo.ArtifactDetailVO;
import com.relic.vo.ArtifactVO;
import com.relic.vo.PageQuery;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtifactServiceImpl implements ArtifactService {

    private final ArtifactMapper artifactMapper;
    private final ArtifactImageMapper artifactImageMapper;
    private final DynastyMapper dynastyMapper;
    private final MuseumMapper museumMapper;
    private final LocationMapper locationMapper;

    /** 允许前端传入的排序字段白名单，key 为前端参数值，value 为数据库列名 */
    private static final Map<String, String> SORT_FIELD_MAP = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("createdAt", "created_at"),
            Map.entry("created_at", "created_at"),
            Map.entry("titleZh", "title_zh"),
            Map.entry("title_zh", "title_zh"),
            Map.entry("titleEn", "title_en"),
            Map.entry("title_en", "title_en"),
            Map.entry("type", "type"),
            Map.entry("material", "material"),
            Map.entry("dynastyId", "dynasty_id"),
            Map.entry("dynasty_id", "dynasty_id"),
            Map.entry("museumId", "museum_id"),
            Map.entry("museum_id", "museum_id"),
            Map.entry("lastUpdated", "last_updated"),
            Map.entry("last_updated", "last_updated")
    );

    private static final Map<String, String> SORT_ORDER_MAP = Map.of(
            "asc", "ASC", "ascending", "ASC",
            "desc", "DESC", "descending", "DESC"
    );

    @Override
    public PageResultVO<ArtifactVO> page(String titleZh, String titleEn, String type, Integer dynastyId,
                                         Integer museumId, String material, String keyword,
                                         String sortBy, String sortOrder, int page, int pageSize) {
        PageQuery pq = PageQuery.of(page, pageSize);
        // 排序字段白名单校验，防止 SQL 注入，同时兼容驼峰/下划线命名
        String safeSortBy = sortBy == null ? "created_at" : SORT_FIELD_MAP.getOrDefault(sortBy.trim(), "created_at");
        String safeSortOrder = sortOrder == null ? "DESC" : SORT_ORDER_MAP.getOrDefault(sortOrder.trim().toLowerCase(), "DESC");
        List<Artifact> entities = artifactMapper.selectByPage(titleZh, titleEn, type, dynastyId,
                museumId, material, keyword, safeSortBy, safeSortOrder, pq.getOffset(), pq.getPageSize());
        long total = artifactMapper.countByPage(titleZh, titleEn, type, dynastyId, museumId, material, keyword);
        List<ArtifactVO> records = entities.stream().map(VoConverter::toArtifactVO).collect(Collectors.toList());
        populateNames(records);
        return pq.toResult(total, records);
    }

    private void populateNames(List<ArtifactVO> vos) {
        List<Integer> dynastyIds = vos.stream().map(ArtifactVO::getDynastyId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Integer> museumIds = vos.stream().map(ArtifactVO::getMuseumId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Integer> locationIds = vos.stream().map(ArtifactVO::getLocationId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Integer, String> dynastyMap = new HashMap<>();
        Map<Integer, String> museumMap = new HashMap<>();
        Map<Integer, String> locationMap = new HashMap<>();
        for (Integer id : dynastyIds) {
            Dynasty d = dynastyMapper.selectById(id);
            if (d != null) dynastyMap.put(id, d.getNameZh());
        }
        for (Integer id : museumIds) {
            Museum m = museumMapper.selectById(id);
            if (m != null) museumMap.put(id, m.getName());
        }
        for (Integer id : locationIds) {
            Location l = locationMapper.selectById(id);
            if (l != null) locationMap.put(id, l.getNameZh());
        }
        for (ArtifactVO vo : vos) {
            vo.setDynastyName(dynastyMap.get(vo.getDynastyId()));
            vo.setMuseumName(museumMap.get(vo.getMuseumId()));
            vo.setLocationName(locationMap.get(vo.getLocationId()));
        }
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
        if (artifact.getDynastyId() != null) {
            Dynasty dynasty = dynastyMapper.selectById(artifact.getDynastyId());
            if (dynasty != null) detail.setDynastyName(dynasty.getNameZh());
        }
        if (artifact.getMuseumId() != null) {
            Museum museum = museumMapper.selectById(artifact.getMuseumId());
            if (museum != null) detail.setMuseumName(museum.getName());
        }
        if (artifact.getLocationId() != null) {
            Location location = locationMapper.selectById(artifact.getLocationId());
            if (location != null) detail.setLocationName(location.getNameZh());
        }
        return detail;
    }

    @Override
    public void create(ArtifactCreateDTO dto) {
        if (dto.getTitleZh() == null || dto.getTitleZh().isEmpty()) {
            throw new IllegalArgumentException("文物中文名不能为空");
        }
        if (dto.getType() == null || dto.getType().isEmpty()) {
            throw new IllegalArgumentException("文物类型不能为空");
        }
        if (dto.getMuseumId() == null) {
            throw new IllegalArgumentException("所属博物馆不能为空");
        }
        // 为 NOT NULL 字段设置默认值
        if (dto.getDetailUrl() == null || dto.getDetailUrl().isEmpty()) {
            dto.setDetailUrl("");
        }
        if (dto.getImageUrl() == null || dto.getImageUrl().isEmpty()) {
            dto.setImageUrl("");
        }
        if (dto.getCrawlDate() == null) {
            dto.setCrawlDate(java.time.LocalDate.now());
        }
        log.info("Creating artifact: titleZh={}, type={}, museumId={}, detailUrl={}, imageUrl={}, crawlDate={}",
                dto.getTitleZh(), dto.getType(), dto.getMuseumId(),
                dto.getDetailUrl(), dto.getImageUrl(), dto.getCrawlDate());
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