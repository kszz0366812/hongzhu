-- 报告管理系统数据库初始化脚本
-- 创建时间: 2024-06-01
-- 版本: v1.0.0

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `minhzrcms` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `minhzrcms`;

-- 1. 员工表
CREATE TABLE `employee_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `employee_no` varchar(50) NOT NULL COMMENT '员工编号',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `department` varchar(50) DEFAULT NULL COMMENT '部门',
  `position` varchar(50) DEFAULT NULL COMMENT '职位',
  `role` varchar(20) DEFAULT 'EMPLOYEE' COMMENT '角色(EMPLOYEE,MANAGER,ADMIN)',
  `status` tinyint DEFAULT 1 COMMENT '状态(0:离职,1:在职)',
  `entry_date` date DEFAULT NULL COMMENT '入职日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_no` (`employee_no`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_department` (`department`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';

-- 2. 用户表
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
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  UNIQUE KEY `uk_unionid` (`unionid`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_deleted` (`deleted`),
  CONSTRAINT `fk_user_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 3. 项目表
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
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_manager` (`manager_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`),
  CONSTRAINT `fk_project_manager` FOREIGN KEY (`manager_id`) REFERENCES `employee_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- 4. 任务表
CREATE TABLE `sys_project_task` (
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
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_project` (`project_id`),
  KEY `idx_assignee` (`assignee_id`),
  KEY `idx_assigner` (`assigner_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`),
  CONSTRAINT `fk_task_project` FOREIGN KEY (`project_id`) REFERENCES `sys_project` (`id`),
  CONSTRAINT `fk_task_assignee` FOREIGN KEY (`assignee_id`) REFERENCES `employee_info` (`id`),
  CONSTRAINT `fk_task_assigner` FOREIGN KEY (`assigner_id`) REFERENCES `employee_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- 5. 报告表
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
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_author` (`author_id`),
  KEY `idx_type` (`type`),
  KEY `idx_date` (`report_date`),
  KEY `idx_deleted` (`deleted`),
  CONSTRAINT `fk_report_author` FOREIGN KEY (`author_id`) REFERENCES `employee_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报告表';

-- 6. 项目成员表
CREATE TABLE `sys_project_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `role` varchar(50) DEFAULT NULL COMMENT '角色',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_user` (`project_id`,`employee_id`),
  KEY `idx_project` (`project_id`),
  KEY `idx_user` (`employee_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目成员表';

-- 7. 附件表
CREATE TABLE `sys_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_size` bigint DEFAULT 0 COMMENT '文件大小(字节)',
  `file_type` varchar(100) DEFAULT NULL COMMENT '文件类型',
  `ref_type` varchar(50) NOT NULL COMMENT '关联类型(PROJECT,TASK,REPORT,PROJECT_PROGRESS)',
  `ref_id` bigint NOT NULL COMMENT '关联ID',
  `uploader_id` bigint NOT NULL COMMENT '上传者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_ref` (`ref_type`,`ref_id`),
  KEY `idx_uploader` (`uploader_id`),
  KEY `idx_deleted` (`deleted`),
  CONSTRAINT `fk_attachment_uploader` FOREIGN KEY (`uploader_id`) REFERENCES `employee_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='附件表';

-- 8. 项目进度表
CREATE TABLE `sys_project_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `progress_percentage` int NOT NULL COMMENT '进度百分比(0-100)',
  `progress_content` text COMMENT '进度内容描述',
  `previous_progress` int DEFAULT NULL COMMENT '更新前进度',
  `progress_change` int DEFAULT NULL COMMENT '进度变化值',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_project` (`project_id`),
  KEY `idx_creator` (`creator_id`),
  KEY `create_time` (`create_time`),
  KEY `idx_project_date` (`project_id`, `create_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目进度表';

-- 9. 微信登录记录表
CREATE TABLE `sys_wx_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `openid` varchar(100) NOT NULL COMMENT '微信openid',
  `session_key` varchar(100) DEFAULT NULL COMMENT '微信session_key',
  `login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `ip` varchar(50) DEFAULT NULL COMMENT '登录IP',
  `device_info` varchar(500) DEFAULT NULL COMMENT '设备信息',
  `status` tinyint DEFAULT 1 COMMENT '状态(0:失败,1:成功)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_openid` (`openid`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信登录记录表';

-- 插入初始数据

-- 插入员工数据
INSERT INTO `employee_info` (`employee_code`, `name`, `status`, `position`, `levels`, `join_date`) VALUES
('EMP001', '张经理', 1, '技术经理', '高级', '2020-01-01'),
('EMP002', '李主管', 1, '技术主管', '中级', '2020-02-01'),
('EMP003', '王专员', 1, '开发工程师', '初级', '2020-03-01'),
('EMP004', '赵助理', 1, '产品助理', '初级', '2020-04-01'),
('EMP005', '陈顾问', 1, '业务顾问', '中级', '2020-05-01'),
('EMP006', '刘设计师', 1, 'UI设计师', '中级', '2020-06-01'),
('EMP007', '孙测试', 1, '测试工程师', '初级', '2020-07-01'),
('EMP008', '周运维', 1, '运维工程师', '中级', '2020-08-01'),
('EMP009', '吴产品', 1, '产品经理', '高级', '2020-09-01'),
('EMP010', '郑销售', 1, '销售经理', '高级', '2020-10-01');

-- 插入项目数据
INSERT INTO `sys_project` (`name`, `description`, `start_date`, `end_date`, `manager_id`, `priority`, `level`, `status`, `progress`) VALUES
('客户管理系统开发', '开发客户关系管理系统，提升客户服务质量', '2024-06-01', '2024-08-31', 1, 'HIGH', 'IMPORTANT', 'ONGOING', 65),
('销售数据分析平台', '构建销售数据分析和可视化平台', '2024-06-15', '2024-09-30', 2, 'MEDIUM', 'URGENT', 'ONGOING', 35),
('团队协作工具优化', '优化现有团队协作工具，提升工作效率', '2024-07-01', '2024-10-31', 1, 'MEDIUM', 'NORMAL', 'ONGOING', 45),
('市场调研报告系统', '建立市场调研数据收集和分析系统', '2024-06-10', '2024-09-15', 9, 'HIGH', 'IMPORTANT', 'ONGOING', 25),
('员工培训管理系统', '开发员工培训和学习管理平台', '2024-05-01', '2024-07-31', 2, 'LOW', 'NORMAL', 'COMPLETED', 100),
('移动端APP开发', '开发企业移动端应用，提升用户体验', '2024-07-01', '2024-12-31', 1, 'HIGH', 'URGENT', 'ONGOING', 25),
('数据库优化项目', '优化现有数据库性能，提升查询效率', '2024-08-01', '2024-10-31', 2, 'MEDIUM', 'NORMAL', 'PENDING', 0);

-- 插入任务数据
INSERT INTO `sys_task` (`title`, `description`, `project_id`, `assignee_id`, `assigner_id`, `priority`, `status`, `deadline`) VALUES
('客户需求调研', '对客户进行深度访谈，收集功能需求和用户体验反馈', 1, 1, 2, 'HIGH', 'ONGOING', '2024-06-20 18:00:00'),
('数据库设计', '设计销售数据存储结构，优化查询性能', 2, 3, 1, 'MEDIUM', 'ONGOING', '2024-06-25 18:00:00'),
('用户界面设计', '重新设计用户界面，提升用户体验', 3, 6, 2, 'LOW', 'COMPLETED', '2024-06-15 18:00:00'),
('API接口开发', '开发数据获取和处理的API接口', 4, 4, 1, 'HIGH', 'OVERDUE', '2024-06-18 18:00:00'),
('系统测试', '进行系统功能测试和性能测试', 5, 7, 1, 'MEDIUM', 'COMPLETED', '2024-06-10 18:00:00'),
('需求文档编写', '编写详细的功能需求文档和技术规格说明', 2, 1, 3, 'HIGH', 'ONGOING', '2024-06-30 18:00:00'),
('代码审查', '对核心功能模块进行代码审查和质量检查', 3, 2, 1, 'MEDIUM', 'OVERDUE', '2024-06-12 18:00:00'),
('性能优化', '优化系统性能，提升响应速度', 1, 8, 2, 'MEDIUM', 'ONGOING', '2024-07-15 18:00:00');

-- 插入报告数据
INSERT INTO `sys_report` (`type`, `title`, `department`, `author_id`, `content`, `issues`, `plan`, `report_date`, `likes`, `comments`) VALUES
('DAILY', '2024-06-15 日报', '销售部', 10, '今日完成客户拜访3家,签订新订单2份,总金额45万元。', '客户A反馈系统响应较慢', '明日计划拜访重点客户A公司', '2024-06-15', 12, 5),
('DAILY', '2024-06-14 日报', '市场部', 9, '今日完成市场调研报告初稿,收集竞品信息5份。', '竞品信息收集不够全面', '下周计划组织部门会议讨论Q3营销方案', '2024-06-14', 8, 3),
('WEEKLY', '2024年第24周周报', '运营部', 1, '本周平台总GMV达到824万元,环比增长12%。新增注册用户324人,用户留存率提升至68%。', '部分功能模块响应较慢', '下周重点优化系统性能', '2024-06-15', 24, 7),
('DAILY', '2024-06-13 日报', '技术部', 2, '完成用户管理模块开发,修复已知bug 5个。', '数据库查询性能需要优化', '明日开始API接口开发', '2024-06-13', 15, 4),
('WEEKLY', '2024年第23周周报', '产品部', 9, '本周完成产品需求文档编写,用户调研报告整理完成。', '部分需求优先级需要调整', '下周开始UI设计工作', '2024-06-08', 18, 6),
('DAILY', '2024-06-12 日报', '设计部', 6, '完成登录页面和首页UI设计,提交设计稿3份。', '设计风格需要统一', '明日开始详情页面设计', '2024-06-12', 10, 2),
('MONTHLY', '2024年6月月报', '技术部', 1, '本月完成系统架构设计,核心功能模块开发完成80%。', '项目进度略有延迟', '下月重点完成测试和部署', '2024-06-30', 35, 12);

-- 插入项目成员数据
INSERT INTO `sys_project_member` (`project_id`, `user_id`, `role`) VALUES
(1, 1, '项目经理'),
(1, 2, '技术主管'),
(1, 3, '开发工程师'),
(1, 4, '产品助理'),
(1, 6, 'UI设计师'),
(1, 7, '测试工程师'),
(2, 2, '技术主管'),
(2, 3, '开发工程师'),
(2, 4, '产品助理'),
(3, 1, '项目经理'),
(3, 2, '技术主管'),
(3, 6, 'UI设计师'),
(4, 9, '产品经理'),
(4, 4, '产品助理'),
(4, 3, '开发工程师'),
(5, 2, '技术主管'),
(5, 7, '测试工程师'),
(6, 1, '项目经理'),
(6, 3, '开发工程师'),
(6, 6, 'UI设计师'),
(7, 2, '技术主管'),
(7, 8, '运维工程师');

-- 插入项目进度数据
INSERT INTO `sys_project_progress` (`project_id`, `creator_id`, `progress_percentage`, `progress_content`, `previous_progress`, `progress_change`, `report_date`) VALUES
(1, 1, 65, '客户管理系统开发项目已完成65%，主要完成了需求调研、数据库设计和用户界面设计。目前正在进行核心功能模块的开发工作。', 60, 5, '2024-06-15'),
(2, 2, 35, '销售数据分析平台项目已完成35%，主要完成了数据收集和竞品分析。下一步将开始数据模型的构建。', 30, 5, '2024-06-15'),
(3, 1, 45, '团队协作工具优化项目已完成45%，主要完成了工具的初步集成和测试。正在进行用户反馈收集。', 40, 5, '2024-06-15'),
(4, 9, 25, '市场调研报告系统项目已完成25%，主要完成了市场调研的初步数据收集。正在进行数据分析工作。', 20, 5, '2024-06-15'),
(5, 2, 100, '员工培训管理系统项目已完成100%，主要完成了培训课程的开发和上线。项目已全部完成。', 95, 5, '2024-06-15'),
(6, 1, 25, '移动端APP开发项目已完成25%，主要完成了基础框架的搭建和UI设计。正在进行核心功能开发。', 20, 5, '2024-06-15'),
(7, 2, 0, '数据库优化项目尚未开始，等待项目经理分配任务。', NULL, NULL, '2024-06-15');

-- 创建视图

-- 项目统计视图
CREATE VIEW `v_project_statistics` AS
SELECT 
    p.status,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM sys_project WHERE deleted = 0), 2) as percentage
