package com.insightflow.service.impl.project;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.dto.ProjectProgressDTO;
import com.insightflow.entity.project.ProjectProgress;
import com.insightflow.entity.project.Project;
import com.insightflow.entity.employee.EmployeeInfo;
import com.insightflow.mapper.project.ProjectProgressMapper;
import com.insightflow.mapper.project.ProjectMapper;
import com.insightflow.mapper.employee.EmployeeInfoMapper;
import com.insightflow.service.project.ProjectProgressService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectProgressServiceImpl extends ServiceImpl<ProjectProgressMapper, ProjectProgress> implements ProjectProgressService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private EmployeeInfoMapper employeeInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ProjectProgress projectProgress) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(projectProgress);
        return super.save(projectProgress);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<ProjectProgress> projectProgressList) {
        // 批量设置创建信息
        projectProgressList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(projectProgressList);
    }

    @Override
    public IPage<ProjectProgressDTO> getProjectProgressList(Integer page, Integer size, Long projectId, Long creatorId) {
        Page<ProjectProgress> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ProjectProgress> wrapper = new LambdaQueryWrapper<>();
        
        if (projectId != null) {
            wrapper.eq(ProjectProgress::getProjectId, projectId);
        }
        
        if (creatorId != null) {
            wrapper.eq(ProjectProgress::getCreatorId, creatorId);
        }
        
        wrapper.orderByDesc(ProjectProgress::getCreateTime);
        
        IPage<ProjectProgress> progressPage = this.page(pageParam, wrapper);
        
        // 转换为DTO
        IPage<ProjectProgressDTO> dtoPage = new Page<>(page, size, progressPage.getTotal());
        List<ProjectProgressDTO> dtoList = progressPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);
        
        return dtoPage;
    }

    @Override
    public List<ProjectProgressDTO> getProjectProgressHistory(Long projectId) {
        LambdaQueryWrapper<ProjectProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectProgress::getProjectId, projectId)
               .orderByDesc(ProjectProgress::getCreateTime);
        
        List<ProjectProgress> progressList = this.list(wrapper);
        
        return progressList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createProjectProgress(ProjectProgressDTO projectProgressDTO) {
        ProjectProgress progress = new ProjectProgress();
        BeanUtils.copyProperties(projectProgressDTO, progress);
        
        // 获取项目当前进度
        Project project = projectMapper.selectById(projectProgressDTO.getProjectId());
        if (project != null) {
            progress.setPreviousProgress(project.getProgress());
            progress.setProgressChange(projectProgressDTO.getProgressPercentage() - project.getProgress());
        }
        
        // 自动设置创建人ID和时间
        EntityUtils.setCreateInfo(progress);
        
        this.save(progress);
        
        // 更新项目进度
        if (project != null) {
            project.setProgress(projectProgressDTO.getProgressPercentage());
            project.setUpdateTime(LocalDateTime.now());
            projectMapper.updateById(project);
        }
        
        return progress.getId();
    }

    @Override
    public boolean updateProjectProgress(ProjectProgressDTO projectProgressDTO) {
        ProjectProgress progress = new ProjectProgress();
        BeanUtils.copyProperties(projectProgressDTO, progress);
        
        // 自动设置更新时间
        EntityUtils.setUpdateInfo(progress);
        
        return this.updateById(progress);
    }



    @Override
    public Object getProjectProgressStatistics(Long projectId) {
        Map<String, Object> statistics = new HashMap<>();
        
        LambdaQueryWrapper<ProjectProgress> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(ProjectProgress::getProjectId, projectId);
        }
        
        // 总进度记录数
        long totalProgress = this.count(wrapper);
        statistics.put("totalProgress", totalProgress);
        
        // 最新进度
        if (projectId != null) {
            wrapper.orderByDesc(ProjectProgress::getCreateTime);
            wrapper.last("LIMIT 1");
            ProjectProgress latestProgress = this.getOne(wrapper);
            if (latestProgress != null) {
                statistics.put("latestProgress", latestProgress.getProgressPercentage());
                statistics.put("latestProgressTime", latestProgress.getCreateTime());
            }
        }
        
        // 平均进度
        if (totalProgress > 0) {
            wrapper.clear();
            if (projectId != null) {
                wrapper.eq(ProjectProgress::getProjectId, projectId);
            }
            List<ProjectProgress> progressList = this.list(wrapper);
            double avgProgress = progressList.stream()
                    .mapToInt(ProjectProgress::getProgressPercentage)
                    .average()
                    .orElse(0.0);
            statistics.put("averageProgress", Math.round(avgProgress));
        }
        
        return statistics;
    }

    @Override
    public ProjectProgressDTO getLatestProjectProgress(Long projectId) {
        LambdaQueryWrapper<ProjectProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectProgress::getProjectId, projectId)
               .orderByDesc(ProjectProgress::getCreateTime)
               .last("LIMIT 1");
        
        ProjectProgress latestProgress = this.getOne(wrapper);
        
        return latestProgress != null ? convertToDTO(latestProgress) : null;
    }

    /**
     * 转换为DTO
     */
    private ProjectProgressDTO convertToDTO(ProjectProgress progress) {
        ProjectProgressDTO dto = new ProjectProgressDTO();
        BeanUtils.copyProperties(progress, dto);
        
        // 获取项目名称
        if (progress.getProjectId() != null) {
            Project project = projectMapper.selectById(progress.getProjectId());
            if (project != null) {
                dto.setProjectName(project.getName());
            }
        }
        
        // 获取创建人姓名
        if (progress.getCreatorId() != null) {
            EmployeeInfo employee = employeeInfoMapper.selectById(progress.getCreatorId());
            if (employee != null) {
                dto.setCreatorName(employee.getName());
            }
        }
        
        return dto;
    }
} 