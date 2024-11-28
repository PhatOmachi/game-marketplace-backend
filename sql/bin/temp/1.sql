drop schema if exists public cascade;
create schema if not exists public;

Create table Account
(
    id            int GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    username      varchar(50)  NOT NULL UNIQUE,
    email         varchar(255) NOT NULL UNIQUE,
    hash_password varchar(255) NOT NULL,
    is_enabled    boolean DEFAULT True
);

Create table Users
(
    sys_id_user int GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) primary key,
    user_name   varchar(255) NOT NULL UNIQUE,
    email       varchar(255) NOT NULL UNIQUE,
    ho_va_ten   varchar(255) default 'Nguyen van A',
    balance     varchar(255) DEFAULT 0,
    join_time   TIMESTAMP,
    avatar      text
);

CREATE TABLE Failed_Login_Attempt
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username         VARCHAR(255) NOT NULL,
    failed_attempts  INT          NOT NULL,
    last_failed_time TIMESTAMP    NOT NULL
);

Create table Roles
(
    sys_id_role   int GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) primary key,
    username      varchar(50)  NOT NULL,
    username_user varchar(255) NOT NULL,
    role          varchar(50)  NOT NULL,
    FOREIGN KEY (username) REFERENCES account (username),
    FOREIGN KEY (username_user) REFERENCES Users (user_name)
);

CREATE TABLE Voucher
(
    sys_id_voucher   INT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    code_voucher     VARCHAR(8) UNIQUE,
    discount_name    VARCHAR(255),
    discount_percent FLOAT,
    start_date       TIMESTAMP,
    end_date         TIMESTAMP,
    description      text
);

CREATE TABLE Voucher_used
(
    sys_id_voucher_used INT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    sys_id_user         INT       NOT NULL,
    sys_id_voucher      INT       NOT NULL,
    use_date            TIMESTAMP NOT NULL,
    FOREIGN KEY (sys_id_user) REFERENCES Users (Sys_id_user),
    FOREIGN KEY (sys_id_voucher) REFERENCES Voucher (Sys_id_voucher)
);

CREATE TABLE Category
(
    sys_id_category INT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    category_name   NCHAR(255),
    description     TEXT
);

CREATE TABLE Game
(
    sys_id_game      INT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    game_code        VARCHAR(255),
    game_name        VARCHAR(255),
    status           BOOLEAN default true,
    price            FLOAT   default 0.0,
    discount_percent FLOAT,
    game_image       VARCHAR(255),
    slug             VARCHAR(255) UNIQUE NOT NULL,
    description      TEXT,
    is_active        BOOLEAN DEFAULT True,
    quantity         int,
    quantity_sold    int
);

CREATE TABLE Owned_game
(
    sys_id_owned_game INT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    owned_date        TIMESTAMP,
    sys_id_user       INT NOT NULL,
    sys_id_game       INT NOT NULL,
    FOREIGN KEY (sys_id_user) REFERENCES Users (sys_id_user),
    FOREIGN KEY (sys_id_game) REFERENCES Game (sys_id_game)
);

drop table if exists Category_detail;

CREATE TABLE Category_detail
(
    sys_id_category_detail INT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    sys_id_category        INT NOT NULL,
    sys_id_game            INT NOT NULL,
    FOREIGN KEY (sys_id_category) REFERENCES Category (sys_id_category),
    FOREIGN KEY (sys_id_game) REFERENCES Game (sys_id_game)
);

CREATE TABLE Orders
(
    sys_id_order       INT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    order_code         VARCHAR(255),
    order_date         TIMESTAMP,
    payment_status     BOOLEAN,
    total_game_price   FLOAT,
    total_payment      FLOAT,
    quantity_purchased INT,
    sys_id_product     INT,
    sys_id_user        INT,
    FOREIGN KEY (sys_id_product) REFERENCES Game (sys_id_game),
    FOREIGN KEY (sys_id_user) REFERENCES Users (sys_id_user)
);

Create table Transaction_History
(
    sys_id_payment int GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    payment_time   TIMESTAMP,
    description    varchar(255),
    amount         FLOAT,
    status         boolean default FALSE,
    user_name      varchar(255) not null,
    FOREIGN KEY (user_name) REFERENCES Users (user_name)
);


CREATE TABLE Comment
(
    sys_id_comment INT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    context        text default 'Game hay',
    comment_date   TIMESTAMP,
    start          int  default 5,
    sys_id_user    INT NOT NULL,
    sys_id_product INT NOT NULL,
    FOREIGN KEY (sys_id_user) REFERENCES Users (sys_id_user),
    FOREIGN KEY (sys_id_product) REFERENCES Game (sys_id_game)
);

CREATE OR REPLACE FUNCTION insert_user_and_role(
    p_username VARCHAR,
    p_email VARCHAR,
    p_role VARCHAR
)
    RETURNS VOID AS
$$
BEGIN
    INSERT INTO users(user_name, email)
    VALUES (p_username, p_email);

    INSERT INTO roles(username, username_user, role)
    VALUES (p_username, p_username, p_role);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_account_user_and_role(
    p_username VARCHAR,
    p_email VARCHAR,
    p_hash_password VARCHAR,
    p_ho_va_ten VARCHAR,
    p_phone_number VARCHAR
)
    RETURNS VOID AS
$$
BEGIN
    INSERT INTO account(username, email, hash_password, is_enabled)
    VALUES (p_username, p_email, p_hash_password, TRUE);

    INSERT INTO users(user_name, email, ho_va_ten, join_time, gender, phone_number)
    VALUES (p_username, p_email, p_ho_va_ten, now(), true, p_phone_number);

    INSERT INTO roles(username, username_user, role)
    VALUES (p_username, p_username, 'CUSTOMER');
END;
$$ LANGUAGE plpgsql;

drop table if exists category cascade;
drop table if exists game cascade;
drop table if exists media cascade;
drop table if exists category_detail cascade;

create table category
(
    sys_id_category integer generated always as identity
        primary key,
    category_name   varchar(255),
    description     text
);

alter table category
    owner to postgres;


create table game
(
    sys_id_game      integer generated always as identity
        primary key,
    game_code        varchar(255),
    game_name        varchar(255),
    status           boolean          default true,
    price            double precision default 0.0,
    discount_percent double precision,
    game_image       varchar(255),
    slug             varchar(255) not null
        unique,
    game_category    varchar(255),
    description      text,
    is_active        boolean          default true,
    sys_id_discount  integer
        references voucher,
    quantity         integer,
    quantity_sold    integer,
    quantity_count   integer,
    rating           double precision default 5.0,
    rating_count     integer          default 0,
    features         text,
    release_date     date,
    developer        varchar(255),
    platform         varchar(255),
    language         varchar(255),
    about            text,
    sys_id_voucher   integer
);

alter table game
    owner to postgres;

create table category_detail
(
    sys_id_category_detail integer generated always as identity
        primary key,
    sys_id_category        integer not null
        references category,
    sys_id_game            integer not null
        references game
);

alter table category_detail
    owner to postgres;

create table media
(
    sys_id_media integer generated always as identity
        primary key,
    media_name   varchar(255) default 'thumbnail'::character varying,
    media_url    text,
    sys_id_game  integer
        references game
);

alter table media
    owner to postgres;