-- 插入简单的数据字典测试数据
USE insight_flow;

-- 插入字典类型
INSERT INTO data_dictionary (code, name, parent_id, type, sort, status, remark) VALUES
('USER_STATUS', '用户状态', NULL, 'DICT_TYPE', 1, 1, '用户状态相关字典'),
('PRODUCT_CATEGORY', '产品分类', NULL, 'DICT_TYPE', 2, 1, '产品分类相关字典'),
('REGION_LEVEL', '区域级别', NULL, 'DICT_TYPE', 3, 1, '区域级别相关字典')
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    sort = VALUES(sort),
    status = VALUES(status),
    remark = VALUES(remark);

-- 获取字典类型ID
SET @user_status_id = (SELECT id FROM data_dictionary WHERE code = 'USER_STATUS' AND type = 'DICT_TYPE' LIMIT 1);
SET @product_category_id = (SELECT id FROM data_dictionary WHERE code = 'PRODUCT_CATEGORY' AND type = 'DICT_TYPE' LIMIT 1);
SET @region_level_id = (SELECT id FROM data_dictionary WHERE code = 'REGION_LEVEL' AND type = 'DICT_TYPE' LIMIT 1);

-- 插入字典项
INSERT INTO data_dictionary (code, name, parent_id, type, sort, status, remark) VALUES
-- 用户状态字典项
('ENABLED', '启用', @user_status_id, 'USER_STATUS', 1, 1, '用户启用状态'),
('DISABLED', '禁用', @user_status_id, 'USER_STATUS', 2, 1, '用户禁用状态'),

-- 产品分类字典项
('FOOD', '食品', @product_category_id, 'PRODUCT_CATEGORY', 1, 1, '食品类产品'),
('DRINK', '饮料', @product_category_id, 'PRODUCT_CATEGORY', 2, 1, '饮料类产品'),

-- 区域级别字典项
('PROVINCE', '省份', @region_level_id, 'REGION_LEVEL', 1, 1, '省级区域'),
('CITY', '城市', @region_level_id, 'REGION_LEVEL', 2, 1, '市级区域')
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    sort = VALUES(sort),
    status = VALUES(status),
    remark = VALUES(remark);

-- 查询验证
SELECT * FROM data_dictionary ORDER BY type, sort; 