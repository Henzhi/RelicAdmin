package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.ArtistCreateDTO;
import com.relic.dto.ArtistUpdateDTO;
import com.relic.entity.Artist;
import com.relic.entity.Dynasty;
import com.relic.mapper.ArtistMapper;
import com.relic.mapper.DynastyMapper;
import com.relic.service.ArtistService;
import com.relic.vo.ArtistVO;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistMapper artistMapper;
    private final DynastyMapper dynastyMapper;

    @Override
    public PageResultVO<ArtistVO> page(String nameZh, String nameEn, Integer dynastyId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Artist> entities = artistMapper.selectByPage(nameZh, nameEn, dynastyId, offset, pageSize);
        long total = artistMapper.countByPage(nameZh, nameEn, dynastyId);
        List<ArtistVO> records = entities.stream().map(VoConverter::toArtistVO).collect(Collectors.toList());
        populateDynastyNames(records);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    private void populateDynastyNames(List<ArtistVO> vos) {
        List<Integer> dynastyIds = vos.stream().map(ArtistVO::getDynastyId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Integer, String> nameMap = new HashMap<>();
        for (Integer id : dynastyIds) {
            Dynasty d = dynastyMapper.selectById(id);
            if (d != null) nameMap.put(id, d.getNameZh());
        }
        for (ArtistVO vo : vos) {
            vo.setDynastyName(nameMap.get(vo.getDynastyId()));
        }
    }

    @Override
    public ArtistVO getById(Integer id) {
        Artist artist = artistMapper.selectById(id);
        if (artist == null) {
            throw new RuntimeException("艺术家不存在");
        }
        return VoConverter.toArtistVO(artist);
    }

    @Override
    public void create(ArtistCreateDTO dto) {
        Artist artist = new Artist();
        artist.setNameZh(dto.getNameZh());
        artist.setNameEn(dto.getNameEn());
        artist.setBirthYear(dto.getBirthYear());
        artist.setDeathYear(dto.getDeathYear());
        artist.setDynastyId(dto.getDynastyId());
        artist.setBiography(dto.getBiography());
        artist.setBaiduUrl(dto.getBaiduUrl());
        artist.setWikiUrl(dto.getWikiUrl());
        LocalDateTime now = LocalDateTime.now();
        artist.setCreatedAt(now);
        artist.setUpdatedAt(now);
        artistMapper.insert(artist);
    }

    @Override
    public void update(Integer id, ArtistUpdateDTO dto) {
        Artist artist = new Artist();
        artist.setId(id);
        artist.setNameZh(dto.getNameZh());
        artist.setNameEn(dto.getNameEn());
        artist.setBirthYear(dto.getBirthYear());
        artist.setDeathYear(dto.getDeathYear());
        artist.setDynastyId(dto.getDynastyId());
        artist.setBiography(dto.getBiography());
        artist.setBaiduUrl(dto.getBaiduUrl());
        artist.setWikiUrl(dto.getWikiUrl());
        artist.setUpdatedAt(LocalDateTime.now());
        artistMapper.update(artist);
    }

    @Override
    public void delete(Integer id) {
        artistMapper.deleteById(id);
    }
}