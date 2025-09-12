package com.insightflow.controller.employee;

import com.github.pagehelper.PageInfo;
import com.insightflow.common.Result;
import com.insightflow.entity.employee.EmployeeInfo;
import com.insightflow.service.employee.EmployeeInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "员工管理")
@RestController
    @RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeInfoController {

    private final EmployeeInfoService employeeInfoService;

    

    @Operation(summary = "获取员工列表")
    @RequestMapping("/list")
    public Result<List<EmployeeInfo>> list(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 支持按员工编号或姓名模糊搜索
            return Result.success(employeeInfoService.searchEmployees(keyword.trim()));
        }
        return Result.success(employeeInfoService.list());
    }
    
    @Operation(summary = "分页查询员工列表")
    @RequestMapping("/page")
    public Result<PageInfo<EmployeeInfo>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词（支持员工编号、姓名、职位、区域、职级等字段的模糊搜索）") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        return Result.success(employeeInfoService.getEmployeePage(current, size, keyword, status));
    }

    @Operation(summary = "获取员工详情")
    @RequestMapping("getById/{id}")
    public Result<EmployeeInfo> getById(@PathVariable Long id) {
        return Result.success(employeeInfoService.getById(id));
    }

    @Operation(summary = "新增员工")
    @RequestMapping("save")
    public Result<Void> save(@RequestBody EmployeeInfo employeeInfo) {
        employeeInfoService.save(employeeInfo);
        return Result.success();
    }

    @Operation(summary = "修改员工")
    @RequestMapping
    public Result<Void> update(@RequestBody EmployeeInfo employeeInfo) {
        employeeInfoService.updateById(employeeInfo);
        return Result.success();
    }

    @Operation(summary = "删除员工")
    @RequestMapping("delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeInfoService.removeById(id);
        return Result.success();
    }
} 