package com.insightflow.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.sys.Menu;
import com.insightflow.mapper.sys.MenuMapper;
import com.insightflow.service.sys.MenuService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public boolean save(Menu entity) {
        // 自动设置创建人ID和时间
        EntityUtils.setCreateInfo(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(Menu entity) {
        // 自动设置更新时间
        EntityUtils.setUpdateInfo(entity);
        return super.updateById(entity);
    }

    @Override
    public List<Menu> listMenuTree(Boolean onlyEnabled) {
        // 构建查询条件
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<Menu>()
                .orderByAsc(Menu::getOrderNum);
        
        // 如果只查询启用的菜单，添加状态过滤条件
        if (Boolean.TRUE.equals(onlyEnabled)) {
            wrapper.eq(Menu::getStatus, 1);
        }
        
        // 获取菜单列表 - 使用逻辑删除，自动过滤已删除的菜单
        List<Menu> allMenus = list(wrapper);
        
        // 构建树形结构
        List<Menu> rootMenus = allMenus.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0)
                .collect(Collectors.toList());
        
        rootMenus.forEach(root -> buildMenuTree(root, allMenus));
        
        return rootMenus;
    }

    private void buildMenuTree(Menu parent, List<Menu> allMenus) {
        List<Menu> children = allMenus.stream()
                .filter(menu -> parent.getId().equals(menu.getParentId()))
                .collect(Collectors.toList());
        
        if (!children.isEmpty()) {
            parent.setChildren(children);
            children.forEach(child -> buildMenuTree(child, allMenus));
        }
    }

    @Override
    public List<Menu> listUserMenus(Long userId) {
        // 获取用户角色关联的菜单（已过滤禁用状态）
        List<Menu> menus = baseMapper.selectUserMenus(userId);
        // 构建树形结构
        List<Menu> rootMenus = menus.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0)
                .collect(Collectors.toList());
        
        rootMenus.forEach(root -> buildMenuTree(root, menus));
        
        return rootMenus;
    }

    @Override
    public List<String> listUserPerms(Long userId) {
        return baseMapper.selectUserPerms(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenu(Long menuId) {
        // 获取所有子菜单ID
        List<Long> menuIds = new ArrayList<>();
        menuIds.add(menuId);
        
        List<Menu> children = list(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, menuId));
        
        if (!children.isEmpty()) {
            children.forEach(child -> menuIds.addAll(getChildMenuIds(child.getId())));
        }
        
        // 删除菜单 - 使用逻辑删除，自动设置 deleted 字段为 1
        return removeByIds(menuIds);
    }

    private List<Long> getChildMenuIds(Long menuId) {
        List<Long> menuIds = new ArrayList<>();
        menuIds.add(menuId);
        
        List<Menu> children = list(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, menuId));
        
        if (!children.isEmpty()) {
            children.forEach(child -> menuIds.addAll(getChildMenuIds(child.getId())));
        }
        
        return menuIds;
    }
} 