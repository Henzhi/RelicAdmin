package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.ArtistCreateDTO;
import com.relic.dto.ArtistUpdateDTO;
import com.relic.entity.Artist;
import com.relic.mapper.ArtistMapper;
import com.relic.service.ArtistService;
import com.relic.vo.ArtistVO;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistMapper artistMapper;

    @Override
    public PageResultVO<ArtistVO> page(String nameZh, String nameEn, Integer dynastyId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Artist> entities = artistMapper.selectByPage(nameZh, nameEn, dynastyId, offset, pageSize);
        long total = artistMapper.countByPage(nameZh, nameEn, dynastyId);
        List<ArtistVO> records = entities.stream().map(VoConverter::toArtistVO).collect(Collectors.toList());
        return new PageResultVO<>(total, records, page, pageSize);
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