package com.insightflow.service.template;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.insightflow.dto.PvTemplateDTO;
import com.insightflow.entity.template.PvTemplate;

import java.util.List;

public interface PvTemplateService extends IService<PvTemplate> {
    void create(PvTemplateDTO dto);
    void update(PvTemplateDTO dto);
    /**
     * 分页查询模板
     * @param current 页码
     * @param size 每页大小
     * @param tepName 模板名称
     * @return 分页结果
     */
    PageInfo<PvTemplate> page(int current, int size, String tepName);
    
    /**
     * 批量保存可视化模板信息
     * @param pvTemplateList 可视化模板列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<PvTemplate> pvTemplateList);
} 