FROM sys_project p
WHERE p.deleted = 0
GROUP BY p.status;

-- 任务统计视图
CREATE VIEW `v_task_statistics` AS
SELECT 
    t.status,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM sys_task WHERE deleted = 0), 2) as percentage
FROM sys_task t
WHERE t.deleted = 0
GROUP BY t.status;

-- 报告统计视图
CREATE VIEW `v_report_statistics` AS
SELECT 
    r.type,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM sys_report WHERE deleted = 0), 2) as percentage
FROM sys_report r
WHERE r.deleted = 0
GROUP BY r.type;

-- 项目进度统计视图
CREATE VIEW `v_project_progress_statistics` AS
SELECT 
    p.id as project_id,
    p.name as project_name,
    p.status as project_status,
    p.progress as current_progress,
    COUNT(pp.id) as progress_count,
    MAX(pp.report_date) as last_progress_date,
    MAX(pp.progress_percentage) as max_progress,
    MIN(pp.progress_percentage) as min_progress,
    AVG(pp.progress_percentage) as avg_progress
FROM sys_project p
LEFT JOIN sys_project_progress pp ON p.id = pp.project_id AND pp.deleted = 0
WHERE p.deleted = 0
GROUP BY p.id, p.name, p.status, p.progress;

-- 项目进度历史视图
CREATE VIEW `v_project_progress_history` AS
SELECT 
    pp.id,
    pp.project_id,
    p.name as project_name,
    pp.creator_id,
    e.real_name as creator_name,
    pp.progress_percentage,
    pp.progress_content,
    pp.previous_progress,
    pp.progress_change,
    pp.report_date,
    pp.create_time
