# API调用方式更新说明

## 概述

根据最新的API接口文档，服务端接口路径和参数传递方式已发生变化，前端 API 调用方式已相应更新。

## 更新内容

### 1. 使用具体的接口路径

所有 API 调用现在都使用具体的接口路径，如 `/report/create`、`/report/list` 等。

### 2. 根据接口类型使用对应的HTTP方法

- GET 请求：用于获取数据，参数通过查询字符串传递
- POST 请求：用于创建和更新数据，参数通过请求体传递

## 更新后的 API 调用方式

### 报告相关接口

#### 创建报告
```javascript
// 更新前
POST /report
{
  "action": "create",
  "type": "1",
  "title": "20241201日报",
  "authorId": "EMP001",
  "content": "工作内容",
  "issues": "遇到的问题",
  "plan": "下一步计划",
  "reportDate": "2024-12-01"
}

// 更新后
POST /report/create
{
  "type": "1",
  "title": "20241201日报",
  "authorId": "EMP001",
  "content": "工作内容",
  "issues": "遇到的问题",
  "plan": "下一步计划",
  "reportDate": "2024-12-01"
}
```

#### 更新报告
```javascript
// 更新前
POST /report
{
  "action": "update",
  "id": "123",
  "type": "1",
  "title": "20241201日报",
  "authorId": "EMP001",
  "content": "工作内容",
  "issues": "遇到的问题",
  "plan": "下一步计划",
  "reportDate": "2024-12-01"
}

// 更新后
POST /report/update
{
  "id": "123",
  "type": "1",
  "title": "20241201日报",
  "authorId": "EMP001",
  "content": "工作内容",
  "issues": "遇到的问题",
  "plan": "下一步计划",
  "reportDate": "2024-12-01"
}
```

#### 删除报告
```javascript
// 更新前
POST /report
{
  "action": "delete",
  "reportId": "123"
}

// 更新后
POST /report/delete/123
```

#### 获取报告列表
```javascript
// 更新前
POST /report
{
  "action": "list",
  "page": 1,
  "size": 10,
  "type": "1",
  "department": "技术部"
}

// 更新后
GET /report/list?page=1&size=10&type=1&department=技术部
```

#### 获取报告统计
```javascript
// 更新前
POST /report
{
  "action": "statistics"
}

// 更新后
GET /report/statistics
```

### 用户相关接口

#### 获取用户信息
```javascript
// 更新前
POST /user
{
  "action": "info"
}

// 更新后
GET /user/info
```

#### 更新用户信息
```javascript
// 更新前
POST /user
{
  "action": "update",
  "nickName": "张三",
  "avatarUrl": "https://...",
  "gender": 1
}

// 更新后
POST /user/update
{
  "nickName": "张三",
  "avatarUrl": "https://...",
  "gender": 1
}
```

### 认证相关接口

#### 绑定员工
```javascript
// 更新前
POST /auth
{
  "action": "bind-employee",
  "employeeCode": "EMP001"
}

// 更新后
GET /auth/wx/bind-employee?params={"employeeCode":"EMP001"}
```

## 技术实现

### 前端代码更新

所有 API 调用都已更新为具体路径格式：

```javascript
// GET 请求格式（用于获取数据）
return requestWithRetry('/endpoint/path', {
  method: 'GET',
  header: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${getToken()}`
  }
})

// POST 请求格式（用于创建/更新数据）
return requestWithRetry('/endpoint/path', {
  method: 'POST',
  data: requestData,
  header: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${getToken()}`
  }
})
```

### 服务端处理

服务端使用具体的控制器方法处理不同的操作：

```java
@PostMapping("/report/create")
public ResponseEntity<?> createReport(@RequestBody ReportDTO reportDTO) {
    return ResponseEntity.ok(reportService.createReport(reportDTO));
}

@GetMapping("/report/list")
public ResponseEntity<?> getReportList(@RequestParam Map<String, Object> params) {
    return ResponseEntity.ok(reportService.getReportList(params));
}
```

## 优势

1. **标准化**：使用标准的 RESTful API 设计
2. **清晰性**：接口路径明确表达操作意图
3. **扩展性**：新增操作只需要添加新的控制器方法
4. **一致性**：符合行业标准的 API 设计规范

## 注意事项

1. **请求方法**：根据操作类型使用对应的 HTTP 方法（GET/POST）
2. **参数传递**：GET 请求参数通过查询字符串传递，POST 请求参数通过请求体传递
3. **错误处理**：服务端需要验证请求参数的有效性
4. **向后兼容**：如果服务端还没有完全更新，可能需要保留原有的接口作为过渡

## 测试建议

1. **功能测试**：测试所有更新后的 API 调用是否正常工作
2. **错误处理**：测试无效参数的错误处理
3. **数据验证**：确保请求参数的数据格式正确
4. **性能测试**：验证新的 API 调用方式对性能的影响 