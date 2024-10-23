SET SCHEMA 'Data_DuLieu';

DROP DATABASE IF EXISTS Data_DuLieu;

CREATE DATABASE Data_DuLieu;

SET SCHEMA 'Data_DuLieu';


Create table Account(
    id              int GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    username        varchar(50) NOT NULL UNIQUE,
    email           varchar(255) NOT NULL UNIQUE,
    hash_password		varchar(255) NOT NULL,
    is_enabled			boolean DEFAULT True
);

Create table Users(
    sys_id_user int GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) primary key,
    user_name varchar(255) NOT NULL UNIQUE,
    email varchar(255) NOT NULL UNIQUE,
    ho_va_ten varchar(255) default 'Nguyen van A',
    balance varchar(255) DEFAULT 0,
    join_time TIMESTAMP,
    avatar text
);

CREATE TABLE Failed_Login_Attempt (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    failed_attempts INT NOT NULL,
    last_failed_time TIMESTAMP NOT NULL
);

Create table Roles(
    sys_id_role int GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) primary key,
    username varchar(50) NOT NULL  ,
    username_user varchar(255) NOT NULL  ,
    role varchar(50) NOT NULL  ,
    FOREIGN KEY (username) REFERENCES account(username),
    FOREIGN KEY (username_user) REFERENCES Users(user_name)
);

CREATE TABLE Voucher (
    sys_id_voucher INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    code_voucher VARCHAR(8) UNIQUE,
    discount_name VARCHAR(255),
    discount_percent FLOAT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    description text
);

CREATE TABLE Voucher_used (
    sys_id_voucher_used INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    sys_id_user INT NOT NULL,
    sys_id_voucher INT NOT NULL,
    use_date TIMESTAMP NOT NULL,
    FOREIGN KEY (sys_id_user) REFERENCES Users(Sys_id_user),
    FOREIGN KEY (sys_id_voucher) REFERENCES Voucher(Sys_id_voucher)
);

CREATE TABLE Category (
    sys_id_category INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    category_name NCHAR(255),
    description TEXT
);

CREATE TABLE Game (
    sys_id_game INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    game_code VARCHAR(255),
    game_name VARCHAR(255),
    status BOOLEAN default true,
    price FLOAT default 0.0,
    discount_percent FLOAT,
    game_image VARCHAR(255),
    slug VARCHAR(255) UNIQUE NOT NULL,
    game_category VARCHAR(255),
    description TEXT,
    is_active BOOLEAN DEFAULT True,
    sys_id_discount INT,
    quantity int,
    quantity_sold int,
    quantity_count int,
    FOREIGN KEY (sys_id_discount) REFERENCES Voucher(sys_id_voucher)
);

CREATE TABLE Owned_game(
    sys_id_owned_game INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    owned_date TIMESTAMP,
    sys_id_user INT NOT NULL,
    sys_id_game INT NOT NULL ,
    FOREIGN KEY (sys_id_user) REFERENCES Users (sys_id_user),
    FOREIGN KEY (sys_id_game) REFERENCES Game (sys_id_game)
);

CREATE TABLE Category_detail (
    sys_id_category INT NOT NULL,
    sys_id_game INT NOT NULL,
    FOREIGN KEY (sys_id_category) REFERENCES Category (sys_id_category),
    FOREIGN KEY (sys_id_game) REFERENCES Game (sys_id_game),
    PRIMARY KEY (sys_id_category, sys_id_game)
);

CREATE TABLE Orders (
    sys_id_order INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    order_code VARCHAR(255),
    order_date TIMESTAMP,
    payment_status BOOLEAN,
    total_game_price FLOAT,
    total_payment FLOAT,
    quantity_purchased INT,
    sys_id_product INT,
    sys_id_user INT,
    FOREIGN KEY (sys_id_product) REFERENCES Game (sys_id_game),
    FOREIGN KEY (sys_id_user) REFERENCES Users (sys_id_user)
);

Create table Transaction_History(
    sys_id_payment int GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY ,
    payment_time TIMESTAMP,
    description varchar(255),
    amount FLOAT,
    status boolean default FALSE,
    user_name varchar(255) not null ,
    FOREIGN KEY (user_name) REFERENCES Users (user_name)
);


CREATE TABLE Comment (
    sys_id_comment INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    context text default 'Game hay',
    comment_date TIMESTAMP,
    start int default 5,
    sys_id_user INT NOT NULL,
    sys_id_product INT NOT NULL,
    FOREIGN KEY (sys_id_user) REFERENCES Users(sys_id_user),
    FOREIGN KEY (sys_id_product) REFERENCES Game (sys_id_game)
);
