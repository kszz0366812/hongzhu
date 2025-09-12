// components/attachment-uploader/attachment-uploader.js
Component({
  properties: {
    // 附件列表
    attachments: {
      type: Array,
      value: []
    },
    // 最大文件数量
    maxCount: {
      type: Number,
      value: 5
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      value: false
    },
    // 标题
    title: {
      type: String,
      value: '附件'
    },
    // 允许的文件类型
    allowedTypes: {
      type: Array,
      value: ['*'] // 默认允许所有类型
    },
    // 最大文件大小（MB）
    maxFileSize: {
      type: Number,
      value: 50
    }
  },

  data: {
    // 组件内部数据
  },

  methods: {
    // 选择文件
    chooseFiles: function() {
      if (this.data.disabled) {
        return;
      }

      const remainingCount = this.data.maxCount - this.data.attachments.length;
      if (remainingCount <= 0) {
        wx.showToast({
          title: `最多只能选择${this.data.maxCount}个文件`,
          icon: 'none'
        });
        return;
      }

      wx.chooseMessageFile({
        count: remainingCount,
        type: 'file',
        success: (res) => {
          const newAttachments = [];
          
          for (let file of res.tempFiles) {
            // 检查文件大小
            if (file.size > this.data.maxFileSize * 1024 * 1024) {
              wx.showToast({
                title: `文件 ${file.name} 超过${this.data.maxFileSize}MB限制`,
                icon: 'none'
              });
              continue;
            }
            
            // 检查文件类型
            if (!this.isFileTypeAllowed(file.name)) {
              wx.showToast({
                title: `文件 ${file.name} 类型不支持`,
                icon: 'none'
              });
              continue;
            }
            
            newAttachments.push({
              name: file.name,
              size: this.formatFileSize(file.size),
              path: file.path
            });
          }
          
          if (newAttachments.length > 0) {
            const currentAttachments = this.data.attachments;
            const updatedAttachments = [...currentAttachments, ...newAttachments];
            
            this.setData({
              attachments: updatedAttachments
            });

            // 触发事件通知父组件
            this.triggerEvent('change', {
              attachments: updatedAttachments
            });
          }
        },
        fail: (err) => {
          console.error('选择文件失败:', err);
          wx.showToast({
            title: '选择文件失败',
            icon: 'none'
          });
        }
      });
    },

    // 移除附件
    removeAttachment: function(e) {
      const index = e.currentTarget.dataset.index;
      const attachments = [...this.data.attachments];
      attachments.splice(index, 1);
      
      this.setData({
        attachments: attachments
      });

      // 触发事件通知父组件
      this.triggerEvent('change', {
        attachments: attachments
      });
    },

    // 格式化文件大小
    formatFileSize: function(size) {
      if (size < 1024) {
        return size + 'B';
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(1) + 'KB';
      } else {
        return (size / (1024 * 1024)).toFixed(1) + 'MB';
      }
    },

    // 检查文件类型是否允许
    isFileTypeAllowed: function(fileName) {
      if (this.data.allowedTypes.includes('*')) {
        return true;
      }
      
      const extension = fileName.split('.').pop().toLowerCase();
      return this.data.allowedTypes.includes(extension);
    }
  }
}) 