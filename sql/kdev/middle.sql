alter table Game
    add column rating       float default 5.0,
    add column rating_count int   default 0,
    add column features     text,
    add column release_date date,
    add column developer    varchar(255),
-- add column publisher varchar(255),
    add column platform     varchar(255),
    add column language     varchar(255),
    add column about        text;

alter table public.voucher
    add column voucher_banner text;
INSERT INTO public.voucher (code_voucher, discount_name, discount_percent, start_date, end_date, description, voucher_banner) VALUES ('fallout', 'Fallout Day Sale', 50, '2024-10-20', '2024-10-31', 'Celebrate Fallout Day with discounts on Fallout games and more', 'https://cdn2.unrealengine.com/falloutday-sale-2024-epic-keyart-1920x1080-01-1920x1080-1c74e27f2de9.jpg?resize=1&w=854&h=480&quality=medium');
INSERT INTO public.voucher (code_voucher, discount_name, discount_percent, start_date, end_date, description, voucher_banner) VALUES ('about-to', 'Brand new seasons', 69, '2024-10-20', '2024-10-31', 'New nuke of voucher seasons', 'https://cdn2.unrealengine.com/egs-nis-america-publisher-sale-2024-breaker-1920x1080-44eaff94888a.jpg?resize=1&w=854&h=480&quality=medium');
INSERT INTO public.voucher (code_voucher, discount_name, discount_percent, start_date, end_date, description, voucher_banner) VALUES ('elder', 'Elder Festival', 44, '2024-10-20', '2024-10-28', 'Elder Festival for Halloween holiday', 'https://cdn2.unrealengine.com/egs-eso-witches-festival-2024-breaker-1920x1080-817df397bcf1.jpg?resize=1&w=854&h=480&quality=medium');

alter table public.game
    drop column if exists sys_id_voucher,
    add column sys_id_voucher int;

create table media
(
    sys_id_media int GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) primary key,
    media_name   varchar(255) default 'thumbnail',
    media_url    text,
    sys_id_game  int,
    FOREIGN KEY (sys_id_game) REFERENCES Game (sys_id_game)
);



CREATE OR REPLACE FUNCTION insert_user_and_role(
    p_username VARCHAR,
    p_email VARCHAR,
    p_role VARCHAR
)
    RETURNS VOID AS
$$
BEGIN
    -- Insert into users table
    INSERT INTO users(user_name, email)
    VALUES (p_username, p_email);

-- Insert into roles table
    INSERT INTO roles(username, username_user, role)
    VALUES (p_username, p_username, p_role);
END;
$$ LANGUAGE plpgsql;

INSERT INTO public.category (category_name)
VALUES ('Action'),
       ('First Person'),
       ('Horror'),
       ('Open World'),
       ('RPG'),
       ('Survival');

INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES (null, 'Dying Light 2 Stay Human - Reloaded Edition', true, 990000, 60, null, 'dying-light-2-stay-human', e'Action
First Person
Horror
Open World
RPG
Survival',
        'Now with guns! With the Reloaded Edition, the arsenal at your disposal has grown massively. Be creative with your combat or outrun your foes as you enjoy the perks of more than 10 post-launch updates. Good luck out there, and stay human!',
        true, null, null, null, null, null, 4.6, 0, e'Achievements
Cloud Saves
Co-op
Controller Support
Multiplayer
Single Player', null, null, null, e'RELOADED EDITION
Dying Light 2 Stay Human: Reloaded Edition includes:

Dying Light 2 Stay Human

‘Bloody Ties’ DLC

Now with guns! With the Reloaded Edition, the arsenal at your disposal has grown massively. Be creative with your combat or outrun your foes as you enjoy the perks of more than 10 post-launch updates. Good luck out there, and stay human!

It’s been 20 years since the events of the original game. The virus won, and humanity is slowly dying. You play as Aiden Caldwell, a wandering Pilgrim who delivers goods, brings news, and connects the few remaining survivor settlements in barren lands devastated by the zombie virus. However, your true goal is to find your little sister Mia, who you left behind as a kid to escape Dr. Waltz\'s torturous experiments. Haunted by the past, you eventually make the decision to confront it when you learn that Mia may still be alive in Villedor — the last city standing on Earth.

You quickly find yourself in a settlement torn by conflict. You’ll need to engage in creative and gory combat, so hone your skills to defeat hordes of zombies and make allies. Roam the city, free run across Villedor’s buildings and rooftops in search of loot in remote areas, and be wary of the night. With every sunset, monsters take control of the streets.',
        '2022-04-02');
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME001', 'The Legend of Heroes', true, 59.99, 10, 'legend_heroes.jpg', 'the-legend-of-heroes', 'RPG',
        'An epic adventure game', true, null, 100, 10, 90, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME002', 'Super Racing Turbo', true, 39.99, 5, 'super_racing.jpg', 'super-racing-turbo', 'Racing',
        'Fast-paced racing game', true, null, 200, 50, 150, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME003', 'Mystic Journey', true, 29.99, 15, 'mystic_journey.jpg', 'mystic-journey', 'Adventure',
        'Explore mysterious lands', true, null, 150, 25, 125, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME004', 'Galactic Warfare', true, 49.99, 20, 'galactic_warfare.jpg', 'galactic-warfare', 'Action',
        'Intergalactic space battle', true, null, 120, 40, 80, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME005', 'Soccer Stars', true, 19.99, 8, 'soccer_stars.jpg', 'soccer-stars', 'Sports',
        'Exciting soccer matches', true, null, 300, 100, 200, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME006', 'Fantasy World Builder', true, 25.99, 12, 'fantasy_world_builder.jpg', 'fantasy-world-builder',
        'Simulation', 'Build your dream world', true, null, 180, 60, 120, null, 5, 0, null, null, null, null, null,
        null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME007', 'Space Invaders Extreme', true, 9.99, 5, 'space_invaders.jpg', 'space-invaders-extreme', 'Arcade',
        'Classic arcade shooter', true, null, 500, 300, 200, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME008', 'Dragon Quest Legends', true, 54.99, 18, 'dragon_quest_legends.jpg', 'dragon-quest-legends', 'RPG',
        'Fantasy RPG adventure', true, null, 100, 20, 80, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME009', 'Battle of Titans', true, 44.99, 25, 'battle_of_titans.jpg', 'battle-of-titans', 'Action',
        'Massive titan battles', true, null, 160, 70, 90, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME010', 'Speed Racer X', true, 34.99, 10, 'speed_racer_x.jpg', 'speed-racer-x', 'Racing',
        'High-speed racing game', true, null, 210, 90, 120, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME011', 'Magic Quest', true, 49.99, 15, 'magic_quest.jpg', 'magic-quest', 'RPG', 'A magical adventure game',
        true, null, 140, 50, 90, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME012', 'FIFA World Championship', true, 69.99, 20, 'fifa_world.jpg', 'fifa-world-championship', 'Sports',
        'The ultimate soccer game', true, null, 500, 300, 200, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME013', 'Adventure Time Heroes', true, 29.99, 10, 'adventure_time.jpg', 'adventure-time-heroes', 'Adventure',
        'Explore the land of Ooo', true, null, 250, 60, 190, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME014', 'Zombie Apocalypse', true, 19.99, 5, 'zombie_apocalypse.jpg', 'zombie-apocalypse', 'Action',
        'Survive the zombie apocalypse', true, null, 300, 120, 180, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME015', 'City Builder Pro', true, 39.99, 8, 'city_builder_pro.jpg', 'city-builder-pro', 'Simulation',
        'Design and build your city', true, null, 400, 150, 250, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME016', 'Street Fighter Legends', true, 59.99, 10, 'street_fighter_legends.jpg', 'street-fighter-legends',
        'Fighting', 'Classic arcade fighting', true, null, 120, 45, 75, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME017', 'Space Odyssey', true, 69.99, 18, 'space_odyssey.jpg', 'space-odyssey', 'Sci-Fi',
        'Explore the galaxy and beyond', true, null, 180, 80, 100, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME018', 'The Hidden Kingdom', true, 19.99, 15, 'hidden_kingdom.jpg', 'the-hidden-kingdom', 'Adventure',
        'Discover hidden treasures', true, null, 220, 70, 150, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME019', 'World War X', true, 49.99, 25, 'world_war_x.jpg', 'world-war-x', 'Strategy',
        'Command armies in World War X', true, null, 170, 60, 110, null, 5, 0, null, null, null, null, null, null);
INSERT INTO public.game (game_code, game_name, status, price, discount_percent, game_image, slug, game_category,
                         description, is_active, sys_id_discount, quantity, quantity_sold, quantity_count,
                         sys_id_voucher, rating, rating_count, features, developer, platform, language, about,
                         release_date)
VALUES ('GAME020', 'Alien Invasion', true, 34.99, 10, 'alien_invasion.jpg', 'alien-invasion', 'Action',
        'Defend Earth from alien invaders', true, null, 280, 90, 190, null, 5, 0, null, null, null, null, null, null);

insert into public.category_detail (sys_id_game, sys_id_category)
values (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6);

INSERT INTO public.media (media_name, media_url, sys_id_game) VALUES
('thumbnail', 'https://cdn1.epicgames.com/offer/87b7846d2eba4bc49eead0854323aba8/EGS_DyingLight2StayHumanReloadedEdition_Techland_S2_1200x1600-76cef594ff94fbac64a8af1ebe4c7590?resize=1&w=360&h=480&quality=medium', 1),
('logo', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhumanreloadededition-techland-ic1-400x400-ef8c8989500e.png?resize=1&w=480&h=270&quality=medium', 1),
('p1', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-03-1920x1080-5acfeff80697.jpg', 1),
('p2', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-04-1920x1080-01df52f79ade.jpg', 1),
('p3', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-05-1920x1080-1c5ac325c83a.jpg', 1),
('p4', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-06-1920x1080-40f55c566ef9.jpg', 1),
('p5', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-07-1920x1080-32df329a3c64.jpg', 1),
('p6', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-08-1920x1080-fc9df7be5ad5.jpg', 1);