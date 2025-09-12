// components/selector-field/selector-field.js
Component({
  properties: {
    // 占位符文本
    placeholder: {
      type: String,
      value: '请选择'
    },
    // 已选择的显示文本
    selectedText: {
      type: String,
      value: ''
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      value: false
    },
    // 是否必填
    required: {
      type: Boolean,
      value: false
    }
  },

  methods: {
    // 点击选择器
    onTap: function() {
      if (this.data.disabled) {
        return;
      }
      this.triggerEvent('tap');
    }
  }
})