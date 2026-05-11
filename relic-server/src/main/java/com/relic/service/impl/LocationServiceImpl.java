package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.LocationCreateDTO;
import com.relic.dto.LocationUpdateDTO;
import com.relic.entity.Location;
import com.relic.mapper.LocationMapper;
import com.relic.service.LocationService;
import com.relic.vo.LocationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationMapper locationMapper;

    @Override
    public List<LocationVO> tree() {
        List<Location> all = locationMapper.selectAll();
        List<LocationVO> vos = all.stream().map(VoConverter::toLocationVO).collect(Collectors.toList());
        Map<Integer, List<LocationVO>> childrenMap = vos.stream()
                .filter(v -> v.getParentId() != null)
                .collect(Collectors.groupingBy(LocationVO::getParentId));
        List<LocationVO> roots = new ArrayList<>();
        for (LocationVO vo : vos) {
            vo.setChildren(childrenMap.getOrDefault(vo.getId(), new ArrayList<>()));
            if (vo.getParentId() == null) {
                roots.add(vo);
            }
        }
        return roots;
    }

    @Override
    public List<LocationVO> list(String type, Integer parentId) {
        List<Location> entities = locationMapper.selectByType(type, parentId);
        return entities.stream().map(VoConverter::toLocationVO).collect(Collectors.toList());
    }

    @Override
    public void create(LocationCreateDTO dto) {
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