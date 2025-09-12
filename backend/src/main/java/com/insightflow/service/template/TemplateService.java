package com.insightflow.service.template;

import com.insightflow.dto.DbTableDTO;

import java.util.List;

/**
 * @Author: sy
 * @CreateTime: 2025-06-10
 * @Description: 模版服务
 * @Version: 1.0
 */
public interface TemplateService {

    //获取数据表列表
    List<DbTableDTO> getTableList(String comment);
    //获取表详情
    DbTableDTO getTableDetail(String tableName);
}
