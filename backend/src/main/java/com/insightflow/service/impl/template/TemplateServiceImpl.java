package com.insightflow.service.impl.template;

import com.insightflow.dto.DbTableDTO;
import com.insightflow.dto.TableColumnDTO;
import com.insightflow.mapper.template.TemplateMapper;
import com.insightflow.service.template.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: sy
 * @CreateTime: 2025-06-10
 * @Description: 模版服务类
 * @Version: 1.0
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    @Override
    public List<DbTableDTO> getTableList(String comment) {
        return templateMapper.getTableList(comment);
    }

    @Override
    public DbTableDTO getTableDetail(String tableName) {
        List<TableColumnDTO> columns = templateMapper.getTable(tableName);
        DbTableDTO dbTable = new DbTableDTO();
        dbTable.setTableName(tableName);
        dbTable.setColumns(columns);
        return dbTable;
    }
}
