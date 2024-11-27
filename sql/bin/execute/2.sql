drop table if exists category cascade ;
drop table if exists game cascade ;
drop table if exists media cascade ;
drop table if exists category_detail cascade ;

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
-- mediaName could be thumbnail, logo, p1 -> p6

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

drop table if exists public.voucher_use cascade;

alter table public.game
    drop column if exists quantity_count cascade,
    drop column if exists sys_id_discount cascade,
    drop column if exists game_category cascade,
    drop column if exists sys_id_voucher cascade;

alter table public.voucher
    add column quantity integer not null default 0,
    add column is_active boolean not null default true,
    add column voucher_banner text,
    add column max_discount integer not null default 0;

alter table comment
    drop column if exists start,
    add column star integer not null default 0;

alter table comment
    drop column if exists sys_id_product,
    add column sys_id_game integer not null references game;

CREATE OR REPLACE PROCEDURE update_game_ratings()
    LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE game g
    SET rating = subquery.avg_rating,
        rating_count = subquery.comment_count
    FROM (
             SELECT
                 c.sys_id_game,
                 AVG(c.star) AS avg_rating,
                 COUNT(c.sys_id_comment) AS comment_count
             FROM
                 comment c
             WHERE
                 c.comment_date >= CURRENT_DATE - INTERVAL '1 day'
               AND c.comment_date < CURRENT_DATE
             GROUP BY
                 c.sys_id_game
         ) AS subquery
    WHERE
        g.sys_id_game = subquery.sys_id_game;
END;
$$;

ALTER TABLE users
    ADD COLUMN gender BOOLEAN DEFAULT true;

ALTER TABLE users
    ADD COLUMN DOB DATE DEFAULT '2000-01-01';

ALTER TABLE users
    ADD COLUMN phone_number VARCHAR(11);

alter table orders
    drop column if exists quantity_purchased,
    drop column if exists total_game_price,
    add column if not exists quantity int default 1,
    add column if not exists price int default 0;