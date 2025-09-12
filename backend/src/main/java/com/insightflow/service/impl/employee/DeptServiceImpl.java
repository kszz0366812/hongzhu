package com.insightflow.service.impl.employee;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.employee.Dept;
import com.insightflow.mapper.employee.DeptMapper;
import com.insightflow.service.employee.DeptService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Dept dept) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(dept);
        return super.save(dept);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<Dept> deptList) {
        // 批量设置创建信息
        deptList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(deptList);
    }

    @Override
    public List<Dept> listDeptTree() {
        // 获取所有部门
        List<Dept> allDepts = list(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getDeleted, 0)
                .orderByAsc(Dept::getOrderNum));
        
        // 构建树形结构
        List<Dept> rootDepts = allDepts.stream()
                .filter(dept -> dept.getParentId() == null || dept.getParentId() == 0)
                .collect(Collectors.toList());
        
        rootDepts.forEach(root -> buildDeptTree(root, allDepts));
        
        return rootDepts;
    }

    private void buildDeptTree(Dept parent, List<Dept> allDepts) {
        List<Dept> children = allDepts.stream()
                .filter(dept -> parent.getId().equals(dept.getParentId()))
                .collect(Collectors.toList());
        
        if (!children.isEmpty()) {
            parent.setChildren(children);
            children.forEach(child -> buildDeptTree(child, allDepts));
        }
    }

    @Override
    public List<Long> listChildDeptIds(Long deptId) {
        List<Long> deptIds = new ArrayList<>();
        deptIds.add(deptId);
        
        List<Dept> children = list(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getParentId, deptId)
                .eq(Dept::getDeleted, 0));
        
        if (!children.isEmpty()) {
            children.forEach(child -> deptIds.addAll(listChildDeptIds(child.getId())));
        }
        
        return deptIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDept(Long deptId) {
        // 获取所有子部门ID
        List<Long> deptIds = listChildDeptIds(deptId);
        // 删除部门
        return removeByIds(deptIds);
    }
} 