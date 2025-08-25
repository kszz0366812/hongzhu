package com.insightflow.service.impl.customer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.customer.WholesalerInfo;
import com.insightflow.mapper.customer.WholesalerInfoMapper;
import com.insightflow.service.customer.WholesalerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WholesalerServiceImpl extends ServiceImpl<WholesalerInfoMapper, WholesalerInfo> implements WholesalerService {
    
    @Override
    public List<WholesalerInfo> getWholesalerList(String level, String keyword) {
        LambdaQueryWrapper<WholesalerInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(level)) {
            wrapper.eq(WholesalerInfo::getLevel, level);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(WholesalerInfo::getDealerName, keyword)
                    .or()
                    .like(WholesalerInfo::getDealerCode, keyword);
        }
        return list(wrapper);
    }
    
    @Override
    public Map<String, Object> getWholesalerSalesStats(Long wholesalerId, String startTime, String endTime) {
        // TODO: 实现批发商销售统计
        return new HashMap<>();
    }
    
    @Override
    public Map<String, Object> getWholesalerManager(Long wholesalerId) {
        WholesalerInfo wholesaler = getById(wholesalerId);
        Map<String, Object> result = new HashMap<>();
        if (wholesaler != null) {
            result.put("customerManager", wholesaler.getCustomerManager());
            result.put("dealerName", wholesaler.getDealerName());
            result.put("dealerCode", wholesaler.getDealerCode());
        }
        return result;
    }
    
    @Override
    public boolean updateWholesalerInfo(WholesalerInfo wholesalerInfo) {
        return updateById(wholesalerInfo);
    }
    
    @Override
    public List<String> getWholesalerLevelList() {
        return list().stream()
                .map(WholesalerInfo::getLevel)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> batchImportWholesalers(List<WholesalerInfo> wholesalerList) {
        Map<String, Object> result = new HashMap<>();
        try {
            saveBatch(wholesalerList);
            result.put("success", true);
            result.put("message", "导入成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "导入失败：" + e.getMessage());
        }
        return result;
    }
} 