FROM sys_project_progress pp
INNER JOIN sys_project p ON pp.project_id = p.id AND p.deleted = 0
INNER JOIN employee_info e ON pp.creator_id = e.id AND e.deleted = 0
WHERE pp.deleted = 0
ORDER BY pp.project_id, pp.report_date DESC, pp.create_time DESC;

-- 创建存储过程

-- 获取用户项目统计
DELIMITER //
CREATE PROCEDURE `sp_get_user_project_stats`(IN user_id BIGINT)
BEGIN
    SELECT 
        COUNT(*) as total_projects,
        SUM(CASE WHEN p.status = 'ONGOING' THEN 1 ELSE 0 END) as ongoing_projects,
        SUM(CASE WHEN p.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_projects,
        SUM(CASE WHEN p.status = 'PENDING' THEN 1 ELSE 0 END) as pending_projects
    FROM sys_project p
    INNER JOIN sys_project_member pm ON p.id = pm.project_id AND pm.deleted = 0
    WHERE pm.user_id = user_id AND p.deleted = 0;
END //
DELIMITER ;

-- 获取用户任务统计
DELIMITER //
CREATE PROCEDURE `sp_get_user_task_stats`(IN user_id BIGINT)
BEGIN
    SELECT 
        COUNT(*) as total_tasks,
        SUM(CASE WHEN t.status = 'PENDING' THEN 1 ELSE 0 END) as pending_tasks,
        SUM(CASE WHEN t.status = 'ONGOING' THEN 1 ELSE 0 END) as ongoing_tasks,
        SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_tasks,
        SUM(CASE WHEN t.status = 'OVERDUE' THEN 1 ELSE 0 END) as overdue_tasks
    FROM sys_task t
    INNER JOIN sys_user u ON t.assignee_id = u.employee_id AND u.deleted = 0
    WHERE u.id = user_id AND t.deleted = 0;
END //
DELIMITER ;

-- 获取项目进度历史
DELIMITER //
CREATE PROCEDURE `sp_get_project_progress_history`(IN project_id BIGINT)
BEGIN
    SELECT 
        pp.id,
        pp.progress_percentage,
        pp.progress_content,
        pp.previous_progress,
        pp.progress_change,
        pp.report_date,
        pp.create_time,
        e.real_name as creator_name,
        e.department as creator_department
    FROM sys_project_progress pp
    INNER JOIN employee_info e ON pp.creator_id = e.id AND e.deleted = 0
    WHERE pp.project_id = project_id AND pp.deleted = 0
    ORDER BY pp.report_date DESC, pp.create_time DESC;
END //
DELIMITER ;

-- 添加项目进度记录
DELIMITER //
CREATE PROCEDURE `sp_add_project_progress`(
    IN p_project_id BIGINT,
    IN p_creator_id BIGINT,
    IN p_progress_percentage INT,
    IN p_progress_content TEXT
)
BEGIN
    DECLARE v_previous_progress INT DEFAULT 0;
    DECLARE v_progress_change INT DEFAULT 0;
    
    -- 获取项目当前进度
    SELECT progress INTO v_previous_progress
    FROM sys_project
    WHERE id = p_project_id AND deleted = 0;
    
    -- 计算进度变化
    SET v_progress_change = p_progress_percentage - v_previous_progress;
    
    -- 插入进度记录
    INSERT INTO sys_project_progress (
        project_id, 
        creator_id, 
        progress_percentage, 
        progress_content, 
        previous_progress, 
        progress_change, 
        report_date
    ) VALUES (
        p_project_id, 
        p_creator_id, 
        p_progress_percentage, 
        p_progress_content, 
        v_previous_progress, 
        v_progress_change, 
        CURDATE()
    );
    
    -- 更新项目进度
    UPDATE sys_project 
    SET progress = p_progress_percentage, update_time = NOW()
    WHERE id = p_project_id AND deleted = 0;
    
    -- 返回新插入的记录ID
    SELECT LAST_INSERT_ID() as progress_id;
END //
DELIMITER ;

-- 创建触发器

-- 更新项目进度触发器
DELIMITER //
CREATE TRIGGER `tr_update_project_progress` 
AFTER UPDATE ON `sys_task`
FOR EACH ROW
BEGIN
    DECLARE total_tasks INT;
    DECLARE completed_tasks INT;
    DECLARE new_progress INT;
    
    -- 获取项目总任务数（未删除的）
    SELECT COUNT(*) INTO total_tasks
    FROM sys_task
    WHERE project_id = NEW.project_id AND deleted = 0;
    
    -- 获取已完成任务数（未删除的）
    SELECT COUNT(*) INTO completed_tasks
    FROM sys_task
    WHERE project_id = NEW.project_id AND status = 'COMPLETED' AND deleted = 0;
    
    -- 计算新进度
    IF total_tasks > 0 THEN
        SET new_progress = ROUND((completed_tasks * 100) / total_tasks);
        
        -- 更新项目进度
        UPDATE sys_project 
        SET progress = new_progress, update_time = NOW()
        WHERE id = NEW.project_id AND deleted = 0;
    END IF;
END //
DELIMITER ;

-- 项目进度更新触发器
DELIMITER //
CREATE TRIGGER `tr_project_progress_after_insert`
AFTER INSERT ON `sys_project_progress`
FOR EACH ROW
BEGIN
    -- 更新项目表中的进度
    UPDATE sys_project 
    SET progress = NEW.progress_percentage,
        update_time = NOW()
    WHERE id = NEW.project_id AND deleted = 0;
END //
DELIMITER ;

-- 终端信息表
CREATE TABLE `terminal_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '终端ID',
  `terminal_code` varchar(50) NOT NULL COMMENT '终端编码',
  `terminal_name` varchar(100) NOT NULL COMMENT '终端名称',
  `terminal_type` varchar(50) DEFAULT NULL COMMENT '终端类型',
  `tags` varchar(200) DEFAULT NULL COMMENT '标签',
  `customer_manager` varchar(50) DEFAULT NULL COMMENT '客户经理',
  `is_scheduled` tinyint DEFAULT 0 COMMENT '是否排线(0:否,1:是)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_terminal_code` (`terminal_code`),
  KEY `idx_terminal_name` (`terminal_name`),
  KEY `idx_terminal_type` (`terminal_type`),
  KEY `idx_customer_manager` (`customer_manager`),
  KEY `idx_is_scheduled` (`is_scheduled`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='终端信息表';

-- 插入测试数据
INSERT INTO `terminal_info` (`terminal_code`, `terminal_name`, `terminal_type`, `tags`, `customer_manager`, `is_scheduled`) VALUES
('T001', '北京朝阳终端', '零售终端', '重点客户,高价值', '张三', 1),
('T002', '上海浦东终端', '批发终端', '普通客户', '李四', 0),
('T003', '广州天河终端', '零售终端', 'VIP客户', '王五', 1),
('T004', '深圳南山终端', '批发终端', '新客户', '赵六', 0),
('T005', '杭州西湖终端', '零售终端', '重点客户', '钱七', 1);

-- 创建索引优化查询性能
CREATE INDEX `idx_project_date_range` ON `sys_project` (`start_date`, `end_date`);
CREATE INDEX `idx_task_deadline` ON `sys_task` (`deadline`);
CREATE INDEX `idx_report_date_type` ON `sys_report` (`report_date`, `type`);
CREATE INDEX `idx_employee_department_role` ON `employee_info` (`department`, `role`);
CREATE INDEX `idx_project_progress_project_date` ON `sys_project_progress` (`project_id`, `report_date`);
CREATE INDEX `idx_project_progress_creator_date` ON `sys_project_progress` (`creator_id`, `report_date`);
CREATE INDEX `idx_project_progress_percentage` ON `sys_project_progress` (`progress_percentage`);

-- 创建deleted字段索引
CREATE INDEX `idx_employee_deleted` ON `employee_info` (`deleted`);
CREATE INDEX `idx_user_deleted` ON `sys_user` (`deleted`);
CREATE INDEX `idx_project_deleted` ON `sys_project` (`deleted`);
CREATE INDEX `idx_task_deleted` ON `sys_task` (`deleted`);
CREATE INDEX `idx_report_deleted` ON `sys_report` (`deleted`);
CREATE INDEX `idx_project_member_deleted` ON `sys_project_member` (`deleted`);
CREATE INDEX `idx_attachment_deleted` ON `sys_attachment` (`deleted`);
CREATE INDEX `idx_project_progress_deleted` ON `sys_project_progress` (`deleted`);
CREATE INDEX `idx_wx_login_log_deleted` ON `sys_wx_login_log` (`deleted`);
CREATE INDEX `idx_terminal_deleted` ON `terminal_info` (`deleted`);

-- 提交事务
COMMIT;