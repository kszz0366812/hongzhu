// pages/create-report/create-report.js
const api = require('../../utils/api.js')
const auth = require('../../utils/auth.js')

Page({
  data: {
    reportForm: {
      type: 'daily',
      title: '',
      date: '',
      content: '',
      issues: '',
      plan: '',
      attachments: []
    },
    
    submitting: false
  },

  onLoad: function(options) {
    // 设置默认日期为今天
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    const defaultDate = `${year}-${month}-${day}`;
    
    // 生成默认标题
    const defaultTitle = this.generateDefaultTitle('daily', defaultDate);
    
    // 检查是否是编辑模式
    if (options.mode === 'edit' && options.report) {
      try {
        const reportData = JSON.parse(decodeURIComponent(options.report));
        
        this.setData({
          'reportForm.type': reportData.type === '日报' ? 'daily' : 'weekly',
          'reportForm.title': reportData.title,
          'reportForm.date': reportData.reportDate || reportData.date,
          'reportForm.content': reportData.content || reportData.workContent || '',
          'reportForm.issues': reportData.issues || '',
          'reportForm.plan': reportData.plan || reportData.nextPlan || '',
          'reportForm.attachments': reportData.attachments || [],
          editingReportId: reportData.id
        });
      } catch (error) {
        console.error('解析报告数据失败:', error);
      }
    } else {
      // 新建模式
      this.setData({
        'reportForm.date': defaultDate,
        'reportForm.title': defaultTitle,
        editingReportId: null
      });
    }
  },

  // 生成默认标题
  generateDefaultTitle: function(type, date) {
    const dateObj = new Date(date);
    const year = dateObj.getFullYear();
    const month = String(dateObj.getMonth() + 1).padStart(2, '0');
    const day = String(dateObj.getDate()).padStart(2, '0');
    const dateStr = `${year}${month}${day}`;
    
    const typeText = type === 'daily' ? '日报' : '周报';
    return `${dateStr}${typeText}`;
  },

  // 选择报告类型
  selectType: function(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({
      'reportForm.type': type
    });
    
    // 如果是新建模式且标题是默认标题，则更新标题
    if (!this.data.editingReportId) {
      const currentTitle = this.data.reportForm.title;
      const currentDate = this.data.reportForm.date;
      
      // 检查当前标题是否是默认格式（YYYYMMDD日报/周报）
      const defaultTitlePattern = /^\d{8}(日报|周报)$/;
      if (defaultTitlePattern.test(currentTitle)) {
        const newTitle = this.generateDefaultTitle(type, currentDate);
        this.setData({
          'reportForm.title': newTitle
        });
      }
    }
  },

  // 更新表单字段
  updateForm: function(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`reportForm.${field}`]: value
    });
  },

  // 附件变化事件处理
  onAttachmentChange: function(e) {
    const { attachments } = e.detail;
    this.setData({
      'reportForm.attachments': attachments
    });
  },

  // 提交报告
  submitReport: function() {
    const { reportForm } = this.data;
    
    // 表单验证
    if (!reportForm.title.trim()) {
      wx.showToast({
        title: '请输入报告标题',
        icon: 'none'
      });
      return;
    }
    
    if (!reportForm.date) {
      wx.showToast({
        title: '请选择报告日期',
        icon: 'none'
      });
      return;
    }
    
    if (!reportForm.content.trim()) {
      console.log('请填写工作内容');
      return;
    }

    if (this.data.submitting) {
      return;
    }

    // 获取员工ID - 自动处理登录和绑定
    auth.getUserInfo()
      .then(userInfo => {
        if (!userInfo.employeeId) {
          console.log('用户未绑定员工号，getUserInfo已自动触发绑定流程');
          // 保存当前操作，绑定成功后继续执行
          const app = getApp();
          app.globalData.pendingOperation = () => {
            console.log('员工绑定成功，继续提交报告');
            this.submitReport();
          };
          return; // getUserInfo会自动处理绑定
        }
        
        const authorId = userInfo.employeeId;
        this.submitReportWithAuthorId(authorId);
      })
             .catch(error => {
         console.error('获取用户信息失败:', error);
         // getUserInfo已经处理了登录失败和绑定失败的情况
         // 如果是绑定失败，用户会被强制停留在绑定页面，无法进行任何操作
         if (error.message.includes('用户未绑定员工号')) {
           console.log('用户未绑定员工号，已阻止操作，等待绑定完成');
           return;
         }
         // 其他错误（如登录失败）已经由getUserInfo处理
       });
    
    return; // 等待异步处理完成
  },
  
  // 使用authorId提交报告
  submitReportWithAuthorId: function(authorId) {
    const { reportForm } = this.data;
    
    this.setData({ submitting: true });

    // 构建报告数据（符合API接口格式）
    const reportData = {
      id: this.data.editingReportId || undefined, // 新建时不传id
      type: reportForm.type === 'daily' ? '1' : '2', // 1=日报, 2=周报
      title: reportForm.title.trim(),
      authorId: authorId, // 添加员工ID
      content: reportForm.content.trim(),
      issues: reportForm.issues.trim() || '',
      plan: reportForm.plan.trim() || '',
      reportDate: reportForm.date
    };

    // 根据是新建还是编辑调用不同的API
    const apiCall = this.data.editingReportId 
      ? api.updateReport(reportData)
      : api.createReport(reportData);

    apiCall
      .then(res => {
        console.log('提交报告成功:', res);
        
        if (res.code === 200) {
          wx.navigateBack();
        } else {
          wx.showToast({
            title: res.message || '提交失败',
            icon: 'error'
          });
        }
      })
      .catch(error => {
        console.error('提交报告失败:', error);
        wx.showToast({
          title: '网络请求失败',
          icon: 'error'
        });
      })

  }
}) 