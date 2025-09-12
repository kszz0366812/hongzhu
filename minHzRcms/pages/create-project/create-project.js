// pages/create-project/create-project.js
const api = require('../../utils/api.js')
const auth = require('../../utils/auth.js')

Page({
  data: {
    isEditMode: false, // 添加编辑模式标识
    projectForm: {
      name: '',
      description: '',
      startDate: '',
      endDate: '',
      managerIndex: null,
      selectedManager: null,
      levelIndex: 0,
      priorityIndex: 1,
      statusIndex: 0,
      attachments: [],
      invitedMembers: []
    },
    teamMembers: [
      { id: 1, name: '张三', department: '技术部' },
      { id: 2, name: '李四', department: '产品部' },
      { id: 3, name: '王五', department: '设计部' },
      { id: 4, name: '赵六', department: '运营部' }
    ],
    levels: ['长期', '紧急', '重要', '一般'],
    priorities: ['低', '中', '高'],
    statuses: ['待启动', '进行中', '已完成'],
    submitting: false,
    // 邀请成员弹窗相关
    showInviteModal: false,
    availableMembers: [],
    invitedMemberIds: [],
    // 负责人选择弹窗相关
    showManagerModal: false

  },

  onLoad: function(options) {
    console.log('项目页面加载，参数:', options);
    
    // 判断是创建模式还是编辑模式
    const isEditMode = options.project ? true : false;
    
    // 设置编辑模式标识到data中
    this.setData({
      isEditMode: isEditMode
    });
    
    // 动态设置导航栏标题
    wx.setNavigationBarTitle({
      title: isEditMode ? '编辑项目' : '创建项目'
    });
    
    if (isEditMode) {
      // 编辑模式：解析传递过来的项目数据
      const project = JSON.parse(decodeURIComponent(options.project));
      console.log('编辑模式 - 解析的项目数据:', project);
      
      // 设置编辑模式的数据
      this.setEditModeData(project);
    } else {
      // 创建模式：设置默认开始日期为今天
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      const day = String(today.getDate()).padStart(2, '0');
      const defaultDate = `${year}-${month}-${day}`;
      
      this.setData({
        'projectForm.startDate': defaultDate
      });
    }

    // 加载员工列表
    this.loadEmployeeList();
  },

  // 设置编辑模式数据
  setEditModeData: function(project) {
    // 设置基本信息
    this.setData({
      'projectForm.id': project.id || '',
      'projectForm.name': project.name || '',
      'projectForm.description': project.description || '',
      'projectForm.startDate': project.startDate || '',
      'projectForm.endDate': project.endDate || '',
      'projectForm.attachments': project.attachments || [],
      'projectForm.invitedMembers': project.invitedMembers || []
    });
    
    // 设置员工列表，供邀请成员使用
    if (project.allTeamMembers && Array.isArray(project.allTeamMembers)) {
      this.setData({
        teamMembers: project.allTeamMembers
      });
      
      // 处理邀请成员数据
      if (project.invitedMembers && Array.isArray(project.invitedMembers)) {
        const processedInvitedMembers = project.invitedMembers.map(member => ({
          id: member.employeeId, 
          name: member.name,
          department: member.department || '未知部门',
          role: member.role || '成员',
          avatar: member.avatar
        }));
        
        this.setData({
          'projectForm.invitedMembers': processedInvitedMembers
        });
      }
    }
    
    // 设置项目等级索引 - 处理中英文映射
    let levelIndex = 0;
    if (project.level === '长期' || project.level === 'LONG_TERM') {
      levelIndex = 0;
    } else if (project.level === '紧急' || project.level === 'URGENT') {
      levelIndex = 1;
    } else if (project.level === '重要' || project.level === 'IMPORTANT') {
      levelIndex = 2;
    } else if (project.level === '一般' || project.level === 'NORMAL') {
      levelIndex = 3;
    }
    
    this.setData({
      'projectForm.levelIndex': levelIndex
    });
    
    // 设置优先级索引 - 处理中英文状态映射
    let priorityIndex = 1; // 默认中
    if (project.priority === '低' || project.priority === 'LOW') {
      priorityIndex = 0;
    } else if (project.priority === '中' || project.priority === 'MEDIUM') {
      priorityIndex = 1;
    } else if (project.priority === '高' || project.priority === 'HIGH') {
      priorityIndex = 2;
    }
    
    this.setData({
      'projectForm.priorityIndex': priorityIndex
    });
    
    // 设置状态索引 - 处理中英文状态映射
    let statusIndex = 0;
    if (project.status === '进行中' || project.status === 'ongoing') {
      statusIndex = 1;
    } else if (project.status === '已完成' || project.status === 'completed') {
      statusIndex = 2;
    } else if (project.status === '待安排' || project.status === 'pending') {
      statusIndex = 0;
    }
    
    this.setData({
      'projectForm.statusIndex': statusIndex
    });
    
    // 设置项目负责人（如果有的话）
    if (project.managerName) {
      this.setData({
        'projectForm.selectedManager': {
          id: project.id, // 临时ID，实际应该从后端获取
          name: project.managerName,
          department: '未知部门',
          role: '负责人'
        }
      });
    }
  },

  // 加载员工列表
  loadEmployeeList: function() {
    console.log('加载员工列表');
    
    // 显示加载提示
    wx.showLoading({
      title: '加载员工列表...'
    });

    api.getEmployeeList()
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200 && res.data) {
          console.log('员工列表加载成功:', res.data);
          
          // 转换数据格式，确保符合前端的数据结构
          const teamMembers = res.data.map(employee => ({
            id: employee.id,
            name: employee.name || employee.employeeName || '未知',
            department: employee.department || '未知部门',
            role: employee.position || employee.role || '员工'
          }));
          
          this.setData({
            teamMembers: teamMembers
          });
        } else {
          console.warn('员工列表返回数据异常:', res);
          // 使用默认数据
          wx.showToast({
            title: '员工列表加载失败，使用默认数据',
            icon: 'none'
          });
        }
      })
      .catch(error => {
        wx.hideLoading();
        console.error('加载员工列表失败:', error);
        
        // 如果是401错误，不显示错误提示，让auth模块处理
        if (error.statusCode !== 401) {
          wx.showToast({
            title: '员工列表加载失败',
            icon: 'none'
          });
        }
        
        // 保留默认数据，确保页面正常使用
        console.log('使用默认员工数据');
      });
  },

  // 更新表单数据
  updateForm: function(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`projectForm.${field}`]: value
    });
  },

  // 更新picker选择器数据
  updatePicker: function(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`projectForm.${field}`]: value
    });
  },

  // 选择开始日期
  selectStartDate: function(e) {
    this.setData({
      'projectForm.startDate': e.detail.value
    });
  },

  // 选择结束日期
  selectEndDate: function(e) {
    this.setData({
      'projectForm.endDate': e.detail.value
    });
  },

  // 选择负责人
  selectManager: function() {
    this.setData({
      showManagerModal: true
    });
  },

  // 选择优先级
  selectLevel: function(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({
      'projectForm.levelIndex': index
    });
  },

  // 选择状态
  selectStatus: function(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({
      'projectForm.statusIndex': index
    });
  },

  // 附件变化事件处理
  onAttachmentChange: function(e) {
    const { attachments } = e.detail;
    this.setData({
      'projectForm.attachments': attachments
    });
  },

  // 邀请成员 - 完全按照项目列表页面的方法实现
  inviteMembers: function() {
    const { teamMembers, projectForm } = this.data;
    
    // 获取已邀请成员的ID列表
    const invitedMemberIds = (projectForm.invitedMembers || []).map(m => m.id);
    // 显示所有团队成员，让用户可以看到已邀请和未邀请的状态
    const availableMembers = teamMembers;
    
    this.setData({
      showInviteModal: true,
      availableMembers: availableMembers,
      invitedMemberIds: invitedMemberIds
    });
  },

  // 移除成员 - 按照项目列表页面的逻辑
  removeMember: function(e) {
    const memberId = e.currentTarget.dataset.id;
    const invitedMembers = this.data.projectForm.invitedMembers.filter(m => m.id !== memberId);
    
    this.setData({
      'projectForm.invitedMembers': invitedMembers
    });
    
    wx.showToast({
      title: '已移除成员',
      icon: 'success'
    });
  },

  // 人员选择组件事件处理
  onInviteModalClose: function() {
    this.setData({
      showInviteModal: false,
      availableMembers: [],
      invitedMemberIds: []
    });
  },

  onInviteMemberSelect: function(e) {
    const { members } = e.detail;
    
    // 替换整个成员列表，而不是追加
    // 这样用户可以选择新的成员组合，系统会自动判断是邀请还是删除
    this.setData({
      'projectForm.invitedMembers': members
    });
    
    // 关闭弹窗
    this.setData({
      showInviteModal: false,
      availableMembers: [],
      invitedMemberIds: []
    });
    
    // 显示成功提示
    const memberNames = members.map(m => m.name).join('、');
    wx.showToast({
      title: `已更新成员列表：${memberNames}`,
      icon: 'success'
    });
  },

  // 负责人选择器事件处理
  onManagerModalClose: function() {
    this.setData({
      showManagerModal: false
    });
  },

  onManagerSelect: function(e) {
    const { member } = e.detail;
    
    if (!member) {
      wx.showToast({
        title: '请选择负责人',
        icon: 'none'
      });
      return;
    }
    
    // 设置选中的负责人
    this.setData({
      'projectForm.selectedManager': member,
      'projectForm.managerIndex': this.data.teamMembers.findIndex(m => m.id === member.id)
    });
    
    // 关闭弹窗
    this.setData({
      showManagerModal: false
    });
    
    // 显示成功提示
    wx.showToast({
      title: `已选择${member.name}为负责人`,
      icon: 'success'
    });
  },

  // 提交项目
  submitProject: function() {
    const { projectForm, teamMembers, levels, priorities, statuses } = this.data;
    
    // 表单验证
    if (!projectForm.name || !projectForm.name.trim()) {
      wx.showToast({
        title: '请输入项目名称',
        icon: 'none'
      });
      return;
    }
    
    if (!projectForm.startDate) {
      wx.showToast({
        title: '请选择开始时间',
        icon: 'none'
      });
      return;
    }
    
    if (!projectForm.endDate) {
      wx.showToast({
        title: '请选择结束时间',
        icon: 'none'
      });
      return;
    }

    // 验证结束时间不能早于开始时间
    if (new Date(projectForm.endDate) <= new Date(projectForm.startDate)) {
      wx.showToast({
        title: '结束时间不能早于开始时间',
        icon: 'none'
      });
      return;
    }

    if (this.data.submitting) {
      return;
    }

    this.setData({ submitting: true });

    // 先检查登录状态和token
    const checkAuthAndSubmit = () => {
      // 检查token是否存在
      const token = wx.getStorageSync('token');
      if (!token) {
        console.log('Token不存在，需要重新登录');
        const app = getApp();
        if (app && app.performGlobalSilentLogin) {
          return app.performGlobalSilentLogin()
            .then(() => {
              console.log('重新登录成功，继续创建项目');
              return this.submitProject();
            })
            .catch(error => {
              console.error('重新登录失败:', error);
              throw error;
            });
        } else {
          throw new Error('无法执行登录');
        }
      }

      // 获取用户信息
      return auth.getUserInfo()
        .then(userInfo => {
          if (!userInfo.employeeId) {
            console.log('用户未绑定员工号，getUserInfo已自动触发绑定流程');
            const app = getApp();
            app.globalData.pendingOperation = () => {
              console.log('员工绑定成功，继续创建项目');
              this.submitProject();
            };
            return Promise.reject(new Error('用户未绑定员工号'));
          }
          
          // 构建项目数据
          const managerName = projectForm.managerIndex !== null && projectForm.managerIndex !== undefined 
            ? teamMembers[projectForm.managerIndex].name 
            : '张经理';
          
          const levelText = levels[projectForm.levelIndex] || '一般';
          const priorityText = priorities[projectForm.priorityIndex] || '中';
          const statusText = statuses[projectForm.statusIndex] || '待启动';
          
          const projectData = {
            name: projectForm.name.trim(),
            description: projectForm.description ? projectForm.description.trim() : '',
            startDate: projectForm.startDate,
            endDate: projectForm.endDate,
            manager: managerName,
            level: levelText,
            priority: priorityText,
            status: statusText,
            managerId: userInfo.employeeId, // 使用当前用户作为项目负责人
            attachments: projectForm.attachments,
            invitedMembers: projectForm.invitedMembers.map(member => member.id) // 只传递ID列表
          };

          if (this.data.isEditMode) {
            // 编辑模式：添加项目ID
            projectData.id = projectForm.id;
            console.log('编辑项目数据:', projectData);
            // 调用更新项目API
            return api.updateProject(projectData);
          } else {
            // 创建模式
            console.log('新项目数据:', projectData);
            // 调用创建项目API
            return api.createProject(projectData);
          }
        });
    };

    checkAuthAndSubmit()
      .then(res => {
        this.setData({ submitting: false });
        
        if (res && res.code === 200) {
          if (this.data.isEditMode) {
            console.log('项目更新成功:', res);
            
            // 设置全局刷新标记，通知相关页面刷新数据
            const app = getApp();
            if (app && app.globalData) {
              app.globalData.needRefreshProjects = true;
              console.log('已设置全局刷新标记，通知相关页面刷新数据');
            }
            
            wx.showToast({
              title: '项目更新成功',
              icon: 'success',
              duration: 1500
            });

            // 编辑模式：直接跳转到项目列表页
            setTimeout(() => {
              wx.reLaunch({
                url: '/pages/project/project'
              });
            }, 1500);
          } else {
            console.log('项目创建成功:', res);
            
            wx.showToast({
              title: '项目创建成功',
              icon: 'success',
              duration: 1500
            });

            // 创建模式：返回上一页
            setTimeout(() => {
              wx.navigateBack();
            }, 1500);
          }
        } else {
          if (this.data.isEditMode) {
            console.error('项目更新失败:', res);
            wx.showToast({
              title: res?.message || '项目更新失败',
              icon: 'error'
            });
          } else {
            console.error('项目创建失败:', res);
            wx.showToast({
              title: res?.message || '项目创建失败',
              icon: 'error'
            });
          }
        }
      })
      .catch(error => {
        console.error('创建项目失败:', error);
        this.setData({ submitting: false });
        
        // 如果是401错误，直接静默重新登录，不显示任何提示
        if (error.statusCode === 401 || (error.message && error.message.includes('401'))) {
          console.log('检测到401错误，开始静默重新登录');
          
          // 清除本地存储的token
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          wx.removeStorageSync('openid');
          wx.removeStorageSync('unionid');
          
          // 静默重新登录
          const app = getApp();
          if (app && app.performGlobalSilentLogin) {
            app.performGlobalSilentLogin()
              .then(() => {
                console.log('静默重新登录成功，重新提交项目');
                // 重新提交项目
                this.submitProject();
              })
              .catch(loginError => {
                console.error('静默重新登录失败:', loginError);
                // 登录失败时才显示错误提示
                wx.showToast({
                  title: '登录失败，请重试',
                  icon: 'error'
                });
              });
          } else {
            wx.showToast({
              title: '登录失败，请重试',
              icon: 'error'
            });
          }
        } else {
          wx.showToast({
            title: error.message || '网络请求失败',
            icon: 'error'
          });
        }
      });
  }
}) 