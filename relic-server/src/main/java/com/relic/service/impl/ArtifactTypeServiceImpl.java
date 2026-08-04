package com.relic.service.impl;

import com.relic.dto.ArtifactTypeCreateDTO;
import com.relic.entity.ArtifactType;
import com.relic.mapper.ArtifactTypeMapper;
import com.relic.service.ArtifactTypeService;
import com.relic.vo.ArtifactTypeVO;
import com.relic.vo.PageQuery;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtifactTypeServiceImpl implements ArtifactTypeService {

    private final ArtifactTypeMapper artifactTypeMapper;

    @Override
    public PageResultVO<ArtifactTypeVO> page(String name, int page, int pageSize) {
        PageQuery pq = PageQuery.of(page, pageSize);
        List<ArtifactType> entities = artifactTypeMapper.selectByPage(name, pq.getOffset(), pq.getPageSize());
        long total = artifactTypeMapper.countByPage(name);
        List<ArtifactTypeVO> records = entities.stream().map(this::toVO).collect(Collectors.toList());
        return pq.toResult(total, records);
    }

    @Override
    public List<ArtifactTypeVO> listAll() {
        return artifactTypeMapper.selectAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void create(ArtifactTypeCreateDTO dto) {
        ArtifactType type = new ArtifactType();
        type.setName(dto.getName());
        type.setDescription(dto.getDescription());
        LocalDateTime now = LocalDateTime.now();
        type.setCreatedAt(now);
        type.setUpdatedAt(now);
        artifactTypeMapper.insert(type);
    }

    @Override
    public void update(Integer id, ArtifactTypeCreateDTO dto) {
        ArtifactType type = new ArtifactType();
        type.setId(id);
        type.setName(dto.getName());
        type.setDescription(dto.getDescription());
        type.setUpdatedAt(LocalDateTime.now());
        artifactTypeMapper.update(type);
    }

    @Override
    public void delete(Integer id) {
        artifactTypeMapper.deleteById(id);
    }

    private ArtifactTypeVO toVO(ArtifactType entity) {
        return ArtifactTypeVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}