-- 创建数据库
CREATE DATABASE IF NOT EXISTS insight_flow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE insight_flow;

-- 数据字典表
CREATE TABLE IF NOT EXISTS data_dictionary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    code VARCHAR(50) NOT NULL COMMENT '字典编码',
    name VARCHAR(100) NOT NULL COMMENT '字典名称',
    parent_id BIGINT COMMENT '上级字典ID',
    type VARCHAR(50) NOT NULL COMMENT '字典类型',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态（0 禁用，1 启用）',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_code_type (code, type)
) COMMENT '数据字典表';

-- 插入数据字典测试数据
INSERT INTO data_dictionary (code, name, parent_id, type, sort, status, remark) VALUES
('REGION_LEVEL1', '华北区', NULL, 'REGION', 1, 1, '华北区域'),
('REGION_LEVEL2', '北京市', 1, 'REGION', 1, 1, '北京市'),
('REGION_LEVEL2', '天津市', 1, 'REGION', 2, 1, '天津市'),
('REGION_LEVEL2', '河北省', 1, 'REGION', 3, 1, '河北省'),
('REGION_LEVEL1', '华东区', NULL, 'REGION', 2, 1, '华东区域'),
('REGION_LEVEL2', '上海市', 5, 'REGION', 1, 1, '上海市'),
('REGION_LEVEL2', '江苏省', 5, 'REGION', 2, 1, '江苏省'),
('REGION_LEVEL2', '浙江省', 5, 'REGION', 3, 1, '浙江省'),
('EMPLOYEE_STATUS', '在职', NULL, 'STATUS', 1, 1, '员工在职状态'),
('EMPLOYEE_STATUS', '离职', NULL, 'STATUS', 2, 1, '员工离职状态'),
('PRODUCT_TYPE', '饮料', NULL, 'PRODUCT', 1, 1, '饮料类产品'),
('PRODUCT_TYPE', '零食', NULL, 'PRODUCT', 2, 1, '零食类产品'),
('PRODUCT_TYPE', '日用品', NULL, 'PRODUCT', 3, 1, '日用品类产品'),
('CUSTOMER_LEVEL', 'VIP客户', NULL, 'CUSTOMER', 1, 1, 'VIP客户等级'),
('CUSTOMER_LEVEL', '普通客户', NULL, 'CUSTOMER', 2, 1, '普通客户等级'),
('CUSTOMER_LEVEL', '潜在客户', NULL, 'CUSTOMER', 3, 1, '潜在客户等级');

-- 员工信息表
CREATE TABLE IF NOT EXISTS employee_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    employee_code VARCHAR(50) NOT NULL COMMENT '工号',
    name VARCHAR(50) NOT NULL COMMENT '员工姓名',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0 在职，1 离职）',
    region_level1 VARCHAR(50) NOT NULL COMMENT '所属大区',
    region_level2 VARCHAR(50) NOT NULL COMMENT '所属地市',
    region_level3 VARCHAR(50) NOT NULL COMMENT '所属区域',
    responsible_regions JSON COMMENT '负责区域',
    direct_leader_id BIGINT COMMENT '直接上级ID',
    position VARCHAR(50) NOT NULL COMMENT '岗位名称',
    rank VARCHAR(50) NOT NULL COMMENT '职级',
    channel VARCHAR(50) NOT NULL COMMENT '渠道',
    join_date DATE NOT NULL COMMENT '入司日期',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_employee_code (employee_code)
) COMMENT '员工信息表';

-- 批发商表
CREATE TABLE IF NOT EXISTS wholesaler_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    dealer_code VARCHAR(50) NOT NULL COMMENT '经销商编码',
    dealer_name VARCHAR(100) NOT NULL COMMENT '经销商名称',
    level VARCHAR(50) NOT NULL COMMENT '等级',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    customer_manager VARCHAR(50) NOT NULL COMMENT '客户经理姓名',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_dealer_code (dealer_code)
) COMMENT '批发商表';

-- 终端商户表
CREATE TABLE IF NOT EXISTS terminal_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    terminal_code VARCHAR(50) NOT NULL COMMENT '终端编码',
    terminal_name VARCHAR(100) NOT NULL COMMENT '终端名称',
    terminal_type VARCHAR(50) NOT NULL COMMENT '类型',
    tags JSON COMMENT '标签',
    customer_manager VARCHAR(50) NOT NULL COMMENT '客户经理姓名',
    is_scheduled TINYINT NOT NULL DEFAULT 0 COMMENT '是否排线',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_terminal_code (terminal_code)
) COMMENT '终端商户表';

