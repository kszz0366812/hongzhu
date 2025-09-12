package com.insightflow.service.impl.customer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insightflow.entity.customer.TerminalInfo;
import com.insightflow.mapper.customer.TerminalInfoMapper;
import com.insightflow.service.customer.TerminalInfoService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TerminalInfoServiceImpl extends ServiceImpl<TerminalInfoMapper, TerminalInfo> implements TerminalInfoService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(TerminalInfo terminalInfo) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(terminalInfo);
        return super.save(terminalInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<TerminalInfo> terminalInfoList) {
        // 批量设置创建信息
        terminalInfoList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(terminalInfoList);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(TerminalInfo terminalInfo) {
        if (terminalInfo.getId() == null) {
            // 新增 - 自动设置创建人ID和时间
            EntityUtils.setCreateInfo(terminalInfo);
            save(terminalInfo);
        } else {
            // 更新 - 自动设置更新时间
            EntityUtils.setUpdateInfo(terminalInfo);
            updateById(terminalInfo);
        }
    }

    @Override
    public List<TerminalInfo> getTerminalList(String terminalName, String terminalType, String customerManager) {
        LambdaQueryWrapper<TerminalInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询未删除的数据
        wrapper.eq(TerminalInfo::getDeleted, 0);
        
        // 终端名称模糊搜索
        if (StringUtils.hasText(terminalName)) {
            wrapper.like(TerminalInfo::getTerminalName, terminalName);
        }
        
        // 终端类型模糊搜索
        if (StringUtils.hasText(terminalType)) {
            wrapper.like(TerminalInfo::getTerminalType, terminalType);
        }
        
        // 客户经理模糊搜索
        if (StringUtils.hasText(customerManager)) {
            wrapper.like(TerminalInfo::getCustomerManager, customerManager);
        }
        
        // 排序：按创建时间倒序
        wrapper.orderByDesc(TerminalInfo::getCreateTime);
        
        return list(wrapper);
    }

    @Override
    public PageInfo<TerminalInfo> getTerminalPage(Integer current, Integer size, String keyword, Integer isScheduled) {
        // 设置分页参数
        PageHelper.startPage(current, size);
        
        LambdaQueryWrapper<TerminalInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询未删除的数据
        wrapper.eq(TerminalInfo::getDeleted, 0);
        
        // 关键词搜索（终端名称、终端编码、客户经理、标签、终端类型）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(TerminalInfo::getTerminalName, keyword)
                .or()
                .like(TerminalInfo::getTerminalCode, keyword)
                .or()
                .like(TerminalInfo::getCustomerManager, keyword)
                .or()
                .like(TerminalInfo::getTags, keyword)
                .or()
                .like(TerminalInfo::getTerminalType, keyword)
            );
        }
        
        // 是否排线精确搜索
        if (isScheduled != null) {
            wrapper.eq(TerminalInfo::getIsScheduled, isScheduled);
        }
        
        // 排序：按创建时间倒序
        wrapper.orderByDesc(TerminalInfo::getCreateTime);
        
        // 执行查询
        List<TerminalInfo> list = list(wrapper);
        
        // 返回PageInfo
        return new PageInfo<>(list);
    }
} 