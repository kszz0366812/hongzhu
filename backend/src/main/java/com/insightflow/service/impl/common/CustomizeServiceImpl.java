package com.insightflow.service.impl.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insightflow.mapper.customer.CustomizeMapper;
import com.insightflow.service.common.CustomizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * @Author: sy
 * @CreateTime: 2025-06-11
 * @Description: 自定义接口服务类
 * @Version: 1.0
 */
@Service
public class CustomizeServiceImpl implements CustomizeService {

    @Autowired
    private CustomizeMapper customizeMapper;

    @Override
    public List<Map<String, Object>> getCustomizeSql(String sql) {
        return customizeMapper.getData(sql);
    }

    @Override
    public IPage<Object> getCustomizeSqlPage(String sql, Integer currentPage, Integer size) {
        IPage<Object> page = new Page<>(currentPage, size);
        return  customizeMapper.getData(page, sql);
    }

}
