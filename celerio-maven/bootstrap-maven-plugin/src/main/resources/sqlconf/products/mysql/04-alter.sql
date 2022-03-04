ALTER TABLE User MODIFY COLUMN PASSWORD  VARCHAR(100);
ALTER TABLE User DROP INDEX account_unique_2;
ALTER TABLE User ADD COLUMN is_encrypted bool not null default false;