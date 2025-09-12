package com.insightflow.service.impl.prod;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.prod.SalesRecord;
import com.insightflow.entity.task.TaskTarget;
import com.insightflow.mapper.prod.SalesRecordMapper;
import com.insightflow.mapper.task.TaskTargetMapper;
import com.insightflow.service.prod.SalesRecordService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class SalesRecordServiceImpl extends ServiceImpl<SalesRecordMapper, SalesRecord> implements SalesRecordService {
    
    @Autowired
    private TaskTargetMapper taskTargetMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SalesRecord salesRecord) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(salesRecord);
        return super.save(salesRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<SalesRecord> salesRecordList) {
        // 批量设置创建信息
        salesRecordList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(salesRecordList);
    }
    
    @Override
    public Map<String, Object> getSalesStats(LocalDateTime startTime, LocalDateTime endTime, String regionCode) {
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(SalesRecord::getSalesDate, startTime, endTime);
        if (regionCode != null) {
            wrapper.eq(SalesRecord::getRegionLevel1, regionCode)
                    .or()
                    .eq(SalesRecord::getRegionLevel2, regionCode)
                    .or()
                    .eq(SalesRecord::getRegionLevel3, regionCode);
        }
        
        List<SalesRecord> records = list(wrapper);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAmount", records.stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        stats.put("totalQuantity", records.stream()
                .mapToInt(SalesRecord::getQuantity)
                .sum());
        
        stats.put("orderCount", records.size());
        
        stats.put("avgOrderAmount", records.isEmpty() ? BigDecimal.ZERO :
                records.stream()
                        .map(SalesRecord::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(records.size()), 2, RoundingMode.HALF_UP));
        
        return stats;
    }
    
    @Override
    public List<Map<String, Object>> getProductSalesRanking(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(SalesRecord::getSalesDate, startTime, endTime);
        
        List<SalesRecord> records = list(wrapper);
        
        Map<String, List<SalesRecord>> productGroup = records.stream()
                .collect(Collectors.groupingBy(SalesRecord::getProductName));
        
        return productGroup.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> productStats = new HashMap<>();
                    productStats.put("productName", entry.getKey());
                    productStats.put("salesAmount", entry.getValue().stream()
                            .map(SalesRecord::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    productStats.put("salesQuantity", entry.getValue().stream()
                            .mapToInt(SalesRecord::getQuantity)
                            .sum());
                    return productStats;
                })
                .sorted((a, b) -> ((BigDecimal) b.get("salesAmount")).compareTo((BigDecimal) a.get("salesAmount")))
                .limit(limit != null ? limit : 10)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getEmployeeSalesPerformance(Long employeeId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesRecord::getSalesperson, employeeId.toString())
                .between(SalesRecord::getSalesDate, startTime, endTime);
        
        List<SalesRecord> records = list(wrapper);
        
        Map<String, Object> performance = new HashMap<>();
        performance.put("totalAmount", records.stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        performance.put("orderCount", records.size());
        
        performance.put("avgOrderAmount", records.isEmpty() ? BigDecimal.ZERO :
                records.stream()
                        .map(SalesRecord::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(records.size()), 2, RoundingMode.HALF_UP));
        
        return performance;
    }
    
    @Override
    public List<Map<String, Object>> getRegionSalesTrend(String regionCode, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(SalesRecord::getSalesDate, startTime, endTime);
        if (regionCode != null) {
            wrapper.eq(SalesRecord::getRegionLevel1, regionCode)
                    .or()
                    .eq(SalesRecord::getRegionLevel2, regionCode)
                    .or()
                    .eq(SalesRecord::getRegionLevel3, regionCode);
        }
        
        List<SalesRecord> records = list(wrapper);
        
        Map<String, List<SalesRecord>> dateGroup = records.stream()
                .collect(Collectors.groupingBy(record -> 
                    record.getSalesDate().toLocalDate().toString()));
        
        return dateGroup.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> dailyStats = new HashMap<>();
                    dailyStats.put("date", entry.getKey());
                    dailyStats.put("amount", entry.getValue().stream()
                            .map(SalesRecord::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    dailyStats.put("quantity", entry.getValue().stream()
                            .mapToInt(SalesRecord::getQuantity)
                            .sum());
                    return dailyStats;
                })
                .sorted(Comparator.comparing(m -> (String) m.get("date")))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean createSalesRecord(SalesRecord salesRecord) {
        // 自动设置创建人ID和时间
        EntityUtils.setCreateInfo(salesRecord);
        return save(salesRecord);
    }
    
    @Override
    public boolean updateSalesRecord(SalesRecord salesRecord) {
        // 自动设置更新时间
        EntityUtils.setUpdateInfo(salesRecord);
        return updateById(salesRecord);
    }
    
    @Override
    public BigDecimal getSalesTargetCompletionRate(Long employeeId, LocalDateTime startTime, LocalDateTime endTime) {
        // 获取销售目标
        LambdaQueryWrapper<TaskTarget> targetWrapper = new LambdaQueryWrapper<>();
        targetWrapper.eq(TaskTarget::getExecutorId, employeeId)
                .between(TaskTarget::getStartTime, startTime, endTime);
        TaskTarget target = taskTargetMapper.selectOne(targetWrapper);
        
        if (target == null) {
            return BigDecimal.ZERO;
        }
        
        // 获取实际销售额
        LambdaQueryWrapper<SalesRecord> salesWrapper = new LambdaQueryWrapper<>();
        salesWrapper.eq(SalesRecord::getSalesperson, employeeId.toString())
                .between(SalesRecord::getSalesDate, startTime, endTime);
        
        List<SalesRecord> records = list(salesWrapper);
        BigDecimal actualAmount = records.stream()
                .map(SalesRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算完成率
        if (target.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return actualAmount.divide(target.getTargetAmount(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    @Override
    public PageInfo<SalesRecord> getSalesRecordPage(Integer current, Integer size, String salesOrderNo, String customerName, String customerManager, String salesperson) {
        // 设置分页参数
        PageHelper.startPage(current, size);
        
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(salesOrderNo)) {
            wrapper.like(SalesRecord::getSalesOrderNo, salesOrderNo);
        }
        
        if (StringUtils.hasText(customerName)) {
            wrapper.like(SalesRecord::getCustomerName, customerName);
        }
        
        if (StringUtils.hasText(customerManager)) {
            wrapper.like(SalesRecord::getCustomerManager, customerManager);
        }
        
        if (StringUtils.hasText(salesperson)) {
            wrapper.like(SalesRecord::getSalesperson, salesperson);
        }
        
        wrapper.eq(SalesRecord::getDeleted, 0)
               .orderByDesc(SalesRecord::getCreateTime);
        
        // 执行查询
        List<SalesRecord> list = list(wrapper);
        
        // 返回PageInfo
        return new PageInfo<>(list);
    }
}