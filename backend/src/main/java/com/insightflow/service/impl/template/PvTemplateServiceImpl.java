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

@Service
public class PvTemplateServiceImpl extends ServiceImpl<PvTemplateMapper, PvTemplate> implements PvTemplateService {

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
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        PvTemplate entity = getById(id);
        if (entity == null) throw new RuntimeException("报表模板不存在");
        entity.setDeleted(1);
        // 自动设置更新时间
        EntityUtils.setUpdateInfo(entity);
        updateById(entity);
    }

    @Override
    public Page<PvTemplate> page(int pageNum, int pageSize, String tepName) {
        LambdaQueryWrapper<PvTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PvTemplate::getDeleted, 0);
        if (StringUtils.hasText(tepName)) {
            wrapper.like(PvTemplate::getTepName, tepName);
        }
        wrapper.orderByDesc(PvTemplate::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
} 