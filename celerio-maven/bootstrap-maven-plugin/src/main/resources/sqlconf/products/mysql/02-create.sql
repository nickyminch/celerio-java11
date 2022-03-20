

CREATE TABLE Address (
    id                          int auto_increment,
    street_name                 varchar(100),
    city                        varchar(100) not null,
    zip							varchar(50),
    version                     int default 0,

    primary key (id)
);

CREATE TABLE User (
    id                          char(36) not null,
    login                       varchar(100) not null,
    password                    varchar(100) not null,
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

    constraint account_unique_1 unique (login),
    constraint account_unique_2 unique (email),
    constraint account_fk_1 foreign key (address_id) references Address(id),
    primary key (id)
);

CREATE TABLE Role (
    id                          int auto_increment,
    role_name                   varchar(100) not null,

    constraint role_unique_1 unique (role_name),
    primary key (id)
);

CREATE TABLE User_Role (
    user_id						char(36) not null,
    role_id                     int not null,

    constraint account_role_fk_1 foreign key (user_id) references User(id),
    constraint account_role_fk_2 foreign key (role_id) references Role(id),
    primary key (user_id, role_id)
);


CREATE TABLE Product (
    id                          int auto_increment,
    name                       	varchar(100) not null,
    quantity					decimal(65,6) not null default 0,
    price						decimal(65,6) not null default 0,
    description					varchar(2000),
    version                     int          default 0,

    primary key (id)
);

CREATE TABLE Saved_Search (
    id								int auto_increment,
    name							varchar(128) not null,
    form_classname					varchar(256) not null,
    form_content					blob,
    user_id						 	char(36),

    constraint saved_search_fk_1 foreign key (user_id) references User(id),
    primary key (id)
);

CREATE TABLE Category (
	id						int auto_increment,
	name					varchar(100),
	description				varchar(2500),
	
	primary key (id)
);

CREATE TABLE Product_Category (
	product_id						int not null,
	category_id						int not null,
	
    constraint product_category_fk_1 foreign key (product_id) references Product(id),
    constraint product_category_fk_2 foreign key (category_id) references Category(id),
    primary key (product_id, category_id)
);

