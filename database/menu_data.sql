-- 菜单数据初始化脚本
-- 清空现有菜单数据（如果需要重新初始化）
-- DELETE FROM sys_role_menu;
-- DELETE FROM sys_menu;

-- 插入菜单数据
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES

-- 一级菜单：仪表盘
(1, '仪表盘', NULL, 1, 'dashboard', NULL, NULL, 1, 0, 'M', 1, 1, NULL, 'dashboard', NOW(), NOW(), 0),

-- 二级菜单：仪表盘子菜单
(11, '数据大屏', 1, 1, 'bigscreen', 'dashboard/BigScreen', NULL, 1, 0, 'C', 1, 1, 'dashboard:bigscreen:view', 'monitor', NOW(), NOW(), 0),
(12, '数据概览', 1, 2, 'overview', 'dashboard/index', NULL, 1, 0, 'C', 1, 1, 'dashboard:overview:view', 'chart', NOW(), NOW(), 0),

-- 一级菜单：客户管理
(2, '客户管理', NULL, 2, 'customer', NULL, NULL, 1, 0, 'M', 1, 1, NULL, 'peoples', NOW(), NOW(), 0),

-- 二级菜单：客户管理子菜单
(21, '终端商户', 2, 1, 'terminal', 'manage/customer/Terminal', NULL, 1, 0, 'C', 1, 1, 'customer:terminal:list', 'shop', NOW(), NOW(), 0),
(22, '批发商', 2, 2, 'wholesaler', 'manage/customer/Wholesaler', NULL, 1, 0, 'C', 1, 1, 'customer:wholesaler:list', 'building', NOW(), NOW(), 0),

-- 一级菜单：员工管理
(3, '员工管理', NULL, 3, 'employee', NULL, NULL, 1, 0, 'M', 1, 1, NULL, 'user', NOW(), NOW(), 0),

-- 二级菜单：员工管理子菜单
(31, '员工信息', 3, 1, 'info', 'manage/employee/Employee', NULL, 1, 0, 'C', 1, 1, 'employee:info:list', 'user', NOW(), NOW(), 0),
(32, '任务管理', 3, 2, 'task', 'manage/employee/Task', NULL, 1, 0, 'C', 1, 1, 'employee:task:list', 'list', NOW(), NOW(), 0),
(33, '用户管理', 3, 3, 'user', 'manage/employee/User', NULL, 1, 0, 'C', 1, 1, 'employee:user:list', 'peoples', NOW(), NOW(), 0),

-- 一级菜单：产品管理
(4, '产品管理', NULL, 4, 'product', NULL, NULL, 1, 0, 'M', 1, 1, NULL, 'shopping', NOW(), NOW(), 0),

-- 二级菜单：产品管理子菜单
(41, '产品信息', 4, 1, 'info', 'manage/product/Product', NULL, 1, 0, 'C', 1, 1, 'product:info:list', 'goods', NOW(), NOW(), 0),

-- 一级菜单：销售管理
(5, '销售管理', NULL, 5, 'sales', NULL, NULL, 1, 0, 'M', 1, 1, NULL, 'money', NOW(), NOW(), 0),

-- 二级菜单：销售管理子菜单
(51, '销售记录', 5, 1, 'record', 'manage/sales/Sales', NULL, 1, 0, 'C', 1, 1, 'sales:record:list', 'document', NOW(), NOW(), 0),
(52, '成交记录', 5, 2, 'deal', 'manage/sales/Deal', NULL, 1, 0, 'C', 1, 1, 'sales:deal:list', 'success', NOW(), NOW(), 0),

-- 一级菜单：项目管理
(6, '项目管理', NULL, 6, 'project', NULL, NULL, 1, 0, 'M', 1, 1, NULL, 'project', NOW(), NOW(), 0),

-- 二级菜单：项目管理子菜单
(61, '项目列表', 6, 1, 'list', 'manage/project/index', NULL, 1, 0, 'C', 1, 1, 'project:list:view', 'list', NOW(), NOW(), 0),
(62, '项目详情', 6, 2, 'detail', 'manage/project/detail', NULL, 1, 0, 'C', 0, 1, 'project:detail:view', 'document', NOW(), NOW(), 0),
(63, '项目进度', 6, 3, 'progress', 'manage/project/progress', NULL, 1, 0, 'C', 1, 1, 'project:progress:view', 'time', NOW(), NOW(), 0),
(64, '项目任务', 6, 4, 'task', 'manage/project/task', NULL, 1, 0, 'C', 1, 1, 'project:task:view', 'checkbox', NOW(), NOW(), 0),

-- 一级菜单：报表管理
(7, '报表管理', NULL, 7, 'report', NULL, NULL, 1, 0, 'M', 1, 1, NULL, 'chart', NOW(), NOW(), 0),

-- 二级菜单：报表管理子菜单
(71, '销售分析', 7, 1, 'sales-analysis', 'manage/report/sales-analysis', NULL, 1, 0, 'C', 1, 1, 'report:sales:view', 'trend-charts', NOW(), NOW(), 0),

-- 一级菜单：系统管理
(8, '系统管理', NULL, 8, 'system', NULL, NULL, 1, 0, 'M', 1, 1, NULL, 'system', NOW(), NOW(), 0),

