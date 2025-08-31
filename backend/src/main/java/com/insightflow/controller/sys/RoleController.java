package com.insightflow.controller.sys;

import com.insightflow.entity.sys.Menu;
import com.insightflow.entity.sys.Role;
import com.insightflow.service.sys.RoleService;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "获取角色列表")
    @RequestMapping("/list")
    public Result<List<Role>> listRoles() {
        List<Role> roles = roleService.listRoles();
        return Result.success(roles);
    }

    @Operation(summary = "新增角色")
    @RequestMapping(value = "/add")
    public Result<Boolean> addRole(@RequestBody Role role) {
        boolean success = roleService.save(role);
        return Result.success(success);
    }

    @Operation(summary = "更新角色")
    @RequestMapping(value = "/update")
    public Result<Boolean> updateRole(@RequestBody Role role) {
        boolean success = roleService.updateById(role);
        return Result.success(success);
    }

    @Operation(summary = "修改角色状态")
    @RequestMapping(value = "/status/{roleId}")
    public Result<Boolean> changeRoleStatus(@PathVariable Long roleId, @RequestBody Role role) {
        role.setId(roleId);
        boolean success = roleService.updateById(role);
        return Result.success(success);
    }

    @Operation(summary = "获取角色菜单列表")
    @RequestMapping("/listRoleMenus/{roleId}")
    public Result<List<Menu>> listRoleMenus(@PathVariable Long roleId) {
        List<Menu> menus = roleService.listRoleMenus(roleId);
        return Result.success(menus);
    }

    @Operation(summary = "分配角色菜单")
    @RequestMapping("/assignRoleMenus/{roleId}")
    public Result<Boolean> assignRoleMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        boolean success = roleService.assignRoleMenus(roleId, menuIds);
        return Result.success(success);
    }

    @Operation(summary = "删除角色")
    @RequestMapping("/delete/{roleId}")
    public Result<Boolean> deleteRole(@PathVariable Long roleId) {
        boolean success = roleService.deleteRole(roleId);
        return Result.success(success);
    }
} 