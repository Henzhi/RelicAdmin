package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.LocationCreateDTO;
import com.relic.dto.LocationUpdateDTO;
import com.relic.entity.Location;
import com.relic.mapper.LocationMapper;
import com.relic.service.LocationService;
import com.relic.vo.LocationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private static final Map<String, String> PARENT_TYPE_MAP = new LinkedHashMap<>();

    static {
        PARENT_TYPE_MAP.put("site", "city");
        PARENT_TYPE_MAP.put("city", "province");
        PARENT_TYPE_MAP.put("province", "country");
    }

    private final LocationMapper locationMapper;

    @Override
    public List<LocationVO> tree() {
        List<Location> all = locationMapper.selectAll();
        log.info("LocationService.tree() - fetched {} records from database", all.size());

        if (all.isEmpty()) {
            return new ArrayList<>();
        }

        List<LocationVO> vos = all.stream()
                .map(VoConverter::toLocationVO)
                .collect(Collectors.toList());
        populateParentNames(vos);

        Map<Integer, List<LocationVO>> childrenMap = vos.stream()
                .filter(v -> v.getParentId() != null)
                .collect(Collectors.groupingBy(LocationVO::getParentId));

        for (LocationVO vo : vos) {
            vo.setChildren(childrenMap.getOrDefault(vo.getId(), new ArrayList<>()));
        }

        List<LocationVO> roots = vos.stream()
                .filter(v -> v.getParentId() == null)
                .collect(Collectors.toList());

        if (roots.isEmpty()) {
            log.warn("No root nodes found (parentId is null). Returning all nodes as flat list.");
            roots = new ArrayList<>(vos);
        }

        log.info("LocationService.tree() - returning {} root nodes", roots.size());
        return roots;
    }

    @Override
    public List<LocationVO> list(String type, Integer parentId) {
        List<Location> entities = locationMapper.selectByType(type, parentId);
        log.info("LocationService.list() - fetched {} records with type={}, parentId={}",
                entities.size(), type, parentId);

        if (entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<LocationVO> vos = entities.stream()
                .map(VoConverter::toLocationVO)
                .collect(Collectors.toList());
        populateParentNames(vos);
        return vos;
    }

    @Override
    public List<LocationVO> listAllFlat() {
        List<Location> all = locationMapper.selectAll();
        log.info("LocationService.listAllFlat() - fetched {} records", all.size());

        if (all.isEmpty()) {
            return new ArrayList<>();
        }

        List<LocationVO> vos = all.stream()
                .map(VoConverter::toLocationVO)
                .collect(Collectors.toList());
        populateParentNames(vos);
        return vos;
    }

    @Override
    public List<LocationVO> listParents(String type) {
        if (type == null || type.isEmpty()) {
            return new ArrayList<>();
        }

        String parentType = PARENT_TYPE_MAP.get(type);
        if (parentType == null) {
            return new ArrayList<>();
        }

        List<Location> entities = locationMapper.selectByType(parentType, null);
        return entities.stream()
                .map(VoConverter::toLocationVO)
                .collect(Collectors.toList());
    }

    private void validateHierarchy(String type, Integer parentId) {
        if (parentId == null) {
            if (!"country".equals(type)) {
                throw new IllegalArgumentException("只有国家类型可以没有上级地点");
            }
            return;
        }

        Location parent = locationMapper.selectById(parentId);
        if (parent == null) {
            throw new IllegalArgumentException("上级地点不存在");
        }

        String expectedParentType = PARENT_TYPE_MAP.get(type);
        if (!expectedParentType.equals(parent.getType())) {
            throw new IllegalArgumentException(String.format(
                    "类型为「%s」的地点的上级必须是「%s」类型，但所选上级地点「%s」的类型为「%s」",
                    type, expectedParentType, parent.getNameZh(), parent.getType()));
        }
    }

    private void populateParentNames(List<LocationVO> vos) {
        List<Integer> parentIds = vos.stream()
                .map(LocationVO::getParentId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (parentIds.isEmpty()) {
            return;
        }

        Map<Integer, String> nameMap = new HashMap<>();
        for (Integer pid : parentIds) {
            Location parent = locationMapper.selectById(pid);
            if (parent != null) {
                nameMap.put(pid, parent.getNameZh());
            }
        }

        for (LocationVO vo : vos) {
            if (vo.getParentId() != null) {
                vo.setParentName(nameMap.get(vo.getParentId()));
            }
        }
    }

    @Override
    public void create(LocationCreateDTO dto) {
        validateHierarchy(dto.getType(), dto.getParentId());
        Location location = new Location();
        location.setNameZh(dto.getNameZh());
        location.setNameEn(dto.getNameEn());
        location.setParentId(dto.getParentId());
        location.setType(dto.getType());
        location.setCreatedAt(LocalDateTime.now());
        locationMapper.insert(location);
    }

    @Override
    public void update(Integer id, LocationUpdateDTO dto) {
        validateHierarchy(dto.getType(), dto.getParentId());
        Location location = new Location();
        location.setId(id);
        location.setNameZh(dto.getNameZh());
        location.setNameEn(dto.getNameEn());
        location.setParentId(dto.getParentId());
        location.setType(dto.getType());
        locationMapper.update(location);
    }

    @Override
    public void delete(Integer id) {
        locationMapper.deleteById(id);
    }
}