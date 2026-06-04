-- 将 permissions.id 重排为 1..N 连续自增，并同步 role_permissions.permission_id
-- 适用场景：开发期多次增删权限导致 ID 出现空洞（如 22 条记录但 MAX(id)=66）

SET @offset = 100000;

CREATE TEMPORARY TABLE tmp_perm_remap (
    old_id INT UNSIGNED NOT NULL PRIMARY KEY,
    new_id INT UNSIGNED NOT NULL
);

INSERT INTO tmp_perm_remap (old_id, new_id)
SELECT id, ROW_NUMBER() OVER (ORDER BY id)
FROM permissions;

-- 1) 角色-权限关联表先迁到临时区间
UPDATE role_permissions rp
    INNER JOIN tmp_perm_remap m ON rp.permission_id = m.old_id
SET rp.permission_id = m.new_id + @offset;

-- 2) 权限主表迁到临时区间
UPDATE permissions p
    INNER JOIN tmp_perm_remap m ON p.id = m.old_id
SET p.id = m.new_id + @offset;

-- 3) 落回 1..N
UPDATE permissions SET id = id - @offset WHERE id >= @offset;
UPDATE role_permissions SET permission_id = permission_id - @offset WHERE permission_id >= @offset;

-- 4) 重置自增起点
SET @next_ai = (SELECT IFNULL(MAX(id), 0) + 1 FROM permissions);
SET @sql_ai = CONCAT('ALTER TABLE permissions AUTO_INCREMENT = ', @next_ai);
PREPARE stmt_ai FROM @sql_ai;
EXECUTE stmt_ai;
DEALLOCATE PREPARE stmt_ai;

DROP TEMPORARY TABLE tmp_perm_remap;
