package com.insightflow.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.sys.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
} 