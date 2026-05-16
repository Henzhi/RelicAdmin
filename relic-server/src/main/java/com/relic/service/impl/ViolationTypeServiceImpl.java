package com.relic.service.impl;

import com.relic.dto.ViolationTypeCreateDTO;
import com.relic.dto.ViolationTypeUpdateDTO;
import com.relic.mapper.ViolationTypeMapper;
import com.relic.service.ViolationTypeService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViolationTypeServiceImpl implements ViolationTypeService {

    private final ViolationTypeMapper violationTypeMapper;

    @Override
    public PageResultVO<Map<String, Object>> listPage(Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> records = violationTypeMapper.selectByPage(status, offset, pageSize);
        long total = violationTypeMapper.countByPage(status);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public void create(ViolationTypeCreateDTO dto) {
        if (dto.getTypeCode() == null || dto.getTypeCode().isBlank()) {
            throw new IllegalArgumentException("违规类型编码不能为空");
        }
        if (dto.getTypeName() == null || dto.getTypeName().isBlank()) {
            throw new IllegalArgumentException("违规类型名称不能为空");
        }
        if (dto.getSeverityLevel() == null) {
            throw new IllegalArgumentException("严重等级不能为空");
        }
        if (violationTypeMapper.checkTypeCodeExists(dto.getTypeCode()) > 0) {
            throw new IllegalArgumentException("违规类型编码已存在");
        }
        violationTypeMapper.insert(dto.getTypeCode(), dto.getTypeName(), dto.getSeverityLevel(),
                dto.getDefaultPenalty(), dto.getDescription());
        log.info("新增违规类型: {}", dto.getTypeCode());
    }

    @Override
    public void update(Integer id, ViolationTypeUpdateDTO dto) {
        if (dto.getTypeName() == null || dto.getTypeName().isBlank()) {
            throw new IllegalArgumentException("违规类型名称不能为空");
        }
        if (dto.getSeverityLevel() == null) {
            throw new IllegalArgumentException("严重等级不能为空");
        }
        violationTypeMapper.update(id, dto.getTypeName(), dto.getSeverityLevel(),
                dto.getDefaultPenalty(), dto.getDescription(), dto.getStatus());
        log.info("更新违规类型 id: {}", id);
    }

    @Override
    public void updateStatus(Integer id, Integer status) {
        violationTypeMapper.updateStatus(id, status);
        log.info("违规类型 id={} 状态更新为: {}", id, status);
    }

    @Override
    public void delete(Integer id) {
        violationTypeMapper.deleteById(id);
        log.info("删除违规类型 id: {}", id);
    }
}