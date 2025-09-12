package com.insightflow.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.sys.Menu;
import com.insightflow.entity.sys.Role;
import com.insightflow.entity.sys.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查询角色菜单列表
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<Menu> selectRoleMenus(@Param("roleId") Long roleId);

    /**
     * 删除角色菜单关联
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteRoleMenus(@Param("roleId") Long roleId);

    /**
     * 批量插入角色菜单关联
     * @param roleMenus 角色菜单关联列表
     * @return 影响行数
     */
    int batchInsertRoleMenus(@Param("list") List<RoleMenu> roleMenus);
} 