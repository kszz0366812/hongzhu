package com.insightflow.service.common;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author: sy
 * @CreateTime: 2025-06-11
 * @Description: 自定义接口服务
 * @Version: 1.0
 */
public interface CustomizeService {

      List<Map<String, Object>> getCustomizeSql(String sql);

      PageInfo<Object> getCustomizeSqlPage(String sql, Integer current, Integer size);
}
