package com.relic.service.impl;

import com.relic.converter.VoConverter;
import com.relic.dto.DynastyCreateDTO;
import com.relic.dto.DynastyUpdateDTO;
import com.relic.entity.Dynasty;
import com.relic.mapper.DynastyMapper;
import com.relic.service.DynastyService;
import com.relic.vo.DynastyVO;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynastyServiceImpl implements DynastyService {

    private final DynastyMapper dynastyMapper;

    @Override
    public PageResultVO<DynastyVO> page(String nameZh, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Dynasty> entities = dynastyMapper.selectByPage(nameZh, offset, pageSize);
        long total = dynastyMapper.countByPage(nameZh);
        List<DynastyVO> records = entities.stream().map(VoConverter::toDynastyVO).collect(Collectors.toList());
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public List<DynastyVO> listAll() {
        List<Dynasty> entities = dynastyMapper.selectAll();
        return entities.stream().map(VoConverter::toDynastyVO).collect(Collectors.toList());
    }

    @Override
    public void create(DynastyCreateDTO dto) {
        Dynasty dynasty = new Dynasty();
        dynasty.setNameZh(dto.getNameZh());
        dynasty.setNameEn(dto.getNameEn());
        dynasty.setStartYear(dto.getStartYear());
        dynasty.setEndYear(dto.getEndYear());
        dynasty.setDescription(dto.getDescription());
        dynasty.setCreatedAt(LocalDateTime.now());
        dynastyMapper.insert(dynasty);
    }

    @Override
    public void update(Integer id, DynastyUpdateDTO dto) {
        Dynasty dynasty = new Dynasty();
        dynasty.setId(id);
        dynasty.setNameZh(dto.getNameZh());
        dynasty.setNameEn(dto.getNameEn());
        dynasty.setStartYear(dto.getStartYear());
        dynasty.setEndYear(dto.getEndYear());
        dynasty.setDescription(dto.getDescription());
        dynastyMapper.update(dynasty);
    }

    @Override
    public void delete(Integer id) {
        dynastyMapper.deleteById(id);
    }
}