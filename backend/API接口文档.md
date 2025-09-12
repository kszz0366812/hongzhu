# 报告管理系统后端接口文档

## 项目概述
基于SpringBoot + MyBatis-Plus的报告管理系统后端API，支持微信登录、项目管理、任务管理、报告管理、团队管理等功能。

## 技术栈
- **框架**: SpringBoot 2.7.x
- **ORM**: MyBatis-Plus 3.5.x
- **数据库**: MySQL 8.0
- **安全**: Spring Security + JWT
- **文档**: Swagger 3.0
- **工具**: Lombok, Hutool

## 数据库设计

### 1. 员工表 (sys_employee)
```sql
CREATE TABLE `sys_employee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `employee_no` varchar(50) NOT NULL COMMENT '员工编号',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `department` varchar(50) DEFAULT NULL COMMENT '部门',
  `position` varchar(50) DEFAULT NULL COMMENT '职位',
  `role` varchar(20) DEFAULT 'EMPLOYEE' COMMENT '角色(EMPLOYEE,MANAGER,ADMIN)',
  `status` tinyint DEFAULT 1 COMMENT '状态(0:离职,1:在职)',
  `entry_date` date DEFAULT NULL COMMENT '入职日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_no` (`employee_no`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_department` (`department`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';
```

### 2. 用户表 (sys_user)
```sql
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `employee_id` bigint DEFAULT NULL COMMENT '关联员工ID',
  `openid` varchar(100) DEFAULT NULL COMMENT '微信openid',
  `unionid` varchar(100) DEFAULT NULL COMMENT '微信unionid',
  `nickname` varchar(100) DEFAULT NULL COMMENT '微信昵称',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '微信头像',
  `gender` tinyint DEFAULT 0 COMMENT '性别(0:未知,1:男,2:女)',
  `country` varchar(50) DEFAULT NULL COMMENT '国家',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `is_bound` tinyint DEFAULT 0 COMMENT '是否已绑定员工(0:未绑定,1:已绑定)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  UNIQUE KEY `uk_unionid` (`unionid`),
  KEY `idx_employee_id` (`employee_id`),
  CONSTRAINT `fk_user_employee` FOREIGN KEY (`employee_id`) REFERENCES `sys_employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 3. 项目表 (sys_project)
```sql
CREATE TABLE `sys_project` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `name` varchar(100) NOT NULL COMMENT '项目名称',
  `description` text COMMENT '项目描述',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `manager_id` bigint NOT NULL COMMENT '项目经理ID',
  `priority` varchar(20) DEFAULT 'MEDIUM' COMMENT '优先级(LOW,MEDIUM,HIGH)',
  `level` varchar(20) DEFAULT 'NORMAL' COMMENT '项目级别(URGENT,IMPORTANT,NORMAL,LONG_TERM)',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态(PENDING,ONGOING,COMPLETED,OVERDUE)',
  `progress` int DEFAULT 0 COMMENT '进度(0-100)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_manager` (`manager_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';
