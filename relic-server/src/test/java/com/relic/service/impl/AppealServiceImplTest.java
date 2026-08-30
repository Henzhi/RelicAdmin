package com.relic.service.impl;

import com.relic.context.BaseContext;
import com.relic.dto.AppealCreateDTO;
import com.relic.mapper.AppealRecordMapper;
import com.relic.mapper.PenaltyRecordMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 申诉服务单元测试：只能针对本人的处罚记录提交申诉
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppealServiceImplTest {

    @Mock
    private AppealRecordMapper appealRecordMapper;

    @Mock
    private PenaltyRecordMapper penaltyRecordMapper;

    @InjectMocks
    private AppealServiceImpl service;

    @BeforeEach
    void setUp() {
        BaseContext.setCurrentId(7L);
    }

    @AfterEach
    void tearDown() {
        BaseContext.removeCurrentId();
    }

    private AppealCreateDTO dto(Long penaltyId, String reason) {
        AppealCreateDTO dto = new AppealCreateDTO();
        dto.setPenaltyId(penaltyId);
        dto.setAppealReason(reason);
        return dto;
    }

    @Test
    void createAppeal_ownPenalty_shouldInsert() {
        Map<String, Object> penalty = new HashMap<>();
        penalty.put("userId", 7);
        when(penaltyRecordMapper.selectById(100L)).thenReturn(penalty);

        service.createAppeal(dto(100L, "我认为处罚有误"));

        verify(appealRecordMapper).insert(100L, 7, "我认为处罚有误", null);
    }

    @Test
    void createAppeal_othersPenalty_shouldThrow() {
        Map<String, Object> penalty = new HashMap<>();
        penalty.put("userId", 8);
        when(penaltyRecordMapper.selectById(100L)).thenReturn(penalty);

        assertThrows(IllegalArgumentException.class, () -> service.createAppeal(dto(100L, "理由")));
        verify(appealRecordMapper, never()).insert(anyLong(), anyInt(), anyString(), anyString());
    }

    @Test
    void createAppeal_penaltyNotFound_shouldThrow() {
        when(penaltyRecordMapper.selectById(404L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.createAppeal(dto(404L, "理由")));
        verify(appealRecordMapper, never()).insert(anyLong(), anyInt(), anyString(), anyString());
    }

    @Test
    void createAppeal_missingPenaltyIdOrReason_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.createAppeal(dto(null, "理由")));
        assertThrows(IllegalArgumentException.class, () -> service.createAppeal(dto(100L, "  ")));
        verify(penaltyRecordMapper, never()).selectById(anyLong());
    }
}
