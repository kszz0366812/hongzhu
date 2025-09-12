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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ItfTemplateServiceImpl extends ServiceImpl<ItfTemplateMapper, ItfTemplate> implements ItfTemplateService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ItfTemplate itfTemplate) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(itfTemplate);
        return super.save(itfTemplate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<ItfTemplate> itfTemplateList) {
        // 批量设置创建信息
        itfTemplateList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(itfTemplateList);
    }

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
    public PageInfo<ItfTemplate> pageTemplate(Integer current, Integer size, String tepName) {
        // 设置分页参数
        PageHelper.startPage(current, size);
        
        LambdaQueryWrapper<ItfTemplate> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询未删除的数据
        wrapper.eq(ItfTemplate::getDeleted, 0);
        
        if (StringUtils.hasText(tepName)) {
            wrapper.like(ItfTemplate::getTepName, tepName);
        }
        
        wrapper.orderByDesc(ItfTemplate::getCreateTime);
        
        // 执行查询
        List<ItfTemplate> list = list(wrapper);
        
        // 返回PageInfo
        return new PageInfo<>(list);
    }
} 