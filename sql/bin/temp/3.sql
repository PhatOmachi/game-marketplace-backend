drop table if exists public.voucher_use cascade;

alter table public.game
drop column if exists quantity_count cascade,
    drop column if exists sys_id_discount cascade,
    drop column if exists game_category cascade,
    drop column if exists sys_id_voucher cascade;

alter table public.voucher
    add column quantity       integer not null default 0,
    add column is_active      boolean not null default true,
    add column voucher_banner text,
    add column max_discount   integer not null default 0;

INSERT INTO public.account (username, email, hash_password, is_enabled)
VALUES ('khoadev2004', 'khoadev2004@gmail.com', '$2a$12$IYKGkvL9vqdI3fJWJzi2QeSajsCVD4VkoU7NVJVmLjK4PwtXLHAP6', true);
INSERT INTO public.account (username, email, hash_password, is_enabled)
VALUES ('kaiz', 'kaisamaslain@gmail.com', '$2a$12$GHE5C2wkSTCm3VGMNRS.r.e6XhxLp7Ciq4evDtIdovVRKtdmWzLk6', true);

INSERT INTO public.users (user_name, email, ho_va_ten, balance, join_time, avatar)
VALUES ('kaiz', 'kaisamaslain@gmail.com', 'Nguyen van A', '2323136.0', null, null);
INSERT INTO public.users (user_name, email, ho_va_ten, balance, join_time, avatar)
VALUES ('khoadev2004', 'khoadev2004@gmail.com', 'Nguyen van A', '793332.25', null, null);

INSERT INTO public.roles (username, username_user, role)
VALUES ('khoadev2004', 'khoadev2004', 'CUSTOMER');
INSERT INTO public.roles (username, username_user, role)
VALUES ('kaiz', 'kaiz', 'ADMIN');

alter table comment
drop column if exists start,
    add column star integer not null default 0;

alter table comment
drop column if exists sys_id_product,
    add column sys_id_game integer not null references game;

CREATE OR REPLACE PROCEDURE update_game_ratings()
    LANGUAGE plpgsql
AS
$$
BEGIN
UPDATE game g
SET rating       = subquery.avg_rating,
    rating_count = subquery.comment_count
    FROM (SELECT c.sys_id_game,
                 AVG(c.star)             AS avg_rating,
                 COUNT(c.sys_id_comment) AS comment_count
          FROM comment c
          WHERE c.comment_date >= CURRENT_DATE - INTERVAL '1 day'
            AND c.comment_date < CURRENT_DATE
          GROUP BY c.sys_id_game) AS subquery
WHERE g.sys_id_game = subquery.sys_id_game;
END;
$$;

ALTER TABLE users
    ADD COLUMN gender BOOLEAN DEFAULT true;

ALTER TABLE users
    ADD COLUMN DOB DATE DEFAULT '2000-01-01';

ALTER TABLE users
    ADD COLUMN phone_number VARCHAR(11);

alter table game
drop column if exists quantity,
    drop column if exists quantity_sold,
    drop column if exists rating,
    drop column if exists rating_count,
    add column if not exists rating        double precision,
    add column if not exists rating_count  integer default 0,
    add column if not exists quantity      integer default 1000,
    add column if not exists quantity_sold integer default 0,
    add check (rating_count >= 0),
    add check (quantity >= 0),
    add check (quantity_sold >= 0);

create table ChatRoom
(
    id       int GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    userName varchar(255)
);

create table Message
(
    id        int GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    sender    varchar(255),
    content   text,
    timestamp DATE,
    staff     boolean
);

alter table orders
drop column if exists quantity_purchased,
    drop column if exists total_game_price,
    add column if not exists quantity int default 1,
    add column if not exists price int default 0;

alter table postgres.public.transaction_history
    add column user_balance double precision;