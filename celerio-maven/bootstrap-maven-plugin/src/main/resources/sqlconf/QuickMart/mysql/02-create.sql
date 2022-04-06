

CREATE TABLE address (
    id                          int auto_increment,
    street_name                 varchar(100),
    city                        varchar(100) not null,
    zip							varchar(50),
    version                     int default 0,
    phone_number 				VARCHAR(50) default null,

    primary key (id)
);

CREATE TABLE user (
    id                          char(36) not null,
    login                       varchar(100) not null,
    password                    varchar(100),
    email                       varchar(100) not null,
    is_enabled                  bool not null default true,
    title 	                    char(2) default 'MR',
    first_name                  varchar(100),
    last_name                   varchar(100),
    birth_date                  timestamp default null,
    description                 varchar(255),
    address_id                  int,
    creation_date               timestamp default null,
    creation_author             varchar(200),
    last_modification_date      timestamp default null,
    last_modification_author    varchar(200),
    version                     int default 0,
    is_encrypted 				bool not null default false,
    last_login 					timestamp,
    this_login 					timestamp,
    reset_token 				VARCHAR(100) default null,
    reset_token_expiration 		timestamp,
    reset_sms_token 			VARCHAR(100) default null,
    reset_sms_token_expiration 	timestamp,

    constraint user_unique_1 unique (login),
    constraint user_fk_1 foreign key (address_id) references address(id),
    primary key (id)
);

CREATE TABLE role (
    id                          int auto_increment,
    role_name                   varchar(100) not null,

    constraint role_unique_1 unique (role_name),
    primary key (id)
);

CREATE TABLE user_role (
    user_id						char(36) not null,
    role_id                     int not null,

    constraint account_role_fk_1 foreign key (user_id) references user(id),
    constraint account_role_fk_2 foreign key (role_id) references role(id),
    primary key (user_id, role_id)
);


CREATE TABLE product (
    id                          int auto_increment,
    name                       	varchar(100) not null,
    description					varchar(2000),
    version                     int          default 0,

    primary key (id)
);

CREATE TABLE saved_search (
    id								int auto_increment,
    name							varchar(128) not null,
    form_classname					varchar(256) not null,
    form_content					blob,
    user_id						 	char(36),

    constraint saved_search_fk_1 foreign key (user_id) references user(id),
    primary key (id)
);

CREATE TABLE category (
	id						int auto_increment,
	name					varchar(100),
	description				varchar(2500),
	parent_category 		int default null,
	
	primary key (id),
	constraint category_unique_1 unique (name),
	constraint category_parent_category_fk_1 FOREIGN KEY (parent_category) REFERENCES category(id)
);

CREATE TABLE product_category (
	product_id						int not null,
	category_id						int not null,
	
    constraint product_category_fk_1 foreign key (product_id) references product(id),
    constraint product_category_fk_2 foreign key (category_id) references category(id),
    primary key (product_id, category_id)
);

CREATE TABLE store (
	id 							int auto_increment,
	role_id                     int not null,
	name						VARCHAR(100),
	employee_id					int,
	line1						VARCHAR(100),
	line2						VARCHAR(100),
	city						VARCHAR(100),
	state						VARCHAR(100),
	zip							VARCHAR(50),
	country						VARCHAR(100),
	phone						VARCHAR(50),
	email						varchar(200),
	created_at					timestamp default null,
	updated_at					timestamp default null,
	
	primary key (id),
	constraint store_role_fk_1 foreign key (role_id) references role(id)
);


CREATE TABLE store_product (
	id							int auto_increment,
	store_id					int not null,
	product_id					int not null,
    quantity					decimal(65,6) not null default 0,
    price						decimal(65,6) not null default 0,
	careated_at					timestamp default null,
	updated_at					timestamp default null,
	
	primary key (id),
    constraint store_product_fk_1 foreign key (store_id) references store(id),
    constraint store_product_fk_2 foreign key (product_id) references product(id)
);