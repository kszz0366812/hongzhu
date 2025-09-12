package com.insightflow.service.impl.project;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectTaskServiceImpl extends ServiceImpl<ProjectTaskMapper, ProjectTask> implements ProjectTaskService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private EmployeeInfoMapper employeeInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ProjectTask projectTask) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(projectTask);
        return super.save(projectTask);
    }

   
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<ProjectTask> projectTaskList) {
        // 批量设置创建信息
        projectTaskList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(projectTaskList);
    }

    @Override
    public PageInfo<ProjectTask> getTaskList(Integer page, Integer size, String type, String status, String assignerId, String assigneeId, Long projectId) {
        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);
        
        LambdaQueryWrapper<ProjectTask> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(ProjectTask::getStatus, status);
        }
        
        if (projectId != null) {
            wrapper.eq(ProjectTask::getProjectId, projectId);
        }

        if (assignerId != null) {
            wrapper.eq(ProjectTask::getAssignerId, assignerId);
        }

        if (assigneeId != null) {
            wrapper.eq(ProjectTask::getAssigneeId, assigneeId);
        }
        
        wrapper.orderByDesc(ProjectTask::getCreateTime);
        
        List<ProjectTask> taskList = list(wrapper);
        return new PageInfo<>(taskList);
    }

    @Override
    public PageInfo<ProjectTaskDTO> getTaskListWithNames(Integer page, Integer size, String type, String status, String assignerId, String assigneeId, Long projectId) {
        // 先获取基础的任务分页数据
        PageInfo<ProjectTask> taskPage = getTaskList(page, size, type, status, assignerId, assigneeId, projectId);
        
        // 转换为 DTO 并填充名称信息
        List<ProjectTaskDTO> dtoList = taskPage.getList().stream().map(task -> {
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
        
        // 创建新的PageInfo对象
        PageInfo<ProjectTaskDTO> resultPage = new PageInfo<>();
        resultPage.setList(dtoList);
        resultPage.setTotal(taskPage.getTotal());
        resultPage.setPageSize(taskPage.getPageSize());
        resultPage.setPageNum(taskPage.getPageNum());
        resultPage.setPages(taskPage.getPages());
        
        return resultPage;
    }

    @Override
    public List<ProjectTask> getTaskList(String type, String status, String assignerId, String assigneeId, Long projectId) {
        LambdaQueryWrapper<ProjectTask> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(ProjectTask::getStatus, status);
        }
        
        if (projectId != null) {
            wrapper.eq(ProjectTask::getProjectId, projectId);
        }

        if (assignerId != null) {
            wrapper.eq(ProjectTask::getAssignerId, assignerId);
        }

        if (assigneeId != null) {
            wrapper.eq(ProjectTask::getAssigneeId, assigneeId);
        }
        
        wrapper.orderByDesc(ProjectTask::getCreateTime);
        
        return list(wrapper);
    }

    @Override
    public List<ProjectTaskDTO> getTaskListWithNames(String type, String status, String assignerId, String assigneeId, Long projectId) {
        // 先获取基础的任务列表
        List<ProjectTask> taskList = getTaskList(type, status, assignerId, assigneeId, projectId);
        
        // 转换为 DTO 并填充名称信息
        return taskList.stream().map(task -> {
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