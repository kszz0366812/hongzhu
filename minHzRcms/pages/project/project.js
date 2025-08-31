// pages/project/project.js
const api = require('../../utils/api.js')
const auth = require('../../utils/auth.js')

Page({
  data: {
    projects: [],
    filteredProjects: [],
    activeTab: 'all',
    // 组件数据
    statsData: [],
    actionButtons: [],
    tabsData: [],
    loading: false,
    // 成员选择器相关
    showInviteModal: false,
    availableMembers: [],
    invitedMemberIds: [],
    currentProjectId: null,
    teamMembers: []
  },

  onLoad: function() {
    this.initComponentData();
    
    // 检查是否有待刷新的标记
    const app = getApp();
    if (app && app.globalData && app.globalData.needRefreshProjects) {
      console.log('onLoad检测到需要刷新项目数据，开始重新加载');
      app.globalData.needRefreshProjects = false;
      this.loadProjectData();
    } else {
      // 正常加载项目数据
      this.loadProjectData();
    }
    
    this.loadEmployeeList();
  },

  onShow: function() {
    console.log('项目管理页面 onShow，检查是否需要刷新数据');
    
    // 检查是否有待刷新的标记
    const app = getApp();
    if (app.globalData && app.globalData.needRefreshProjects) {
      console.log('检测到需要刷新项目数据，开始重新加载');
      app.globalData.needRefreshProjects = false;
      this.loadProjectData();
    } else {
      console.log('无需刷新，使用现有数据');
      // 确保当前标签页的数据正确显示
      this.filterProjects();
    }
  },

  // 初始化组件数据
  initComponentData: function() {
    // 初始化统计数据
    this.setData({
      statsData: [
        { number: 0, label: '总项目' },
        { number: 0, label: '进行中' },
        { number: 0, label: '已逾期' },
        { number: 0, label: '已完成' }
      ],
      actionButtons: [
        { text: '+ 创建项目', type: 'secondary' }
      ],
      tabsData: [
        { key: 'all', label: '全部' },
        { key: 'ongoing', label: '进行中' },
        { key: 'pending', label: '待启动' },
        { key: 'completed', label: '已完成' }
      ]
    });
  },

  // 加载项目数据
  loadProjectData: function() {
    this.setData({ loading: true });
    
    // 获取用户信息
    auth.getUserInfo()
      .then(userInfo => {
        if (!userInfo.employeeId) {
          console.log('用户未绑定员工号，getUserInfo已自动触发绑定流程');
          const app = getApp();
          app.globalData.pendingOperation = () => {
            console.log('员工绑定成功，继续加载项目数据');
            this.loadProjectData();
          };
          return;
        }
        
        // 加载项目列表
        return api.getProjectList({
          page: 1,
          size: 100
        });
      })
      .then(res => {
        if (res && res.code === 200) {
          const projects = res.data.records || res.data || [];
          console.log('获取项目列表成功，项目数量:', projects.length);
          if (projects.length > 0 && projects[0].members) {
            console.log('成员数据示例:', projects[0].members);
          }
          
          // 处理成员数据，确保字段映射正确
          const processedProjects = projects.map(project => {
            if (project.members && Array.isArray(project.members)) {
              // 处理成员数据，映射字段名
              const processedMembers = project.members.map(member => ({
                id: member.id || member.employeeId,
                name: member.employeeName || member.name,
                avatar: member.avatar,
                department: member.department
              }));
              
              return {
                ...project,
                members: processedMembers,
                invitedMembers: processedMembers // 保持兼容性
              };
            }
            return project;
          });
          
          console.log('处理后的项目数据:', processedProjects);
          
          this.setData({ 
            projects: processedProjects,
            loading: false
          });
          this.calculateStats();
          this.filterProjects();
        } else {
          console.error('获取项目列表失败:', res);
          this.setData({ loading: false });
          wx.showToast({
            title: res?.message || '获取项目列表失败',
            icon: 'error'
          });
        }
      })
      .catch(error => {
        console.error('加载项目数据失败:', error);
        this.setData({ loading: false });
        wx.showToast({
          title: '网络请求失败',
          icon: 'error'
        });
      });
  },

  // 计算统计数据
  calculateStats: function() {
    const projects = this.data.projects;
    const totalProjects = projects.length;
    const ongoingProjects = projects.filter(p => p.status === '进行中' || p.status === 'ongoing').length;
    const completedProjects = projects.filter(p => p.status === '已完成' || p.status === 'completed').length;
    const overdueProjects = projects.filter(p => p.status === '已逾期' || p.status === 'overdue').length;

    this.setData({
      statsData: [
        { number: totalProjects, label: '总项目' },
        { number: ongoingProjects, label: '进行中' },
        { number: overdueProjects, label: '已逾期' },
        { number: completedProjects, label: '已完成' }
      ]
    });
  },

  // 格式化项目数据为组件格式
  formatProjectForComponent: function(project) {
    const formattedProject = {
      id: project.id,
      title: project.name,
      level: this.getLevelText(project.level),
      levelClass: this.getLevelClass(project.level),
      priority: this.getPriorityText(project.priority),
      priorityClass: this.getPriorityClass(project.priority),
      status: project.status,
      isTop: project.topUp === 1, // 添加置顶标识
      info: [
        { label: '项目周期', value: `${project.startDate} 至 ${project.endDate}` },
        { label: '项目简介', value: project.description, isDescription: true }
      ],
      progress: project.progress || 0,
      progressLabel: '项目进度',
      members: project.members || project.invitedMembers || [],
      showMembers: project.showMembers || false, // 使用项目自身的显示状态，默认不展开
      actions: this.getProjectActions(project)
    };
    
    // 调试置顶标识
    if (project.topUp === 1) {
      console.log('发现置顶项目:', {
        id: project.id,
        name: project.name,
        topUp: project.topUp,
        isTop: formattedProject.isTop
      });
    }
    
    return formattedProject;
  },

  // 获取等级文本
  getLevelText: function(level) {
    const levelTextMap = {
      'URGENT': '紧急',
      'IMPORTANT': '重要',
      'NORMAL': '普通',
      'LONG_TERM': '长期',
      '紧急': '紧急',
      '重要': '重要',
      '普通': '普通',
      '长期': '长期'
    };
    return levelTextMap[level] || '普通';
  },

  // 获取等级CSS类名
  getLevelClass: function(level) {
    const levelClassMap = {
      'URGENT': 'jinji',
      'IMPORTANT': 'zhongyao',
      'NORMAL': 'putong',
      'LONG_TERM': 'changqi',
      '紧急': 'jinji',
      '重要': 'zhongyao',
      '普通': 'putong',
      '长期': 'changqi'
    };
    return levelClassMap[level] || 'putong';
  },

  // 获取优先级文本
  getPriorityText: function(priority) {
    const priorityTextMap = {
      'HIGH': '高',
      'MEDIUM': '中',
      'LOW': '低',
      '高': '高',
      '中': '中',
      '低': '低'
    };
    return priorityTextMap[priority] || '中';
  },

  // 获取优先级样式类
  getPriorityClass: function(priority) {
    const priorityMap = {
      'HIGH': 'high',
      'MEDIUM': 'medium',
      'LOW': 'low',
      '高': 'high',
      '中': 'medium', 
      '低': 'low'
    };
    return priorityMap[priority] || 'medium';
  },

  // 获取项目操作按钮
  getProjectActions: function(project) {
    const actions = [];
    
    // 邀请成员按钮 - 始终显示
    actions.push({ text: '邀请成员', type: '' });
    
    // 更新进度按钮 - 当进度为100%时置灰并禁用
    const isProgress100 = project.progress === 100;
    actions.push({ 
      text: '更新进度', 
      type: '',
      disabled: isProgress100
    });

    // 状态按钮 - 根据项目状态显示不同文本和状态
    if (project.status === '待启动' || project.status === 'pending') {
      // 未启用状态 - 显示启动项目
      actions.push({ 
        text: '启动项目', 
        type: 'status-btn',
        disabled: false
      });
    } else if (project.status === '进行中' || project.status === 'ongoing') {
      // 进行中状态 - 显示确认完成
      actions.push({ 
        text: '确认完成', 
        type: 'status-btn',
        disabled: false
      });
    } else if (project.status === '已完成' || project.status === 'completed') {
      // 已完成状态 - 置灰并禁用
      actions.push({ 
        text: '确认完成', 
        type: 'status-btn',
        disabled: true
      });
    }

    return actions;
  },

  // 切换标签页
  switchTab: function(e) {
    const tab = e.currentTarget.dataset.tab;
    console.log('切换标签页:', tab);
    
    // 先清空当前显示的项目列表，防止数据错乱
    this.setData({ 
      activeTab: tab,
      filteredProjects: []
    });
    
    // 延迟执行过滤，确保状态更新完成
    setTimeout(() => {
      this.filterProjects();
    }, 50);
  },

  // 组件事件处理
  onActionButtonTap: function(e) {
    const { button } = e.detail;
    if (button.text.includes('创建')) {
      this.navigateToCreateProject();
    }
  },

  onTabChange: function(e) {
    const { key } = e.detail;
    console.log('组件标签页切换:', key);
    
    // 先清空当前显示的项目列表，防止数据错乱
    this.setData({ 
      activeTab: key,
      filteredProjects: []
    });
    
    // 延迟执行过滤，确保状态更新完成
    setTimeout(() => {
      this.filterProjects();
    }, 50);
  },

  // 筛选项目
  filterProjects: function() {
    const { projects, activeTab } = this.data;
    
    console.log('开始筛选项目，当前标签页:', activeTab);
    console.log('原始项目数据:', projects);
    
    // 确保projects数据存在且为数组
    if (!projects || !Array.isArray(projects)) {
      console.warn('projects数据不存在或格式错误:', projects);
      this.setData({
        filteredProjects: []
      });
      return;
    }
    
    let filteredProjects = [];

    switch (activeTab) {
      case 'all':
        filteredProjects = projects;
        break;
      case 'ongoing':
        filteredProjects = projects.filter(p => {
          const status = p.status;
          const isOngoing = status === '进行中' || status === 'ongoing';
          console.log(`项目 ${p.name} 状态: ${status}, 是否进行中: ${isOngoing}`);
          return isOngoing;
        });
        break;
      case 'pending':
        filteredProjects = projects.filter(p => {
          const status = p.status;
          const isPending = status === '待启动' || status === 'pending';
          console.log(`项目 ${p.name} 状态: ${status}, 是否待启动: ${isPending}`);
          return isPending;
        });
        break;
      case 'completed':
        filteredProjects = projects.filter(p => {
          const status = p.status;
          const isCompleted = status === '已完成' || status === 'completed';
          console.log(`项目 ${p.name} 状态: ${status}, 是否已完成: ${isCompleted}`);
          return isCompleted;
        });
        break;
      default:
        filteredProjects = projects;
    }

    console.log('筛选后的项目数量:', filteredProjects.length);
    console.log('筛选后的项目数据:', filteredProjects);

    // 格式化项目数据为组件格式
    const formattedProjects = filteredProjects.map(project => this.formatProjectForComponent(project));

    console.log('格式化后的项目数据:', formattedProjects);

    // 确保数据更新完成后再设置
    this.setData({
      filteredProjects: formattedProjects
    }, () => {
      console.log('项目列表数据更新完成，当前显示项目数量:', this.data.filteredProjects.length);
    });
  },

  // 项目操作处理
  onProjectActionTap: function(e) {
    const { action, item } = e.detail;
    const projectId = item.id;

    // 检查按钮是否被禁用
    if (action.disabled) {
      console.log('按钮已被禁用，不执行操作');
      return;
    }

    switch (action.text) {
      case '邀请成员':
        this.inviteMembers(projectId);
        break;
      case '更新进度':
        this.updateProgress(projectId);
        break;
      case '启动项目':
        this.changeProjectStatus(projectId, '进行中');
        break;
      case '确认完成':
        this.changeProjectStatus(projectId, '已完成');
        break;
      default:
        console.log('未知操作:', action.text);
    }
  },

  // 加载员工列表
  loadEmployeeList: function() {
    console.log('项目列表页 - 开始加载员工列表');
    
    api.getEmployeeList()
      .then(res => {
        console.log('项目列表页 - 获取员工列表成功:', res);
        if (res.code === 200 && res.data) {
          // 将后端数据转换为前端需要的格式
          const teamMembers = res.data.map(employee => ({
            id: employee.id,
            name: employee.name,
            department: employee.department || '未知部门',
            role: employee.position || '员工',
            phone: employee.phone || '',
            email: employee.email || ''
          }));
          
          console.log('项目列表页 - 员工数据转换完成:', teamMembers);
          
          this.setData({
            teamMembers: teamMembers
          });
        } else {
          console.error('项目列表页 - 获取员工列表失败:', res.message);
          wx.showToast({
            title: res.message || '获取员工列表失败',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        console.error('项目列表页 - 获取员工列表出错:', error);
        if (error.code === 401) {
          console.log('项目列表页 - 用户未登录，跳转登录页');
          auth.redirectToLogin();
        } else {
          wx.showToast({
            title: '网络请求失败',
            icon: 'none'
          });
        }
      });
  },

  // 邀请成员
  inviteMembers: function(projectId) {
    const { teamMembers, projects } = this.data;
    const project = projects.find(p => p.id === projectId);
    
    if (!project) {
      wx.showToast({
        title: '项目不存在',
        icon: 'error'
      });
      return;
    }
    
    // 获取已邀请成员的ID列表
    const invitedMemberIds = (project.members || project.invitedMembers || []).map(m => m.id);
    // 显示所有团队成员，让用户可以看到已邀请和未邀请的状态
    const availableMembers = teamMembers;
    
    this.setData({
      showInviteModal: true,
      availableMembers: availableMembers,
      invitedMemberIds: invitedMemberIds,
      currentProjectId: projectId
    });
  },

  // 成员选择器事件处理
  onInviteModalClose: function() {
    this.setData({
      showInviteModal: false,
      availableMembers: [],
      invitedMemberIds: [],
      currentProjectId: null
    });
  },

  onInviteMemberSelect: function(e) {
    console.log('项目列表页 - 收到成员选择事件:', e.detail);
    const { members } = e.detail;
    const { currentProjectId, projects } = this.data;
    
    if (!currentProjectId) {
      console.error('当前项目ID为空');
      return;
    }
    
    // 找到当前项目
    const currentProject = projects.find(p => p.id === currentProjectId);
    if (!currentProject) {
      console.error('找不到当前项目');
      return;
    }
    
    // 获取原有成员ID列表
    const existingMemberIds = (currentProject.members || currentProject.invitedMembers || []).map(m => m.id);
    
    if (!members || members.length === 0) {
      console.log('项目列表页 - 没有选择任何成员');
      wx.showToast({
        title: '请选择要邀请的成员',
        icon: 'none'
      });
      return;
    }
    
    // 直接使用用户选择的成员列表更新项目
    // 不管是有新增、删除还是保持不变，都应该允许用户操作
    this.updateProjectMembers(currentProjectId, members);
  },

  // 更新项目成员信息
  updateProjectMembers: function(projectId, newMembers) {
    wx.showLoading({
      title: '正在更新项目成员...'
    });

    // 找到当前项目
    const { projects } = this.data;
    const currentProject = projects.find(p => p.id === projectId);
    
    if (!currentProject) {
      wx.hideLoading();
      wx.showToast({
        title: '项目不存在',
        icon: 'error'
      });
      return;
    }

    // 直接使用用户选择的成员列表
    // 不需要合并原有成员，直接替换整个成员列表
    const updateData = {
      id: projectId,
      invitedMembers: newMembers.map(member => member.id) // 传递用户选择的员工ID列表
    };

    console.log('准备更新的项目数据:', updateData);
    console.log('用户选择的成员:', newMembers);
    console.log('传递的员工ID列表:', updateData.invitedMembers);

    // 调用更新项目接口
    api.updateProject(updateData)
      .then(res => {
        wx.hideLoading();
        
        if (res.code === 200) {
          console.log('服务端返回的更新结果:', res);
          
          // 使用服务端返回的数据更新本地数据
          let updatedProjects = projects;
          
          // 如果服务端返回了完整的项目数据，优先使用服务端数据
          if (res.data && res.data.id === projectId) {
            console.log('使用服务端返回的完整项目数据:', res.data);
            updatedProjects = projects.map(project => {
              if (project.id === projectId) {
                const updatedProject = {
                  ...project,
                  ...res.data, // 使用服务端返回的所有数据
                  showMembers: false // 成员区域默认不展开
                };
                console.log('使用服务端数据更新后的项目:', updatedProject);
                return updatedProject;
              }
              return project;
            });
          } else if (res.data && res.data.invitedMembers) {
            // 如果服务端返回了邀请成员ID列表，需要根据ID获取完整的成员信息
            console.log('服务端返回的邀请成员ID列表:', res.data.invitedMembers);
            
            // 从本地员工列表中获取完整的成员信息
            const { teamMembers } = this.data;
            const updatedMembers = res.data.invitedMembers.map(memberId => {
              const member = teamMembers.find(tm => tm.id === memberId);
              return member || { id: memberId, name: '未知成员' };
            });
            
            console.log('根据ID获取的完整成员信息:', updatedMembers);
            
            updatedProjects = projects.map(project => {
              if (project.id === projectId) {
                const updatedProject = {
                  ...project,
                  members: updatedMembers,
                  invitedMembers: updatedMembers,
                  showMembers: false // 成员区域默认不展开
                };
                console.log('使用服务端ID列表更新后的项目:', updatedProject);
                return updatedProject;
              }
              return project;
            });
          } else {
            // 如果服务端没有返回相关数据，则使用本地合并的数据
            console.log('服务端未返回相关数据，使用本地合并数据');
            updatedProjects = projects.map(project => {
              if (project.id === projectId) {
                const updatedProject = {
                  ...project,
                  members: newMembers,
                  invitedMembers: newMembers,
                  showMembers: false // 成员区域默认不展开
                };
                console.log('使用本地数据更新后的项目:', updatedProject);
                return updatedProject;
              }
              return project;
            });
          }
          
          this.setData({
            projects: updatedProjects,
            showInviteModal: false,
            availableMembers: [],
            invitedMemberIds: [],
            currentProjectId: null
          });
          
          console.log('本地数据更新完成，开始重新过滤项目');
          
          // 重新过滤项目数据
          this.filterProjects();
          
          // 可选：重新获取项目列表以确保数据最新
          // 如果服务端返回的数据不完整，可以考虑重新获取
          if (!res.data || (!res.data.invitedMembers && !res.data.members)) {
            console.log('服务端数据不完整，考虑重新获取项目列表');
            // 这里可以添加重新获取项目列表的逻辑
            // this.loadProjectList();
          }
          
          // 显示成功提示
          const memberNames = newMembers.map(m => m.name).join('、');
          wx.showToast({
            title: `已成功更新成员：${memberNames}`,
            icon: 'success'
          });
        } else {
          wx.showToast({
            title: res.message || '更新项目成员失败',
            icon: 'error'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        console.error('更新项目成员失败:', error);
        
        wx.showToast({
          title: error.message || '更新项目成员失败，请重试',
          icon: 'error',
          duration: 3000
        });
      });
  },

  // 更新进度
  updateProgress: function(projectId) {
    wx.navigateTo({
      url: `/pages/update-progress/update-progress?projectId=${projectId}`
    });
  },

  // 更新项目进度（供更新进度页面调用）
  updateProjectProgress: function(projectId, newProgress) {
    const progressData = {
      progress: newProgress,
      description: '进度更新'
    };

    api.updateProjectProgress(projectId, progressData)
      .then(res => {
        if (res.code === 200) {
          // 更新本地数据
          const projects = this.data.projects.map(p => {
            if (p.id === projectId) {
              return { ...p, progress: newProgress };
            }
            return p;
          });
          
          this.setData({ projects: projects });
          this.calculateStats();
          this.filterProjects();
          
          wx.showToast({
            title: '进度更新成功',
            icon: 'success'
          });
        } else {
          wx.showToast({
            title: res.message || '进度更新失败',
            icon: 'error'
          });
        }
      })
      .catch(error => {
        console.error('更新项目进度失败:', error);
        wx.showToast({
          title: '网络请求失败',
          icon: 'error'
        });
      });
  },

  // 删除项目
  deleteProject: function(e) {
    const projectId = parseInt(e.currentTarget.dataset.id);
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个项目吗？',
      success: (res) => {
        if (res.confirm) {
          api.deleteProject(projectId)
            .then(res => {
              if (res.code === 200) {
                // 从本地数据中移除
                const projects = this.data.projects.filter(p => p.id !== projectId);
                this.setData({ projects: projects });
                
                // 重新计算统计数据和筛选项目
                this.calculateStats();
                this.filterProjects();
                
                wx.showToast({
                  title: '项目已删除',
                  icon: 'success'
                });
              } else {
                wx.showToast({
                  title: res.message || '删除失败',
                  icon: 'error'
                });
              }
            })
            .catch(error => {
              console.error('删除项目失败:', error);
              wx.showToast({
                title: '网络请求失败',
                icon: 'error'
              });
            });
        }
      }
    });
  },

  // 变更项目状态
  changeProjectStatus: function(projectId, newStatus) {
    const project = this.data.projects.find(p => p.id === projectId);
    if (!project) return;

    const updateData = {
      id: projectId,
      name: project.name,
      description: project.description,
      startDate: project.startDate,
      endDate: project.endDate,
      status: newStatus,
      level: project.level,
      managerId: project.managerId,
      progress: project.progress
    };

    api.updateProject(updateData)
      .then(res => {
        if (res.code === 200) {
          // 更新本地数据
          const projects = this.data.projects.map(p => {
            if (p.id === projectId) {
              return { ...p, status: newStatus };
            }
            return p;
          });
          
          this.setData({ projects: projects });
          this.filterProjects();
          
          wx.showToast({
            title: '状态已更新',
            icon: 'success'
          });
        } else {
          wx.showToast({
            title: res.message || '状态更新失败',
            icon: 'error'
          });
        }
      })
      .catch(error => {
        console.error('更新项目状态失败:', error);
        wx.showToast({
          title: '网络请求失败',
          icon: 'error'
        });
      });
  },

  // 跳转到创建项目页面
  navigateToCreateProject: function() {
    wx.navigateTo({
      url: '/pages/create-project/create-project'
    });
  },

  // 跳转到项目详情页面
  navigateToProjectDetail: function(e) {
    console.log('=== 项目详情点击事件触发 ===');
    const { item } = e.detail;
    console.log('项目数据:', item);
    
    if (item && item.id) {
      console.log('使用项目ID跳转:', item.id);
      
      wx.navigateTo({
        url: `/pages/project-detail/project-detail?projectId=${item.id}`,
        success: function() {
          console.log('跳转成功');
        },
        fail: function(err) {
          console.error('跳转失败:', err);
          wx.showToast({
            title: '跳转失败',
            icon: 'error'
          });
        }
      });
    } else {
      console.error('未找到项目ID');
      wx.showToast({
        title: '未找到项目',
        icon: 'error'
      });
    }
  },

  // 切换成员显示
  onToggleMembers: function(e) {
    const { itemId } = e.detail;
    const projectId = itemId;
    
    console.log('切换成员显示，项目ID:', projectId);
    
    // 更新项目的成员显示状态
    const projects = this.data.projects.map(p => {
      if (p.id === projectId) {
        console.log('切换项目成员显示状态:', p.name, '当前状态:', p.showMembers);
        return { ...p, showMembers: !p.showMembers };
      }
      return p;
    });
    
    this.setData({ projects: projects });
    this.filterProjects();
  },

  // 获取状态样式类
  getStatusClass: function(status) {
    switch (status) {
      case '进行中':
      case 'ongoing':
        return 'ongoing';
      case '待启动':
      case 'pending':
        return 'pending';
      case '已完成':
      case 'completed':
        return 'completed';
      case '已逾期':
      case 'overdue':
        return 'overdue';
      default:
        return '';
    }
  }
})