```

### 4. 任务表 (sys_task)
```sql
CREATE TABLE `sys_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `title` varchar(200) NOT NULL COMMENT '任务标题',
  `description` text COMMENT '任务描述',
  `project_id` bigint NOT NULL COMMENT '所属项目ID',
  `assignee_id` bigint NOT NULL COMMENT '负责人ID',
  `assigner_id` bigint NOT NULL COMMENT '指派者ID',
  `priority` varchar(20) DEFAULT 'MEDIUM' COMMENT '优先级(LOW,MEDIUM,HIGH)',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态(PENDING,ONGOING,COMPLETED,OVERDUE)',
  `deadline` datetime NOT NULL COMMENT '截止时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project` (`project_id`),
  KEY `idx_assignee` (`assignee_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';
```

### 5. 报告表 (sys_report)
```sql
CREATE TABLE `sys_report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `type` varchar(20) NOT NULL COMMENT '报告类型(DAILY,WEEKLY,MONTHLY,OTHER)',
  `title` varchar(200) NOT NULL COMMENT '报告标题',
  `department` varchar(50) NOT NULL COMMENT '部门',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `content` text COMMENT '工作内容',
  `issues` text COMMENT '遇到的问题',
  `plan` text COMMENT '下期计划',
  `report_date` date NOT NULL COMMENT '报告日期',
  `likes` int DEFAULT 0 COMMENT '点赞数',
  `comments` int DEFAULT 0 COMMENT '评论数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_author` (`author_id`),
  KEY `idx_type` (`type`),
  KEY `idx_date` (`report_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告表';
```

### 6. 项目成员表 (sys_project_member)
```sql
CREATE TABLE `sys_project_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(50) DEFAULT NULL COMMENT '角色',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_user` (`project_id`,`user_id`),
  KEY `idx_project` (`project_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目成员表';
```

### 7. 附件表 (sys_attachment)
```sql
CREATE TABLE `sys_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_size` bigint DEFAULT 0 COMMENT '文件大小(字节)',
  `file_type` varchar(100) DEFAULT NULL COMMENT '文件类型',
  `ref_type` varchar(50) NOT NULL COMMENT '关联类型(PROJECT,TASK,REPORT)',
  `ref_id` bigint NOT NULL COMMENT '关联ID',
  `uploader_id` bigint NOT NULL COMMENT '上传者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ref` (`ref_type`,`ref_id`),
  KEY `idx_uploader` (`uploader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件表';
```

## API接口文档

### 1. 微信登录认证接口

#### 1.1 微信小程序登录
```
POST /api/auth/wx/login
Content-Type: application/json

Request:
{
  "code": "wx_login_code",
  "phoneCode": "phone_code",
  "userInfo": {
    "nickName": "用户昵称",
    "avatarUrl": "https://example.com/avatar.jpg",
    "gender": 1,
    "country": "China",
    "province": "Guangdong",
    "city": "Shenzhen"
  }
}

Response:
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "openid": "wx_openid_123",
      "nickname": "用户昵称",
      "avatarUrl": "https://example.com/avatar.jpg",
      "gender": 1,
      "country": "China",
      "province": "Guangdong",
      "city": "Shenzhen",
      "isBound": true,
      "employeeId": 1,
      "employeeInfo": {
        "employeeNo": "EMP001",
        "realName": "张经理",
        "phone": "13800138000",
        "email": "zhangsan@example.com",
        "department": "技术部",
        "position": "技术经理",
        "role": "MANAGER"
      }
    },
    "isNewUser": false
  }
}
```

#### 1.2 检查员工信息
```
POST /api/auth/wx/check-employee
Content-Type: application/json

Request:
{
  "phone": "13800138000"
}

Response:
{
  "code": 200,
  "message": "检查完成",
  "data": {
    "exists": true,
    "employeeInfo": {
      "id": 1,
      "employeeNo": "EMP001",
      "realName": "张经理",
      "department": "技术部",
      "position": "技术经理",
      "role": "MANAGER",
      "status": 1
    }
  }
}
```

#### 1.3 绑定员工信息
```
POST /api/auth/wx/bind-employee
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "employeeId": 1,
  "phone": "13800138000"
}

Response:
{
  "code": 200,
  "message": "绑定成功",
  "data": {
    "userInfo": {
      "id": 1,
      "openid": "wx_openid_123",
      "nickname": "用户昵称",
      "avatarUrl": "https://example.com/avatar.jpg",
      "employeeId": 1,
      "employeeInfo": {
        "employeeNo": "EMP001",
        "realName": "张经理",
        "phone": "13800138000",
        "email": "zhangsan@example.com",
        "department": "技术部",
        "position": "技术经理",
        "role": "MANAGER"
      },
      "isBound": true
    }
  }
}
```

### 2. 项目管理接口

#### 2.1 获取项目列表
```
GET /api/project/list?page=1&size=10&status=ONGOING&keyword=客户管理
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "客户管理系统开发",
        "description": "开发客户关系管理系统，提升客户服务质量",
        "startDate": "2024-06-01",
        "endDate": "2024-08-31",
        "managerId": 1,
        "managerName": "张经理",
        "priority": "HIGH",
        "priorityText": "高",
        "level": "IMPORTANT",
        "levelText": "重要",
        "status": "ONGOING",
        "statusText": "进行中",
        "progress": 65,
        "createTime": "2024-06-01 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 2.2 获取项目统计信息
```
GET /api/project/statistics
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalProjects": 23,
    "ongoingProjects": 15,
    "completedProjects": 6,
    "pendingProjects": 2,
    "overdueProjects": 0,
    "projectStats": [
      {
        "status": "ONGOING",
        "statusText": "进行中",
        "count": 15,
        "percentage": 65.2
      },
      {
        "status": "COMPLETED",
        "statusText": "已完成",
        "count": 6,
        "percentage": 26.1
      },
      {
        "status": "PENDING",
        "statusText": "待安排",
        "count": 2,
        "percentage": 8.7
      }
    ]
  }
}
```

#### 2.3 创建项目
```
POST /api/project/create
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "name": "新项目",
  "description": "项目描述",
  "startDate": "2024-06-01",
  "endDate": "2024-08-31",
  "managerId": 1,
  "priority": "HIGH",
  "level": "IMPORTANT",
  "invitedMembers": [2, 3, 4]
}

