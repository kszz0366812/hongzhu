// pages/update-progress/update-progress.js
const api = require('../../utils/api.js')
const auth = require('../../utils/auth.js')

Page({
  data: {
    projectInfo: {},
    progressForm: {
      progress: 0,
      content: '',
      attachments: []
    },
    submitting: false,
    isEditMode: false,  // 是否为编辑模式
    editingProgressId: null  // 正在编辑的进度ID
  },

  onLoad: function(options) {
    console.log('页面参数:', options);
    
    // 检查是否为编辑模式
    if (options.mode === 'edit' && options.data) {
      this.setData({ isEditMode: true });
      
      try {
        const editData = JSON.parse(decodeURIComponent(options.data));
        console.log('编辑模式数据:', editData);
        
        if (editData.progress && editData.project) {
          this.setData({
            projectInfo: editData.project,
            editingProgressId: editData.progress.id,
            'progressForm.progress': editData.progress.progress || editData.project.progress || 0,
            'progressForm.content': editData.progress.description || '',
            'progressForm.attachments': editData.progress.attachments || []
          });
        }
      } catch (error) {
        console.error('解析编辑数据失败:', error);
        wx.showToast({
          title: '数据解析失败',
          icon: 'error'
        });
      }
    } else if (options.project) {
      // 兼容旧的传递方式 - 新增模式
      const project = JSON.parse(decodeURIComponent(options.project));
      console.log('项目数据:', project);
      this.setData({
        projectInfo: project,
        isEditMode: false
      });
    } else if (options.projectId) {
      // 新的传递方式：通过projectId获取项目详情 - 新增模式
      console.log('通过项目ID获取详情:', options.projectId);
      this.setData({ isEditMode: false });
      this.loadProjectDetail(options.projectId);
    }
  },

  // 加载项目详情
  loadProjectDetail: function(projectId) {
    wx.showLoading({
      title: '加载中...'
    });

    api.getProjectDetail(projectId)
      .then(res => {
        wx.hideLoading();
        
        if (res && res.code === 200) {
          const projectData = res.data;
          console.log('获取到的项目详情:', projectData);
          
          if (projectData.projectInfo) {
            this.setData({
              projectInfo: projectData.projectInfo
            });
          } else {
            console.error('项目详情数据格式错误');
            wx.showToast({
              title: '数据格式错误',
              icon: 'error'
            });
          }
        } else {
          console.error('获取项目详情失败:', res);
          wx.showToast({
            title: res?.message || '获取项目详情失败',
            icon: 'error'
          });
        }
      })
      .catch(error => {
        console.error('加载项目详情失败:', error);
        wx.hideLoading();
        wx.showToast({
          title: '网络请求失败',
          icon: 'error'
        });
      });
  },

  onReady: function() {
    // 在页面渲染完成后设置初始值
    if (this.data.projectInfo.progress !== undefined) {
      console.log('当前进度:', this.data.projectInfo.progress);
      // 设置默认值为当前项目进度
      this.setData({
        'progressForm.progress': this.data.projectInfo.progress
      });
    }
  },

  // 更新进度输入
  updateProgressInput: function(e) {
    const value = e.detail.value;
    this.setData({
      'progressForm.progress': value
    });
  },

  // 验证进度输入
  validateProgressInput: function(e) {
    const inputValue = e.detail.value;
    const currentProgress = this.data.projectInfo.progress || 0;
    
    // 如果输入框为空，使用当前进度
    if (!inputValue || inputValue.trim() === '') {
      this.setData({
        'progressForm.progress': currentProgress
      });
      return;
    }
    
    const value = parseInt(inputValue);
    
    if (isNaN(value)) {
      // 如果输入的不是数字，重置为当前进度
      this.setData({
        'progressForm.progress': currentProgress
      });
      wx.showToast({
        title: '请输入有效数字',
        icon: 'none'
      });
      return;
    }
    
    if (value < currentProgress) {
      // 如果小于当前进度，重置为当前进度
      this.setData({
        'progressForm.progress': currentProgress
      });
      wx.showToast({
        title: `进度不能低于当前进度(${currentProgress}%)`,
        icon: 'none'
      });
      return;
    }
    
    if (value > 100) {
      // 如果大于100，重置为100
      this.setData({
        'progressForm.progress': 100
      });
      wx.showToast({
        title: '进度不能超过100%',
        icon: 'none'
      });
      return;
    }
    
    // 验证通过，更新为输入的值
    this.setData({
      'progressForm.progress': value
    });
  },

  // 更新表单字段
  updateForm: function(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`progressForm.${field}`]: value
    });
  },

  // 附件变化事件处理
  onAttachmentChange: function(e) {
    const { attachments } = e.detail;
    this.setData({
      'progressForm.attachments': attachments
    });
  },

  // 提交进度
  submitProgress: function() {
    const { projectInfo, progressForm, isEditMode, editingProgressId } = this.data;
    
    // 表单验证
    if (progressForm.progress === '' || progressForm.progress === null || progressForm.progress === undefined) {
      wx.showToast({
        title: '请输入进度',
        icon: 'none'
      });
      return;
    }
    
    const progress = parseInt(progressForm.progress);
    if (isNaN(progress) || progress < 0 || progress > 100) {
      wx.showToast({
        title: '请输入有效的进度值(0-100)',
        icon: 'none'
      });
      return;
    }
    
    // 检查进度是否小于项目当前进度
    const currentProgress = projectInfo.progress || 0;
    if (progress < currentProgress) {
      wx.showToast({
        title: `进度不能低于当前进度(${currentProgress}%)`,
        icon: 'none'
      });
      return;
    }
    
    if (this.data.submitting) {
      return;
    }

    this.setData({ submitting: true });

    // 获取用户信息
    auth.getUserInfo()
      .then(userInfo => {
        if (!userInfo.employeeId) {
          console.log('用户未绑定员工号，getUserInfo已自动触发绑定流程');
          const app = getApp();
          app.globalData.pendingOperation = () => {
            console.log('员工绑定成功，继续更新进度');
            this.submitProgress();
          };
          return;
        }
        
        // 构建进度数据
        const progressData = {
          progress: progress,
          description: progressForm.content ? progressForm.content.trim() : ''
        };

        console.log(`${isEditMode ? '编辑' : '创建'}进度数据:`, progressData);

        // 根据模式调用不同的API
        if (isEditMode) {
          // 编辑模式：调用更新进度记录API
          return api.updateProgressRecord(editingProgressId, progressData);
        } else {
          // 新增模式：调用创建进度API
          return api.createProjectProgress(projectInfo.id, progressData);
        }
      })
      .then(res => {
        this.setData({ submitting: false });
        
        if (res && res.code === 200) {
          console.log(`进度${isEditMode ? '更新' : '提交'}成功:`, res);
          
          // 设置全局刷新标记，通知项目管理页面需要刷新数据
          const app = getApp();
          if (app.globalData) {
            app.globalData.needRefreshProjects = true;
            console.log('设置全局刷新标记，项目管理页面将在返回时刷新数据');
          }
          
          wx.showToast({
            title: `进度${isEditMode ? '更新' : '提交'}成功`,
            icon: 'success',
            duration: 2000
          });

          // 延迟返回上一页
          setTimeout(() => {
            wx.navigateBack();
          }, 2000);
        } else {
          console.error(`进度${isEditMode ? '更新' : '提交'}失败:`, res);
          wx.showToast({
            title: res?.message || `进度${isEditMode ? '更新' : '提交'}失败`,
            icon: 'error'
          });
        }
      })
      .catch(error => {
        console.error(`${isEditMode ? '更新' : '提交'}进度失败:`, error);
        this.setData({ submitting: false });
        wx.showToast({
          title: '网络请求失败',
          icon: 'error'
        });
      });
  },

  // 返回上一页
  goBack: function() {
    wx.navigateBack();
  }
}) 