-- 二级菜单：系统管理子菜单
(81, '用户管理', 8, 1, 'user', 'manage/system/user/index', NULL, 1, 0, 'C', 1, 1, 'system:user:list', 'user', NOW(), NOW(), 0),
(82, '角色管理', 8, 2, 'role', 'manage/system/role/index', NULL, 1, 0, 'C', 1, 1, 'system:role:list', 'peoples', NOW(), NOW(), 0),
(83, '菜单管理', 8, 3, 'menu', 'manage/system/menu/index', NULL, 1, 0, 'C', 1, 1, 'system:menu:list', 'tree-table', NOW(), NOW(), 0),
(84, '部门管理', 8, 4, 'dept', 'manage/system/dept/index', NULL, 1, 0, 'C', 1, 1, 'system:dept:list', 'tree', NOW(), NOW(), 0),
(85, '数据字典', 8, 5, 'dictionary', 'manage/system/Dictionary', NULL, 1, 0, 'C', 1, 1, 'system:dictionary:list', 'dict', NOW(), NOW(), 0),

-- 一级菜单：模板管理
(9, '模板管理', NULL, 9, 'template', NULL, NULL, 1, 0, 'M', 1, 1, NULL, 'document', NOW(), NOW(), 0),

-- 二级菜单：模板管理子菜单
(91, '接口模板', 9, 1, 'interface', 'manage/system/interface/index', NULL, 1, 0, 'C', 1, 1, 'template:interface:list', 'api', NOW(), NOW(), 0),
(92, '模板编辑', 9, 2, 'edit', 'manage/system/interface/TemplateEdit', NULL, 1, 0, 'C', 0, 1, 'template:edit:view', 'edit', NOW(), NOW(), 0),

-- 一级菜单：可视化配置
(10, '可视化配置', NULL, 10, 'visual', NULL, NULL, 1, 0, 'M', 1, 1, NULL, 'monitor', NOW(), NOW(), 0),

-- 二级菜单：可视化配置子菜单
(101, '模板配置', 10, 1, 'config', 'manage/visualConfig/VisualConfig', NULL, 1, 0, 'C', 1, 1, 'visual:config:view', 'setting', NOW(), NOW(), 0),
(102, '模板编辑', 10, 2, 'edit', 'manage/visualConfig/TemplateEdit', NULL, 1, 0, 'C', 0, 1, 'visual:edit:view', 'edit', NOW(), NOW(), 0),
(103, '子表配置', 10, 3, 'child-table', 'manage/visualConfig/ChildTableDialog', NULL, 1, 0, 'C', 0, 1, 'visual:child-table:view', 'table', NOW(), NOW(), 0),

-- 一级菜单：统计分析
(13, '统计分析', NULL, 11, 'statistics', NULL, NULL, 1, 0, 'M', 1, 1, NULL, 'chart', NOW(), NOW(), 0),

-- 二级菜单：统计分析子菜单
(131, '销售统计', 13, 1, 'sales', 'manage/statistics/sales', NULL, 1, 0, 'C', 1, 1, 'statistics:sales:view', 'trend-charts', NOW(), NOW(), 0),
(132, '客户统计', 13, 2, 'customer', 'manage/statistics/customer', NULL, 1, 0, 'C', 1, 1, 'statistics:customer:view', 'peoples', NOW(), NOW(), 0),
(133, '产品统计', 13, 3, 'product', 'manage/statistics/product', NULL, 1, 0, 'C', 1, 1, 'statistics:product:view', 'goods', NOW(), NOW(), 0);

-- 插入角色菜单关联数据（为超级管理员分配所有菜单权限）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 11), (1, 12),  -- 仪表盘
(1, 2), (1, 21), (1, 22),  -- 客户管理
(1, 3), (1, 31), (1, 32), (1, 33),  -- 员工管理
(1, 4), (1, 41),  -- 产品管理
(1, 5), (1, 51), (1, 52),  -- 销售管理
(1, 6), (1, 61), (1, 62), (1, 63), (1, 64),  -- 项目管理
(1, 7), (1, 71),  -- 报表管理
(1, 8), (1, 81), (1, 82), (1, 83), (1, 84), (1, 85),  -- 系统管理
(1, 9), (1, 91), (1, 92),  -- 模板管理
(1, 10), (1, 101), (1, 102), (1, 103),  -- 可视化配置
(1, 13), (1, 131), (1, 132), (1, 133);  -- 统计分析

-- 为普通用户分配基础菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 1), (2, 11), (2, 12),  -- 仪表盘
(2, 2), (2, 21), (2, 22),  -- 客户管理
(2, 3), (2, 31), (2, 32),  -- 员工管理（不含用户管理）
(2, 4), (2, 41),  -- 产品管理
(2, 5), (2, 51), (2, 52),  -- 销售管理
(2, 6), (2, 61), (2, 63), (2, 64),  -- 项目管理（不含项目详情）
(2, 7), (2, 71),  -- 报表管理
(2, 9), (2, 91),  -- 模板管理（不含编辑权限）
(2, 10), (2, 101),  -- 可视化配置（不含编辑权限）
(2, 13), (2, 131), (2, 132), (2, 133);  -- 统计分析

-- 更新菜单表的自增ID
ALTER TABLE `sys_menu` AUTO_INCREMENT = 134; 