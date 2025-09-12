package com.insightflow.mapper.template;

import com.insightflow.dto.DbTableDTO;
import com.insightflow.dto.TableColumnDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: sy
 * @CreateTime: 2025-06-10
 * @Description:
 * @Version: 1.0
 */
@Mapper
public interface TemplateMapper {

    List<DbTableDTO> getTableList(String comment);
    List<TableColumnDTO> getTable(String tableName);
}
