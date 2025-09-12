package com.insightflow.service.impl.employee;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insightflow.entity.employee.EmployeeInfo;
import com.insightflow.mapper.employee.EmployeeInfoMapper;
import com.insightflow.service.employee.EmployeeInfoService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeInfoServiceImpl extends ServiceImpl<EmployeeInfoMapper, EmployeeInfo> implements EmployeeInfoService {

    @Override
    public List<EmployeeInfo> getByRegion(String regionLevel1, String regionLevel2, String regionLevel3) {
        LambdaQueryWrapper<EmployeeInfo> wrapper = new LambdaQueryWrapper<>();
        if (regionLevel1 != null) {
            wrapper.eq(EmployeeInfo::getRegionLevel1, regionLevel1);
        }
        if (regionLevel2 != null) {
            wrapper.eq(EmployeeInfo::getRegionLevel2, regionLevel2);
        }
        if (regionLevel3 != null) {
            wrapper.eq(EmployeeInfo::getRegionLevel3, regionLevel3);
        }
        return list(wrapper);
    }

    @Override
    public List<EmployeeInfo> getByPosition(String position) {
        LambdaQueryWrapper<EmployeeInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeInfo::getPosition, position);
        return list(wrapper);
    }

    @Override
    public List<EmployeeInfo> getByChannel(String channel) {
        LambdaQueryWrapper<EmployeeInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeInfo::getChannel, channel);
        return list(wrapper);
    }

    @Override
    public List<EmployeeInfo> getByDirectLeader(Long leaderId) {
        LambdaQueryWrapper<EmployeeInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeInfo::getDirectLeaderId, leaderId);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(EmployeeInfo employeeInfo) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(employeeInfo);
        return super.save(employeeInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<EmployeeInfo> employeeInfoList) {
        // 批量设置创建信息
        employeeInfoList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(employeeInfoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        EmployeeInfo employeeInfo = getById(id);
        if (employeeInfo != null) {
            employeeInfo.setStatus(status);
            return updateById(employeeInfo);
        }
        return false;
    }

    @Override
    public List<EmployeeInfo> getByJoinDateRange(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<EmployeeInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(EmployeeInfo::getJoinDate, startDate, endDate);
        return list(wrapper);
    }

    @Override
    public List<EmployeeInfo> getByRank(String rank) {
        LambdaQueryWrapper<EmployeeInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeInfo::getLevels, rank);
        return list(wrapper);
    }

    @Override
    public List<EmployeeInfo> searchEmployees(String keyword) {
        LambdaQueryWrapper<EmployeeInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
            .like(EmployeeInfo::getEmployeeCode, keyword)
            .or()
            .like(EmployeeInfo::getName, keyword)
        );
        return list(wrapper);
    }
    
    @Override
    public PageInfo<EmployeeInfo> getEmployeePage(Integer page, Integer size, String keyword, Integer status) {
        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);
        
        LambdaQueryWrapper<EmployeeInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词模糊搜索（支持员工编号、姓名、职位、区域、职级等字段）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(EmployeeInfo::getEmployeeCode, keyword)
                .or()
                .like(EmployeeInfo::getName, keyword)
                .or()
                .like(EmployeeInfo::getPosition, keyword)
                .or()
                .like(EmployeeInfo::getRegionLevel1, keyword)
                .or()
                .like(EmployeeInfo::getRegionLevel2, keyword)
                .or()
                .like(EmployeeInfo::getRegionLevel3, keyword)
                .or()
                .like(EmployeeInfo::getLevels, keyword)
                .or()
                .like(EmployeeInfo::getChannel, keyword)
            );
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(EmployeeInfo::getStatus, status);
        }
        
        // 排序：按创建时间倒序
        wrapper.orderByAsc(EmployeeInfo::getCreateTime);

        // 执行查询
        List<EmployeeInfo> list = list(wrapper);

        return new PageInfo<>( list);
    }
} 