-- 商品表
CREATE TABLE IF NOT EXISTS product_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    product_code VARCHAR(50) NOT NULL COMMENT '商品编码',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    specification VARCHAR(50) COMMENT '规格',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '当前单价',
    case_price DECIMAL(10,2) NOT NULL COMMENT '当前件价',
    series VARCHAR(50) NOT NULL COMMENT '所属系列',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_product_code (product_code)
) COMMENT '商品表';

-- 拜访信息表
CREATE TABLE IF NOT EXISTS visit_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    visitor_id BIGINT NOT NULL COMMENT '拜访人ID',
    visit_time DATETIME NOT NULL COMMENT '拜访时间',
    terminal_id BIGINT NOT NULL COMMENT '拜访终端ID',
    is_deal TINYINT NOT NULL DEFAULT 0 COMMENT '是否成交',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    KEY idx_visitor_id (visitor_id),
    KEY idx_visit_time (visit_time),
    KEY idx_terminal_id (terminal_id)
) COMMENT '拜访信息表';

-- 成交记录表
CREATE TABLE IF NOT EXISTS deal_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    deal_time DATETIME NOT NULL COMMENT '成交时间',
    visit_id BIGINT NOT NULL COMMENT '拜访ID',
    deal_employee_id BIGINT NOT NULL COMMENT '成交人ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '成交数量',
    unit VARCHAR(20) NOT NULL COMMENT '单位',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总价',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    KEY idx_deal_time (deal_time),
    KEY idx_visit_id (visit_id),
    KEY idx_deal_employee_id (deal_employee_id),
    KEY idx_product_id (product_id)
) COMMENT '成交记录表';

-- 销售记录表
CREATE TABLE IF NOT EXISTS sales_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    sales_order_no VARCHAR(50) NOT NULL COMMENT '销售单号',
    sales_date DATETIME NOT NULL COMMENT '销售日期',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户名称',
    customer_level VARCHAR(50) NOT NULL COMMENT '客户等级',
    distributor VARCHAR(100) COMMENT '配送商',
    customer_manager VARCHAR(50) NOT NULL COMMENT '客户经理',
    salesperson VARCHAR(50) NOT NULL COMMENT '业务员',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '销售数量',
    is_gift TINYINT NOT NULL DEFAULT 0 COMMENT '是否赠品',
    unit VARCHAR(20) NOT NULL COMMENT '销售单位',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '销售单价',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总价',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_sales_order_no (sales_order_no),
    KEY idx_sales_date (sales_date),
    KEY idx_customer_name (customer_name),
    KEY idx_product_id (product_id)
) COMMENT '销售记录表';

-- 任务目标记录表
CREATE TABLE IF NOT EXISTS task_target (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    executor_id BIGINT NOT NULL COMMENT '执行人ID',
    start_time DATETIME NOT NULL COMMENT '任务开始时间',
    end_time DATETIME NOT NULL COMMENT '任务结束时间',
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    target_amount DECIMAL(10,2) NOT NULL COMMENT '任务目标金额',
    achieved_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '已达成金额',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    KEY idx_executor_id (executor_id),
    KEY idx_start_time (start_time),
    KEY idx_end_time (end_time)
) COMMENT '任务目标记录表';

-- 创建用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建部门表
CREATE TABLE IF NOT EXISTS `sys_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dept_name` varchar(50) NOT NULL COMMENT '部门名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父部门ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 创建菜单表
CREATE TABLE IF NOT EXISTS `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `is_frame` tinyint(1) DEFAULT '1' COMMENT '是否为外链：0-是，1-否',
  `is_cache` tinyint(1) DEFAULT '0' COMMENT '是否缓存：0-是，1-否',
  `menu_type` char(1) DEFAULT NULL COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` tinyint(1) DEFAULT '1' COMMENT '菜单状态：0-隐藏，1-显示',
  `status` tinyint(1) DEFAULT '1' COMMENT '菜单状态：0-禁用，1-启用',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 创建角色菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 插入初始数据
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`) VALUES
('超级管理员', 'ROLE_ADMIN', '系统超级管理员'),
('普通用户', 'ROLE_USER', '普通用户');

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `perms`, `icon`) VALUES
('系统管理', 0, 1, 'system', NULL, 'M', NULL, 'system'),
('用户管理', 1, 1, 'user', 'system/user/index', 'C', 'system:user:list', 'user'),
('角色管理', 1, 2, 'role', 'system/role/index', 'C', 'system:role:list', 'peoples'),
('菜单管理', 1, 3, 'menu', 'system/menu/index', 'C', 'system:menu:list', 'tree-table'),
('部门管理', 1, 4, 'dept', 'system/dept/index', 'C', 'system:dept:list', 'tree');

-- 为超级管理员分配所有菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `sys_menu`; 