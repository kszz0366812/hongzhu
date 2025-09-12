package com.insightflow.controller.employee;

import com.insightflow.entity.employee.Dept;
import com.insightflow.service.employee.DeptService;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理")
@RestController
    @RequestMapping("/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @Operation(summary = "获取部门树形结构")
    @RequestMapping("/tree")
    public Result<List<Dept>> listDeptTree() {
        List<Dept> deptTree = deptService.listDeptTree();
        return Result.success(deptTree);
    }

    @Operation(summary = "获取子部门ID列表")
    @RequestMapping("/children/{deptId}")
    public Result<List<Long>> listChildDeptIds(@PathVariable Long deptId) {
        List<Long> deptIds = deptService.listChildDeptIds(deptId);
        return Result.success(deptIds);
    }

    @Operation(summary = "删除部门")
    @RequestMapping("deleteDept/{deptId}")
    public Result<Boolean> deleteDept(@PathVariable Long deptId) {
        boolean success = deptService.deleteDept(deptId);
        return Result.success(success);
    }
} 