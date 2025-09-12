package com.insightflow.service.impl.common;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    public PageInfo<Object> getCustomizeSqlPage(String sql, Integer current, Integer size) {
        PageHelper.startPage(current, size);
        List<Map<String, Object>> list = customizeMapper.getData(sql);
        return new PageInfo<>(list);
    }

}
