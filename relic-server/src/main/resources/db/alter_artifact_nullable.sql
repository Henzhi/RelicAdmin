-- 修改文物表字段允许为空，解决创建文物时必填字段过多的问题

-- 将 image_url 改为允许 NULL（图片后续通过上传功能添加）
ALTER TABLE artifacts MODIFY COLUMN image_url VARCHAR(500) NULL COMMENT '主图原图URL';

-- 将 detail_url 改为允许 NULL
ALTER TABLE artifacts MODIFY COLUMN detail_url VARCHAR(500) NULL COMMENT '博物馆详情页URL';

-- 将 crawl_date 改为允许 NULL
ALTER TABLE artifacts MODIFY COLUMN crawl_date DATE NULL COMMENT '爬取日期';

-- 可选：如果博物馆ID确实必填，保持 NOT NULL，但前端必须强制选择
-- ALTER TABLE artifacts MODIFY COLUMN museum_id INT UNSIGNED NULL COMMENT '现藏博物馆ID';
