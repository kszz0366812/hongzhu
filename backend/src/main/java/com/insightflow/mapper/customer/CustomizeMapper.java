package com.insightflow.mapper.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author: sy
 * @CreateTime: 2025-06-11
 * @Description:
 * @Version: 1.0
 */
@Mapper
public interface CustomizeMapper extends BaseMapper<Object> {

    List<Map<String, Object>> getData(String sql);

    IPage<Object> getData(IPage<Object> page, String sql);
}
