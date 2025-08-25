package com.insightflow.service.impl.task;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.task.TaskTarget;
import com.insightflow.mapper.task.TaskTargetMapper;
import com.insightflow.dto.TaskTargetDTO;
import com.insightflow.service.task.TaskTargetService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TaskTargetServiceImpl extends ServiceImpl<TaskTargetMapper, TaskTarget> implements TaskTargetService {

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
        taskTarget.setDeleted(0);
        taskTarget.setAchievedAmount(BigDecimal.ZERO);
        return super.save(taskTarget);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        TaskTarget taskTarget = getById(id);
        if (taskTarget != null) {
            taskTarget.setDeleted(1);
            // 自动设置更新时间
            EntityUtils.setUpdateInfo(taskTarget);
            updateById(taskTarget);
        }
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