// components/stats-section/stats-section.js
Component({
  properties: {
    stats: {
      type: Array,
      value: []
    },
    layout: {
      type: String,
      value: 'grid3' // 默认3列布局，可选 'grid3' 或 'grid4'
    }
  },

  lifetimes: {
    attached() {
      // 确保stats数组存在且有效
      if (!this.data.stats || !Array.isArray(this.data.stats)) {
        this.setData({
          stats: []
        });
      }
    }
  },

  observers: {
    'stats': function(stats) {
      // 当stats数据变化时，确保数据有效
      if (!stats || !Array.isArray(stats)) {
        this.setData({
          stats: []
        });
      }
    }
  }
}) 