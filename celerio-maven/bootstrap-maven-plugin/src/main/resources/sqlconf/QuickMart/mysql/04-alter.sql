


ALTER TABLE Category ADD COLUMN parent_category int default null;
ALTER TABLE Category ADD CONSTRAINT parent_category_category_fk_1 FOREIGN KEY (parent_category) REFERENCES Category(id);
