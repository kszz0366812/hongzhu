package com.insightflow.service.template;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.dto.ItfTemplateDTO;
import com.insightflow.entity.template.ItfTemplate;

import java.util.List;
import com.github.pagehelper.PageInfo;

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
     * 获取接口列表
     */
    List<ItfTemplate> getTemplateList(String tepName);

    /**
     * 分页查询接口模板
     * @param current 页码
     * @param size 每页大小
     * @param tepName 模板名称
     * @return 分页结果
     */
    PageInfo<ItfTemplate> pageTemplate(Integer current, Integer size, String tepName);
    
    /**
     * 批量保存接口模板信息
     * @param itfTemplateList 接口模板列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<ItfTemplate> itfTemplateList);
} 