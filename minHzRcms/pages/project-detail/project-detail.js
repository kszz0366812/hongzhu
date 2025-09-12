// pages/project-detail/project-detail.js
const api = require('../../utils/api.js')
const auth = require('../../utils/auth.js')

Page({
  data: {
    projectInfo: {
      name: '',
      startDate: '',
      endDate: '',
      status: '',
      statusText: '',
      creator: '',
      totalDays: 0,
      remainingDays: 0,
      progressDays: 0,
      attachments: [],
      level: '',
      description: '',
      progress: 0
    },
    progressList: [],
    projectMembers: [],
    projectTasks: [],
    loading: false
  },

  onLoad: function(options) {
    console.log('项目详情页面加载，参数:', options);
    
    if (options.project) {
      try {
        const project = JSON.parse(decodeURIComponent(options.project));
        console.log('解析后的项目数据:', project);
        
        // 验证项目数据的完整性
        if (!project || typeof project !== 'object') {
          throw new Error('项目数据格式无效');
        }
        
        this.setProjectInfo(project);
        this.generateProgressList(project);
        
        // 如果有项目ID，则从服务器获取最新数据
        if (project.id) {
          this.loadProjectDetail(project.id);
        } else {
          // 如果没有项目ID，直接加载员工列表
          this.loadEmployeeList();
        }
      } catch (error) {
        console.error('解析项目数据失败:', error);
        wx.showToast({
          title: '数据解析失败',
          icon: 'error'
        });
        
        // 设置默认数据，避免页面显示空白
        this.setDefaultProjectInfo();
      }
    } else if (options.projectId) {
      // 如果有项目ID，直接从服务器获取数据
      console.log('通过projectId获取项目详情:', options.projectId);
      this.loadProjectDetail(options.projectId);
    } else {
      console.error('未接收到项目数据或项目ID');
      wx.showToast({
        title: '未接收到项目数据',
        icon: 'error'
      });
      
      // 设置默认数据
      this.setDefaultProjectInfo();
    }
  },

  onShow: function() {
    // 检查是否需要刷新项目数据
    const app = getApp();
    if (app && app.globalData && app.globalData.needRefreshProjects) {
      console.log('检测到需要刷新项目数据，重新加载项目详情');
      
      // 重新加载项目详情
      if (this.data.projectInfo && this.data.projectInfo.id) {
        this.loadProjectDetail(this.data.projectInfo.id);
      }
      
      // 重置刷新标记
      app.globalData.needRefreshProjects = false;
    }
  },

  // 设置默认项目信息
  setDefaultProjectInfo: function() {
    this.setData({
      projectInfo: {
        name: '未找到项目',
        description: '暂无项目描述',
        startDate: '',
        endDate: '',
        status: '进行中',
        statusText: '进行中',
        creator: '未知',
        totalDays: 0,
        remainingDays: 0,
        progressDays: 0,
        attachments: [],
        level: '一般',
        progress: 0
      },
      progressList: []
    });
  },

  // 加载项目详情
  loadProjectDetail: function(projectId) {
    console.log('开始加载项目详情，项目ID:', projectId);
    this.setData({ loading: true });
    
    api.getProjectDetail(projectId)
      .then(res => {
        console.log('接口返回的原始数据:', res);
        this.setData({ loading: false });
        
        if (res && res.code === 200) {
          const projectData = res.data;
          console.log('获取到的项目详情:', projectData);
          
          // 调试项目数据结构
          if (projectData.projectInfo) {
            console.log('项目基本信息字段:', Object.keys(projectData.projectInfo));
            console.log('项目基本信息内容:', projectData.projectInfo);
          }
          
          // 使用新的数据处理方法
          this.setFullProjectData(projectData);
          
          // 加载员工列表，供编辑项目使用
          this.loadEmployeeList();
        } else {
          console.error('获取项目详情失败:', res);
          wx.showToast({
            title: res?.message || '获取项目详情失败',
            icon: 'error'
          });
          
          // 如果接口失败，设置默认数据
          this.setDefaultProjectInfo();
        }
      })
      .catch(error => {
        console.error('加载项目详情失败:', error);
        this.setData({ loading: false });
        wx.showToast({
          title: '网络请求失败',
          icon: 'error'
        });
        
        // 如果网络请求失败，设置默认数据
        this.setDefaultProjectInfo();
      });
  },

  // 设置项目信息
  setProjectInfo: function(project) {
    console.log('设置项目信息，原始数据:', project);
    
    // 确保日期格式正确
    const startDate = project.startDate ? new Date(project.startDate) : new Date();
    const endDate = project.endDate ? new Date(project.endDate) : new Date();
    const today = new Date();
    
    // 计算工期
    const totalDays = Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24));
    const progressDays = Math.ceil((today - startDate) / (1000 * 60 * 60 * 24));
    const remainingDays = Math.max(0, totalDays - progressDays);
    
    // 格式化日期
    const formatDate = (date) => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };
    
    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        'ongoing': '进行中',
        'pending': '待安排',
        'completed': '已完成',
        'overdue': '已逾期',
        '进行中': '进行中',
        '待安排': '待安排',
        '已完成': '已完成',
        '已逾期': '已逾期'
      };
      return statusMap[status] || status;
    };
    
    // 获取等级CSS类名
    const getLevelClass = (level) => {
      const levelMap = {
        '长期': 'long-term',
        '中期': 'medium-term',
        '短期': 'short-term',
        '一般': 'normal',
        '重要': 'important',
        '紧急': 'urgent'
      };
      return levelMap[level] || 'normal';
    };
    
    // 获取优先级CSS类名
    const getPriorityClass = (priority) => {
      const priorityMap = {
        '高': 'high',
        '中': 'medium',
        '低': 'low',
        '普通': 'normal',
        '紧急': 'urgent',
        '一般': 'normal'
      };
      return priorityMap[priority] || 'normal';
    };
    
    // 获取状态CSS类名
    const getStatusClass = (status) => {
      const statusMap = {
        'ongoing': 'ongoing',
        'pending': 'pending',
        'completed': 'completed',
        'overdue': 'overdue',
        '进行中': 'ongoing',
        '待安排': 'pending',
        '已完成': 'completed',
        '已逾期': 'overdue'
      };
      return statusMap[status] || 'ongoing';
    };
    
    const projectInfo = {
      id: project.id,
      name: project.name || '未命名项目',
      description: project.description || '暂无项目描述',
      startDate: formatDate(startDate),
      endDate: formatDate(endDate),
      status: project.status || '进行中',
      statusText: getStatusText(project.status),
      statusClass: getStatusClass(project.status),
      createName: project.createName || project.creator || project.manager || project.owner || project.createdBy || project.createUser || project.userName || project.user || project.author || '未知',
      managerName: project.managerName || project.manager || project.owner || '未设置',
      totalDays: totalDays,
      remainingDays: remainingDays,
      progressDays: Math.max(0, progressDays),
      attachments: project.attachments || [],
      level: project.level || '一般',
      levelClass: getLevelClass(project.level),
      priority: project.priority || '普通',
      priorityClass: getPriorityClass(project.priority),
      progress: project.progress || 0,
      topUp: project.topUp || 0  // 添加置顶状态字段
    };
    

    
    this.setData({
      projectInfo: projectInfo
    });
  },

  // 设置完整的项目数据
  setFullProjectData: function(data) {
    console.log('设置完整项目数据:', data);
    
    // 设置项目基本信息
    if (data.projectInfo) {
      this.setProjectInfo(data.projectInfo);
    }
    
    // 设置进度节点 - 从progressNodes获取
    if (data.progressNodes && Array.isArray(data.progressNodes)) {
      console.log('发现进度节点数据:', data.progressNodes);
      console.log('进度节点数量:', data.progressNodes.length);
      
      if (data.progressNodes.length > 0) {
        // 确保每个进度节点都有必要的字段
        const processedProgressNodes = data.progressNodes.map((node, index) => ({
          id: node.id || index + 1,
          date: node.date || node.createTime || new Date().toISOString().split('T')[0],
          progress: node.progress || node.progressPercentage || 0,
          description: node.description || node.content || node.progressContent || '进度更新'
        }));
        
        console.log('处理后的进度节点:', processedProgressNodes);
        
        this.setData({
          progressList: processedProgressNodes
        });
      } else {
        console.log('progressNodes数组为空');
        this.setData({
          progressList: []
        });
      }
    } else {
      console.log('没有progressNodes数据，生成默认进度');
      // 如果没有进度数据，生成默认进度
      if (data.projectInfo) {
        this.generateProgressList(data.projectInfo);
      }
    }
    
    // 设置项目成员 - 确保每个成员都有employeeId字段
    let projectMembers = [];
    
    if (data.projectMembers && Array.isArray(data.projectMembers)) {
      projectMembers = data.projectMembers;
    } else if (data.projectInfo && data.projectInfo.members && Array.isArray(data.projectInfo.members)) {
      console.log('从projectInfo.members获取到成员数据:', data.projectInfo.members);
      projectMembers = data.projectInfo.members;
    }
    
            // 处理项目成员数据，确保有employeeId字段
        if (projectMembers.length > 0) {
          const processedProjectMembers = projectMembers.map(member => ({
            id: member.id || member.employeeId || member.userId,
            employeeId: member.employeeId || member.id || member.userId, // 确保有employeeId字段
            name: member.name || member.employeeName || member.userName,
            department: member.department || member.dept || '未知部门',
            role: member.role || member.position || member.type || 'MEMBER',
            avatar: member.avatar || member.portrait || null
          }));
          
          this.setData({
            projectMembers: processedProjectMembers
          });
        } else {
          this.setData({
            projectMembers: []
          });
        }
    
    // 设置项目任务
    if (data.projectTasks && Array.isArray(data.projectTasks)) {
      this.setData({
        projectTasks: data.projectTasks
      });
    }
  },

  // 生成进度列表
  generateProgressList: function(project) {
    const progressList = [];
    const startDate = project.startDate ? new Date(project.startDate) : new Date();
    const endDate = project.endDate ? new Date(project.endDate) : new Date();
    const totalDays = Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24));
    
    // 生成进度记录（这里可以根据实际需求从API获取）
    for (let i = 0; i < Math.min(5, totalDays); i++) {
      const date = new Date(startDate.getTime() + i * 24 * 60 * 60 * 1000);
      progressList.push({
        id: i + 1,
        date: date.toISOString().split('T')[0],
        progress: Math.min(100, (i + 1) * 20),
        description: `第${i + 1}天进度更新`
      });
    }
    
    this.setData({
      progressList: progressList
    });
  },

  // 角色转换为中文
  convertRoleToChinese: function(role) {
    const roleMap = {
      'CREATOR': '创建者',
      'MANAGER': '负责人', 
      'MEMBER': '成员',
      '创建者': '创建者',
      '负责人': '负责人',
      '成员': '成员',
      'creator': '创建者',
      'manager': '负责人',
      'member': '成员'
    };
    return roleMap[role] || '成员';
  },

  // 加载员工列表
  loadEmployeeList: function() {
    console.log('项目详情页面 - 开始加载员工列表');
    
    const api = require('../../utils/api.js');
    api.getEmployeeList()
      .then(res => {
        console.log('项目详情页面 - 获取员工列表成功:', res);
        if (res.code === 200 && res.data) {
          // 将后端数据转换为前端需要的格式
          const teamMembers = res.data.map(employee => ({
            id: employee.id,
            name: employee.name,
            department: employee.department || '未知部门',
            role: employee.position || 'MEMBER',
            phone: employee.phone || '',
            email: employee.email || '',
            avatar: employee.avatar || null
          }));
          
          console.log('项目详情页面 - 员工数据转换完成:', teamMembers);
          
          this.setData({
            teamMembers: teamMembers
          });
        } else {
          console.error('项目详情页面 - 获取员工列表失败:', res.message);
        }
      })
      .catch(error => {
        console.error('项目详情页面 - 加载员工列表出错:', error);
        if (error.code === 401) {
          console.log('项目详情页面 - 用户未登录，跳转登录页');
          const auth = require('../../utils/auth.js');
          auth.redirectToLogin();
        }
      });
  },

  // 编辑项目
  editProject: function() {
    const { projectInfo, projectMembers, teamMembers } = this.data;
    
    // 构建完整的项目数据，包含编辑页面需要的所有字段
    const editProjectData = {
      id: projectInfo.id,
      name: projectInfo.name,
      description: projectInfo.description,
      startDate: projectInfo.startDate,
      endDate: projectInfo.endDate,
      level: projectInfo.level,
      priority: projectInfo.priority,
      status: projectInfo.status,
      attachments: projectInfo.attachments || [],
      // 优先使用projectMembers，如果没有则使用projectInfo.members
      invitedMembers: (projectMembers || projectInfo.members || []).map(member => ({
        id: member.id || member.employeeId, // 保持原有的id字段
        employeeId: member.employeeId || member.id, // 确保有employeeId字段
        name: member.name || member.employeeName,
        department: member.department || '未知部门',
        role: this.convertRoleToChinese(member.role || member.position || 'MEMBER'),
        avatar: member.avatar || null
      })),
      // 添加完整的员工列表，供编辑页面使用
      allTeamMembers: (teamMembers || []).map(member => ({
        id: member.id || member.employeeId, // 保持原有的id字段
        employeeId: member.employeeId || member.id, // 确保有employeeId字段
        name: member.name || member.employeeName,
        department: member.department || '未知部门',
        role: this.convertRoleToChinese(member.role || member.position || 'MEMBER'),
        avatar: member.avatar || null
      })),
      // 添加其他可能需要的字段
      managerName: projectInfo.managerName,
      createName: projectInfo.createName,
      progress: projectInfo.progress || 0
    };
    
    const projectData = encodeURIComponent(JSON.stringify(editProjectData));
    
    wx.navigateTo({
      url: `/pages/create-project/create-project?project=${projectData}`
    });
  },

  // 删除项目
  deleteProject: function() {
    const { projectInfo } = this.data;
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个项目吗？删除后无法恢复。',
      success: (res) => {
        if (res.confirm) {
          api.deleteProject(projectInfo.id)
            .then(res => {
              if (res.code === 200) {
                wx.showToast({
                  title: '项目已删除',
                  icon: 'success'
                });
                
                // 返回上一页
                setTimeout(() => {
                  wx.navigateBack();
                }, 1500);
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
  updateProgress: function() {
    const { projectInfo } = this.data;
    const projectData = encodeURIComponent(JSON.stringify(projectInfo));
    
    wx.navigateTo({
      url: `/pages/update-progress/update-progress?project=${projectData}`
    });
  },

  // 编辑进度节点
  editProgress: function(e) {
    const progressData = e.currentTarget.dataset.progress;
    const { projectInfo } = this.data;
    
    console.log('点击的进度节点:', progressData);
    
    // 准备传递给编辑页面的数据
    const editData = {
      progress: progressData,
      project: {
        id: projectInfo.id,
        name: projectInfo.name,
        description: projectInfo.description
      }
    };
    
    // 编码数据并跳转到进度编辑页面
    const encodedData = encodeURIComponent(JSON.stringify(editData));
    
    wx.navigateTo({
      url: `/pages/update-progress/update-progress?data=${encodedData}&mode=edit`
    });
  },

  // 查看附件
  viewAttachment: function(e) {
    const { attachment } = e.currentTarget.dataset;
    
    if (attachment.path) {
      wx.downloadFile({
        url: attachment.path,
        success: (res) => {
          wx.openDocument({
            filePath: res.tempFilePath,
            success: () => {
              console.log('打开文档成功');
            },
            fail: (error) => {
              console.error('打开文档失败:', error);
              wx.showToast({
                title: '无法打开此文件',
                icon: 'none'
              });
            }
          });
        },
        fail: (error) => {
          console.error('下载文件失败:', error);
          wx.showToast({
            title: '下载文件失败',
            icon: 'none'
          });
        }
      });
    } else {
      wx.showToast({
        title: '文件路径无效',
        icon: 'none'
      });
    }
  },

  // 显示操作菜单
  showActionMenu: function() {
    const { projectInfo } = this.data;
    
    // 根据当前置顶状态动态显示操作文本
    const topActionText = projectInfo.topUp === 1 ? '取消置顶' : '置顶项目';
    
    wx.showActionSheet({
      itemList: ['编辑项目', '更新进度', topActionText, '删除项目'],
      success: (res) => {
        switch (res.tapIndex) {
          case 0:
            this.editProject();
            break;
          case 1:
            this.updateProgress();
            break;
          case 2:
            this.toggleTopProject();
            break;
          case 3:
            this.deleteProject();
            break;
        }
      }
    });
  },

  // 置顶/取消置顶项目
  toggleTopProject: function() {
    const { projectInfo } = this.data;
    const isTop = projectInfo.topUp === 1;
    const newTopStatus = !isTop;
    
    wx.showLoading({
      title: newTopStatus ? '置顶中...' : '取消置顶中...'
    });
    
    // 调用API来置顶/取消置顶项目
    const api = require('../../utils/api.js');
    api.toggleProjectTop(projectInfo.id, newTopStatus)
      .then(res => {
        wx.hideLoading();
        if (res && res.code === 200) {
          // 更新本地数据
          this.setData({
            'projectInfo.topUp': newTopStatus ? 1 : 0
          });
          
          wx.showToast({
            title: newTopStatus ? '置顶成功' : '取消置顶成功',
            icon: 'success'
          });
          
          // 通知项目列表页面刷新数据
          const app = getApp();
          if (app.globalData) {
            app.globalData.needRefreshProjects = true;
          }
        } else {
          wx.showToast({
            title: res?.message || '操作失败',
            icon: 'error'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        console.error('置顶操作失败:', error);
        wx.showToast({
          title: '网络请求失败',
          icon: 'error'
        });
      });
  },

  // 分享项目
  shareProject: function() {
    const { projectInfo } = this.data;
    
    wx.showShareMenu({
      withShareTicket: true,
      menus: ['shareAppMessage', 'shareTimeline']
    });
  },

  // 返回上一页
  goBack: function() {
    wx.navigateBack();
  }
}) 