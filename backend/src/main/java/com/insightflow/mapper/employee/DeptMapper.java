package com.insightflow.mapper.employee;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.employee.Dept;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeptMapper extends BaseMapper<Dept> {
} 