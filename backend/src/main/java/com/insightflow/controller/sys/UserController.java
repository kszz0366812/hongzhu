package com.insightflow.controller.sys;

import com.insightflow.service.sys.MenuService;
import com.insightflow.service.sys.RoleService;
import com.insightflow.vo.LoginInfoVO;
import com.insightflow.entity.sys.User;
import com.insightflow.service.sys.UserService;
import com.insightflow.common.util.JwtUtils;
import com.insightflow.common.util.Result;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService roleMenuService;

    @Autowired
    HttpServletRequest req;

    @Operation(summary = "用户登录")
    @RequestMapping("/login")
    public Result<String> login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        if (token == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(token);
    }

    @Operation(summary = "用户注册")
    @RequestMapping("/register")
    public Result<Boolean> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (!success) {
            return Result.error("用户名已存在");
        }
        return Result.success(success);
    }

    @Operation(summary = "修改密码")
    @RequestMapping("/change-password")
    public Result<Boolean> changePassword(
                                        @RequestParam String oldPassword,
                                        @RequestParam String newPassword) {
        if (!JwtUtils.validateToken(req.getHeader("Authorization").substring(7))) {
            return Result.error(HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
        }
        String username = JwtUtils.getLoginInfo().getUsername();
        boolean success = userService.changePassword(username, oldPassword, newPassword);
        return Result.success(success);
    }

    @Operation(summary = "获取用户登录信息")
    @RequestMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        if (!JwtUtils.validateToken(req.getHeader("Authorization").substring(7))) {
            return Result.error(HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
        }
        LoginInfoVO loginInfo = JwtUtils.getLoginInfo();
        if (loginInfo == null) {
            return Result.error(HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
        }
        //查询角色信息
        loginInfo.setRole(roleService.getById(loginInfo.getRoleId()));
        //查询菜单信息
        loginInfo.setMenus(roleMenuService.listUserMenus(loginInfo.getId()));
        return Result.success(loginInfo);
    }

    @Operation(summary = "用户退出登录")
    @RequestMapping("/logout")
    public Result<Boolean> logout() {
        // JWT无状态，前端只需删除token即可
        return Result.success(true);
    }

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<PageInfo<User>> getUserPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "真实姓名") @RequestParam(required = false) String realName,
            @Parameter(description = "手机号") @RequestParam(required = false) String mobile,
            @Parameter(description = "邮箱") @RequestParam(required = false) String email,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        PageInfo<User> userPage = userService.getUserPage(current, size, username, realName, mobile, email, status);
        return Result.success(userPage);
    }

    @Operation(summary = "获取用户列表（不分页）")
    @GetMapping("/list")
    public Result<List<User>> getAllUserList(
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "真实姓名") @RequestParam(required = false) String realName,
            @Parameter(description = "手机号") @RequestParam(required = false) String mobile,
            @Parameter(description = "邮箱") @RequestParam(required = false) String email,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        List<User> userList = userService.getUserList(username, realName, mobile, email, status);
        return Result.success(userList);
    }

    @Operation(summary = "根据ID获取用户详情")
    @RequestMapping("/getById/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @Operation(summary = "新增用户")
    @RequestMapping("/add")
    public Result<User> addUser(@RequestBody User user) {
        User newUser = userService.addUser(user);
        return  Result.success(newUser) ;
    }

    @Operation(summary = "更新用户")
    @RequestMapping("/update")
    public Result<String> updateUser(@RequestBody User user) {
        String token = userService.updateUser(user);
        return  Result.success(token);
    }

    @Operation(summary = "删除用户")
    @RequestMapping("/delete/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        return success ? Result.success(true) : Result.error("删除用户失败");
    }

    @Operation(summary = "重置用户密码")
    @RequestMapping("/reset-password/{id}")
    public Result<Boolean> resetPassword(@PathVariable Long id) {
        boolean success = userService.resetPassword(id);
        return success ? Result.success(true) : Result.error("重置密码失败");
    }

    @Operation(summary = "修改用户状态")
    @RequestMapping("/change-status/{id}")
    public Result<Boolean> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = userService.changeStatus(id, status);
        return success ? Result.success(true) : Result.error("修改状态失败");
    }

    @Operation(summary = "获取用户角色")
    @RequestMapping("/roles/{userId}")
    public Result<List<Long>> getUserRoles(@PathVariable Long userId) {
        List<Long> roleIds = userService.getUserRoles(userId);
        return Result.success(roleIds);
    }

    @Operation(summary = "分配用户角色")
    @RequestMapping("/assign-roles/{userId}")
    public Result<Boolean> assignUserRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        boolean success = userService.assignUserRoles(userId, roleIds);
        return Result.success(success);
    }
} 