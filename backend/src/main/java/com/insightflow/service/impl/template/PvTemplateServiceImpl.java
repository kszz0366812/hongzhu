package com.insightflow.service.impl.template;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.dto.PvTemplateDTO;
import com.insightflow.entity.template.PvTemplate;
import com.insightflow.mapper.template.PvTemplateMapper;
import com.insightflow.service.template.PvTemplateService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class PvTemplateServiceImpl extends ServiceImpl<PvTemplateMapper, PvTemplate> implements PvTemplateService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PvTemplate pvTemplate) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(pvTemplate);
        return super.save(pvTemplate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<PvTemplate> pvTemplateList) {
        // 批量设置创建信息
        pvTemplateList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(pvTemplateList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(PvTemplateDTO dto) {
        PvTemplate entity = new PvTemplate();
        BeanUtils.copyProperties(dto, entity);
        // 自动设置创建人ID和时间
        EntityUtils.setCreateInfo(entity);
        entity.setDeleted(0);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PvTemplateDTO dto) {
        PvTemplate entity = getById(dto.getId());
        if (entity == null) throw new RuntimeException("报表模板不存在");
        BeanUtils.copyProperties(dto, entity);
        // 自动设置更新时间
        EntityUtils.setUpdateInfo(entity);
        updateById(entity);
    }

    @Override
    public PageInfo<PvTemplate> page(int current, int size, String tepName) {
        // 设置分页参数
        PageHelper.startPage(current, size);
        
        LambdaQueryWrapper<PvTemplate> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询未删除的数据
        wrapper.eq(PvTemplate::getDeleted, 0);
        
        if (StringUtils.hasText(tepName)) {
            wrapper.like(PvTemplate::getTepName, tepName);
        }
        
        wrapper.orderByDesc(PvTemplate::getCreateTime);
        
        // 执行查询
        List<PvTemplate> list = list(wrapper);
        
        // 返回PageInfo
        return new PageInfo<>(list);
    }
} 