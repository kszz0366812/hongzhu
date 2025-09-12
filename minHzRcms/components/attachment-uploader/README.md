# 附件上传组件 (attachment-uploader)

## 概述

这是一个可复用的附件上传组件，支持文件选择、删除、大小限制和类型限制功能。

## 使用方法

### 1. 在页面配置中引入组件

```json
{
  "usingComponents": {
    "attachment-uploader": "/components/attachment-uploader/attachment-uploader"
  }
}
```

### 2. 在WXML中使用组件

```xml
<attachment-uploader 
  attachments="{{attachments}}"
  maxCount="{{5}}"
  maxFileSize="{{50}}"
  allowedTypes="{{['pdf', 'doc', 'docx', 'jpg', 'png']}}"
  bind:change="onAttachmentChange"
/>
```

### 3. 在JS中处理事件

```javascript
Page({
  data: {
    attachments: []
  },

  // 附件变化事件处理
  onAttachmentChange: function(e) {
    const { attachments } = e.detail;
    this.setData({
      attachments: attachments
    });
  }
})
```

## 属性说明

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| attachments | Array | [] | 附件列表数据 |
| maxCount | Number | 5 | 最大文件数量 |
| disabled | Boolean | false | 是否禁用 |
| title | String | '附件' | 组件标题 |
| allowedTypes | Array | ['*'] | 允许的文件类型（扩展名） |
| maxFileSize | Number | 50 | 最大文件大小（MB） |

## 事件说明

| 事件名 | 说明 | 参数 |
|--------|------|------|
| change | 附件列表变化时触发 | { attachments: Array } |

## 附件数据结构

```javascript
{
  name: "文件名.pdf",
  size: "1.2MB", 
  path: "文件路径"
}
```

## 示例

### 基本用法

```xml
<view class="form-item">
  <text class="label">附件</text>
  <attachment-uploader 
    attachments="{{formData.attachments}}"
    maxCount="{{5}}"
    bind:change="onAttachmentChange"
  />
</view>
```

### 限制文件类型和大小

```xml
<attachment-uploader 
  attachments="{{attachments}}"
  maxCount="{{3}}"
  maxFileSize="{{10}}"
  allowedTypes="{{['pdf', 'doc', 'docx']}}"
  bind:change="onAttachmentChange"
/>
```

### 禁用状态

```xml
<attachment-uploader 
  attachments="{{attachments}}"
  maxCount="{{5}}"
  disabled="{{true}}"
/>
```

## 样式定制

组件使用统一的样式，与其他页面保持一致。如需定制样式，可以修改 `attachment-uploader.wxss` 文件。

## 功能特性

- ✅ 文件选择和删除
- ✅ 文件数量限制
- ✅ 文件大小限制
- ✅ 文件类型限制
- ✅ 统一的UI样式
- ✅ 错误提示
- ✅ 事件通知 