package com.relic.service.impl;

import com.relic.cache.CacheNames;
import com.relic.entity.Dynasty;
import com.relic.entity.Location;
import com.relic.entity.Museum;
import com.relic.mapper.DynastyMapper;
import com.relic.mapper.LocationMapper;
import com.relic.mapper.MuseumMapper;
import com.relic.service.DictCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictCacheServiceImpl implements DictCacheService {

    private final DynastyMapper dynastyMapper;
    private final MuseumMapper museumMapper;
    private final LocationMapper locationMapper;

    @Override
    @Cacheable(cacheNames = CacheNames.DICT, key = "'dynasties'")
    public Map<Integer, String> getDynastyNames() {
        List<Dynasty> list = dynastyMapper.selectAll();
        if (list == null || list.isEmpty()) {
            return Map.of();
        }
        return list.stream().collect(Collectors.toMap(Dynasty::getId, Dynasty::getNameZh, (a, b) -> a));
    }

    @Override
    @Cacheable(cacheNames = CacheNames.DICT, key = "'museums'")
    public Map<Integer, String> getMuseumNames() {
        List<Museum> list = museumMapper.selectAll();
        if (list == null || list.isEmpty()) {
            return Map.of();
        }
        return list.stream().collect(Collectors.toMap(Museum::getId, Museum::getName, (a, b) -> a));
    }

    @Override
    @Cacheable(cacheNames = CacheNames.DICT, key = "'locations'")
    public Map<Integer, String> getLocationNames() {
        List<Location> list = locationMapper.selectAll();
        if (list == null || list.isEmpty()) {
            return Map.of();
        }
        return list.stream().collect(Collectors.toMap(Location::getId, Location::getNameZh, (a, b) -> a));
    }
}