Response:
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 2
  }
}
```

### 3. 任务管理接口

#### 3.1 获取任务列表
```
GET /api/task/list?page=1&size=10&type=myTasks&status=ONGOING&projectId=1
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "客户需求调研",
        "description": "对客户进行深度访谈，收集功能需求和用户体验反馈",
        "projectId": 1,
        "projectName": "客户管理系统开发",
        "assigneeId": 1,
        "assigneeName": "张经理",
        "assignerId": 2,
        "assignerName": "李主管",
        "priority": "HIGH",
        "priorityText": "高",
        "status": "ONGOING",
        "statusText": "进行中",
        "deadline": "2024-06-20 18:00:00",
        "isOverdue": false,
        "type": "myTasks",
        "createTime": "2024-06-01 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 3.2 获取任务统计信息
```
GET /api/task/statistics
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalTasks": 89,
    "ongoingTasks": 45,
    "completedTasks": 38,
    "overdueTasks": 6,
    "taskStats": [
      {
        "status": "ONGOING",
        "statusText": "进行中",
        "count": 45,
        "percentage": 50.6
      },
      {
        "status": "COMPLETED",
        "statusText": "已完成",
        "count": 38,
        "percentage": 42.7
      },
      {
        "status": "OVERDUE",
        "statusText": "已逾期",
        "count": 6,
        "percentage": 6.7
      }
    ]
  }
}
```

#### 3.3 创建任务
```
POST /api/task/create
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "title": "新任务",
  "description": "任务描述",
  "projectId": 1,
  "assigneeId": 2,
  "priority": "HIGH",
  "deadline": "2024-06-20 18:00:00"
}

Response:
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 2
  }
}
```

### 4. 报告管理接口

#### 4.1 获取报告列表
```
GET /api/report/list?page=1&size=10&type=DAILY&department=技术部
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "type": "DAILY",
        "typeText": "日报",
        "title": "2024-06-15 日报",
        "department": "销售部",
        "authorId": 1,
        "authorName": "张经理",
        "content": "今日完成客户拜访3家,签订新订单2份,总金额45万元。",
        "issues": "客户A反馈系统响应较慢",
        "plan": "明日计划拜访重点客户A公司",
        "reportDate": "2024-06-15",
        "likes": 12,
        "comments": 5,
        "createTime": "2024-06-15 18:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 4.2 获取报告统计信息
```
GET /api/report/statistics
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalReports": 156,
    "reportStats": [
      {
        "type": "DAILY",
        "typeText": "日报",
        "count": 15,
        "percentage": 60
      },
      {
        "type": "WEEKLY",
        "typeText": "周报",
        "count": 6,
        "percentage": 24
      },
      {
        "type": "MONTHLY",
        "typeText": "月报",
        "count": 3,
        "percentage": 12
      },
      {
        "type": "OTHER",
        "typeText": "其他",
        "count": 1,
        "percentage": 4
      }
    ],
    "departmentStats": [
      {
        "name": "销售部",
        "reports": 12,
        "percentage": 48
      },
      {
        "name": "技术部",
        "reports": 8,
        "percentage": 32
      },
      {
        "name": "市场部",
        "reports": 5,
        "percentage": 20
      }
    ]
  }
}
```

#### 4.3 创建报告
```
POST /api/report/create
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "type": "DAILY",
  "title": "2024-06-15 日报",
  "department": "销售部",
  "content": "今日完成客户拜访3家,签订新订单2份,总金额45万元。",
  "issues": "客户A反馈系统响应较慢",
  "plan": "明日计划拜访重点客户A公司",
  "reportDate": "2024-06-15"
}

Response:
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 2
  }
}
```

### 5. 员工管理接口

#### 5.1 获取员工列表
```
GET /api/employee/list?page=1&size=10&department=技术部&keyword=张经理
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "employeeNo": "EMP001",
        "realName": "张经理",
        "phone": "13800138000",
        "email": "zhangsan@example.com",
        "department": "技术部",
        "position": "技术经理",
        "role": "MANAGER",
        "status": 1,
        "entryDate": "2020-01-01",
        "createTime": "2020-01-01 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 5.2 创建员工
```
POST /api/employee/create
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "employeeNo": "EMP002",
  "realName": "李主管",
  "phone": "13800138001",
  "email": "lizhuguan@example.com",
  "department": "技术部",
  "position": "技术主管",
  "role": "MANAGER",
  "entryDate": "2020-02-01"
}

Response:
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 2
  }
}
```

### 6. 文件上传接口

#### 6.1 上传文件
```
POST /api/file/upload
Authorization: Bearer {token}
Content-Type: multipart/form-data

Request:
file: [文件]

Response:
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "id": 1,
    "fileName": "需求文档.pdf",
    "filePath": "/uploads/2024/06/需求文档.pdf",
    "fileSize": 2621440,
    "fileType": "application/pdf"
  }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 通用响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 分页响应格式

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [],
    "total": 0,
    "size": 10,
    "current": 1,
    "pages": 0
  }
}
``` 