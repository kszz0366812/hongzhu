package com.insightflow.service.template;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.dto.PvTemplateDTO;
import com.insightflow.entity.template.PvTemplate;

public interface PvTemplateService extends IService<PvTemplate> {
    void create(PvTemplateDTO dto);
    void update(PvTemplateDTO dto);
    Page<PvTemplate> page(int pageNum, int pageSize, String tepName);
} 