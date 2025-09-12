package com.insightflow.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.sys.Menu;

import java.util.List;

/**
 * 菜单管理服务接口
 */
public interface MenuService extends IService<Menu> {

    /**
     * 获取菜单树形结构
     * @param onlyEnabled 是否只查询启用的菜单，true-只查询启用的，false-查询所有
     * @return 菜单树
     */
    List<Menu> listMenuTree(Boolean onlyEnabled);

    /**
     * 获取用户菜单列表
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Menu> listUserMenus(Long userId);

    /**
     * 获取用户权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> listUserPerms(Long userId);

    /**
     * 删除菜单
     * @param menuId 菜单ID
     * @return 是否成功
     */
    boolean deleteMenu(Long menuId);
} 