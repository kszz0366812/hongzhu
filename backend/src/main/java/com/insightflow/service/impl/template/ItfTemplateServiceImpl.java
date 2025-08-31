package com.insightflow.service.impl.template;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.dto.ItfTemplateDTO;
import com.insightflow.entity.template.ItfTemplate;
import com.insightflow.mapper.template.ItfTemplateMapper;
import com.insightflow.service.template.ItfTemplateService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
public class ItfTemplateServiceImpl extends ServiceImpl<ItfTemplateMapper, ItfTemplate> implements ItfTemplateService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTemplate(ItfTemplateDTO dto) {
        ItfTemplate template = new ItfTemplate();
        BeanUtils.copyProperties(dto, template);
        // 自动设置创建人ID和时间
        EntityUtils.setCreateInfo(template);
        template.setDeleted(0);
        save(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(ItfTemplateDTO dto) {
        ItfTemplate template = getById(dto.getId());
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        BeanUtils.copyProperties(dto, template);
        // 自动设置更新时间
        EntityUtils.setUpdateInfo(template);
        updateById(template);
    }

    @Override
    public List<ItfTemplate> getTemplateList(String tepName) {
        LambdaQueryWrapper<ItfTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItfTemplate::getDeleted, 0);
        if (StringUtils.hasText(tepName)) {
            wrapper.like(ItfTemplate::getTepName, tepName);
        }
        wrapper.orderByDesc(ItfTemplate::getCreateTime);
        return list(wrapper);
    }

    @Override
    public Page<ItfTemplate> pageTemplate(Integer pageNum, Integer pageSize, String tepName) {
        LambdaQueryWrapper<ItfTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItfTemplate::getDeleted, 0);
        if (StringUtils.hasText(tepName)) {
            wrapper.like(ItfTemplate::getTepName, tepName);
        }
        wrapper.orderByDesc(ItfTemplate::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
} 