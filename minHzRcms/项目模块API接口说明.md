# 项目模块API接口说明

## 概述

根据接口文档 `http://localhost:8080/api/v3/api-docs` 中的项目相关接口，已完成项目模块的增删改查功能实现。

## API接口说明

### 1. 创建项目
- **接口**: `POST /project/create`
- **功能**: 创建新的项目
- **请求体**: ProjectDTO
- **响应**: ResultLong (返回项目ID)

### 2. 更新项目
- **接口**: `PUT /project/update`
- **功能**: 更新现有项目
- **请求体**: ProjectDTO
- **响应**: ResultBoolean

### 3. 删除项目
- **接口**: `DELETE /project/delete/{id}`
- **功能**: 删除指定项目
- **参数**: id (项目ID)
- **响应**: ResultBoolean

### 4. 获取项目列表
- **接口**: `GET /project/list`
- **功能**: 分页获取项目列表
- **参数**: 
  - page: 页码 (默认1)
  - size: 每页大小 (默认10)
  - status: 项目状态 (可选)
  - level: 项目级别 (可选)
  - managerId: 负责人ID (可选)
- **响应**: ResultIPageProject

### 5. 获取项目详情
- **接口**: `GET /project/detail/{id}`
- **功能**: 获取指定项目的详细信息
- **参数**: id (项目ID)
- **响应**: ResultProject

### 6. 获取项目统计
- **接口**: `GET /project/statistics`
- **功能**: 获取项目统计信息
- **响应**: ResultObject

### 7. 更新项目进度
- **接口**: `PUT /project/progress/{id}`
- **功能**: 更新项目进度
- **参数**: id (项目ID)
- **请求体**: ProgressDTO
- **响应**: ResultBoolean

### 8. 获取项目成员列表
- **接口**: `GET /project/members/{id}`
- **功能**: 获取项目成员列表
- **参数**: id (项目ID)
- **响应**: ResultListMember

### 9. 添加项目成员
- **接口**: `POST /project/members/{id}`
- **功能**: 添加项目成员
- **参数**: id (项目ID)
- **请求体**: MemberDTO
- **响应**: ResultBoolean

### 10. 移除项目成员
- **接口**: `DELETE /project/members/{id}/{memberId}`
- **功能**: 移除项目成员
- **参数**: id (项目ID), memberId (成员ID)
- **响应**: ResultBoolean

### 11. 获取项目附件列表
- **接口**: `GET /project/attachments/{id}`
- **功能**: 获取项目附件列表
- **参数**: id (项目ID)
- **响应**: ResultListAttachment

### 12. 上传项目附件
- **接口**: `POST /project/attachments/{id}`
- **功能**: 上传项目附件
- **参数**: id (项目ID)
- **请求体**: MultipartFile
- **响应**: ResultBoolean

### 13. 删除项目附件
- **接口**: `DELETE /project/attachments/{id}/{attachmentId}`
- **功能**: 删除项目附件
- **参数**: id (项目ID), attachmentId (附件ID)
- **响应**: ResultBoolean

## ProjectDTO数据结构

### 请求体格式
```json
{
  "id": "integer (int64)",
  "name": "string",
  "description": "string",
  "startDate": "string (date)",
  "endDate": "string (date)",
  "status": "string (ongoing|pending|completed|overdue)",
  "level": "string (high|medium|low|urgent|important|normal)",
  "managerId": "integer (int64)",
  "progress": "integer (0-100)",
  "attachments": "array",
  "invitedMembers": "array"
}
```

### 响应体格式
```json
{
  "id": "integer (int64)",
  "name": "string",
  "description": "string",
  "startDate": "string (date)",
  "endDate": "string (date)",
  "status": "string",
  "level": "string",
  "managerId": "integer (int64)",
  "managerName": "string",
  "progress": "integer (0-100)",
  "createTime": "string (date-time)",
  "updateTime": "string (date-time)",
  "attachments": "array",
  "members": "array"
}
```

## ProgressDTO数据结构

### 请求体格式
```json
{
  "progress": "integer (0-100)",
  "description": "string",
  "updateTime": "string (date-time)"
}
```

## MemberDTO数据结构

### 请求体格式
```json
{
  "memberId": "integer (int64)",
  "role": "string",
  "joinTime": "string (date-time)"
}
```

## 前端实现说明

### 1. 项目列表页面 (pages/project/project.js)
- 使用 `api.getProjectList()` 获取项目列表
- 支持按状态、级别、负责人筛选
- 实时更新统计信息
- 支持项目操作（编辑、删除、更新进度等）

### 2. 创建项目页面 (pages/create-project/create-project.js)
- 使用 `api.createProject()` 创建新项目
- 完整的表单验证
- 支持附件上传和成员邀请
- 自动获取当前用户作为项目负责人

### 3. 项目详情页面 (pages/project-detail/project-detail.js)
- 使用 `api.getProjectDetail()` 获取项目详情
- 显示项目进度、成员、附件等信息
- 支持项目编辑、删除、进度更新等操作

### 4. 编辑项目页面 (pages/edit-project/edit-project.js)
- 使用 `api.updateProject()` 更新项目信息
- 支持项目基本信息、成员、附件的编辑

### 5. 更新进度页面 (pages/update-progress/update-progress.js)
- 使用 `api.updateProjectProgress()` 更新项目进度
- 支持进度描述和附件上传

## 错误处理

所有API接口都包含统一的错误处理机制：

1. **网络错误**: 自动重试机制，最多重试3次
2. **业务错误**: 显示具体的错误信息
3. **权限错误**: 自动跳转到登录页面
4. **数据错误**: 显示友好的错误提示

## 数据同步

项目模块实现了完整的数据同步机制：

1. **实时更新**: 操作后立即更新本地数据
2. **页面刷新**: 页面显示时自动刷新数据
3. **状态同步**: 项目状态变更后同步更新相关页面
4. **缓存机制**: 合理使用本地缓存提升用户体验

## 注意事项

1. **权限控制**: 所有项目操作都需要用户登录和员工绑定
2. **数据验证**: 前端和后端都进行数据验证
3. **文件上传**: 支持多种文件格式，有大小限制
4. **状态管理**: 项目状态变更需要权限验证
5. **成员管理**: 项目成员管理需要项目负责人权限 