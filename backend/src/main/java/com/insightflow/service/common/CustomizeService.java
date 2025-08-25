package com.insightflow.service.common;
import com.baomidou.mybatisplus.core.metadata.IPage;

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

      IPage<Object> getCustomizeSqlPage(String sql, Integer page, Integer size);
}
