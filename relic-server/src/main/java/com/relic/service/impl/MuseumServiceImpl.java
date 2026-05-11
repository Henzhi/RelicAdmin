package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.MuseumCreateDTO;
import com.relic.dto.MuseumUpdateDTO;
import com.relic.entity.Museum;
import com.relic.mapper.MuseumMapper;
import com.relic.service.MuseumService;
import com.relic.vo.MuseumVO;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MuseumServiceImpl implements MuseumService {

    private final MuseumMapper museumMapper;

    @Override
    public PageResultVO<MuseumVO> page(String name, String country, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Museum> entities = museumMapper.selectByPage(name, country, offset, pageSize);
        long total = museumMapper.countByPage(name, country);
        List<MuseumVO> records = entities.stream().map(VoConverter::toMuseumVO).collect(Collectors.toList());
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public MuseumVO getById(Integer id) {
        Museum museum = museumMapper.selectById(id);
        if (museum == null) {
            throw new RuntimeException("博物馆不存在");
        }
        return VoConverter.toMuseumVO(museum);
    }

    @Override
    public void create(MuseumCreateDTO dto) {
        Museum museum = new Museum();
        museum.setName(dto.getName());
        museum.setShortName(dto.getShortName());
        museum.setCountry(dto.getCountry());
        museum.setCity(dto.getCity());
        museum.setWebsite(dto.getWebsite());
        museum.setCollectionUrl(dto.getCollectionUrl());
        LocalDateTime now = LocalDateTime.now();
        museum.setCreatedAt(now);
        museum.setUpdatedAt(now);
        museumMapper.insert(museum);
    }

    @Override
    public void update(Integer id, MuseumUpdateDTO dto) {
        Museum museum = new Museum();
        museum.setId(id);
        museum.setName(dto.getName());
        museum.setShortName(dto.getShortName());
        museum.setCountry(dto.getCountry());
        museum.setCity(dto.getCity());
        museum.setWebsite(dto.getWebsite());
        museum.setCollectionUrl(dto.getCollectionUrl());
        museum.setUpdatedAt(LocalDateTime.now());
        museumMapper.update(museum);
    }

    @Override
    public void delete(Integer id) {
        museumMapper.deleteById(id);
    }
}