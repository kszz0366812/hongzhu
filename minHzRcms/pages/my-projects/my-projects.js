// pages/my-projects/my-projects.js
const api = require('../../utils/api.js')
const auth = require('../../utils/auth.js')

Page({
  data: {
    activeTab: 'all',
    totalProjects: 0,
    ongoingProjects: 0,
    pendingProjects: 0,
    completedProjects: 0,
    // 邀请成员弹窗相关
    showInviteModal: false,
    inviteProjectId: null,
    availableMembers: [],
    invitedMemberIds: [],
    projects: [],
    filteredProjects: [],
    loading: false,
    teamMembers: []
  },

  onLoad: function() {
    console.log('我的项目页面加载');
    this.loadProjectData();
    this.loadEmployeeList();
  },

  onShow: function() {
    // 页面显示时重新加载数据
    this.loadProjectData();
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
          size: 100,
          managerId: userInfo.employeeId // 只获取当前用户负责的项目
        });
      })
      .then(res => {
        if (res && res.code === 200) {
          const projects = res.data.records || res.data || [];
          this.setData({ 
            projects: projects,
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
    const projects = this.data.projects || [];
    const totalProjects = projects.length;
    const ongoingProjects = projects.filter(p => p.status === '进行中' || p.status === 'ongoing').length;
    const pendingProjects = projects.filter(p => p.status === '待安排' || p.status === 'pending').length;
    const completedProjects = projects.filter(p => p.status === '已完成' || p.status === 'completed').length;

    console.log('统计数据:', { totalProjects, ongoingProjects, pendingProjects, completedProjects });

    this.setData({
      totalProjects,
      ongoingProjects,
      pendingProjects,
      completedProjects
    });
  },

  // 切换项目成员显示
  toggleMembers: function(e) {
    const projectId = parseInt(e.currentTarget.dataset.id);
    const projects = this.data.projects.map(p => {
      if (p.id === projectId) {
        return { ...p, showMembers: !p.showMembers };
      }
      return p;
    });
    
    this.setData({ projects: projects });
    this.filterProjects();
  },

  // 筛选项目
  filterProjects: function() {
    const { projects, activeTab } = this.data;
    let filteredProjects = [];

    // 根据标签页筛选
    if (activeTab === 'all') {
      filteredProjects = projects;
    } else if (activeTab === 'ongoing') {
      filteredProjects = projects.filter(p => p.status === '进行中' || p.status === 'ongoing');
    } else if (activeTab === 'pending') {
      filteredProjects = projects.filter(p => p.status === '待安排' || p.status === 'pending');
    } else if (activeTab === 'completed') {
      filteredProjects = projects.filter(p => p.status === '已完成' || p.status === 'completed');
    } else {
      filteredProjects = projects;
    }

    // 为每个项目添加样式类
    filteredProjects = filteredProjects.map(project => {
      const levelClass = this.getLevelClass(project.level);
      const statusClass = this.getStatusClass(project.status);
      return { ...project, levelClass, statusClass };
    });

    this.setData({
      filteredProjects: filteredProjects
    });
  },

  // 获取等级样式类
  getLevelClass: function(level) {
    switch (level) {
      case '长期': return 'long-term';
      case '紧急': return 'urgent';
      case '重要': return 'important';
      case '一般': return 'normal';
      default: return '';
    }
  },

  // 获取状态样式类
  getStatusClass: function(status) {
    switch (status) {
      case '进行中':
      case 'ongoing':
        return 'ongoing';
      case '待安排':
      case 'pending':
        return 'pending';
      case '已完成':
      case 'completed':
        return 'completed';
      default:
        return '';
    }
  },

  // 切换标签页
  switchTab: function(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({
      activeTab: tab
    });
    this.filterProjects();
  },

  // 跳转到项目详情页面
  navigateToProjectDetail: function(e) {
    const projectId = parseInt(e.currentTarget.dataset.id);
    const project = this.data.filteredProjects.find(p => p.id === projectId);
    if (project) {
      const projectData = encodeURIComponent(JSON.stringify(project));
      wx.navigateTo({
        url: `/pages/project-detail/project-detail?project=${projectData}`
      });
    }
  },

  // 编辑项目
  editProject: function(e) {
    const projectId = parseInt(e.currentTarget.dataset.id);
    const project = this.data.projects.find(p => p.id === projectId);
    
    if (project) {
      // 将项目数据编码并跳转到编辑页面
      const projectData = encodeURIComponent(JSON.stringify(project));
      wx.navigateTo({
        url: `/pages/edit-project/edit-project?project=${projectData}`
      });
    }
  },

  // 删除项目
  deleteProject: function(e) {
    const projectId = parseInt(e.currentTarget.dataset.id);
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个项目吗？删除后无法恢复。',
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

  // 更新进度
  updateProgress: function(e) {
    const projectId = parseInt(e.currentTarget.dataset.id);
    wx.navigateTo({
      url: `/pages/update-progress/update-progress?projectId=${projectId}`
    });
  },

  // 加载员工列表
  loadEmployeeList: function() {
    console.log('我的项目页 - 开始加载员工列表');
    
    api.getEmployeeList()
      .then(res => {
        console.log('我的项目页 - 获取员工列表成功:', res);
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
          
          console.log('我的项目页 - 员工数据转换完成:', teamMembers);
          
          this.setData({
            teamMembers: teamMembers
          });
        } else {
          console.error('我的项目页 - 获取员工列表失败:', res.message);
          wx.showToast({
            title: res.message || '获取员工列表失败',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        console.error('我的项目页 - 获取员工列表出错:', error);
        if (error.code === 401) {
          console.log('我的项目页 - 用户未登录，跳转登录页');
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
  inviteMembers: function(e) {
    const projectId = parseInt(e.currentTarget.dataset.id);
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
    // 过滤掉已邀请的成员
    const availableMembers = teamMembers.filter(m => !invitedMemberIds.includes(m.id));
    
    if (availableMembers.length === 0) {
      wx.showToast({
        title: '所有成员已邀请',
        icon: 'none'
      });
      return;
    }
    
    this.setData({
      showInviteModal: true,
      availableMembers: availableMembers,
      invitedMemberIds: [],
      inviteProjectId: projectId
    });
  },

  // 成员选择器事件处理
  onInviteModalClose: function() {
    this.setData({
      showInviteModal: false,
      availableMembers: [],
      invitedMemberIds: [],
      inviteProjectId: null
    });
  },

  onInviteMemberSelect: function(e) {
    console.log('我的项目页 - 收到成员选择事件:', e.detail);
    const { members } = e.detail;
    const { inviteProjectId } = this.data;
    
    if (!members || members.length === 0) {
      console.log('我的项目页 - 没有选择任何成员');
      wx.showToast({
        title: '请选择要邀请的成员',
        icon: 'none'
      });
      return;
    }
    
    if (!inviteProjectId) {
      console.error('我的项目页 - 项目ID为空');
      return;
    }
    
    console.log('我的项目页 - 邀请成员到项目:', inviteProjectId, members);
    
    // 这里应该调用邀请成员的API，暂时更新本地数据
    const projects = this.data.projects.map(project => {
      if (project.id === inviteProjectId) {
        const existingMembers = project.invitedMembers || [];
        const newMembers = [...existingMembers, ...members];
        return { ...project, invitedMembers: newMembers };
      }
      return project;
    });
    
    this.setData({
      projects: projects,
      showInviteModal: false,
      availableMembers: [],
      invitedMemberIds: [],
      inviteProjectId: null
    });
    
    this.filterProjects();
    
    // 显示成功提示
    const memberNames = members.map(m => m.name).join('、');
    wx.showToast({
      title: `已邀请${memberNames}`,
      icon: 'success'
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
  }
}) 