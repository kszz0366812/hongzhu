package com.insightflow.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.sys.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询用户菜单列表
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Menu> selectUserMenus(@Param("userId") Long userId);

    /**
     * 查询用户权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectUserPerms(@Param("userId") Long userId);
} 