// components/list-item-card/list-item-card.js
Component({
  properties: {
    item: {
      type: Object,
      value: {}
    }
  },
  
  lifetimes: {
    attached() {
      // 确保item数据完整性
      const item = this.data.item || {};
      if (!item.info) item.info = [];
      if (!item.actions) item.actions = [];
      if (!item.members) item.members = [];
      if (!item.title) item.title = '';
      if (!item.status) item.status = '';
      if (!item.priority) item.priority = '';
      if (!item.priorityClass) item.priorityClass = '';
      if (!item.summary) item.summary = '';
      if (!item.description) item.description = '';
      // 只有项目才需要进度条，任务和报告不需要
      if (item.progress === undefined && item.type === 'project') {
        item.progress = 0;
      }
      if (!item.progressLabel && item.type === 'project') item.progressLabel = '进度';
      // 成员区域默认不展开，需要用户点击才能展开
      if (item.showMembers === undefined) {
        item.showMembers = false;
      }
      
      this.setData({ item });
    }
  },
  
  methods: {
    onContentTap() {
      this.triggerEvent('contentTap', { item: this.data.item });
    },
    onActionTap(e) {
      const action = e.currentTarget.dataset.action;
      this.triggerEvent('actionTap', { action, item: this.data.item });
    },
    toggleMembers(e) {
      const itemId = e.currentTarget.dataset.id;
      this.triggerEvent('toggleMembers', { itemId, item: this.data.item });
    }
  }
}) 