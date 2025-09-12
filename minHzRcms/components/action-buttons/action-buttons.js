// components/action-buttons/action-buttons.js
Component({
  properties: {
    buttons: {
      type: Array,
      value: []
    }
  },

  lifetimes: {
    attached() {
      // 确保buttons数组存在且有效
      if (!this.data.buttons || !Array.isArray(this.data.buttons)) {
        this.setData({
          buttons: []
        });
      }
    }
  },

  observers: {
    'buttons': function(buttons) {
      // 当buttons数据变化时，确保数据有效
      if (!buttons || !Array.isArray(buttons)) {
        this.setData({
          buttons: []
        });
      }
    }
  },

  methods: {
    onButtonTap(e) {
      const index = e.currentTarget.dataset.index;
      const button = this.data.buttons[index];
      if (button) {
        this.triggerEvent('buttonTap', { button, index });
      }
    }
  }
}) 