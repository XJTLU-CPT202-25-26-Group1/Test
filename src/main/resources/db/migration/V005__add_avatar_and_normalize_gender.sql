SET @schema_name = DATABASE();

SET @avatar_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'app_user'
      AND COLUMN_NAME = 'avatar_path'
);

SET @avatar_column_ddl = IF(
    @avatar_column_exists = 0,
    'ALTER TABLE app_user ADD COLUMN avatar_path VARCHAR(255) NULL',
    'SELECT 1'
);

PREPARE stmt FROM @avatar_column_ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE app_user
SET gender = CASE
    WHEN UPPER(TRIM(IFNULL(gender, ''))) = 'FEMALE' THEN 'FEMALE'
    ELSE 'MALE'
END
WHERE gender IS NULL
   OR UPPER(TRIM(gender)) NOT IN ('MALE', 'FEMALE');

ALTER TABLE app_user
    MODIFY COLUMN gender VARCHAR(32) NOT NULL DEFAULT 'MALE';
