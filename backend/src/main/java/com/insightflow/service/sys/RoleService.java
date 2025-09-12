package com.insightflow.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.sys.Role;
import com.insightflow.entity.sys.Menu;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色管理服务接口
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取角色列表
     *
     * @return 角色列表
     */
    List<Role> listRoles();

    /**
     * 获取角色菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<Menu> listRoleMenus(Long roleId);

    /**
     * 分配角色菜单
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     * @return 是否成功
     */
    boolean assignRoleMenus(Long roleId, List<Long> menuIds);

    @Transactional(rollbackFor = Exception.class)
    boolean saveBatch(List<Role> roleList);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean deleteRole(Long roleId);
}