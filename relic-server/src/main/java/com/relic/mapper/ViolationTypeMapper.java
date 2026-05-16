package com.relic.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ViolationTypeMapper {
    List<Map<String, Object>> selectAll();
}