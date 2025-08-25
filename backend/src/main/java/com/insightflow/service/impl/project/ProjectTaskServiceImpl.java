package com.insightflow.service.impl.project;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.common.util.JwtUtils;
import com.insightflow.dto.ProjectTaskDTO;
import com.insightflow.entity.project.ProjectTask;
import com.insightflow.entity.project.Project;
import com.insightflow.entity.employee.EmployeeInfo;
import com.insightflow.mapper.project.ProjectTaskMapper;
import com.insightflow.mapper.project.ProjectMapper;
import com.insightflow.mapper.employee.EmployeeInfoMapper;
import com.insightflow.service.project.ProjectTaskService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectTaskServiceImpl extends ServiceImpl<ProjectTaskMapper, ProjectTask> implements ProjectTaskService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private EmployeeInfoMapper employeeInfoMapper;

    @Override
    public IPage<ProjectTask> getTaskList(Integer page, Integer size, String type, String status,String assignerId,String assigneeId,  Long projectId) {
        Page<ProjectTask> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ProjectTask> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(ProjectTask::getStatus, status);
        }
        
        if (projectId != null) {
            wrapper.eq(ProjectTask::getProjectId, projectId);
        }

        if(assignerId !=null){
            wrapper.eq(ProjectTask::getAssignerId, assignerId);
        }

        if(assigneeId !=null){
            wrapper.eq(ProjectTask::getAssigneeId, assigneeId);
        }
        
        wrapper.orderByDesc(ProjectTask::getCreateTime);
        
        return this.page(pageParam, wrapper);
    }

    @Override
    public IPage<ProjectTaskDTO> getTaskListWithNames(Integer page, Integer size, String type, String status,String assignerId,String assigneeId,  Long projectId) {
        // 先获取基础的任务列表
        IPage<ProjectTask> taskPage = getTaskList(page, size, type, status,assignerId,assigneeId, projectId);
        
        // 转换为 DTO 并填充名称信息
        List<ProjectTaskDTO> dtoList = taskPage.getRecords().stream().map(task -> {
            ProjectTaskDTO dto = new ProjectTaskDTO();
            BeanUtils.copyProperties(task, dto);

            // 填充项目名称
            if (task.getProjectId() != null) {
                Project project = projectMapper.selectById(task.getProjectId());
                if (project != null) {
                    dto.setProjectName(project.getName());
                }
            }
            
            // 填充负责人姓名
            if (task.getAssigneeId() != null) {
                EmployeeInfo assignee = employeeInfoMapper.selectById(task.getAssigneeId());
                if (assignee != null) {
                    dto.setAssigneeName(assignee.getName());
                }
            }
            
            // 填充指派者姓名
            if (task.getAssignerId() != null) {
                EmployeeInfo assigner = employeeInfoMapper.selectById(task.getAssignerId());
                if (assigner != null) {
                    dto.setAssignerName(assigner.getName());
                }
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        // 创建新的分页结果
        Page<ProjectTaskDTO> resultPage = new Page<>(page, size);
        resultPage.setRecords(dtoList);
        resultPage.setTotal(taskPage.getTotal());
        resultPage.setCurrent(taskPage.getCurrent());
        resultPage.setSize(taskPage.getSize());
        
        return resultPage;
    }

    @Override
    public Long createTask(ProjectTaskDTO taskDTO) {
        ProjectTask task = new ProjectTask();
        BeanUtils.copyProperties(taskDTO, task);

        task.setAssignerId(JwtUtils.getLoginInfo().getEmployeeId());
        
        // 自动设置创建人ID和时间
        EntityUtils.setCreateInfo(task);
        
        this.save(task);
        return task.getId();
    }

    @Override
    public boolean updateTask(ProjectTaskDTO taskDTO) {
        ProjectTask task = new ProjectTask();
        BeanUtils.copyProperties(taskDTO, task);
        
        // 自动设置更新时间
        EntityUtils.setUpdateInfo(task);
        
        return this.updateById(task);
    }

    @Override
    public boolean deleteTask(Long id) {
        return this.removeById(id);
    }

    @Override
    public Object getTaskStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总项目任务数
        long totalTasks = this.count();
        statistics.put("totalTasks", totalTasks);
        
        // 各状态项目任务数
        long ongoingTasks = this.count(new LambdaQueryWrapper<ProjectTask>().eq(ProjectTask::getStatus, "ONGOING"));
        long completedTasks = this.count(new LambdaQueryWrapper<ProjectTask>().eq(ProjectTask::getStatus, "COMPLETED"));
        long overdueTasks = this.count(new LambdaQueryWrapper<ProjectTask>().eq(ProjectTask::getStatus, "OVERDUE"));
        
        statistics.put("ongoingTasks", ongoingTasks);
        statistics.put("completedTasks", completedTasks);
        statistics.put("overdueTasks", overdueTasks);
        
        return statistics;
    }
} 