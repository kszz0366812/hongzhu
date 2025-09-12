package com.insightflow.controller.sys;

import com.insightflow.entity.sys.Menu;
import com.insightflow.service.sys.MenuService;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/menu")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Operation(summary = "获取菜单树形结构")
    @RequestMapping("/tree")
    public Result<List<Menu>> listMenuTree(
            @RequestParam(value = "onlyEnabled", required = false, defaultValue = "false") Boolean onlyEnabled) {
        List<Menu> menuTree = menuService.listMenuTree(onlyEnabled);
        return Result.success(menuTree);
    }

    @Operation(summary = "获取用户菜单列表")
    @RequestMapping("/user/{userId}")
    public Result<List<Menu>> listUserMenus(@PathVariable Long userId) {
        List<Menu> menus = menuService.listUserMenus(userId);
        return Result.success(menus);
    }

    @Operation(summary = "获取用户权限列表")
    @RequestMapping("/perms/{userId}")
    public Result<List<String>> listUserPerms(@PathVariable Long userId) {
        List<String> perms = menuService.listUserPerms(userId);
        return Result.success(perms);
    }

    @Operation(summary = "删除菜单")
    @RequestMapping(value = "/deleteMenu/{menuId}")
    public Result<Boolean> deleteMenu(@PathVariable Long menuId) {
        boolean success = menuService.deleteMenu(menuId);
        return Result.success(success);
    }

    @Operation(summary = "创建菜单")
    @RequestMapping(value = "/createMenu")
    public Result<Boolean> createMenu(@RequestBody Menu menu) {
        boolean success = menuService.save(menu);
        return Result.success(success);
    }

    @Operation(summary = "更新菜单")
    @RequestMapping(value = "/updateMenu")
    public Result<Boolean> updateMenu(@RequestBody Menu menu) {
        boolean success = menuService.updateById(menu);
        return Result.success(success);
    }
} 