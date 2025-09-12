package com.insightflow.service.impl.customer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.customer.WholesalerInfo;
import com.insightflow.mapper.customer.WholesalerInfoMapper;
import com.insightflow.service.customer.WholesalerService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class WholesalerServiceImpl extends ServiceImpl<WholesalerInfoMapper, WholesalerInfo> implements WholesalerService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(WholesalerInfo wholesalerInfo) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(wholesalerInfo);
        return super.save(wholesalerInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<WholesalerInfo> wholesalerInfoList) {
        // 批量设置创建信息
        wholesalerInfoList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(wholesalerInfoList);
    }
    
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
    public PageInfo<WholesalerInfo> getWholesalerPage(Integer current, Integer size, String keyword, String level) {
        // 设置分页参数
        PageHelper.startPage(current, size);
        
        LambdaQueryWrapper<WholesalerInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询未删除的数据
        wrapper.eq(WholesalerInfo::getDeleted, 0);
        
        // 关键词搜索（批发商名称、编码、客户经理、联系人、联系电话）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(WholesalerInfo::getDealerName, keyword)
                .or()
                .like(WholesalerInfo::getDealerCode, keyword)
                .or()
                .like(WholesalerInfo::getCustomerManager, keyword)
                .or()
                .like(WholesalerInfo::getContactPerson, keyword)
                .or()
                .like(WholesalerInfo::getContactPhone, keyword)
            );
        }
        
        // 等级精确搜索
        if (StringUtils.hasText(level)) {
            wrapper.eq(WholesalerInfo::getLevel, level);
        }
        
        // 排序：按创建时间倒序
        wrapper.orderByDesc(WholesalerInfo::getCreateTime);
        
        // 执行查询
        List<WholesalerInfo> list = list(wrapper);
        
        // 返回PageInfo
        return new PageInfo<>(list);
    }
} 