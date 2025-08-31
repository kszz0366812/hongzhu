package com.insightflow.service.impl.customer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.customer.TerminalInfo;
import com.insightflow.mapper.customer.TerminalInfoMapper;
import com.insightflow.service.customer.TerminalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TerminalServiceImpl extends ServiceImpl<TerminalInfoMapper, TerminalInfo> implements TerminalService {

    @Override
    public List<TerminalInfo> getTerminalList(String type, String keyword) {
        LambdaQueryWrapper<TerminalInfo> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(TerminalInfo::getTerminalType, type);
        }
        if (keyword != null) {
            wrapper.like(TerminalInfo::getTerminalName, keyword)
                    .or()
                    .like(TerminalInfo::getTerminalCode, keyword);
        }
        return list(wrapper);
    }

    @Override
    public Map<String, Object> getTerminalManager(Long terminalId) {
        TerminalInfo terminal = getById(terminalId);
        Map<String, Object> result = new HashMap<>();
        if (terminal != null) {
            result.put("customerManager", terminal.getCustomerManager());
            result.put("terminalName", terminal.getTerminalName());
            result.put("terminalType", terminal.getTerminalType());
        }
        return result;
    }

    @Override
    public List<String> getTerminalTypeList() {
        LambdaQueryWrapper<TerminalInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(TerminalInfo::getTerminalType)
                .groupBy(TerminalInfo::getTerminalType);
        return list(wrapper).stream()
                .map(TerminalInfo::getTerminalType)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> batchImportTerminals(List<TerminalInfo> terminalList) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = saveBatch(terminalList);
            result.put("success", success);
            result.put("count", terminalList.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @Override
    @Transactional
    public boolean updateTerminalTags(Long terminalId, List<String> tags) {
        TerminalInfo terminal = getById(terminalId);
        if (terminal != null) {
            terminal.setTags(String.join(",", tags));
            return updateById(terminal);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateTerminalInfo(TerminalInfo terminalInfo) {
        return updateById(terminalInfo);
    }

    @Override
    public List<String> getTerminalTagList() {
        LambdaQueryWrapper<TerminalInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(TerminalInfo::getTags)
                .isNotNull(TerminalInfo::getTags);
        return list(wrapper).stream()
                .map(TerminalInfo::getTags)
                .filter(tags -> tags != null && !tags.isEmpty())
                .flatMap(tags -> List.of(tags.split(",")).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getTerminalSalesStats(Long terminalId, String startTime, String endTime) {
        // TODO: 实现终端销售统计
        // 1. 根据终端ID和时间范围查询销售记录
        // 2. 计算销售额、销售量等统计指标
        // 3. 返回统计结果
        Map<String, Object> stats = new HashMap<>();
        stats.put("terminalId", terminalId);
        stats.put("startTime", startTime);
        stats.put("endTime", endTime);
        return stats;
    }
} 