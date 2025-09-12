package com.insightflow.service.impl.project;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.dto.ProjectDTO;
import com.insightflow.dto.ProjectDetailDTO;
import com.insightflow.dto.ProjectListDTO;

import com.insightflow.entity.project.Project;
import com.insightflow.entity.project.ProjectMember;
import com.insightflow.entity.project.ProjectTask;
import com.insightflow.entity.project.ProjectProgress;
import com.insightflow.entity.employee.EmployeeInfo;


import com.insightflow.mapper.project.ProjectMapper;
import com.insightflow.mapper.project.ProjectMemberMapper;
import com.insightflow.mapper.project.ProjectTaskMapper;
import com.insightflow.mapper.project.ProjectProgressMapper;
import com.insightflow.mapper.employee.EmployeeInfoMapper;


import com.insightflow.service.project.ProjectService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private ProjectTaskMapper projectTaskMapper;

    @Autowired
    private ProjectProgressMapper projectProgressMapper;

    @Autowired
    private EmployeeInfoMapper employeeInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Project project) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(project);
        return super.save(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<Project> projectList) {
        // 批量设置创建信息
        projectList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(projectList);
    }

    @Override
    public List<Project> getProjectList(String status, String keyword) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(Project::getStatus, status);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Project::getName, keyword)
                    .or()
                    .like(Project::getDescription, keyword);
        }
        // 排序，置顶的项目优先显示，并根据最新的置顶时间进行排序
        wrapper.orderByDesc(Project::getTopUp);
        wrapper.orderByDesc(Project::getTopUpTime);
        wrapper.orderByDesc(Project::getCreateTime);

        return this.list(wrapper);
    }

    @Override
    public PageInfo<Project> getProjectList(Integer page, Integer size, String status, String keyword) {
        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);
        
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        
        // 状态筛选
        if (StringUtils.hasText(status)) {
            wrapper.eq(Project::getStatus, status);
        }
        
        // 关键词搜索（项目名称、描述等）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(Project::getName, keyword)
                .or()
                .like(Project::getDescription, keyword)
            );
        }
        
        // 排序：按创建时间倒序
        wrapper.orderByDesc(Project::getCreateTime);
        
        // 执行查询并返回PageInfo
        List<Project> projectList = list(wrapper);
        return new PageInfo<>(projectList);
    }

    @Override
    public PageInfo<ProjectListDTO> getProjectListWithMembers(Integer current, Integer size, String status, String keyword) {
        // 先获取基础项目分页数据
        PageInfo<Project> projectPage = getProjectList(current, size, status, keyword);
        
        // 转换为ProjectListDTO并添加成员信息
        List<ProjectListDTO> projectListDTOs = new ArrayList<>();
        
        for (Project project : projectPage.getList()) {
            ProjectListDTO dto = new ProjectListDTO();
            BeanUtils.copyProperties(project, dto);
            
            // 获取并设置责任人信息
            if (project.getManagerId() != null) {
                EmployeeInfo manager = employeeInfoMapper.selectById(project.getManagerId());
                if (manager != null) {
                    dto.setManagerName(manager.getName());
                }
            }
            
            // 获取并设置创建人信息
            if (project.getCreatorId() != null) {
                EmployeeInfo creator = employeeInfoMapper.selectById(project.getCreatorId());
                if (creator != null) {
                    dto.setCreatorName(creator.getName());
                }
            }
            
            // 获取项目成员信息
            LambdaQueryWrapper<ProjectMember> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.eq(ProjectMember::getProjectId, project.getId())
                        .eq(ProjectMember::getDeleted, 0);
            List<ProjectMember> members = projectMemberMapper.selectList(memberWrapper);
            
            List<ProjectListDTO.ProjectMemberInfo> memberInfos = new ArrayList<>();
            for (ProjectMember member : members) {
                EmployeeInfo employee = employeeInfoMapper.selectById(member.getEmployeeId());
                if (employee != null && employee.getDeleted() == 0) {
                    ProjectListDTO.ProjectMemberInfo memberInfo = new ProjectListDTO.ProjectMemberInfo();
                    memberInfo.setEmployeeId(member.getEmployeeId());
                    memberInfo.setEmployeeName(employee.getName());
                    memberInfo.setRole(member.getRole());
                    memberInfo.setJoinTime(member.getCreateTime());
                    memberInfo.setAvatar(employee.getAvatar());
                    memberInfos.add(memberInfo);
                }
            }
            dto.setMembers(memberInfos);
            
            projectListDTOs.add(dto);
        }
        
        // 创建新的PageInfo对象
        PageInfo<ProjectListDTO> resultPage = new PageInfo<>();
        resultPage.setList(projectListDTOs);
        resultPage.setTotal(projectPage.getTotal());
        resultPage.setPageSize(projectPage.getPageSize());
        resultPage.setPageNum(projectPage.getPageNum());
        resultPage.setPages(projectPage.getPages());
        
        return resultPage;
    }

    @Override
    public List<ProjectListDTO> getProjectListWithMembers(String status, String keyword) {
        // 先获取基础项目列表数据
        List<Project> projectList = getProjectList(status, keyword);
        
        // 转换为ProjectListDTO并添加成员信息
        List<ProjectListDTO> projectListDTOs = new ArrayList<>();
        
        for (Project project : projectList) {
            ProjectListDTO dto = new ProjectListDTO();
            BeanUtils.copyProperties(project, dto);
            
            // 获取并设置责任人信息
            if (project.getManagerId() != null) {
                EmployeeInfo manager = employeeInfoMapper.selectById(project.getManagerId());
                if (manager != null) {
                    dto.setManagerName(manager.getName());
                }
            }
            
            // 获取并设置创建人信息
            if (project.getCreatorId() != null) {
                EmployeeInfo creator = employeeInfoMapper.selectById(project.getCreatorId());
                if (creator != null) {
                    dto.setCreatorName(creator.getName());
                }
            }
            
            // 获取项目成员信息
            LambdaQueryWrapper<ProjectMember> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.eq(ProjectMember::getProjectId, project.getId())
                        .eq(ProjectMember::getDeleted, 0);
            List<ProjectMember> members = projectMemberMapper.selectList(memberWrapper);
            
            List<ProjectListDTO.ProjectMemberInfo> memberInfos = new ArrayList<>();
            for (ProjectMember member : members) {
                EmployeeInfo employee = employeeInfoMapper.selectById(member.getEmployeeId());
                if (employee != null && employee.getDeleted() == 0) {
                    ProjectListDTO.ProjectMemberInfo memberInfo = new ProjectListDTO.ProjectMemberInfo();
                    memberInfo.setEmployeeId(member.getEmployeeId());
                    memberInfo.setEmployeeName(employee.getName());
                    memberInfo.setRole(member.getRole());
                    memberInfo.setJoinTime(member.getCreateTime());
                    memberInfo.setAvatar(employee.getAvatar());
                    memberInfos.add(memberInfo);
                }
            }
            dto.setMembers(memberInfos);
            
            projectListDTOs.add(dto);
        }
        
        return projectListDTOs;
    }

    @Override
    @Transactional
    public Long createProject(ProjectDTO projectDTO) {
        Project project = new Project();
        BeanUtils.copyProperties(projectDTO, project);

        //置顶设置
        if(project.getTopUp()!=null&&project.getTopUp()==1){
            project.setTopUpTime(LocalDateTime.now());
        }
        project.setProgress(0);
        // 自动设置创建人ID和时间
        EntityUtils.setCreateInfo(project);
        
        this.save(project);

        //添加项目进度
        ProjectProgress progress = new ProjectProgress();
        progress.setProjectId(project.getId());
        progress.setProgressPercentage(0);
        //获取创建人名称
        EmployeeInfo creator = employeeInfoMapper.selectById(project.getCreatorId());
        progress.setProgressContent(creator.getName() +"创建项目");
        EntityUtils.setCreateInfo(progress);
        projectProgressMapper.insert(progress);

        // 收集所有需要添加的成员ID，使用Set避免重复
        Set<Long> memberIds = new HashSet<>();
        
        // 添加项目责任人（经理）
        if (project.getManagerId() != null) {
            memberIds.add(project.getManagerId());
        }
        
        // 添加项目创建人
        if (project.getCreatorId() != null) {
            memberIds.add(project.getCreatorId());
        }
        
        // 添加邀请的成员
        if (projectDTO.getInvitedMembers() != null && !projectDTO.getInvitedMembers().isEmpty()) {
            memberIds.addAll(projectDTO.getInvitedMembers());
        }
        
        // 批量添加项目成员
        for (Long memberId : memberIds) {
            ProjectMember member = new ProjectMember();
            member.setProjectId(project.getId());
            member.setEmployeeId(memberId);
            member.setJoinTime(LocalDateTime.now());
            
            // 设置角色：责任人设为MANAGER，创建人设为CREATOR，其他设为MEMBER
            if (memberId.equals(project.getManagerId()) && memberId.equals(project.getCreatorId())) {
                // 如果责任人和创建人是同一人，设置为MANAGER
                member.setRole("MANAGER");
            } else if (memberId.equals(project.getManagerId())) {
                member.setRole("MANAGER");
            } else if (memberId.equals(project.getCreatorId())) {
                member.setRole("CREATOR");
            } else {
                member.setRole("MEMBER");
            }
            
            // 设置创建人ID和时间
            EntityUtils.setCreateInfo(member);
            projectMemberMapper.insert(member);
        }
        
        return project.getId();
    }

    @Override
    @Transactional()
    public boolean updateProject(ProjectDTO projectDTO) {
        Project project = new Project();
        BeanUtils.copyProperties(projectDTO, project);
        //置顶设置
        if(project.getTopUp()!=null&&project.getTopUp()==1){
            Project oldProject = this.getById(project.getId());
            if(oldProject.getTopUp()==null||oldProject.getTopUp()==0){
                project.setTopUpTime(LocalDateTime.now());
            }
        }
        // 自动设置更新时间
        EntityUtils.setUpdateInfo(project);
        this.updateById(project);
        //更新项目成员
        List<ProjectMember> members = new ArrayList<>();
        if (projectDTO.getInvitedMembers() != null && !projectDTO.getInvitedMembers().isEmpty()) {
            projectDTO.getInvitedMembers().forEach(memberId -> {
                ProjectMember member = new ProjectMember();
                member.setProjectId(project.getId());
                member.setEmployeeId(memberId);
                member.setJoinTime(LocalDateTime.now());
                // 添加角色：OWNER
                member.setRole("OWNER");
                // 添加创建人ID和时间
                EntityUtils.setCreateInfo(member);
                members.add(member);
            });
        }
        syncProjectMembers(project.getId(), members);

        return true;
    }



    @Override
    public Object getProjectStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总项目数
        long totalProjects = this.count();
        statistics.put("totalProjects", totalProjects);
        
        // 各状态项目数
        long ongoingProjects = this.count(new LambdaQueryWrapper<Project>().eq(Project::getStatus, "ONGOING"));
        long completedProjects = this.count(new LambdaQueryWrapper<Project>().eq(Project::getStatus, "COMPLETED"));
        long pendingProjects = this.count(new LambdaQueryWrapper<Project>().eq(Project::getStatus, "PENDING"));
        long overdueProjects = this.count(new LambdaQueryWrapper<Project>().eq(Project::getStatus, "OVERDUE"));
        
        statistics.put("ongoingProjects", ongoingProjects);
        statistics.put("completedProjects", completedProjects);
        statistics.put("pendingProjects", pendingProjects);
        statistics.put("overdueProjects", overdueProjects);
        
        return statistics;
    }

    @Override
    public ProjectDetailDTO getProjectDetail(Long projectId) {
        // 获取项目基本信息
        Project project = this.getById(projectId);
        if (project == null) {
            return null;
        }

        ProjectDetailDTO detailDTO = new ProjectDetailDTO();

        // 设置项目基本信息
        ProjectDetailDTO.ProjectInfo projectInfo = new ProjectDetailDTO.ProjectInfo();
        BeanUtils.copyProperties(project, projectInfo);
        
        // 获取项目经理姓名
        if (project.getManagerId() != null) {
            EmployeeInfo manager = employeeInfoMapper.selectById(project.getManagerId());
            if (manager != null) {
                projectInfo.setManagerName(manager.getName());
            }
        }
        //获取创建者姓名
        if (project.getCreatorId() != null) {
            EmployeeInfo creator = employeeInfoMapper.selectById(project.getCreatorId());
            if (creator != null) {
                projectInfo.setCreateName(creator.getName());
            }
        }
        detailDTO.setProjectInfo(projectInfo);
        // 获取项目进度节点列表
        LambdaQueryWrapper<ProjectProgress> progressWrapper = new LambdaQueryWrapper<>();
        progressWrapper.eq(ProjectProgress::getProjectId, projectId)
                      .orderByDesc(ProjectProgress::getCreateTime);
        List<ProjectProgress> progressList = projectProgressMapper.selectList(progressWrapper);
        
        List<ProjectDetailDTO.ProgressNode> progressNodes = progressList.stream()
                .map(this::convertToProgressNode)
                .collect(Collectors.toList());
        detailDTO.setProgressNodes(progressNodes);

        // 获取项目成员列表
        QueryWrapper<ProjectMember> memberWrapper = new QueryWrapper<>();
        memberWrapper.eq("project_id", projectId)
                .orderByAsc("role")
                .orderByDesc("join_time");
        List<ProjectMember> memberList = projectMemberMapper.selectList(memberWrapper);
        
        List<ProjectDetailDTO.ProjectMember> projectMembers = memberList.stream()
                .map(this::convertToProjectMember)
                .collect(Collectors.toList());
        detailDTO.setProjectMembers(projectMembers);

        // 获取项目任务列表
        LambdaQueryWrapper<ProjectTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(ProjectTask::getProjectId, projectId)
                  .orderByDesc(ProjectTask::getCreateTime);
        List<ProjectTask> taskList = projectTaskMapper.selectList(taskWrapper);

        List<ProjectDetailDTO.ProjectTask> projectTasks = taskList.stream()
                .map(this::convertToProjectTask)
                .collect(Collectors.toList());
        detailDTO.setProjectTasks(projectTasks);

        return detailDTO;
    }

    /**
     * 转换为进度节点
     */
    private ProjectDetailDTO.ProgressNode convertToProgressNode(ProjectProgress progress) {
        ProjectDetailDTO.ProgressNode node = new ProjectDetailDTO.ProgressNode();
        BeanUtils.copyProperties(progress, node);
        
        // 获取创建人姓名
        if (progress.getCreatorId() != null) {
            EmployeeInfo creator = employeeInfoMapper.selectById(progress.getCreatorId());
            if (creator != null) {
                node.setCreatorName(creator.getName());
            }
        }
        
        return node;
    }

    /**
     * 转换为项目成员
     */
    private ProjectDetailDTO.ProjectMember convertToProjectMember(ProjectMember member) {
        ProjectDetailDTO.ProjectMember projectMember = new ProjectDetailDTO.ProjectMember();
        BeanUtils.copyProperties(member, projectMember);
        
        // 获取员工信息
        if (member.getEmployeeId() != null) {
            EmployeeInfo employee = employeeInfoMapper.selectById(member.getEmployeeId());
            if (employee != null) {
                projectMember.setEmployeeName(employee.getName());
                projectMember.setAvatar(employee.getAvatar());
            }
        }
        
        return projectMember;
    }

    /**
     * 转换为项目任务
     */
    private ProjectDetailDTO.ProjectTask convertToProjectTask(ProjectTask task) {
        ProjectDetailDTO.ProjectTask projectTask = new ProjectDetailDTO.ProjectTask();
        BeanUtils.copyProperties(task, projectTask);
        
        // 获取负责人姓名
        if (task.getAssigneeId() != null) {
            EmployeeInfo assignee = employeeInfoMapper.selectById(task.getAssigneeId());
            if (assignee != null) {
                projectTask.setAssigneeName(assignee.getName());
            }
        }
        
        // 获取指派者姓名
        if (task.getAssignerId() != null) {
            EmployeeInfo assigner = employeeInfoMapper.selectById(task.getAssignerId());
            if (assigner != null) {
                projectTask.setAssignerName(assigner.getName());
            }
        }
        
        return projectTask;
    }

    @Override
    @Transactional
    public boolean syncProjectMembers(Long projectId, List<ProjectMember> members) {
        try {
            // 验证项目是否存在
            Project project = this.getById(projectId);
            if (project == null || project.getDeleted() == 1) {
                return false;
            }

            // 1. 硬删除原有的项目成员
            projectMemberMapper.deleteByProjectId(projectId);
            // 2. 如果传入的成员列表为空，直接返回
            if (members == null || members.isEmpty()) {
                return true;
            }
            
            // 3. 将members中的employeeId转换成set
            Set<Long> memberIds = members.stream().map(ProjectMember::getEmployeeId).collect(Collectors.toSet());
            
            // 4. 如果项目负责人不在项目成员中，则自动添加
            if (project.getManagerId() != null && !memberIds.contains(project.getManagerId())) {
                memberIds.add(project.getManagerId());
            }
            
            // 5. 添加新的项目成员
            List<ProjectMember> memberList = new ArrayList<>();
            memberIds.forEach(employeeId -> {
                ProjectMember member = new ProjectMember();
                member.setProjectId(projectId);
                member.setEmployeeId(employeeId);
                member.setJoinTime(LocalDateTime.now());
                
                // 设置角色：如果是指定负责人则设为MANAGER，否则设为MEMBER
                if (employeeId.equals(project.getManagerId())) {
                    member.setRole("MANAGER");
                } else {
                    member.setRole("MEMBER");
                }
                
                EntityUtils.setCreateInfo(member);
                memberList.add(member);
            });
            
            // 批量插入新成员
            if (!memberList.isEmpty()) {
                projectMemberMapper.insert(memberList);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 