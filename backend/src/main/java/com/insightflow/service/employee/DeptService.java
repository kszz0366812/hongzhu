package com.insightflow.service.employee;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.employee.Dept;

import java.util.List;

public interface DeptService extends IService<Dept> {

    /**
     * 获取部门树形结构
     *
     * @return 部门树形结构
     */
    List<Dept> listDeptTree();

    /**
     * 获取子部门ID列表
     *
     * @param deptId 部门ID
     * @return 子部门ID列表
     */
    List<Long> listChildDeptIds(Long deptId);

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     * @return 是否成功
     */
    boolean deleteDept(Long deptId);

}