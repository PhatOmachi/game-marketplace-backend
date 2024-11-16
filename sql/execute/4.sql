INSERT INTO public.account (username, email, hash_password, is_enabled) VALUES ('khoadev2004', 'khoadev2004@gmail.com', '$2a$12$IYKGkvL9vqdI3fJWJzi2QeSajsCVD4VkoU7NVJVmLjK4PwtXLHAP6', true);
INSERT INTO public.account (username, email, hash_password, is_enabled) VALUES ('kaiz', 'kaisamaslain@gmail.com', '$2a$12$GHE5C2wkSTCm3VGMNRS.r.e6XhxLp7Ciq4evDtIdovVRKtdmWzLk6', true);

INSERT INTO public.users (user_name, email, ho_va_ten, balance, join_time, avatar) VALUES ('kaiz', 'kaisamaslain@gmail.com', 'Nguyen van A', '2323136.0', null, null);
INSERT INTO public.users (user_name, email, ho_va_ten, balance, join_time, avatar) VALUES ('khoadev2004', 'khoadev2004@gmail.com', 'Nguyen van A', '793332.25', null, null);

INSERT INTO public.roles (username, username_user, role) VALUES ('khoadev2004', 'khoadev2004', 'CUSTOMER');
INSERT INTO public.roles (username, username_user, role) VALUES ('kaiz', 'kaiz', 'ADMIN');

alter table comment
    drop column start,
    add column star integer not null default 0;

alter table comment
drop column sys_id_product,
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