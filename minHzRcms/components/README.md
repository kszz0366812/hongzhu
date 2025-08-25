# 组件使用说明

本文档介绍了项目中使用的各个组件的使用方法。

## 1. stats-section 统计区域组件

### 功能
显示统计数据，通常用于页面顶部的统计信息展示。

### 使用方法
```xml
<stats-section stats="{{statsData}}" />
```

### 数据格式
```javascript
statsData: [
  { number: 5, label: '总项目' },
  { number: 2, label: '进行中' },
  { number: 1, label: '已逾期' },
  { number: 2, label: '已完成' }
]
```

## 2. action-buttons 操作按钮组件

### 功能
显示操作按钮，通常用于创建、编辑等操作。

### 使用方法
```xml
<action-buttons buttons="{{actionButtons}}" bind:buttonTap="onActionButtonTap" />
```

### 数据格式
```javascript
actionButtons: [
  { text: '+ 创建项目', type: 'secondary' },
  { text: '批量操作', type: 'primary' }
]
```

### 事件处理
```javascript
onActionButtonTap: function(e) {
  const { button } = e.detail;
  // 处理按钮点击事件
}
```

## 3. tabs-section 标签页组件

### 功能
显示标签页，支持单级标签和二级标签。

### 使用方法

#### 单级标签（原有功能）
```xml
<tabs-section tabs="{{tabsData}}" activeTab="{{activeTab}}" bind:tabChange="onTabChange" />
```

#### 二级标签（新功能）
```xml
<tabs-section 
  primaryTabs="{{primaryTabsData}}" 
  activePrimaryTab="{{activePrimaryTab}}"
  secondaryTabs="{{secondaryTabsData}}" 
  activeSecondaryTab="{{activeSecondaryTab}}"
  bind:primaryTabChange="onPrimaryTabChange"
  bind:secondaryTabChange="onSecondaryTabChange"
/>
```

### 数据格式

#### 单级标签
```javascript
tabsData: [
  { key: 'all', label: '全部' },
  { key: 'ongoing', label: '进行中' },
  { key: 'completed', label: '已完成' }
]
```

#### 二级标签
```javascript
// 一级标签数据
primaryTabsData: [
  { key: 'myTasks', label: '我的任务' },
  { key: 'assignedByMe', label: '我指派的' },
  { key: 'assignedByOthers', label: '指派给我的' }
],

// 二级标签数据
secondaryTabsData: [
  { key: 'all', label: '全部' },
  { key: 'ongoing', label: '进行中' },
  { key: 'pending', label: '待处理' },
  { key: 'completed', label: '已完成' }
]
```

### 事件处理
```javascript
// 单级标签事件
onTabChange: function(e) {
  const { key } = e.detail;
  // 处理标签切换
}

// 二级标签事件
onPrimaryTabChange: function(e) {
  const { key } = e.detail;
  // 处理一级标签切换
}

onSecondaryTabChange: function(e) {
  const { key } = e.detail;
  // 处理二级标签切换
}
```

## 4. list-item-card 列表项卡片组件

### 功能
显示列表项，支持项目、任务、报告等多种类型的数据展示。

### 使用方法
```xml
<list-item-card
  item="{{item}}"
  bind:contentTap="onContentTap"
  bind:actionTap="onActionTap"
  bind:toggleMembers="onToggleMembers"
/>
```

### 数据格式
```javascript
item: {
  id: 1,
  title: '项目标题',
  priority: '高',
  priorityClass: 'high',
  status: '进行中',
  info: [
    { label: '项目周期', value: '2024-01-01 至 2024-12-31' },
    { label: '项目简介', value: '项目描述', isDescription: true }
  ],
  progress: 65, // 可选，用于项目进度显示
  progressLabel: '项目进度', // 可选，进度标签
  members: [
    { id: 1, name: '张三' },
    { id: 2, name: '李四' }
  ],
  showMembers: false,
  actions: [
    { text: '编辑', type: '' },
    { text: '删除', type: 'delete' }
  ],
  description: '任务描述内容', // 可选，用于任务描述显示
  summary: '报告摘要内容' // 可选，仅报告类型使用
}
```

### 事件处理
```javascript
onContentTap: function(e) {
  const item = e.detail.item;
  // 处理内容点击事件
}

onActionTap: function(e) {
  const { action } = e.detail;
  const item = e.detail.item;
  // 处理操作按钮点击事件
}

onToggleMembers: function(e) {
  const itemId = e.detail.id;
  // 处理成员展开/收起事件
}
```

