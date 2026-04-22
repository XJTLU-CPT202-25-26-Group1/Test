SET @schema_name = DATABASE();
SET @gender_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'app_user'
      AND COLUMN_NAME = 'gender'
);

SET @gender_column_ddl = IF(
    @gender_column_exists = 0,
    'ALTER TABLE app_user ADD COLUMN gender VARCHAR(32) NOT NULL DEFAULT ''UNSPECIFIED''',
    'SELECT 1'
);

PREPARE stmt FROM @gender_column_ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
