package com.insightflow.service.impl.customer;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.customer.VisitRecord;
import com.insightflow.mapper.customer.VisitRecordMapper;
import com.insightflow.service.customer.VisitRecordService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class VisitRecordServiceImpl extends ServiceImpl<VisitRecordMapper, VisitRecord> implements VisitRecordService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(VisitRecord visitRecord) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(visitRecord);
        return super.save(visitRecord);
    }
    
    @Override
    public List<VisitRecord> getVisitRecordList(String customerManager, String terminalCode, LocalDateTime startTime, LocalDateTime endTime, Integer isDeal) {
        // 使用Mapper的关联查询方法获取完整信息
        return baseMapper.getVisitRecordListWithDetails(customerManager, terminalCode, startTime, endTime, isDeal);
    }
    
    @Override
    public PageInfo<VisitRecord> getVisitRecordPage(Integer current, Integer size, String customerManager, String terminalCode, LocalDateTime startTime, LocalDateTime endTime, Integer isDeal) {
        // 设置分页参数
        PageHelper.startPage(current, size);
        
        // 使用Mapper的关联查询方法获取完整信息
        List<VisitRecord> list = baseMapper.getVisitRecordListWithDetails(customerManager, terminalCode, startTime, endTime, isDeal);
        
        // 返回PageInfo
        return new PageInfo<>(list);
    }
} 