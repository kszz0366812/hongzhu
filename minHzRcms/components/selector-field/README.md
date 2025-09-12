# selector-field 选择器字段组件

## 功能描述
通用的选择器字段组件，用于统一样式的选择框展示。适用于各种需要点击选择的场景。

## 使用方法

### 基础用法
```xml
<selector-field 
  placeholder="请选择"
  selectedText="{{selectedValue}}"
  bind:tap="openSelector"
/>
```

### 必填字段
```xml
<selector-field 
  placeholder="请选择项目负责人"
  selectedText="{{selectedManager ? selectedManager.name : ''}}"
  required="{{true}}"
  bind:tap="selectManager"
/>
```

### 禁用状态
```xml
<selector-field 
  placeholder="不可选择"
  selectedText=""
  disabled="{{true}}"
/>
```

## 属性说明

| 属性名 | 类型 | 默认值 | 必填 | 说明 |
|--------|------|--------|------|------|
| placeholder | String | '请选择' | 否 | 未选择时的占位符文本 |
| selectedText | String | '' | 否 | 已选择时显示的文本 |
| disabled | Boolean | false | 否 | 是否禁用选择器 |
| required | Boolean | false | 否 | 是否显示必填标记(*) |

## 事件说明

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| tap | 点击选择器时触发 | 无 |

## 样式特点
- 统一的边框和圆角样式
- 点击时的视觉反馈效果
- 禁用状态的灰色显示
- 响应式的箭头指示器
- 必填字段的红色星号标记

## 使用场景
- 人员选择字段
- 项目选择字段
- 状态选择字段
- 任何需要点击弹出选择器的字段

## 示例代码

```xml
<!-- 在页面中使用 -->
<view class="form-item">
  <text class="label">项目负责人</text>
  <selector-field 
    placeholder="请选择项目负责人"
    selectedText="{{selectedManager ? selectedManager.name : ''}}"
    bind:tap="selectManager"
  />
</view>
```

```javascript
// 在页面JS中处理点击事件
selectManager: function() {
  // 打开选择器弹窗
  this.setData({
    showManagerModal: true
  });
}
```

## 组件配置

在页面的JSON文件中注册组件：

```json
{
  "usingComponents": {
    "selector-field": "/components/selector-field/selector-field"
  }
}
```