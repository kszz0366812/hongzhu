// components/project-member-manager/project-member-manager.js
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
      value: '管理项目成员'
    },
    // 搜索框占位符
    placeholder: {
      type: String,
      value: '搜索成员...'
    },
    // 所有可用的员工列表
    allEmployees: {
      type: Array,
      value: []
    },
    // 当前已选择的成员列表
    selectedMembers: {
      type: Array,
      value: []
    }
  },

  data: {
    searchText: '',
    filteredEmployees: [],
    tempSelectedMembers: [], // 临时存储已选择的成员对象
    memberSelectedStatus: {}, // 存储每个成员的选中状态
    scrollTop: 0 // 滚动位置
  },

  observers: {
    'show, allEmployees, selectedMembers': function(show, allEmployees, selectedMembers) {
      if (show) {
        console.log('Project member manager opened:', {
          show,
          allEmployeesCount: allEmployees.length,
          selectedMembers,
          selectedMembersType: typeof selectedMembers,
          allEmployees: allEmployees
        });
        
        // 确保 selectedMembers 是数组
        let selectedMembersArray = [];
        if (Array.isArray(selectedMembers)) {
          selectedMembersArray = selectedMembers;
        }
        
        console.log('Processed selectedMembers:', selectedMembersArray);
        
        // 根据ID找到对应的员工对象
        const selectedEmployeeObjects = allEmployees.filter(employee => 
          selectedMembersArray.some(selectedMember => selectedMember.id === employee.id)
        );
        
        console.log('Selected employee objects:', selectedEmployeeObjects);
        console.log('All employees array:', allEmployees);
        console.log('Selected members array:', selectedMembersArray);
        
        this.setData({
          searchText: '',
          filteredEmployees: allEmployees,
          tempSelectedMembers: [...selectedEmployeeObjects]
        }, () => {
          // 在setData完成后更新成员选中状态
          this.updateMemberSelectedStatus();
          
          // 再次检查数据是否正确设置
          console.log('After setData and updateMemberSelectedStatus:', {
            tempSelectedMembers: this.data.tempSelectedMembers,
            memberSelectedStatus: this.data.memberSelectedStatus,
            selectedMembers: selectedMembersArray
          });
        });
      }
    }
  },

  methods: {
    // 更新成员选中状态
    updateMemberSelectedStatus: function() {
      const memberSelectedStatus = {};
      this.data.allEmployees.forEach(employee => {
        memberSelectedStatus[employee.id] = this.data.tempSelectedMembers.some(
          selectedMember => selectedMember.id === employee.id
        );
      });
      this.setData({ memberSelectedStatus });
    },

    // 关闭弹窗
    onClose: function() {
      this.triggerEvent('close');
    },

    // 阻止事件冒泡
    stopPropagation: function(e) {
      if (e && e.stopPropagation) {
        e.stopPropagation();
      }
    },

    // 处理滚动区域的触摸事件
    onScrollTouchMove: function(e) {
      // 允许滚动区域内的触摸事件，但不阻止冒泡
    },

    // 处理滚动事件
    onScroll: function(e) {
      console.log('Scroll event:', e.detail);
    },

    // 搜索输入
    onSearchInput: function(e) {
      const searchText = e.detail.value;
      const allEmployees = this.data.allEmployees;
      
      let filteredEmployees = allEmployees;
      if (searchText.trim()) {
        filteredEmployees = allEmployees.filter(employee => 
          employee.name.toLowerCase().includes(searchText.toLowerCase())
        );
      }
      
      this.setData({
        searchText: searchText,
        filteredEmployees: filteredEmployees
      });
    },

    // 选择成员
    onSelectMember: function(e) {
      const { member } = e.currentTarget.dataset;
      
      console.log('Member selected:', member);
      
      const tempSelectedMembers = [...this.data.tempSelectedMembers];
      const index = tempSelectedMembers.findIndex(selectedMember => selectedMember.id === member.id);
      
      if (index > -1) {
        tempSelectedMembers.splice(index, 1);
        console.log('Member removed:', member.name);
      } else {
        tempSelectedMembers.push(member);
        console.log('Member added:', member.name);
      }
      
      console.log('Updated tempSelectedMembers:', tempSelectedMembers);
      
      this.setData({
        tempSelectedMembers: tempSelectedMembers
      }, () => {
        // 更新成员选中状态
        this.updateMemberSelectedStatus();
      });
    },

    // 确认选择
    onConfirm: function() {
      const selectedMembers = this.data.tempSelectedMembers;
      
      if (selectedMembers.length === 0) {
        wx.showToast({
          title: '请至少选择一名成员',
          icon: 'none'
        });
        return;
      }
      
      // 触发确认事件，传递选择的成员
      this.triggerEvent('confirm', { members: selectedMembers });
    },

    // 取消选择
    onCancel: function() {
      this.triggerEvent('close');
    }
  }
}) 