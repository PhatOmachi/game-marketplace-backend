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


