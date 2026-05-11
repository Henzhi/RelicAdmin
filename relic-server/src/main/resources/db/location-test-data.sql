-- 地点表测试数据
-- 先清空现有数据（谨慎使用）
-- DELETE FROM locations;

-- 插入国家数据
INSERT INTO locations (name_zh, name_en, parent_id, type, created_at) VALUES
('中国', 'China', NULL, 'country', NOW()),
('美国', 'United States', NULL, 'country', NOW()),
('英国', 'United Kingdom', NULL, 'country', NOW());

-- 获取中国ID（假设为1）并插入省份数据
INSERT INTO locations (name_zh, name_en, parent_id, type, created_at) VALUES
('陕西', 'Shaanxi', 1, 'province', NOW()),
('河南', 'Henan', 1, 'province', NOW()),
('北京', 'Beijing', 1, 'province', NOW()),
('江苏', 'Jiangsu', 1, 'province', NOW());

-- 获取陕西ID（假设为4）并插入城市数据
INSERT INTO locations (name_zh, name_en, parent_id, type, created_at) VALUES
('西安', 'Xi\'an', 4, 'city', NOW()),
('咸阳', 'Xianyang', 4, 'city', NOW());

-- 获取河南ID（假设为5）并插入城市数据
INSERT INTO locations (name_zh, name_en, parent_id, type, created_at) VALUES
('洛阳', 'Luoyang', 5, 'city', NOW()),
('开封', 'Kaifeng', 5, 'city', NOW());

-- 获取西安ID（假设为8）并插入遗址数据
INSERT INTO locations (name_zh, name_en, parent_id, type, created_at) VALUES
('兵马俑', 'Terracotta Army', 8, 'site', NOW()),
('大雁塔', 'Giant Wild Goose Pagoda', 8, 'site', NOW());

-- 获取洛阳ID（假设为10）并插入遗址数据
INSERT INTO locations (name_zh, name_en, parent_id, type, created_at) VALUES
('龙门石窟', 'Longmen Grottoes', 10, 'site', NOW());

-- 查询验证
SELECT * FROM locations ORDER BY type, name_zh;
