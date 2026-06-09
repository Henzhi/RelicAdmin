package com.relic.service.impl;

import com.relic.entity.Dynasty;
import com.relic.mapper.DynastyMapper;
import com.relic.service.DynastyService;
import com.relic.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DynastyServiceImpl implements DynastyService {

    private final DynastyMapper dynastyMapper;

    @Override
    public PageResultVO<Dynasty> page(String nameZh, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Dynasty> records = dynastyMapper.selectByPage(nameZh, offset, pageSize);
        long total = dynastyMapper.countByPage(nameZh);
        return new PageResultVO<>(total, records, page, pageSize);
    }

    @Override
    public Dynasty getById(Integer id) {
        return dynastyMapper.selectById(id);
    }

    @Override
    public void create(Dynasty dynasty) {
        dynasty.setCreatedAt(LocalDateTime.now());
        dynastyMapper.insert(dynasty);
    }

    @Override
    public void update(Dynasty dynasty) {
        dynastyMapper.update(dynasty);
    }

    @Override
    public void delete(Integer id) {
        dynastyMapper.deleteById(id);
    }
}
