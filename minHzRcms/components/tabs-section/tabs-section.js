// components/tabs-section/tabs-section.js
Component({
  properties: {
    // 单级标签（兼容原有功能）
    tabs: {
      type: Array,
      value: []
    },
    activeTab: {
      type: String,
      value: ''
    },
    // 二级标签功能
    primaryTabs: {
      type: Array,
      value: []
    },
    activePrimaryTab: {
      type: String,
      value: ''
    },
    secondaryTabs: {
      type: Array,
      value: []
    },
    activeSecondaryTab: {
      type: String,
      value: ''
    }
  },
  
  methods: {
    // 单级标签点击事件
    onTabTap: function(e) {
      const key = e.currentTarget.dataset.key;
      this.triggerEvent('tabChange', { key });
    },

    // 一级标签点击事件
    onPrimaryTabTap: function(e) {
      const key = e.currentTarget.dataset.key;
      this.triggerEvent('primaryTabChange', { key });
    },

    // 二级标签点击事件
    onSecondaryTabTap: function(e) {
      const key = e.currentTarget.dataset.key;
      this.triggerEvent('secondaryTabChange', { key });
    }
  }
}) 