## 5. empty-state 空状态组件

### 功能
当没有数据时显示的空状态提示。

### 使用方法
```xml
<empty-state show="{{showEmpty}}" text="暂无数据" icon="📋" />
```

### 属性说明
- `show`: 是否显示空状态
- `text`: 提示文本
- `icon`: 图标（可选）

## 6. member-selector 人员选择组件

### 功能
显示人员选择弹窗，采用微信原生风格的底部弹出框，用于任务指派、项目成员选择等场景。支持搜索功能，支持单选和多选两种模式。

### 使用方法

#### 单选模式（任务指派）
```xml
<member-selector 
  show="{{showSelector}}"
  title="指派任务"
  placeholder="搜索成员..."
  members="{{membersList}}"
  multiple="{{false}}"
  bind:close="onSelectorClose"
  bind:select="onMemberSelect"
/>
```

#### 多选模式（项目成员邀请）
```xml
<member-selector 
  show="{{showSelector}}"
  title="邀请成员"
  placeholder="搜索成员..."
  members="{{membersList}}"
  multiple="{{true}}"
  selectedIds="{{selectedMemberIds}}"
  confirmText="邀请"
  bind:close="onSelectorClose"
  bind:select="onMembersSelect"
/>
```

### 属性说明
- `show`: 是否显示弹窗
- `title`: 弹窗标题
- `placeholder`: 搜索框占位符
- `members`: 成员列表数据
- `multiple`: 是否多选模式（默认false）
- `selectedIds`: 已选择的成员ID列表（多选模式）
- `showEmptyState`: 是否显示空状态（可选，默认true）
- `emptyText`: 空状态文本（可选，默认"未找到匹配的成员"）
- `confirmText`: 确认按钮文本（多选模式，默认"确定"）

### 数据格式
```javascript
members: [
  { id: 1, name: '张三', avatar: '👤' },
  { id: 2, name: '李四', avatar: '👤' },
  { id: 3, name: '王五', avatar: '👤' }
]

selectedIds: [1, 3] // 多选模式下的已选择ID列表
```

### 事件处理

#### 单选模式
```javascript
// 关闭弹窗
onSelectorClose: function() {
  this.setData({ showSelector: false });
},

// 选择成员
onMemberSelect: function(e) {
  const { member } = e.detail;
  console.log('选择的成员:', member);
  // 处理成员选择逻辑
}
```

#### 多选模式
```javascript
// 关闭弹窗
onSelectorClose: function() {
  this.setData({ showSelector: false });
},

// 选择多个成员
onMembersSelect: function(e) {
  const { members } = e.detail;
  console.log('选择的成员:', members);
  // 处理多个成员选择逻辑
}
```

## 通用使用说明

### 在页面中使用组件

1. 在页面的 `.json` 文件中声明组件：
```json
{
  "usingComponents": {
    "stats-section": "/components/stats-section/stats-section",
    "action-buttons": "/components/action-buttons/action-buttons",
    "tabs-section": "/components/tabs-section/tabs-section",
    "list-item-card": "/components/list-item-card/list-item-card",
    "empty-state": "/components/empty-state/empty-state",
    "member-selector": "/components/member-selector/member-selector"
  }
}
```

2. 在页面的 `.wxml` 文件中使用组件：
```xml
<view class="container">
  <stats-section stats="{{statsData}}" />
  <action-buttons buttons="{{actionButtons}}" bind:buttonTap="onActionButtonTap" />
  <tabs-section tabs="{{tabsData}}" activeTab="{{activeTab}}" bind:tabChange="onTabChange" />
  <list-item-card wx:for="{{items}}" wx:key="id" item="{{item}}" />
  <empty-state show="{{items.length === 0}}" text="暂无数据" />
  <member-selector show="{{showSelector}}" members="{{members}}" bind:select="onMemberSelect" />
</view>
```

3. 在页面的 `.js` 文件中处理数据和事件：
```javascript
Page({
  data: {
    statsData: [],
    actionButtons: [],
    tabsData: [],
    items: []
  },
  
  onLoad: function() {
    this.initComponentData();
    this.loadData();
  },
  
  initComponentData: function() {
    // 初始化组件数据
  },
  
  loadData: function() {
    // 加载页面数据
  },
  
  onActionButtonTap: function(e) {
    // 处理按钮点击
  },
  
  onTabChange: function(e) {
    // 处理标签切换
  }
})
``` 