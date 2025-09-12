package com.insightflow.service.task;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.task.TaskTarget;
import com.insightflow.dto.TaskTargetDTO;
import com.github.pagehelper.PageInfo;
import java.math.BigDecimal;
import java.util.List;

public interface TaskTargetService extends IService<TaskTarget> {
    
    PageInfo<TaskTarget> getTaskTargetPage(Integer page, Integer size, String keyword);
    
    List<TaskTargetDTO> getTaskTargetList(Long executorId,String taskName);
    
    List<TaskTargetDTO> getTaskTargetAchievement(Long executorId,String taskName);
    
    boolean save(TaskTarget taskTarget);
    
    TaskTarget update(TaskTarget taskTarget);
    
    TaskTarget findById(Long id);
    
    void updateAchievedAmount(Long id, BigDecimal achievedAmount);
} 