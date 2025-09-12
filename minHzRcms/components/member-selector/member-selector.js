// components/member-selector/member-selector.js
Component({
  properties: {
    // 是否显示弹窗
    show: {
      type: Boolean,
      value: false
    },
    // 弹窗标题
    title: {
      type: String,
      value: '选择成员'
    },
    // 搜索框占位符
    placeholder: {
      type: String,
      value: '搜索成员...'
    },
    // 成员列表数据
    members: {
      type: Array,
      value: []
    },
    // 是否多选模式
    multiple: {
      type: Boolean,
      value: false
    },
    // 已选择的成员ID列表（多选模式）
    selectedIds: {
      type: Array,
      value: []
    },
    // 是否显示空状态
    showEmptyState: {
      type: Boolean,
      value: true
    },
    // 空状态文本
    emptyText: {
      type: String,
      value: '未找到匹配的成员'
    },
    // 确认按钮文本
    confirmText: {
      type: String,
      value: '确定'
    }
  },

  data: {
    searchText: '',
    filteredMembers: [],
    tempSelectedMembers: [], // 临时存储已选择的成员对象
    memberSelectedStatus: {}, // 存储每个成员的选中状态
    scrollTop: 0 // 滚动位置
  },

  observers: {
    'show, members, selectedIds': function(show, members, selectedIds) {
      if (show && members && Array.isArray(members)) {
        // 确保 selectedIds 是数组
        let selectedIdsArray = [];
        if (Array.isArray(selectedIds)) {
          selectedIdsArray = selectedIds;
        } else if (typeof selectedIds === 'string' && selectedIds) {
          // 如果是字符串，尝试解析
          selectedIdsArray = selectedIds.split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id));
        } else if (selectedIds && typeof selectedIds === 'number') {
          selectedIdsArray = [selectedIds];
        }
        
        // 根据ID找到对应的成员对象
        // 支持多种ID字段名：id, employeeId, userId等
        const selectedMembers = members.filter(member => {
          const memberId = member.id || member.employeeId || member.userId;
          return selectedIdsArray.indexOf(memberId) >= 0;
        });
        
        // 确保所有成员都有有效的ID字段
        const validMembers = members.filter(member => {
          const memberId = member.id || member.employeeId || member.userId;
          return member && memberId !== undefined;
        });
        
        this.setData({
          searchText: '',
          filteredMembers: validMembers,
          tempSelectedMembers: this.data.multiple ? [...selectedMembers] : []
        }, () => {
          // 在setData完成后更新成员选中状态
          this.updateMemberSelectedStatus();
        });
      }
    }
  },

  methods: {
    // 更新成员选中状态
    updateMemberSelectedStatus: function() {
      const memberSelectedStatus = {};
      this.data.members.forEach(member => {
        const memberId = member.id || member.employeeId || member.userId;
        if (memberId !== undefined) {
          memberSelectedStatus[memberId] = this.data.tempSelectedMembers.some(
            selectedMember => {
              const selectedMemberId = selectedMember.id || selectedMember.employeeId || selectedMember.userId;
              return selectedMemberId === memberId;
            }
          );
        }
      });
      this.setData({ memberSelectedStatus });
    },

    // 关闭弹窗
    onClose: function() {
      this.triggerEvent('close');
    },

    // 阻止事件冒泡
    stopPropagation: function(e) {
      // 阻止事件冒泡到父级
      if (e && e.stopPropagation) {
        e.stopPropagation();
      }
    },

    // 处理滚动区域的触摸事件
    onScrollTouchMove: function(e) {
      // 允许滚动区域内的触摸事件，但不阻止冒泡
      // 这样scroll-view可以正常滚动，但不会穿透到外部
    },

    // 处理滚动事件
    onScroll: function(e) {
      // 滚动事件处理
    },

    // 搜索输入
    onSearchInput: function(e) {
      const searchText = e.detail.value;
      const members = this.data.members;
      
      let filteredMembers = members;
      if (searchText.trim()) {
        filteredMembers = members.filter(member => 
          member.name.toLowerCase().includes(searchText.toLowerCase())
        );
      }
      
      this.setData({
        searchText: searchText,
        filteredMembers: filteredMembers
      });
    },

    // 选择成员
    onSelectMember: function(e) {
      const { member } = e.currentTarget.dataset;
      
      if (this.data.multiple) {
        // 多选模式
        const tempSelectedMembers = [...this.data.tempSelectedMembers];
        const memberId = member.id || member.employeeId || member.userId;
        const index = tempSelectedMembers.findIndex(selectedMember => {
          const selectedMemberId = selectedMember.id || selectedMember.employeeId || selectedMember.userId;
          return selectedMemberId === memberId;
        });
        
        if (index > -1) {
          tempSelectedMembers.splice(index, 1);
        } else {
          tempSelectedMembers.push(member);
        }
        
        this.setData({
          tempSelectedMembers: tempSelectedMembers
        }, () => {
          // 更新成员选中状态
          this.updateMemberSelectedStatus();
        });
      } else {
        // 单选模式
        this.triggerEvent('select', { member: member });
      }
    },

    // 确认选择（多选模式）
    onConfirm: function() {
      if (this.data.multiple) {
        const selectedMembers = this.data.tempSelectedMembers;
        this.triggerEvent('select', { members: selectedMembers });
        // 关闭弹窗
        this.triggerEvent('close');
      }
    },

    // 取消选择
    onCancel: function() {
      // 重置为初始状态
      const selectedIds = this.data.selectedIds || [];
      const selectedMembers = this.data.members.filter(member => {
        const memberId = member.id || member.employeeId || member.userId;
        return selectedIds.indexOf(memberId) >= 0;
      });
      
      this.setData({
        tempSelectedMembers: selectedMembers
      });
      this.triggerEvent('close');
    }
  }
}) 