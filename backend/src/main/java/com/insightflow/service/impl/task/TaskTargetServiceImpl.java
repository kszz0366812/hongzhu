package com.insightflow.service.impl.task;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.insightflow.entity.task.TaskTarget;
import com.insightflow.mapper.task.TaskTargetMapper;
import com.insightflow.dto.TaskTargetDTO;
import com.insightflow.service.task.TaskTargetService;
import com.insightflow.common.util.EntityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TaskTargetServiceImpl extends ServiceImpl<TaskTargetMapper, TaskTarget> implements TaskTargetService {

    @Override
    public PageInfo<TaskTarget> getTaskTargetPage(Integer page, Integer size, String keyword) {
        PageHelper.startPage(page, size);
        LambdaQueryWrapper<TaskTarget> wrapper = new LambdaQueryWrapper<>();
        
        // 添加模糊查询条件
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(TaskTarget::getExecutor, keyword)
                .or()
                .like(TaskTarget::getTaskName, keyword)
            );
        }
        
        // 只查询未删除的记录
        wrapper.eq(TaskTarget::getDeleted, 0);
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(TaskTarget::getCreateTime);
        
        List<TaskTarget> list = list(wrapper);
        return new PageInfo<>(list);
    }

    @Override
    public List<TaskTargetDTO> getTaskTargetList(Long executorId,String taskName) {
        return baseMapper.getTaskTargetList(executorId, taskName);
    }

    @Override
    public List<TaskTargetDTO> getTaskTargetAchievement(Long executorId, String taskName) {
        return baseMapper.getTaskTargetAchievement(executorId, taskName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(TaskTarget taskTarget) {
        // 自动设置创建人ID和时间
        EntityUtils.setCreateInfo(taskTarget);
        return super.save(taskTarget);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskTarget update(TaskTarget taskTarget) {
        // 自动设置更新时间
        EntityUtils.setUpdateInfo(taskTarget);
        updateById(taskTarget);
        return taskTarget;
    }

    @Override
    public TaskTarget findById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAchievedAmount(Long id, BigDecimal achievedAmount) {
        baseMapper.updateAchievedAmount(id, achievedAmount);
    }
} 