package com.insightflow.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.sys.Menu;
import com.insightflow.entity.sys.Role;
import com.insightflow.entity.sys.RoleMenu;
import com.insightflow.mapper.sys.RoleMapper;
import com.insightflow.service.sys.RoleService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Role> listRoles() {
        return list(new LambdaQueryWrapper<Role>()
                .eq(Role::getDeleted, 0)
                .orderByAsc(Role::getCreateTime));
    }

    @Override
    public List<Menu> listRoleMenus(Long roleId) {
        return roleMapper.selectRoleMenus(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoleMenus(Long roleId, List<Long> menuIds) {
        // 删除原有角色菜单关联
        roleMapper.deleteRoleMenus(roleId);
        // 批量插入新的角色菜单关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenu> roleMenus = menuIds.stream()
                    .map(menuId -> {
                        RoleMenu roleMenu = new RoleMenu();
                        roleMenu.setRoleId(roleId);
                        roleMenu.setMenuId(menuId);
                        return roleMenu;
                    })
                    .collect(Collectors.toList());
            return roleMapper.batchInsertRoleMenus(roleMenus) > 0;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Role role) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(role);
        return super.save(role);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<Role> roleList) {
        // 批量设置创建信息
        roleList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(roleList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        // 删除角色菜单关联
        roleMapper.deleteRoleMenus(roleId);
        // 删除角色
        return removeById(roleId);
    }
} 