package com.insightflow.service.template;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.dto.ItfTemplateDTO;
import com.insightflow.entity.template.ItfTemplate;

import java.util.List;

public interface ItfTemplateService extends IService<ItfTemplate> {
    
    /**
     * 创建接口模板
     */
    void createTemplate(ItfTemplateDTO dto);
    
    /**
     * 更新接口模板
     */
    void updateTemplate(ItfTemplateDTO dto);
    
    /**
     * 删除接口模板
     */
    void deleteTemplate(Integer id);

    /**
     * 获取接口列表
     */
    List<ItfTemplate> getTemplateList(String tepName);

    /**
     * 分页查询接口模板
     */
    Page<ItfTemplate> pageTemplate(Integer pageNum, Integer pageSize, String tepName);
} 