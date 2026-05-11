-- 文物类型表
CREATE TABLE artifact_types (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '类型名称（如：绘画、瓷器、青铜器、书法、雕塑、玉器等）',
    description TEXT COMMENT '类型描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='文物类型管理表';

-- 创建索引
CREATE INDEX idx_name ON artifact_types(name);

-- 插入默认数据（可选）
INSERT INTO artifact_types (name, description) VALUES
('Painting', '绘画作品，包括国画、油画、水彩等'),
('Ceramics', '陶瓷器物，包括瓷器、陶器等'),
('Bronze', '青铜器，包括礼器、兵器、工具等'),
('Calligraphy', '书法作品，包括碑帖、手卷等'),
('Sculpture', '雕塑作品，包括石雕、木雕、泥塑等'),
('Jade', '玉器，包括玉璧、玉佩、玉雕等'),
('Lacquer', '漆器'),
('Textile', '纺织品，包括丝绸、刺绣等'),
('Metalwork', '金属工艺品，包括金银器等'),
('Enamel', '珐琅器');
