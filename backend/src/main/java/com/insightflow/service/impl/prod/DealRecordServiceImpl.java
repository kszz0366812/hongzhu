package com.insightflow.service.impl.prod;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.prod.DealRecord;
import com.insightflow.mapper.prod.DealRecordMapper;
import com.insightflow.service.prod.DealRecordService;
import com.insightflow.common.util.EntityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DealRecordServiceImpl extends ServiceImpl<DealRecordMapper, DealRecord> implements DealRecordService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(DealRecord dealRecord) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(dealRecord);
        return super.save(dealRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<DealRecord> dealRecordList) {
        // 批量设置创建信息
        dealRecordList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(dealRecordList);
    }
    
    @Override
    public List<DealRecord> getDealRecordList(String keyword, LocalDateTime salesDateTime, String customerManager) {
        LambdaQueryWrapper<DealRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(DealRecord::getSalesOrderNo, keyword)
                    .or().like(DealRecord::getCustomerName, keyword)
                    .or().like(DealRecord::getProductName, keyword)
                    .or().like(DealRecord::getCustomerCode, keyword));
        }
        
        // 销售时间筛选
        if (salesDateTime != null) {
            wrapper.eq(DealRecord::getSalesDateTime, salesDateTime);
        }
        
        // 客户经理筛选
        if (StringUtils.hasText(customerManager)) {
            wrapper.eq(DealRecord::getCustomerManager, customerManager);
        }
        
        wrapper.orderByDesc(DealRecord::getSalesDateTime).orderByDesc(DealRecord::getId);
        
        return list(wrapper);
    }
    
    @Override
    public PageInfo<DealRecord> getDealRecordPage(Integer current, Integer size, String keyword, LocalDateTime salesDateTime, String customerManager) {
        PageHelper.startPage(current, size);
        
        LambdaQueryWrapper<DealRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(DealRecord::getSalesOrderNo, keyword)
                    .or().like(DealRecord::getCustomerName, keyword)
                    .or().like(DealRecord::getProductName, keyword)
                    .or().like(DealRecord::getCustomerCode, keyword));
        }
        
        // 销售时间筛选
        if (salesDateTime != null) {
            wrapper.eq(DealRecord::getSalesDateTime, salesDateTime);
        }
        
        // 客户经理筛选
        if (StringUtils.hasText(customerManager)) {
            wrapper.eq(DealRecord::getCustomerManager, customerManager);
        }
        
        wrapper.orderByDesc(DealRecord::getSalesDateTime).orderByDesc(DealRecord::getId);
        
        List<DealRecord> list = this.list(wrapper);
        return new PageInfo<>(list);
    }
    
    @Override
    public Map<String, Object> getDealRecordStats(LocalDateTime startDateTime, LocalDateTime endDateTime, String customerManager) {
        LambdaQueryWrapper<DealRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (startDateTime != null) {
            wrapper.ge(DealRecord::getSalesDateTime, startDateTime);
        }
        if (endDateTime != null) {
            wrapper.le(DealRecord::getSalesDateTime, endDateTime);
        }
        if (StringUtils.hasText(customerManager)) {
            wrapper.eq(DealRecord::getCustomerManager, customerManager);
        }
        
        List<DealRecord> records = list(wrapper);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", records.size());
        stats.put("totalQuantity", records.stream()
                .mapToDouble(r -> r.getSalesQuantity().doubleValue())
                .sum());
        stats.put("uniqueCustomers", records.stream()
                .map(DealRecord::getCustomerCode)
                .distinct()
                .count());
        stats.put("uniqueProducts", records.stream()
                .map(DealRecord::getProductName)
                .distinct()
                .count());
        
        return stats;
    }
}
