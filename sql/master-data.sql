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
    INSERT INTO users(user_name, email, join_time)
    VALUES (p_username, p_email, now());

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

CREATE OR REPLACE PROCEDURE get_analytics_summary(
    OUT total_revenue FLOAT,
    OUT total_items_sold INT,
    OUT total_users INT
)
    LANGUAGE plpgsql
AS
$$
BEGIN
    -- Calculate total revenue
    SELECT COALESCE(SUM(price), 0) INTO total_revenue FROM Orders;

    -- Calculate total items sold
    SELECT COALESCE(SUM(quantity), 0) INTO total_items_sold FROM Orders;

    -- Calculate total users
    SELECT COUNT(*) INTO total_users FROM Users;
END;
$$;

CREATE OR REPLACE PROCEDURE get_revenue_vs_profit(
    OUT revenue TEXT,
    OUT profit TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Tính toán doanh thu hàng tháng
    WITH revenue_data AS (
        SELECT to_char(order_date, 'YYYY-MM') AS month, SUM(price) AS amount
        FROM Orders
        GROUP BY to_char(order_date, 'YYYY-MM')
        ORDER BY to_char(order_date, 'YYYY-MM')
    )
    SELECT json_agg(json_build_object('month', month, 'amount', amount))::TEXT INTO revenue
    FROM revenue_data;

    -- Tính toán lợi nhuận hàng tháng (giả sử lợi nhuận là doanh thu trừ đi một số chi phí, ở đây chúng ta giả định một tỷ lệ chi phí cố định cho đơn giản)
    WITH profit_data AS (
        SELECT to_char(order_date, 'YYYY-MM') AS month, SUM(price) * 0.5 AS amount
        FROM Orders
        GROUP BY to_char(order_date, 'YYYY-MM')
        ORDER BY to_char(order_date, 'YYYY-MM')
    )
    SELECT json_agg(json_build_object('month', month, 'amount', amount))::TEXT INTO profit
    FROM profit_data;
END;
$$;

CREATE OR REPLACE PROCEDURE get_monthly_user_growth(
    OUT user_growth TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Tính toán tăng trưởng người dùng hàng tháng
    WITH user_growth_data AS (
        SELECT to_char(join_time, 'YYYY-MM') AS month, COUNT(*) AS new_users
        FROM Users
        WHERE join_time IS NOT NULL
        GROUP BY to_char(join_time, 'YYYY-MM')
        ORDER BY to_char(join_time, 'YYYY-MM')
    )
    SELECT json_agg(json_build_object('month', month, 'new_users', new_users))::TEXT INTO user_growth
    FROM user_growth_data;
END;
$$;

CALL get_revenue_vs_profit(NULL, NULL);
CALL get_monthly_user_growth(NULL);


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
    add column if not exists price    int default 0;

alter table postgres.public.transaction_history
    add column user_balance double precision;

--- data inserting
---other table
truncate table orders cascade;
truncate table transaction_history cascade;
truncate table owned_game cascade;
---category
truncate table category cascade;
ALTER TABLE category
    ALTER COLUMN sys_id_category DROP IDENTITY;

INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (3, 'Indie', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (15, 'Rogue-Lite', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (11, 'Fantasy', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (8, 'Action-RPG', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (27, 'Music', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (26, 'Casual', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (2, 'Horror', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (14, 'Survival', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (12, 'Adventure', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (7, 'Platformer', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (6, 'Puzzle', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (5, 'Action-Adventure', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (21, 'Action', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (1, '3D', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (9, 'RPG ', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (34, 'First Person ', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (13, 'Simulation', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (23, 'Demonic', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (20, 'Turn-Based', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (4, 'Hardcore RPG', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (24, 'Comedy', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (25, 'Single Player', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (22, 'Open World ', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (33, 'Strategy', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (35, 'Shooter', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (30, 'Turn-Based Strategy', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (31, 'Tower Defense', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (18, 'Dungeon Crawler', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (10, 'City Builder', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (19, 'Exploration', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (28, 'Semi Open World', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (29, 'Racing', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (16, 'MOBA', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (17, 'Party', '');
INSERT INTO public.category (sys_id_category, category_name, description)
VALUES (32, 'Bloody Party', '');

ALTER TABLE category
    ALTER COLUMN sys_id_category ADD GENERATED ALWAYS AS IDENTITY;
---game
truncate table game cascade;
ALTER TABLE game
    ALTER COLUMN sys_id_game DROP IDENTITY;

INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (17, 'GAME-7D6F5', '3 Minutes to Midnight', true, 261000, 0, null, '3-minutes-to-midnight-3b506f',
        'In a race against time, Betty embarks on a thrilling, comedy-packed adventure to crack a conspiracy that starts with a bang and ends at 3 Minutes to Midnight.',
        true, null, '2024-07-15', ' ', ' ', ' ', e'3 Minutes to Midnight
Somewhere, in the middle of nowhere, an explosion shatters the night. Everyone wakes up with a jolt. Where are they? Who are they? It turns out that whatever blew up in the desert seems to have done more than just wipe their memories clean.

Embark on an adventure with Betty as she delves into a town where danger lurks at every turn, and characters are as deceiving and bizarre as they come. Featuring a power-hungry mayor, a potbelly pig with a vendetta, a sheriff obsessed with the supernatural, and believe it or not, a kraken who doesn’t read fine print.

Oh, and there’s also this itsy-bitsy thing about a doomsday plot to eradicate everyone’s very existence. But hey, let’s worry about that later, shall we?

CLASSIC ADVENTURE GAMEPLAY
Immerse yourself in a timeless point-and-click adventure game paired with striking HD cartoon art and playful humor.

TWO PERSPECTIVES, ONE MYSTERY
Join forces with Betty Anderson and Mayor Eliza Barret—two characters with contrasting traits—as they piece together a mystery that holds more than meets the eye.

NOT JUST NEIGHBORS
In a town where strangers and secrets intertwine, navigate a dense web of deceit to unravel a catastrophic scheme that binds them all.

PUZZLES UP THE WAZOO
Put on your thinking cap, because 3 Minutes to Midnight will keep your brain working on overclock! With a multitude of unique objects at your disposal, you’ll crack every puzzle that stands in your way.

DIFFERENT SOLUTIONS
A puzzle-packed adventure where every twist and turn shapes your journey. Think outside the box because there might be more than one way to connect the dots.

MULTIPLE ENDINGS
Experience the consequences of your actions. Every choice you make carves your path through intricate branching narratives, leading you to an ending as unique as your journey.

THE JOURNEY NEVER ENDS
Each replay is a gateway to uncovering thrilling mysteries, hidden Easter eggs and references, and even the possibility of encountering new characters.

HEAR THE ADVENTURE
With complete English voiceovers, each character vividly comes to life, enriching the 1940s setting and taking the story to new heights.

3 Minutes to Midnight pays homage to the golden era of gaming while forging its own path with innovative gameplay and storytelling. Whether you\'re a fan of brain-teasing puzzles, compelling narratives, or just a good laugh, this game is your ticket to an unforgettable experience.',
        4.1, 113, 1000, 14547);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (32, 'GAME-9F9CC', 'Honeycomb: The World Beyond', true, 24999, 5, null, 'honeycomb-45501f',
        'Honeycomb: The World Beyond is a survival-sandbox game set on an alien planet – Sota7. Explore its vast world and face challenges on your bioengineering journey. Crossbreed plants or animals, gather resources, craft items, and conduct experiments in order to develop new species.',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Welcome to operation Honeycomb.
EON Corp appreciates your contribution to Earth’s survival and would like to kindly remind you that all data you procure should remain strictly confidential.

EXPLORE A FARAWAY PLANET
Prepare for the unknown in the lush meadows and deep caverns of Sota7, the planet where we hope you can find the key to humanity’ survival. Harness your bioengineering knowledge and make use of the extensive training provided by EON to navigate a world brimming with wonders and mysteries.

MEET FASCINATING LIFE FORMS AND CREATE NEW ONES
The flora and fauna of Sota7 might feel less than familiar at first - expanding your knowledge and understanding is a crucial part of your mission. As a bioengineer you will crossbreed plants to create entirely new species with different properties, some of which could save thousands of lives. Watch out for the animals as well. Although some might be friendly, others are less than happy to share their habitat with a traveler from the stars.

EXPAND YOUR BASE OF OPERATIONS
For your safety, we advise that you employ the help of our hive drones to expand your base of operations and make it suitable both for inhabitation and research. Discover new resources to help you build precisely what you need and make your work easier. This mission is a marathon, not a sprint – and as such it is crucial you maintain yourself and your environment well.

SURVIVE AND LIVE TO TELL THE TALE
You are a pioneer, taking steps where no man has ever walked before. Nobody can predict what wonders or terrors you will encounter on this faraway planet, so your number one mission is to persevere. Create the tools you need to do so and remember – curiosity can be both a curse and a blessing.

KEY FEATURES
Explore the unknown - Sota7 is a world with vast diverse biomes, each with different plants and animals for you to discover.

Awaken your inner bioengineer – find and experiment with new fauna and flora species.

Find a way – crossbreed various plants and animals using bioengineering mechanics.

Set up the lab – to survive and conduct experiments, you’ll have to build your base first. Gather materials and start building.

Gather, plan, done – If you’re not a keen builder, utilize the planning mode, which will do the job for you!

Find the “gold mine” – remember that Sota7 is a unique yet challenging place. You should always be on the lookout for better resources.

Survive at any cost – face the upcoming challenges and try to survive in an unfamiliar world.', 4, 326, 1000, 3992);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (14, 'GAME-38D39', 'MechWarrior 5: Clans', true, 522000, 0, null, 'mechwarrior-5-clans-e50e3a', e'In MECHWARRIOR 5: CLANS, players are new Smoke Jaguar pilots in the Clan Invasion of the Inner Sphere. Lead your five-mech "Star" across diverse planets, engaging in an expansive campaign with immersive gameplay and combat. Customize BattleMechs and explore tactical options.
', true, null, '2024-07-15', ' ', ' ', ' ', e'MECHWARRIOR 5: CLANS
In MECHWARRIOR 5: CLANS, players take on the role of a newly graduated pilot from the Smoke Jaguar cadet program, thrust into the heart of the Clan Invasion of the Inner Sphere during Operation Revival. This pivotal moment in the MechWarrior universe sees players leading a five-mech "Star" squad across numerous planets with diverse biomes, engaging in an expansive campaign filled with immersive gameplay and intricate combat encounters. Armed with customizable BattleMechs featuring cutting-edge technologies, players explore a wealth of tactical options and strategic possibilities. The game leverages the power of the Unreal Engine to deliver stunning environmental details and visceral destruction, making every battle against the tyrants of the Inner Sphere an epic experience. As the first Clan-based MechWarrior game in nearly three decades, it combines a captivating narrative with well-crafted characters and moral dilemmas, culminating in a dynamic and adaptive combat experience enhanced by a revamped MechLab, Hardpoint, and OmniPod system.',
        3.4, 741, 1000, 10861);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (6, 'GAME-D7131', 'Megapolis', true, 52000, 0, null, 'megapolis-925016',
        'Casual city building. Become an architect. Manage the small polis. Construct beautiful city zones with plenty of modern blocks. Work your way to the top through the campaign mode, or create your own scenarios.',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Build your own city and make heaps of money in this casual simulation game.

Construct beautiful and functional city zones with plenty of modern blocks and keep your residents happy by providing all services they need, and they will reward you with a tidy profit. It’s up to you how to play - you can work your way to the top in the comprehensive campaign mode or you can create and play your own custom scenarios. Build dozens of houses, structures and other buildings. Trophies and awards available for the meticulous gamer!',
        4.5, 567, 1000, 41141);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (2, 'GAME-38CE1', 'Frostpunk 2', true, 525000, 0, null, 'frostpunk-2',
        'Develop, expand, and advance your city in a society survival game set 30 years after an apocalyptic blizzard ravaged Earth. In Frostpunk 2, you face not only the perils of never-ending winter, but also the powerful factions that watch your every step inside the Council Hall.',
        true, null, '2024-07-15', ' ', ' ', ' ',
        'Frostpunk 2 elevates the city-survival genre to a new level. Take the role of a Steward and lead your city through a cascade of calamities taking place in a postapocalyptic, snowy setting. Build large city districts with their string of endless needs and demands. Navigate through conflicting interests of factions that populate your metropolis. As the needs of the city grow and factional power at its core rises, only you can steer the society towards an uncertain future.',
        3.1, 862, 1000, 5887);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (15, 'GAME-B6679', 'Legion TD 2', true, 209000, 0, null, 'legion-td-2-a244b9-2',
        'An infinitely replayable multiplayer and single-player tower defense. Defend against waves of enemies and destroy the enemy''s king before they destroy yours. Legion TD 2 is a one-of-a-kind game of tactics, teamwork, and prediction. Party as 1-8 players.',
        true, null, '2002-10-22', ' ', ' ', ' ',
        'ACTIVE DEVELOPMENT & THRIVING COMMUNITY! As of 2024, Legion TD 2 has continued to receive major game updates every month since Early Access launched in 2017, and has a steady playerbase of tens of thousands of players. With full cross-play between Epic & Steam, fair matches are formed typically within a minute or two. If multiplayer isn''t your cup of tea, the included solo/co-op campaign & Play vs. AI modes provide plenty of hours of fun.',
        4.4, 461, 1000, 33273);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (26, 'GAME-C91F3', 'Dark and Darker', true, 24999, 0, null, 'dark-and-darker-qa-c1e629',
        'An unforgiving hardcore fantasy FPS dungeon PvPvE adventure. Band together with your friends and use your courage, wits, and cunning to uncover mythical treasures, defeat gruesome monsters, while staying one step ahead of the other devious treasure-hunters.',
        true, null, '2024-06-08', ' ', ' ', ' ', e'Free-To-Play Version!
The free version of Dark and Darker allows players to create one character and play Normal mode on all maps, complete quests, and build a stash of loot! Players who enjoy their time with the free version and want to experience everything the game has to offer can upgrade to the full version for 15 Redstone shards (equivalent to $30.00). The full version of the game includes access to the High-Roller mode, where players can equip their hard-earned gear to take on even more dangerous dungeons and increase the stakes. Players will also gain full access to the marketplace where they can trade with other adventurers, 9 total character slots, and the Shared Stash!

We hope that this version will give players a chance to experience some of what Dark and Darker has to offer and to give players who have not played since the playtests a chance to see what\'s new before they dive into the full edition.',
        3.8, 571, 1000, 47806);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (21, 'GAME-58100', e'KMON: World of Kogaea
', true, 24999, 0, null, 'kmon-world-of-kogaea-f81f63',
        'Discover the massive world of Kogaea in this cross-platform MMORPG. Craft, Battle and find a way to survive this Universe with your Kryptomon team.',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Discover the World of Kogaea in KMON: An Epic Web3 MMORPG Adventure
Step into the mystical realm of Kogaea, a parallel world where ancient elements and mythical creatures thrive. "KMON: World of Kogaea" is a groundbreaking web3-enabled MMORPG that combines the expansive gameplay of classics like World of Warcraft with the collectible charm of Pokémon. Navigate through a continent divided into eight unique regions, each representing one of the elemental forces that influence the creatures known as \'Kryptomons.\'

Game Features
Dynamic Exploration & Quests: Traverse diverse landscapes from lush forests to volcanic plains. Uncover hidden secrets and embark on epic quests.

Robust Combat System: Engage in strategic PvE battles or challenge other players in intense PvP matchups.

Community and Guilds: Join forces with players worldwide, form guilds, and partake in community-driven events.

Crafting & Farming: Harness resources to craft powerful items and gear. Cultivate your land to produce rare materials.

Dungeons: Team up to conquer challenging dungeons for legendary rewards.

Unique Web3 Integration:
Every Kryptomon and many in-game items are unique NFTs, owned entirely by you. Secure your own piece of Kogaea with customizable land plots where you can build homes, forges, and shops. Utilize the in-game token $KMON for trading, enhancing your gameplay experience with true digital ownership and economy.',
        4.3, 682, 1000, 18104);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (8, 'GAME-DA6A6', 'Romopolis', true, 52000, 80, null, 'romopolis-49e1bf',
        'Casual city building. Become an architect. Manage the small polis. Construct an idyllic ancient Roman city with plenty of houses. Work your way to the top through the campaign mode, or create your own scenarios.',
        true, null, '2009-03-29', ' ', ' ', ' ', e'Build ancient Roman cities. Earn money, fame and honor in this casual simulation game.

Construct an idyllic ancient Roman city with plenty of houses and keep your residents happy by providing all the services they need. You\'ll be rewarded with money, fame and honor. It\'s up to you how to play - you can work your way to the top in the comprehensive campaign mode or you can create and play your own custom scenarios. Build dozens of Roman-styled houses, structures and other buildings. Trophies and awards available for the meticulous gamer!',
        4.5, 265, 1000, 32282);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (9, 'GAME-16D9C', 'Barbie Project Friendship™', true, 470000, 0, null, 'barbie-project-friendship-9434fc',
        'Join Barbie™ on a fun summer adventure to save the Malibu Waves Community Center! Help friends, unlock minigames and earn points for upgrades while you restore a beloved landmark.',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Get ready for the ultimate Barbie™ gaming adventure as Barbie and Barbie work together to save a beloved Malibu landmark, the Malibu Waves Community Center! The once thriving destination is nearly abandoned and on the verge of being closed. It’s time to band together to restore the beloved community center to its former glory.

Play as Barbie and Barbie as you team up with their BFFs and family to save the day! Put your own stamp on the Malibu landmark by combining talents and creativity to transform six spaces including the Animal Care Center, Campsite, Crafting Studio, and more!',
        3.7, 460, 1000, 40368);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (28, 'GAME-FEEBB', 'Surfing Legends', true, 146000, 15, null, 'surfing-legends-880017',
        'Surf the waves, rescue the wildlife, and collect the magic diamonds in this thrilling single player adventure! Buy faster boards and beat the clock to open new levels!',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Surf the waves, rescue the wildlife, and collect the magic diamonds in this thrilling single player adventure!
Embark on a thrilling adventure and surf across the globe, from tropical islands to the arctic, from desert oases to canyon rivers, and beyond. Rescue endangered wildlife and collect dazzling magic diamonds to boost your speed. Clean up the water by removing oil cans, plastic, and other hazards. Solve a fun ball challenge in every level. Leap over majestic waterfalls, gather shiny coins, buy awesome boards and beat the clock. Surfing Legends offers you a fun and unique surfing experience.

Features:

● 3 characters with 3 outfits each
● 9 levels
● Each level has multiple challenges
● 19 boards
● Boost upgrades
● No dying, just pure fun and enjoyable surfing
● And more!', 4.1, 339, 1000, 19705);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (20, 'GAME-93A0C', 'BATTLE BEARS HEROES', true, 24999, 0, null, 'battle-bears-heroes-690879',
        'Relive your childhood in this fast thrilling PvP brawler! Enjoy new modes like Mini MOBA, Battle Royale, and Deathmatch, Survive the classic BB-1 PvE campaign. Upgrade & customize all the bears from the award-wnning Battle Bears series. Winner of PocketGamer''s Big Indie Pitch!',
        true, null, '2024-07-15', ' ', ' ', ' ', e'BATTLE BEARS HEROES
Battle Bears is Back! Celebrating 15 Years of Battle Bears! Winner of PocketGamer\'s 2024 Big Indie Pitch! Battle it out in all new 3v3 multiplayer modes like Mini MOBA, Brick Battle, Deathmatch and Battle Royale. Play campaign mode and earn skins by surviving the Huggable invasion in a re-imagined version of Battle Bears -1 starring Oliver, Riggs and Wil. Warning: Do NOT get hugged to death! Play and level up all your favorite bears from Battle Bears Gold with wacky weapons! Collect unique skins to show you’re the finest fighter on the Ursa Major!

NEW ORIGINAL MODES!
- Brick Battle: Inspired by Battle Bears Zombies, collect and carry the most Rainbow Bricks back to your Unicorn Cart to win. Don’t let the opposing team blow up your Cart or else you gotta start over. Watch out for the Bearbershop Quartet and their deadly Angry Bombs!

- Mini MOBA: A fun fast LoL inspired 3v3 MOBA anyone can play! Protect your Main Base while defending your Huggable minions as they take down your opponent’s shields. Destroy the enemy Tesla Turrets and Main Base to secure the win. Navigate a variety of three-lane maps with Lavable Golems that when destroyed can provide damage buffs or health boosts to your team.

- Deathmatch: Based on Battle Bears Gold’s team death match! Each kill awards a point. The team with the most points at the end wins it all.

- Battle Royale: Every bear for themselves! 10 players battle it out to be the Last Bear Standing. Destroy boxes to get Supplies and increase your strength. Avoid the Huggable storm cloud and use teleportation pads. May the odds be ever in your favor ;)

UNLOCK & UPGRADE HEROES
Collect and upgrade the famous Battle Bears!
Level them up and collect unique skins.
Oliver the Soldier
Wil the Chub Scout
Riggs the Heavy
Astoria the Sniper
Graham the Engineer
Tillman the Demo
Huggy the Huggable
Saberi the Healer
Sanchez the Arbiter
Botch the Toxic Assassin
B1000 the Assault
Necromancer the Zombocalypse
More heroes in development!

WIN MORE IN QUESTS!
Complete a wide variety of fun quests to earn rewards.

INVITE YOUR FRIENDS!
Invite and join your friends to play matches together.

SHOP!
Keep checking back for new skins and bundles in the Shop. Buy Bear Boxes, Joule Packs and Gas Can Packs in the Shop as well.

BATTLE PASS!
Buy the Golden Bear Pass to unlock Gold Bear Pass Rewards and Premium Quests.

BE THE TOP BEAR!
Climb the leaderboards to prove you’re the GOAT! Check your News and Inbox for special events and prizes for top leaderboard players.

NEW BB MUSIC OST!
Experience new Battle Bears music and remixes of classic BB tracks on different maps.

NEW UPDATES COMING!
We\'re working on new heroes, skins, maps, and modes for upcoming seasons.

SUPPORT:
Submit a ticket at BattleBears.com/support

JOIN THE BB COMMUNITY!
Discord.gg/BattleBears
X.com/BattleBears
YouTube.com/BattleBears
Facebook.com/BattleBears
Instagram.com/BattleBears
Telegram t.me/BattleBears
TikTok.com/BattleBearsGame

MERCH:
Collectible BB merch, plushies and board game at BattleBears.com

BATTLE BEARS is based on characters created by @BenVu', 3, 45, 1000, 16010);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (24, 'GAME-8EBB5', 'Arhaekon', true, 157000, 0, null, 'arhaekon-f69cf5',
        'Embark on a perilous journey as an Arhaekon in this challenging roguelike turn-based RPG dungeon crawler. Lead your minions through treacherous dungeons, uncover ancient treasures, and face relentless abominations in a quest for humanity''s deliverance from corruption.',
        true, null, '2024-08-08', ' ', ' ', ' ', e'Embark on a perilous journey as an Arhaekon in this challenging roguelike turn-based RPG dungeon crawler, where every choice is crucial
Arhaekon is a challenging roguelike turn-based RPG dungeon crawler, set in a grimdark world teetering on the brink of destruction. As humanity grapples with the corruption they unwittingly unleashed, players must navigate treacherous dungeons, confront empowered enemies, and make strategic decisions to prevent humanity\'s extinction. Immerse yourself in a relentless struggle for survival, where a single misstep can lead to utter ruin. Are you ready to face your predestined arch-nemesis and redeem humanity?

Turn-Based Combat: Engage in battle-centered, turn-based gameplay with very limited mobility
• Roguelike Elements: Enjoy units permadeath, procedural dungeons & loot
• Diverse Units: Command 12 units across 4 classes, each with unique abilities and progressions
• 14 Themed Regions: Explore diverse dungeons filled with themed enemies & lore
• Punishing Decisions: Every choice matters in both management and combat - there\'s no going back
• Varied Loot: Discover crafting recipes, lore pages, and ritual rites of five rarity tiers
• Augment Equipment: Equipment system to that prevents hoarding, with randomly generated, soul-bound augments
• Dark Atmosphere: Immerse yourself in a bleak, grim world
• Endless Music: Experience experimental, quasi-procedurally generated music
• Corruption System: Manage your units\' health and sanity as they tune into the enemies\' song
• Unit & Party Management: Recruit, enhance, level-up, mend wounds and strengthen resolve of your units
• Camp Management: Upgrade and manage your refugee camp and facilities
• Crafting System: Use the compendium of shaping to upgrade or obtain new equipment
• Progress Management: Non-linear dungeon progression and allocable unit stat points on level-up
• Collectible Lore: Enjoy bonuses from lore, or at least the experience - if lore isn\'t your thing.
• Indie Development: Created by a very small, passionate team.

', 3.7, 119, 1000, 34312);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (3, 'GAME-0E0DC', 'Trinity Building Editor', true, 261000, 0, null, 'trinity-building-editor-94501c',
        'Home building simulator game with a goal of ultimate freedom for your creativity. Play with landscape, build the foundation, change the size of all building parts, combine pieces for a unique design, stack, rotate and scale. Complete tasks and increase your architect level!',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Home building simulator game with a goal of ultimate freedom for your creativity.
Trinity Building Editor is a standalone simulator game that we are making in preparation for a bigger release in the future. Our main big project is Trinity Mysterious System, but we have decided that allowing players to access a complex and unique Building Editor on its own will be a lot of fun, so we have made it our first step towards the goal.

Trinity Building Editor will give you an unlimited freedom of creation. Build homes, play with interior design and landscaping, try special tools and mechanics. Our block focused system allows for an ultimate variety of shapes and sizes of almost every detail in the house.',
        4, 772, 1000, 1507);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (33, 'GAME-4D7F4', 'The Magical Mixture Mill', true, 260000, 5, null, 'magical-mixture-mill-f66c25',
        'Brew magical mixtures for the local questing heroes - and keep them coming back for more! Team up with a mushroom-loving witch to restore her run-down shop. Build automated production lines, experiment with exotic ingredients, explore a cozy world, and hope nothing blows up.',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Brew big or go broke!
For centuries, Griselda’s Magical Mixtures has been the go-to shop for travelers and heroes in search of performance-enhancing concoctions; be it to obtain mighty strength, incredible intelligence, enviable beauty or... less heroic attributes.

Giselda, an elderly mushroom-loving witch, has always run the business with great dedication but time has taken its toll, and it\'s time to train someone who can replace her. In short, her knowledge must be handed down and YOU - a former RPG adventurer whose career was cut short mixture intolerance - are the perfect candidate!

Together with your goblin assistant, build automated production lines of alchemical workstations to provide potent mixtures to the local heroes, assisting them in their epic quests, and making you their favorite potion supplier. Explore diverse biomes and use your remaining adventuring abilities to gather and research exotic ingredients, leading to new mixing opportunities. Get ready to face all the “wax on, wax off” challenges Griselda throws at you to bring the shop back to its former glory!

Magical Features:

• Accessible, automated potion brewing
Create fully automated production pipelines from a varied catalogue of workstations to produce potions. Balance liquid ratio, maximize potency, pick the right ingredients, balance flavors and pour it all on the fanciest bottle you can craft to create the potion!

• A Vibrant, Cozy, and Colorful world!
A mix of colorful illustration designs with a pinch of cheeky classic fantasy humor but still an alchemically-cozy experience. Different environments to explore and gather your ingredients. But be careful! Some flora and fauna will fight back…

• Happy customers always come back! Especially if they are “hooked”
Not all heroes want the same potions, so take your time to get to know your customers. As they level up, their demand for stronger and more advanced mixtures grow, turning them into very loyal customers - and turning your pockets heavy with coins!',
        3.5, 314, 1000, 47561);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (27, 'GAME-715C9', 'Tomb nightmares', true, 42000, 70, null, 'tomb-nightmares-8aa2a4',
        'One excerpt from the adventure story of two treasure seekers. Explore the mysterious dungeon, pass all the tests and get out alive! The catacombs full of mysteries are waiting for you!',
        true, null, '2024-06-05', ' ', ' ', ' ', e'A cozy and atmospheric game for the evening.
The plot tells the player about one of the many expeditions of two speleologists Bane and Jess. This time they decided to go to the abandoned catacombs, where it is rumored to store many lost artifacts, including the crown of the leader of an ancient tribe. The caves were home to the ancient people, few tribe members were willing to venture out of them into the open world full of danger. Legend has it that the tribe was buried alive in these caves during an earthquake. Attempts by enthusiasts to find artifacts from that time have been unsuccessful. Can Bane and Jess do it?

Play Time
30 - 60 minutes

Game Features:

Two endings.

Tense atmosphere;

Treasure hunt.

3D surround sound;

Choice of path through the game.

Elements of platforming.', 3.8, 380, 1000, 28097);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (13, 'GAME-9B57D', 'Brilliantcrypto', true, 24999, 0, null, 'brilliant-crypto-5b06e1',
        'Explore deep into the mines of Brilliantcrypto, mine brilliantstones, and receive crypto for each one you find! The more you find, the more crypto you can earn. You''ll even get crypto based on your mining amount when others find them!',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Discover the world of Brilliantcrypto
Brilliantcrypto aims to be a sustainable play-to-earn game, where players create real value through the generation of authentic gemstones.

Long before the advent of currency, the brilliance of gemstones has captivated the eye of humanity, and given them use as a medium for the exchange of value.

There is no doubt that at the dawn of the metaverse era, people will desire to use gemstones as they do in the real world.

The value of these gemstones in the digital world will be guaranteed by the gameplay of the millionsーthis is "Proof of Gaming."

As more players from around the world participate, the gemstones become increasingly precious and radiate with brilliance.

So grab your pickaxe, and head to the mineーit\'s time to create new value together.', 3.8, 24, 1000, 24388);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (1, 'GAME-7E016', 'Dying Light 2 Stay Human - Reloaded Edition', true, 990000, 60, null,
        'dying-light-2-stay-human',
        'Now with guns! With the Reloaded Edition, the arsenal at your disposal has grown massively. Be creative with your combat or outrun your foes as you enjoy the perks of more than 10 post-launch updates. Good luck out there, and stay human!',
        true, null, '2022-02-04', ' ', ' ', ' ', e'RELOADED EDITION
Dying Light 2 Stay Human: Reloaded Edition includes:

Dying Light 2 Stay Human

‘Bloody Ties’ DLC

Now with guns! With the Reloaded Edition, the arsenal at your disposal has grown massively. Be creative with your combat or outrun your foes as you enjoy the perks of more than 10 post-launch updates. Good luck out there, and stay human!

It’s been 20 years since the events of the original game. The virus won, and humanity is slowly dying. You play as Aiden Caldwell, a wandering Pilgrim who delivers goods, brings news, and connects the few remaining survivor settlements in barren lands devastated by the zombie virus. However, your true goal is to find your little sister Mia, who you left behind as a kid to escape Dr. Waltz\'s torturous experiments. Haunted by the past, you eventually make the decision to confront it when you learn that Mia may still be alive in Villedor — the last city standing on Earth.

You quickly find yourself in a settlement torn by conflict. You’ll need to engage in creative and gory combat, so hone your skills to defeat hordes of zombies and make allies. Roam the city, free run across Villedor’s buildings and rooftops in search of loot in remote areas, and be wary of the night. With every sunset, monsters take control of the streets.',
        3.6, 949, 1000, 15105);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (23, 'GAME-1BCD1', 'Etherscape', true, 24999, 0, null, 'the-etherscape-b2e3d2',
        'Etherscape is a multiplayer online rogue-lite action RPG with an on-chain backend that allows users to own and trade their items as NFTs.',
        true, null, '2024-08-16', ' ', ' ', ' ', e'Welcome Etherborn
Welcome to Etherscape, a realm where the boundaries between life and death blur, and the eternal struggle for survival takes on a new meaning. As an Etherborn, you possess an eternal soul that defies the finality of death, resurrecting you to face the challenges anew. In this multiplayer fantasy action RPG, every death is a lesson, and every resurrection is a chance to rise stronger.

Prepare to embark on an epic adventure where your actions echo through eternity. Welcome to Etherscape – where death is but a doorway, and your legacy is immortal.',
        3, 200, 1000, 18503);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (35, 'GAME-BLWK7', 'Black Myth: Wukong', true, 999999, 22, null, 'Black_Myth_Wukong',
        'Black Myth: Wukong is an action RPG rooted in Chinese mythology. You shall set out as the Destined One to venture into the challenges and marvels ahead, to uncover the obscured truth beneath the veil of a glorious legend from the past.',
        true, e'Single-player
                                                                                                                                                                                                                                               Steam Achievements
                                                                                                                                                                                                                                               Steam Cloud
                                                                                                                                                                                                                                               Family Sharing',
        '2024-08-20', 'Game Science', ' ', ' ', e'Black Myth: Wukong is an action RPG rooted in Chinese mythology. The story is based on Journey to the West, one of the Four Great Classical Novels of Chinese literature. You shall set out as the Destined One to venture into the challenges and marvels ahead, to uncover the obscured truth beneath the veil of a glorious legend from the past.
                                                                                                                                                                                                                                               "A world unseen, where wonders gleam,
                                                                                                                                                                                                                                               And with each stride, a new scene streams."

                                                                                                                                                                                                                                               Enter a fascinating realm filled with the wonders and discoveries of ancient Chinese mythology!
                                                                                                                                                                                                                                                  As the Destined One, you shall traverse an array of breathtaking and distinctive landscapes from the classic tale, composing an epic of adventure that is seen anew.


Mature Content Description
The developers describe the content like this:

This Game may contain content not appropriate for all ages, or may not be appropriate for viewing at work: Frequent Violence or Gore, General Mature Content


System Requirements
Minimum:
Requires a 64-bit processor and operating system
OS: Windows 10 64-bit
Processor: Intel Core i5-8400 / AMD Ryzen 5 1600
Memory: 16 GB RAM
Graphics: NVIDIA GeForce GTX 1060 6GB / AMD Radeon RX 580 8GB
DirectX: Version 11
Storage: 130 GB available space
Sound Card: Windows Compatible Audio Device
Additional Notes: HDD Supported, SSD Recommended. The above specifications were tested with DLSS/FSR/XeSS enabled.
Recommended:
Requires a 64-bit processor and operating system
OS: Windows 10 64-bit
Processor: Intel Core i7-9700 / AMD Ryzen 5 5500
Memory: 16 GB RAM
Graphics: NVIDIA GeForce RTX 2060 / AMD Radeon RX 5700 XT / INTEL Arc A750
DirectX: Version 12
Storage: 130 GB available space
Sound Card: Windows Compatible Audio Device
Additional Notes: SSD Required. The above specifications were tested with DLSS/FSR/XeSS enabled.
Copyright © Game Science Interactive Technology Co., Ltd. All Rights Reserved 游科互动科技有限公司', 4.9, 4999, 1000,
        99998);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (7, 'GAME-E3642', 'Illuvium Zero', true, 24999, 0, null, 'illuvium-zero-ca46a6',
        'Engage in the ultimate city-building and economic strategy experience with Illuvium: Zero. Strategically build your city, manage resources, to rule resources in Illuvium, every decision impacts your City’s success.',
        true, null, '2024-07-12', ' ', ' ', ' ', e'This is an Early Access Game

Early Access games are still under development and may change significantly over time. As a result, you may experience unforeseen issues or completely new gameplay elements while playing this game.
You can play now to experience the game while it\'s being built or wait until it offers a more complete experience.',
        2.7, 54, 1000, 40657);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (5, 'GAME-B8911', 'Horticular', true, 209000, 0, null, 'horticular-7ddf4c',
        'You have been magically summoned by mysterious gnomes to restore a long-lost garden. Attract adorable animals, build a lush environment, and immerse yourself in this relaxing garden-builder. Will you manage to reclaim the wasteland or succumb to a looming corruption?',
        true, null, '2024-07-17', ' ', ' ', ' ', e'Horticular is a relaxing garden-builder that begins with mysterious gnomes summoning you. Their wish? For you to breathe new life into a long-lost garden, abandoned by its previous caretaker.

Enter a magical world where you build up and expand a lush garden at your own pace. In your journey, you attract adorable animals to inhabit every corner; uncover helpful upgrades; and assist quirky characters for rewards and story development—all while juggling decay and fending off corruption sent by your nemesis!',
        2.9, 463, 1000, 24301);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (22, 'GAME-1E0ED', 'Juna - The Dreamwalker', true, 209000, 0, null, 'juna-the-dreamwalker-d4fbd7',
        'Immerse yourself in a pixelated retro-style action-adventure with real-time combats. Utilize the transformative power of a mystical rod to take on the appearance and abilities of monsters and objects. Unravel the dark origins of the monstrous invasion from another world.',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Juna - The Dreamwalker
Embark on an extraordinary journey in "Juna - The Dreamwalker", a captivating retro-style adventure game set in a mystical land plagued by menacing monsters. Step into the shoes of a courageous young woman determined to uncover the truth behind the invasion and put an end to the sinister forces threatening her world.

In this enchanting tale, you will navigate through a beautifully pixelated foreign land, embracing a low-resolution bird\'s-eye view perspective. Prepare to be immersed in a mesmerizing world as you explore its diverse regions, each teeming with treacherous creatures and hidden secrets.

But fear not, for you possess a remarkable tool – a mysterious rod capable of transforming you into any monster or object. This unique ability proves to be your greatest ally, allowing you to adapt to your surroundings and overcome numerous challenges. Harness the rod\'s power to unravel mind-bending puzzles, traverse dangerous landscapes, and deceive formidable foes.

The fate of the land rests upon your shoulders as you delve into forgotten temples, navigate treacherous dungeons, and interact with intriguing characters. Uncover the dark origins of the monstrous invasion, by reconstructing an ancient portal and eventually stepping through it.

With approximately 30 hours of captivating gameplay, "Juna - The Dreamwalker" delivers an unforgettable adventure brimming with nostalgia and discovery. Traverse lush forests, desolate deserts, and haunted ruins as you piece together the puzzle of the invasion. Engage in thrilling real-time combat, honing your skills to overcome hordes of fearsome creatures standing in your way.

Will you have the courage to face the unknown, unlock the truth, and save the land from impending doom? Prepare yourself for an immersive journey where your wits, transformation abilities, and determination will be tested. Unleash the power of the rod and shape your destiny in "Juna - The Dreamwalker", a retro-style epic that will leave a lasting mark on your gaming experience.

Key Features:

Immerse yourself in a captivating retro-style adventure.

Utilize the transformative power of the rod to take on the appearance and abilities of monsters and objects.

Explore a beautifully pixelated world, rich in diverse environments and secrets waiting to be discovered.

Unravel the dark origins of the monstrous invasion and discover the secrets behind the ancient portal.

Engage in real-time combat against formidable creatures, honing your skills to become a true heroine.

Experience approximately 30 hours of immersive gameplay, filled with exploration, puzzle-solving, and thrilling encounters.

Embark on an unforgettable journey in "Juna - The Dreamwalker" and become the heroine the land needs. Your destiny awaits!',
        4.3, 138, 1000, 18897);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (12, 'GAME-ED2CE', 'Legion TD 2', true, 209000, 0, null, 'legion-td-2-a244b9',
        'An infinitely replayable multiplayer and single-player tower defense. Defend against waves of enemies and destroy the enemy''s king before they destroy yours. Legion TD 2 is a one-of-a-kind game of tactics, teamwork, and prediction. Party as 1-8 players.',
        true, null, '2024-07-15', ' ', ' ', ' ',
        'ACTIVE DEVELOPMENT & THRIVING COMMUNITY! As of 2024, Legion TD 2 has continued to receive major game updates every month since Early Access launched in 2017, and has a steady playerbase of tens of thousands of players. With full cross-play between Epic & Steam, fair matches are formed typically within a minute or two. If multiplayer isn''t your cup of tea, the included solo/co-op campaign & Play vs. AI modes provide plenty of hours of fun.',
        3.8, 655, 1000, 23374);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (19, 'GAME-805D6', 'WW2 Frontline 1942', true, 24999, 0, null, 'ww2-frontline-1942-96fe8f',
        'FPS online multiplayer action World war II military simulator and army battle.', true, null, '2024-07-15', ' ',
        ' ', ' ', e'FPS online multiplayer action World war II military simulator and army battle
World War II Shooter - an exciting blend of action and military games. Choose your side and join online shooting games with players from different countries. Equip a variety of weapons and immerse yourself in realistic battles with stunning graphics. Experience the atmosphere of war as you engage in dynamic shooting action that will give you unforgettable impressions and adrenaline.

Key Features of the WW2 Shooter:

Explore thrilling battlegrounds during World War II where the action never stops. The maps are filled with legendary locations, including tanks, ships, and constant combat. Each map is unique, offering diverse tactical opportunities.

Step into the shoes of heroes from different countries - the United States, Russia, Japan, and Germany. These war games offer the choice of characters with different appearances, allowing you to relive epic battles.

Unleash a vast arsenal of 23 different weapons, including legendary Mosin and Mauser sniper rifles, powerful Thompson submachine guns, and a wide selection of pistols and machine guns. Feel the power and variety of weapons as you dive into the dynamic action shooter.

Upgrade your favorite weapons using the upgrade tree to add new characteristics and increase damage. Customize your weapons to suit your play style and become an even more effective fighter.

Each soldier possesses unique passive skills that come into play during combat engagements. This means that you don\'t have to activate them manually; they automatically influence the gameplay, making your soldier more effective in battle.

Join forces with like-minded players in war games and engage in team battles to crush enemies in online shooters dedicated to World War II. Collaborative efforts, coordination, and strategic thinking will help you achieve victory and become an irresistible force on the battlefield.

Frontline 1942 is an exhilarating online action shooter that allows players to immerse themselves in the intense atmosphere of warfare. With a vast arsenal of weapons, unique maps, and diverse characters, this online military game offers an unforgettable experience and the opportunity to compete with players from around the world. With team battles and weapon upgrades, players can feel the true intensity and strategic depth of military action. Be part of history and relive epic moments of World War II in this gripping shooter.',
        3.8, 142, 1000, 17994);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (30, 'GAME-066EC', 'Labyrinth Destroyer', true, 24999, 5, null, 'labyrinth-destroyer-1-0994f2',
        'A mind game who can break your brain. And your patience ! You have to resolve 10 labyrinth. But you have some quest to do inside too !!! Keep in mind that the first one is easy. But it got harder the farther you go !',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Labyrinth Destroyer
Follow Kirt in the strange world he got stuck inside, and follow the way your patience tell you to follow.

Play this game with all your mind and keep calm. You will need to.

Destroy the labyrinth and listen what some strange little girl tell you.

Will you be able to go back to Earth ? Will it be alright ?

Give it a try, after all you don\'t know until you do it !

You can play this game with almost anything.', 3.2, 635, 1000, 10151);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (25, 'GAME-6374C', 'Dragon Chronicles: Black Tears', true, 157000, 0, null,
        'dragon-chronicles-black-tears-46329e',
        'Dragon Chronicles: Black Tears is a strategic RPG where you use combinations of various heroes to clear dungeons. Train and utilize heroes to fight off those corrupted by the Black Tears and overcome difficult trials.',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Dragon Chronicles: Black Tears is a strategic RPG where you use combinations of various heroes to clear dungeons.
The Black Tears of those filled with anger and sorrow sweep across the world at an unstoppable pace.
The only hope for this cursed world is this expedition I lead.

Will we survive to see the end of this story?

■ Character Advancement
This is a strategic RPG with unique skills, attributes, different hero combinations, and more.
Build an expedition that suits your playstyle
and make use of various items to seize victory.
20 Heroes, 72 Artifacts, and 180 Imprint Stones await you on your adventure.

■ Town Reconstruction
Welcome! It may look a little rundown and dark, but this place is your home.
Repair the facilities to unlock abilities that will help you on your expedition.
Make clever use of the scarce resources you have, and you may just survive to see the next day.
Reconstructing the Town and advancing your Heroes will aid in your fight against the Black Tears.

■ Adventures and Decisions
A shady merchant, an immortal being clad in burning armor, a floating dragon...
They await you in places you have yet to tread.
Pay attention to the random events and quests that pop up during your journey.
Your choices may lead to your success. Or downfall.
Choose wisely. Who knows what they may grant you...

■ Immersive Visuals
The high-quality graphics of the game will push you into lava pits and fields of ice.
Powerful attacks may be intimidating, but their stunning visuals make them even more terrifying.
Do you have the courage to face these dangers?

■ Unending Challenge
Your enemies will come at you with a slew of skills and abilities.
Their roars may put an unexpected end to your adventure.
But each boss you face will be an experience that will make you stronger.
Don\'t be afraid of failure. Put your life on the line to excavate Artifacts, regroup, and then embark once more.
The challenge never ends!

Nothing ever goes as planned.
You must answer the call of the desperate and face otherworldly beings.
Steel yourself as you lead your expedition, battle after battle, to the summit of the Ordain Mountains.
Reach the peak and silence their cries!', 4.4, 485, 1000, 33751);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (18, 'GAME-E91AC', 'EA SPORTS™ WRC 24', true, 899000, 0, null, 'ea-sports-wrc-24',
        'Experience the thrill of the 2024 rally season with new locations, moments, stages, high-performance vehicles, new liveries and more. Brace yourself to embark on your continued rally journey!',
        true, null, '2024-07-15', ' ', ' ', ' ', e'Buy EA SPORTS™ WRC 24* to get:
EA SPORTS™ WRC

Locations & Car Content Pack

Le Maestros Content Pack

Hard Chargers Content Pack', 4, 192, 1000, 40350);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (16, 'GAME-9D28D', 'Blindfire', true, 94000, 0, null, 'eos_test-5cd09d',
        'Blindfire is an online multiplayer first-person shooter that takes place in the dark. Brave the arena alone or team up in matches of up to 8 players.',
        true, null, '2024-07-15', ' ', ' ', ' ', e'WEAPONS LIVE, LIGHTS OUT
Blindfire is an online multiplayer first-person shooter. Set in a shadowy underworld, the ultra-wealthy bet on illicit bloodsports beneath a neon skyline. For contestants lured by the promise of untold riches and unimaginable glory, it’s kill or be killed.

EVERY KILL COUNTS
Brave the arena alone or team up in matches of up to 8 players. In Bodycount mode, score the most points over five rapid-fire rounds. Be the last person standing when the lights come on and earn your place in the winner’s showcase.

DANGER LURKS IN THE DARK
Light is a rarity in close-quarter arenas riddled with traps for intense firefights. Players must decide where and when to fire at the risk of exposing their position. Hone your senses with the room-scanning Echo mechanic to help you navigate the arena and locate your opponents.

REVENGE UNDER THE BLACKLIGHTS
When defeated, spectators gain access to the arena\'s cutting-edge night vision cameras. Blacklight reveals the vibrant hues of hidden street art and transforms the slick designs of contestants’ outfits. The eliminated spectators can assume control of traps in the arena to influence the outcome of the match and exact revenge.

LIGHT THEM UP
The pitch-black battlefields of Blindfire turn the first-person shooter genre on its head. Stalk the shadows as the tension builds, then run for cover as the shots start flying in a volley of muzzle flashes. Think you’ve got what it takes to eliminate the competition in Blindfire? Then grab your weapon and LIGHT THEM UP.',
        2.5, 81, 1000, 5184);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (31, 'GAME-6A471', 'Interlude', true, 24999, 5, null, 'interlude-c2e8b4',
        'Explore a virtual dungeon where each room is an entire game world, each with its own characters, gameplay, and themes. Dive deeper and deeper, unlock more and more worlds, and be the first to reach the Core!',
        true, null, '2024-07-15', ' ', ' ', ' ', e'A virtual dungeon where each room is an entire game world, each with its own characters, gameplay, and themes.
Interlude can be seen as a dungeon crawler with a time-based exploration loop, with one key particularity: each room is an entire game map. The game can be of any genre (action, shooter, platformer, 2D or 3D...), and the map can be of any size and type (from a small room to an entire game world with many biomes and cities)

Each portal costs a given amount of energy to traverse. Energy is produced by special assets that can be found by exploring and playing the games in the rooms. Thus exploring the dungeon/network and playing its games will make you ever more powerful, and allow you to dive even deeper - and maybe, one day, discover the unspeakable power that lies in the depth of the Interlude!',
        3.4, 462, 1000, 16921);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (34, 'GAME-4D7F7', 'The Scourge | Tai Ương', true, 260000, 10, null, 'The_Scourge__Tai_ng',
        'The Scourge game tells stories revolving around urban legends from the 1990s in Saigon, Vietnam. The male protagonist is trapped by past sins caused by his and his family''s mistakes. Now, he must confront and overcome the consequences to find a path to redemption, and you are him.',
        true, e'Single-player
                                                                                                                                                                                                                                               Family sharing
                                                                                                                                                                                                                                               Xbox Controller',
        '2024-10-23', 'Rare Reversee, Beaztek', ' ', ' ', e'"The Scourge" is a first person horror puzzle game, immersing you in the heart of Saigon (Vietnam) through urban legends from the 1990s. You will follow the tragic fate of a family and the main character, who made mistakes in the past and is now haunted by a sinister supernatural force.

The protagonist, a person overwhelmed by delusions due to past tragedies and life\'s despair, frequently finds himself in nightmares. In these dreams, the line between reality and illusion becomes blurred. To rediscover his inner light and escape from the shadows of his past, he must confront his sorrows, make amends for his mistakes, and defeat the most formidable enemy that exists not only in his mind but also in the real world.

                                                                                                                                                                                                                                               Starting in a rundown apartment during an era of an unnamed epidemic, where mysteries and supernatural phenomena occur incessantly, you\'ll need to use your intellect to solve intricate puzzles, explore new areas, and prepare for the ultimate showdown. Every decision, every action, will determine his fate and future. Stay alert and be ready to face all challenges.

Mature Content Description
The developers describe the content like this:

Dear players,

                                                                                                                                                                                                                                               We\'d like to provide some clarity about the mature content present in our game. It\'s essential for our community to have a clear understanding of what to expect and to ensure a comfortable gaming experience for all:

                                                                                                                                                                                                                                               Sexual Content: Our horror game does not contain depictions of sexual acts, nor does it touch upon topics of sexual assault or non-consensual sex. The narrative focuses on horror elements that are separate from any sexual themes.

Violence: The game does contain elements of horror and suspense, which can manifest in intense scenes. However, this is in line with the genre and is not gratuitous. Players should be prepared for tense atmospheres and potentially startling moments, but not graphic violence.

Drug and Alcohol Abuse: There are no depictions or narratives surrounding drug and alcohol abuse in our game.

Self-harm: We have taken care to avoid sensitive topics such as self-harm, and it is not present or implied within our storyline.

                                                                                                                                                                                                                                               We urge potential players to consider these points and gauge their comfort level with the content. Our primary goal is to provide a thrilling horror experience without delving into topics that could be triggering for some players. If you have any concerns or questions about the content, please feel free to reach out to us. We\'re here to assist and ensure everyone enjoys our game safely and comfortably.



                                                                                                                                                                                                                                               System Requirements
                                                                                                                                                                                                                                               Minimum:
                                                                                                                                                                                                                                               Requires a 64-bit processor and operating system
                                                                                                                                                                                                                                               OS: Windows 10 x64
                                                                                                                                                                                                                                               Processor: Core I5 Gen 9, Ryzen 5 3600/5600X
Memory: 8 GB RAM
Graphics: GTX 1050 4GB
DirectX: Version 11
Storage: 10 GB available space
Sound Card: Yes
VR Support:
Recommended:
Requires a 64-bit processor and operating system
OS: Windows 10 x64
Processor: Core I7 Gen 9, Ryzen 7 3700X
Memory: 16 GB RAM
Graphics: GTX 1660
DirectX: Version 11
Storage: 10 GB available space
Sound Card: Yes
VR Support:', 4.4, 2999, 1000, 129999);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (29, 'GAME-21F31', 'Voidwrought', true, 209000, 20, null, 'voidwrought-ce8f4b',
        'Voidwrought is a 2D action-platformer set in a hand-drawn world of cosmic horrors. Explore the thawing ruins of the First Civilisation and strike down the gods that dwell there. Grow your powers, uncover ancient Artifacts, and expand your shrine amid the wreckage.',
        true, null, '2024-07-15', ' ', ' ', ' ', e'The coming of the Red Star heralds a new age. Emerging from its cocoon, the Simulacrum is driven to collect Ichor, the blood of the gods, from the monstrosities who hoard it.

Voidwrought is a fast-paced action-platformer with tight traversal, varied abilities, and formidable boss battles. Find and equip powerful Artifacts to customize your playstyle. Excavate into the rubble of the Gray City to construct a shrine filled with loyal followers.

Delve Beneath the Surface
Descend below the star-scorched surface and explore the multidimensional depths below. Witness the corrupted revelry of the Court, lose yourself in the icy tunnels of the Old Waters, and discover the grim fate of the Abandoned Expedition.

With sharp controls, engaging exploration, and deep lore to discover, Voidwrought is a dynamic addition to the modern Metroidvania genre.

Unleash Ancient Artifacts
The world is filled with treasures sought by the learned, the brave, and the mad. Scour the halls of your shrine, rend the corpses of defeated deities, and hunt in the hidden corners of the cosmos to find Artifacts capable of granting unique powers.

Discover and equip over 50 Relics and Souls, from spectral weapons to passive buffs, to match your preferred playstyle.

Build your Shrine
Some still cling to the original faith. From a tiny cult, expand your influence and excavate further into the ruins. As you expand your shrine and followers, new secrets, rewards, and horrors come to light.

Features
Experience smooth, satisfying movement and tight controls.

Explore atmospheric, hand-drawn biomes, from the star-scorched Surface to the biomechanical Abandoned Expedition.

Fight over 70 enemies and 11 formidable bosses.

Customize your playstyle with over 50 active Relics and passive Souls.

Expand your shrine to discover new treasures, abilities, and secrets.

Enjoy a rich, foreboding score by Neverinth and Vigil: The Longest Night composer Jouni Valjakka.', 3.5, 471, 1000,
        42547);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (10, 'GAME-69EEA', 'The Jackbox Survey Scramble', true, 142000, 0, null, 'the-jackbox-survey-scramble-8cfeb4',
        'Discover how people across the country think! The Jackbox Survey Scramble is a collection of hilarious survey-based games, using real one-word answers from real people. Really!',
        true, null, '2002-10-24', ' ', ' ', ' ', e'Welcome to a new era of party games!
Jackbox has made an all-new party experience! Taking real surveys from real people around the globe, The Jackbox Survey Scramble is constantly changing based on answers submitted from players, including you! Invite your friends, family, coworkers, and enemies to see how they think when it comes to questions like, “In one word, what’s the cutest nickname for butts?” or “In one word, what’s the best sandwich topping?”',
        2.7, 841, 1000, 12194);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (4, 'GAME-92811', 'Earniverse', true, 24999, 0, null, 'earniverse-732d09',
        'A unique VR Metaverse made of a 1080km2 Open-World Map accessible as Free to Play where players can own and develop their land in a AAA graphics environment.',
        true, null, '2024-08-15', ' ', ' ', ' ', e'The Play&Earn Metaverse

Earniverse is the first-ever “Play&Earn Metaverse” created. It’s an immersive virtual world where players can discover innovative experiences, and game developers can build and launch games with various GameFi features. This unique approach increases the overall value of the Metaverse within a single ecosystem, making it more decentralized, transparent, and user-experience-oriented. Players can engage in an ultra-immersive VR environment and earn by enhancing their Lands & NFTs',
        3.2, 824, 1000, 38097);
INSERT INTO public.game (sys_id_game, game_code, game_name, status, price, discount_percent, game_image, slug,
                         description, is_active, features, release_date, developer, platform, language, about, rating,
                         rating_count, quantity, quantity_sold)
VALUES (11, 'GAME-C20D4', 'No More Room in Hell 2', true, 313000, 0, null, 'no-more-room-in-hell-2-ea1b5c',
        'No More Room in Hell 2 is a terrifying 8 player co-op action horror experience. Start alone, find your friends in the dark and survive - in a dynamic, endlessly replayable zombie apocalypse.',
        true, null, '2024-07-15', ' ', ' ', ' ', e'ABOUT THE GAME
No More Room in Hell 2 is an intense & terrifying permadeath co-op journey into the eerie darkness of zombie-infested zones. As an emergency responder you must survive, scavenge, stockpile and fulfill your mission - then repeat, each time in an ever-changing experience.',
        3.3, 611, 1000, 49246);


ALTER TABLE game
    ALTER COLUMN sys_id_game ADD GENERATED ALWAYS AS IDENTITY;

---category_detail
truncate table category_detail cascade;

INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (8, 1);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (34, 1);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (32, 1);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (22, 1);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (9, 1);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (14, 1);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (25, 2);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (25, 3);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (10, 4);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (19, 4);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (28, 4);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (10, 5);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (13, 5);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (33, 5);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (26, 6);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (10, 6);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (13, 6);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (10, 7);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (13, 7);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (33, 7);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (13, 8);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (5, 9);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (26, 9);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (27, 9);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (26, 10);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (24, 10);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (17, 10);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (21, 11);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (23, 11);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (2, 11);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (26, 12);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (3, 12);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (31, 12);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (19, 13);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (13, 13);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (21, 14);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (35, 14);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (13, 14);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (26, 15);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (3, 15);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (31, 15);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (23, 16);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (35, 16);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (12, 17);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (3, 17);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (6, 17);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (29, 18);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (13, 18);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (23, 19);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (35, 19);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (33, 19);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (21, 20);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (5, 20);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (16, 20);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (11, 21);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (28, 21);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (4, 21);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (12, 22);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (18, 22);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (19, 22);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (18, 23);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (4, 23);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (15, 23);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (18, 24);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (4, 24);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (20, 24);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (1, 24);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (18, 25);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (4, 25);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (30, 25);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (12, 26);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (18, 26);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (14, 26);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (12, 27);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (18, 27);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (2, 27);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (5, 28);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (19, 28);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (11, 28);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (21, 29);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (19, 29);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (7, 29);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (11, 30);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (6, 30);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (12, 31);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (19, 31);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (5, 32);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (19, 32);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (14, 32);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (19, 33);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (11, 33);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (3, 33);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (1, 34);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (2, 34);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (5, 35);
INSERT INTO public.category_detail (sys_id_category, sys_id_game)
VALUES (8, 35);

---media
truncate table media cascade;

INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/offer/87b7846d2eba4bc49eead0854323aba8/EGS_DyingLight2StayHumanReloadedEdition_Techland_S2_1200x1600-76cef594ff94fbac64a8af1ebe4c7590?resize=1&w=360&h=480&quality=medium',
        1);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn2.unrealengine.com/egs-dyinglight2stayhumanreloadededition-techland-ic1-400x400-ef8c8989500e.png?resize=1&w=480&h=270&quality=medium',
        1);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-03-1920x1080-5acfeff80697.jpg', 1);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-04-1920x1080-01df52f79ade.jpg', 1);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-05-1920x1080-1c5ac325c83a.jpg', 1);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-06-1920x1080-40f55c566ef9.jpg', 1);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-07-1920x1080-32df329a3c64.jpg', 1);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6', 'https://cdn2.unrealengine.com/egs-dyinglight2stayhuman-techland-g1a-08-1920x1080-fc9df7be5ad5.jpg', 1);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/offer/32bc7c6aaecd40eeb5f58bb83dba1c05/EGS_Frostpunk2_11bitstudios_S2_1200x1600-8b452490754cb4a4fe1a983c533cfb5d?resize=1&w=360&h=480&quality=medium',
        2);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn2.unrealengine.com/egs-frostpunk2-11bitstudios-ic1-400x400-93c10180debb.png?resize=1&w=480&h=270&quality=medium',
        2);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/6d6661d418814571aba8475dab84615c/frostpunk-2-1ge40.jpg?resize=1&w=480&h=270&quality=medium',
        2);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/6d6661d418814571aba8475dab84615c/frostpunk-2-1m4yz.jpg?resize=1&w=480&h=270&quality=medium',
        2);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/6d6661d418814571aba8475dab84615c/frostpunk-2-hvpux.jpg?resize=1&w=480&h=270&quality=medium',
        2);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/6d6661d418814571aba8475dab84615c/frostpunk-2-5qpje.jpg?resize=1&w=480&h=270&quality=medium',
        2);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/6d6661d418814571aba8475dab84615c/frostpunk-2-9w4dj.jpg?resize=1&w=480&h=270&quality=medium',
        2);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/6d6661d418814571aba8475dab84615c/frostpunk-2-owqog.jpg?resize=1&w=480&h=270&quality=medium',
        2);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/ad0c24d54e804bdd9dd4dba453eeef2b/trinity-building-editor-1cdwv.png?resize=1&w=360&h=480&quality=medium',
        3);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/ad0c24d54e804bdd9dd4dba453eeef2b/trinity-building-editor-logo-6cqfb.png?resize=1&w=480&h=270&quality=medium',
        3);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/ad0c24d54e804bdd9dd4dba453eeef2b/trinity-building-editor-video-ur59q.png?resize=1&w=480&h=270&quality=medium',
        3);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/ad0c24d54e804bdd9dd4dba453eeef2b/trinity-building-editor-1oz3h.jpg?resize=1&w=480&h=270&quality=medium',
        3);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/ad0c24d54e804bdd9dd4dba453eeef2b/trinity-building-editor-e75sg.jpg?resize=1&w=480&h=270&quality=medium',
        3);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/ad0c24d54e804bdd9dd4dba453eeef2b/trinity-building-editor-awu1q.jpg?resize=1&w=480&h=270&quality=medium',
        3);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/ad0c24d54e804bdd9dd4dba453eeef2b/trinity-building-editor-4ko9a.jpg?resize=1&w=480&h=270&quality=medium',
        3);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/ad0c24d54e804bdd9dd4dba453eeef2b/trinity-building-editor-zyvum.jpg?resize=1&w=480&h=270&quality=medium',
        3);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/b03cedcb4dcc45c382061fed43fac004/earniverse-63ss6.jpg?resize=1&w=360&h=480&quality=medium',
        4);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/b03cedcb4dcc45c382061fed43fac004/earniverse-logo-1116w.png?resize=1&w=480&h=270&quality=medium',
        4);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/b03cedcb4dcc45c382061fed43fac004/earniverse-17h31.jpg?resize=1&w=480&h=270&quality=medium',
        4);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/b03cedcb4dcc45c382061fed43fac004/earniverse-dmuj0.jpg?resize=1&w=480&h=270&quality=medium',
        4);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/b03cedcb4dcc45c382061fed43fac004/earniverse-qurjc.jpg?resize=1&w=480&h=270&quality=medium',
        4);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/b03cedcb4dcc45c382061fed43fac004/earniverse-dljvk.jpg?resize=1&w=480&h=270&quality=medium',
        4);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/b03cedcb4dcc45c382061fed43fac004/earniverse-1pera.jpg?resize=1&w=480&h=270&quality=medium',
        4);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/b03cedcb4dcc45c382061fed43fac004/earniverse-16tdu.jpg?resize=1&w=480&h=270&quality=medium',
        4);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/99f725101fb94d17805cc15618beacb8/horticular-nib8a.jpg?resize=1&w=360&h=480&quality=medium',
        5);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/99f725101fb94d17805cc15618beacb8/horticular-logo-6qqwn.png?resize=1&w=480&h=270&quality=medium',
        5);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/99f725101fb94d17805cc15618beacb8/horticular-178sb.png?resize=1&w=480&h=270&quality=medium',
        5);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/99f725101fb94d17805cc15618beacb8/horticular-12x1e.png?resize=1&w=480&h=270&quality=medium',
        5);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/99f725101fb94d17805cc15618beacb8/horticular-10mzq.png?resize=1&w=480&h=270&quality=medium',
        5);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/99f725101fb94d17805cc15618beacb8/horticular-5abmu.jpg?resize=1&w=480&h=270&quality=medium',
        5);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/99f725101fb94d17805cc15618beacb8/horticular-dpfb1.jpg?resize=1&w=480&h=270&quality=medium',
        5);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/99f725101fb94d17805cc15618beacb8/horticular-18rhn.jpg?resize=1&w=480&h=270&quality=medium',
        5);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/acbb4b1cf4ce44db81b63f420db4e6ec/megapolis-1ee07.jpg?resize=1&w=360&h=480&quality=medium',
        6);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/acbb4b1cf4ce44db81b63f420db4e6ec/megapolis-logo-b62bn.png?resize=1&w=480&h=270&quality=medium',
        6);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/acbb4b1cf4ce44db81b63f420db4e6ec/megapolis-hee9k.jpg?resize=1&w=480&h=270&quality=medium',
        6);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/acbb4b1cf4ce44db81b63f420db4e6ec/megapolis-cucn4.jpg?resize=1&w=480&h=270&quality=medium',
        6);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/acbb4b1cf4ce44db81b63f420db4e6ec/megapolis-1rvq5.jpg?resize=1&w=480&h=270&quality=medium',
        6);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/acbb4b1cf4ce44db81b63f420db4e6ec/megapolis-1h2uw.jpg?resize=1&w=480&h=270&quality=medium',
        6);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/acbb4b1cf4ce44db81b63f420db4e6ec/megapolis-7qq3b.jpg?resize=1&w=480&h=270&quality=medium',
        6);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/acbb4b1cf4ce44db81b63f420db4e6ec/megapolis-c8llq.jpg?resize=1&w=480&h=270&quality=medium',
        6);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/12f3f8148d0b44c68e2dea57b45352af/illuvium-zero-5thxx.png?resize=1&w=360&h=480&quality=medium',
        7);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/12f3f8148d0b44c68e2dea57b45352af/illuvium-zero-logo-1hflb.png?resize=1&w=480&h=270&quality=medium',
        7);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/12f3f8148d0b44c68e2dea57b45352af/illuvium-zero-1q62w.jpg?resize=1&w=480&h=270&quality=medium',
        7);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/12f3f8148d0b44c68e2dea57b45352af/illuvium-zero-76c5p.png?resize=1&w=480&h=270&quality=medium',
        7);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/12f3f8148d0b44c68e2dea57b45352af/illuvium-zero-xsbt1.png?resize=1&w=480&h=270&quality=medium',
        7);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/12f3f8148d0b44c68e2dea57b45352af/illuvium-zero-1odgd.png?resize=1&w=480&h=270&quality=medium',
        7);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/12f3f8148d0b44c68e2dea57b45352af/illuvium-zero-87nbr.png?resize=1&w=480&h=270&quality=medium',
        7);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/12f3f8148d0b44c68e2dea57b45352af/illuvium-zero-1tmh2.png?resize=1&w=480&h=270&quality=medium',
        7);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/bec36b80c3cc4a76a20919c5b04a7ba1/romopolis-1noh3.jpg?resize=1&w=360&h=480&quality=medium',
        8);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/bec36b80c3cc4a76a20919c5b04a7ba1/romopolis-logo-l84wd.png?resize=1&w=480&h=270&quality=medium',
        8);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/bec36b80c3cc4a76a20919c5b04a7ba1/romopolis-1w4xb.jpg?resize=1&w=480&h=270&quality=medium',
        8);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/bec36b80c3cc4a76a20919c5b04a7ba1/romopolis-1trxu.jpg?resize=1&w=480&h=270&quality=medium',
        8);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/bec36b80c3cc4a76a20919c5b04a7ba1/romopolis-j62qu.jpg?resize=1&w=480&h=270&quality=medium',
        8);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/bec36b80c3cc4a76a20919c5b04a7ba1/romopolis-1jtvt.jpg?resize=1&w=480&h=270&quality=medium',
        8);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/bec36b80c3cc4a76a20919c5b04a7ba1/romopolis-1jtvt.jpg?resize=1&w=480&h=270&quality=medium',
        8);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/bec36b80c3cc4a76a20919c5b04a7ba1/romopolis-37ba1.jpg?resize=1&w=480&h=270&quality=medium',
        8);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/a1aabc537f0a4332868989726a9f52e5/barbie-project-friendship-107ld.png?resize=1&w=360&h=480&quality=medium',
        9);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/a1aabc537f0a4332868989726a9f52e5/barbie-project-friendship-logo-1mwid.png?resize=1&w=480&h=270&quality=medium',
        9);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/a1aabc537f0a4332868989726a9f52e5/barbie-project-friendship-35bkn.jpg?resize=1&w=480&h=270&quality=medium',
        9);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/a1aabc537f0a4332868989726a9f52e5/barbie-project-friendship-h3i3r.jpg?resize=1&w=480&h=270&quality=medium',
        9);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/a1aabc537f0a4332868989726a9f52e5/barbie-project-friendship-1cts9.jpg?resize=1&w=480&h=270&quality=medium',
        9);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/a1aabc537f0a4332868989726a9f52e5/barbie-project-friendship-u3kkz.jpg?resize=1&w=480&h=270&quality=medium',
        9);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/a1aabc537f0a4332868989726a9f52e5/barbie-project-friendship-1xpnz.jpg?resize=1&w=480&h=270&quality=medium',
        9);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/a1aabc537f0a4332868989726a9f52e5/barbie-project-friendship-1xpnz.jpg?resize=1&w=480&h=270&quality=medium',
        9);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/0b9b6c70ba6e41f5ba28f71ff06f0971/the-jackbox-survey-scramble-1lgmp.png?resize=1&w=360&h=480&quality=medium',
        10);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/0b9b6c70ba6e41f5ba28f71ff06f0971/the-jackbox-survey-scramble-logo-qbp4c.png?resize=1&w=480&h=270&quality=medium',
        10);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/0b9b6c70ba6e41f5ba28f71ff06f0971/the-jackbox-survey-scramble-lb4k2.png?resize=1&w=480&h=270&quality=medium',
        10);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/a80b01a9794a4d0288427a2fba7fc0c9/no-more-room-in-hell-2-x0znt.jpg?resize=1&w=360&h=480&quality=medium',
        11);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/a80b01a9794a4d0288427a2fba7fc0c9/no-more-room-in-hell-2-logo-13bq7.png?resize=1&w=480&h=270&quality=medium',
        11);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1', 'https://cdn1.epicgames.com/spt-assets/a80b01a9794a4d0288427a2fba7fc0c9/no-more-room-in-hell-2-14z55.png',
        11);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/a80b01a9794a4d0288427a2fba7fc0c9/no-more-room-in-hell-2-3vjeb.png?resize=1&w=480&h=270&quality=medium',
        11);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/a80b01a9794a4d0288427a2fba7fc0c9/no-more-room-in-hell-2-na9l3.png?resize=1&w=480&h=270&quality=medium',
        11);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/a80b01a9794a4d0288427a2fba7fc0c9/no-more-room-in-hell-2-19khn.png?resize=1&w=480&h=270&quality=medium',
        11);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/a80b01a9794a4d0288427a2fba7fc0c9/no-more-room-in-hell-2-1iz61.png?resize=1&w=480&h=270&quality=medium',
        11);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/a80b01a9794a4d0288427a2fba7fc0c9/no-more-room-in-hell-2-wnh81.jpg?resize=1&w=480&h=270&quality=medium',
        11);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-d1xu1.png?resize=1&w=360&h=480&quality=medium',
        12);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-logo-tmmj1.png?resize=1&w=480&h=270&quality=medium',
        12);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-1y2h7.jpg?resize=1&w=480&h=270&quality=medium',
        12);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-cdpdf.jpg?resize=1&w=480&h=270&quality=medium',
        12);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-lij9z.jpg?resize=1&w=480&h=270&quality=medium',
        12);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-snopx.jpg?resize=1&w=480&h=270&quality=medium',
        12);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-26106.jpg?resize=1&w=480&h=270&quality=medium',
        12);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-lld1p.jpg?resize=1&w=480&h=270&quality=medium',
        12);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/1b58d6c6623f4d44a7ba2174e62e2cfa/brilliantcrypto-wj2m3.jpg?resize=1&w=360&h=480&quality=medium',
        13);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/1b58d6c6623f4d44a7ba2174e62e2cfa/brilliantcrypto-logo-1cpuy.png?resize=1&w=480&h=270&quality=medium',
        13);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/1b58d6c6623f4d44a7ba2174e62e2cfa/brilliantcrypto-logo-1cpuy.png?resize=1&w=480&h=270&quality=medium',
        13);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/1b58d6c6623f4d44a7ba2174e62e2cfa/brilliantcrypto-logo-1cpuy.png?resize=1&w=480&h=270&quality=medium',
        13);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/1b58d6c6623f4d44a7ba2174e62e2cfa/brilliantcrypto-logo-1cpuy.png?resize=1&w=480&h=270&quality=medium',
        13);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/1b58d6c6623f4d44a7ba2174e62e2cfa/brilliantcrypto-logo-1cpuy.png?resize=1&w=480&h=270&quality=medium',
        13);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/1b58d6c6623f4d44a7ba2174e62e2cfa/brilliantcrypto-logo-1cpuy.png?resize=1&w=480&h=270&quality=medium',
        13);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/1b58d6c6623f4d44a7ba2174e62e2cfa/brilliantcrypto-logo-1cpuy.png?resize=1&w=480&h=270&quality=medium',
        13);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/832fe73ecc634845974b2c9234283487/mechwarrior-5-clans-81zwr.jpg?resize=1&w=360&h=480&quality=medium',
        14);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/832fe73ecc634845974b2c9234283487/mechwarrior-5-clans-logo-1dzr2.png?resize=1&w=480&h=270&quality=medium',
        14);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/832fe73ecc634845974b2c9234283487/mechwarrior-5-clans-109gb.jpg?resize=1&w=480&h=270&quality=medium',
        14);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/832fe73ecc634845974b2c9234283487/mechwarrior-5-clans-en9sn.jpg?resize=1&w=480&h=270&quality=medium',
        14);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/832fe73ecc634845974b2c9234283487/mechwarrior-5-clans-1gsq1.jpg?resize=1&w=480&h=270&quality=medium',
        14);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/832fe73ecc634845974b2c9234283487/mechwarrior-5-clans-cjgjt.jpg?resize=1&w=480&h=270&quality=medium',
        14);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/832fe73ecc634845974b2c9234283487/mechwarrior-5-clans-cjgjt.jpg?resize=1&w=480&h=270&quality=medium',
        14);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/832fe73ecc634845974b2c9234283487/mechwarrior-5-clans-9d6xi.jpg?resize=1&w=480&h=270&quality=medium',
        14);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-d1xu1.png?resize=1&w=360&h=480&quality=medium',
        15);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-logo-tmmj1.png?resize=1&w=480&h=270&quality=medium',
        15);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-1y2h7.jpg?resize=1&w=480&h=270&quality=medium',
        15);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-cdpdf.jpg?resize=1&w=480&h=270&quality=medium',
        15);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-lij9z.jpg?resize=1&w=480&h=270&quality=medium',
        15);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-snopx.jpg?resize=1&w=480&h=270&quality=medium',
        15);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-pddsu.jpg?resize=1&w=480&h=270&quality=medium',
        15);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/29586d03c1c147569a1991a8616c1413/legion-td-2-5fz4f.jpg?resize=1&w=480&h=270&quality=medium',
        15);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/98e4ed14a7014551bc4a41a85ecef25d/parmo-6mik9.jpg?resize=1&w=360&h=480&quality=medium',
        16);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/98e4ed14a7014551bc4a41a85ecef25d/parmo-logo-oi7gy.png?resize=1&w=480&h=270&quality=medium',
        16);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/98e4ed14a7014551bc4a41a85ecef25d/parmo-logo-oi7gy.png?resize=1&w=480&h=270&quality=medium',
        16);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/98e4ed14a7014551bc4a41a85ecef25d/parmo-logo-oi7gy.png?resize=1&w=480&h=270&quality=medium',
        16);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/98e4ed14a7014551bc4a41a85ecef25d/parmo-logo-oi7gy.png?resize=1&w=480&h=270&quality=medium',
        16);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/98e4ed14a7014551bc4a41a85ecef25d/parmo-logo-oi7gy.png?resize=1&w=480&h=270&quality=medium',
        16);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/98e4ed14a7014551bc4a41a85ecef25d/parmo-logo-oi7gy.png?resize=1&w=480&h=270&quality=medium',
        16);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/98e4ed14a7014551bc4a41a85ecef25d/parmo-logo-oi7gy.png?resize=1&w=480&h=270&quality=medium',
        16);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/667c480e8d0a41dc87b1fcdd8d491dc5/3-minutes-to-midnight-4k58r.jpg?resize=1&w=360&h=480&quality=medium',
        17);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/667c480e8d0a41dc87b1fcdd8d491dc5/3-minutes-to-midnight-logo-1u770.png?resize=1&w=480&h=270&quality=medium',
        17);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/667c480e8d0a41dc87b1fcdd8d491dc5/3-minutes-to-midnight-gdf3c.jpg?resize=1&w=480&h=270&quality=medium',
        17);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/667c480e8d0a41dc87b1fcdd8d491dc5/3-minutes-to-midnight-yjv2i.jpg?resize=1&w=480&h=270&quality=medium',
        17);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/667c480e8d0a41dc87b1fcdd8d491dc5/3-minutes-to-midnight-3u7cn.jpg?resize=1&w=480&h=270&quality=medium',
        17);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/667c480e8d0a41dc87b1fcdd8d491dc5/3-minutes-to-midnight-17w72.jpg?resize=1&w=480&h=270&quality=medium',
        17);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/667c480e8d0a41dc87b1fcdd8d491dc5/3-minutes-to-midnight-1ocq1.jpg?resize=1&w=480&h=270&quality=medium',
        17);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/667c480e8d0a41dc87b1fcdd8d491dc5/3-minutes-to-midnight-wdztr.jpg?resize=1&w=480&h=270&quality=medium',
        17);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/667c480e8d0a41dc87b1fcdd8d491dc5/3-minutes-to-midnight-4k58r.jpg?resize=1&w=360&h=480&quality=medium',
        18);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn2.unrealengine.com/egs-wrc24locationcarcontentpack-codemasters-dlc-ic1-200x200-406a79617f9d.png?resize=1&w=480&h=270&quality=medium',
        18);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn2.unrealengine.com/egs-easportswrc24-codemasters-g1a-05-1920x1080-05e1ba6c2d00.jpg?resize=1&w=480&h=270&quality=medium',
        18);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn2.unrealengine.com/egs-easportswrc24-codemasters-g1a-06-1920x1080-7c77f17bb417.jpg?resize=1&w=480&h=270&quality=medium',
        18);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn2.unrealengine.com/egs-easportswrc24-codemasters-g1a-07-1920x1080-76a89cd11f6a.jpg?resize=1&w=480&h=270&quality=medium',
        18);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn2.unrealengine.com/egs-easportswrc24-codemasters-g1a-09-1920x1080-6a83edb2c71e.jpg?resize=1&w=480&h=270&quality=medium',
        18);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn2.unrealengine.com/egs-easportswrc24-codemasters-g1a-10-1920x1080-2dba62176862.jpg?resize=1&w=480&h=270&quality=medium',
        18);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn2.unrealengine.com/egs-easportswrc24-codemasters-g1a-10-1920x1080-2dba62176862.jpg?resize=1&w=480&h=270&quality=medium',
        18);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/offer/50b6612049324d0faf8642014139b082/EGS_EASPORTSWRC24_Codemasters_S2_1200x1600-485f732f94088f59a02d68eebf64c6e5?resize=1&w=360&h=480&quality=medium',
        19);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/7c5d45622bbc44dc970f5bedc3bdad21/ww2-frontline-1942-logo-1xlvu.png?resize=1&w=480&h=270&quality=medium',
        19);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/7c5d45622bbc44dc970f5bedc3bdad21/ww2-frontline-1942-1rlro.png?resize=1&w=480&h=270&quality=medium',
        19);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/7c5d45622bbc44dc970f5bedc3bdad21/ww2-frontline-1942-19zka.png?resize=1&w=480&h=270&quality=medium',
        19);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/7c5d45622bbc44dc970f5bedc3bdad21/ww2-frontline-1942-1ovjd.png?resize=1&w=480&h=270&quality=medium',
        19);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/7c5d45622bbc44dc970f5bedc3bdad21/ww2-frontline-1942-16kts.png?resize=1&w=480&h=270&quality=medium',
        19);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/7c5d45622bbc44dc970f5bedc3bdad21/ww2-frontline-1942-13l6g.png?resize=1&w=480&h=270&quality=medium',
        19);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/7c5d45622bbc44dc970f5bedc3bdad21/ww2-frontline-1942-1qhvh.png?resize=1&w=480&h=270&quality=medium',
        19);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/7c5d45622bbc44dc970f5bedc3bdad21/ww2-frontline-1942-1yl9z.png?resize=1&w=360&h=480&quality=medium',
        20);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/e25ec860b85542039edfe43ac0623bf2/battle-bears-heroes-logo-bzvb3.png?resize=1&w=480&h=270&quality=medium',
        20);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/e25ec860b85542039edfe43ac0623bf2/battle-bears-heroes-4aved.png?resize=1&w=480&h=270&quality=medium',
        20);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/e25ec860b85542039edfe43ac0623bf2/battle-bears-heroes-jusse.png?resize=1&w=480&h=270&quality=medium',
        20);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/e25ec860b85542039edfe43ac0623bf2/battle-bears-heroes-vqjia.png?resize=1&w=480&h=270&quality=medium',
        20);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/e25ec860b85542039edfe43ac0623bf2/battle-bears-heroes-1m8bn.png?resize=1&w=480&h=270&quality=medium',
        20);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/e25ec860b85542039edfe43ac0623bf2/battle-bears-heroes-1isoe.png?resize=1&w=480&h=270&quality=medium',
        20);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/e25ec860b85542039edfe43ac0623bf2/battle-bears-heroes-13hxk.png?resize=1&w=480&h=270&quality=medium',
        20);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/e25ec860b85542039edfe43ac0623bf2/battle-bears-heroes-c2ts7.png?resize=1&w=360&h=480&quality=medium',
        21);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/af3dd9d43caa411b903a037ee9583b06/kmon-world-of-kogaea-logo-hgf8c.png?resize=1&w=480&h=270&quality=medium',
        21);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/af3dd9d43caa411b903a037ee9583b06/kmon-world-of-kogaea-3at4w.png?resize=1&w=480&h=270&quality=medium',
        21);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/af3dd9d43caa411b903a037ee9583b06/kmon-world-of-kogaea-1nyf8.png?resize=1&w=480&h=270&quality=medium',
        21);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/af3dd9d43caa411b903a037ee9583b06/kmon-world-of-kogaea-p24d0.png?resize=1&w=480&h=270&quality=medium',
        21);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/af3dd9d43caa411b903a037ee9583b06/kmon-world-of-kogaea-2y6po.png?resize=1&w=480&h=270&quality=medium',
        21);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/af3dd9d43caa411b903a037ee9583b06/kmon-world-of-kogaea-180z8.png?resize=1&w=480&h=270&quality=medium',
        21);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/af3dd9d43caa411b903a037ee9583b06/kmon-world-of-kogaea-180z8.png?resize=1&w=480&h=270&quality=medium',
        21);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/d7a0378f2d2b43bcafb0c16b1228ecec/juna--the-dreamwalker-6p9zc.jpg?resize=1&w=360&h=480&quality=medium',
        22);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/d7a0378f2d2b43bcafb0c16b1228ecec/juna--the-dreamwalker-logo-sbcqa.png?resize=1&w=480&h=270&quality=medium',
        22);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/d7a0378f2d2b43bcafb0c16b1228ecec/juna--the-dreamwalker-1qly6.jpg?resize=1&w=480&h=270&quality=medium',
        22);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/d7a0378f2d2b43bcafb0c16b1228ecec/juna--the-dreamwalker-j196j.jpg?resize=1&w=480&h=270&quality=medium',
        22);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/d7a0378f2d2b43bcafb0c16b1228ecec/juna--the-dreamwalker-hycla.jpg?resize=1&w=480&h=270&quality=medium',
        22);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/d7a0378f2d2b43bcafb0c16b1228ecec/juna--the-dreamwalker-1p3gl.jpg?resize=1&w=480&h=270&quality=medium',
        22);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/d7a0378f2d2b43bcafb0c16b1228ecec/juna--the-dreamwalker-hij6n.jpg?resize=1&w=480&h=270&quality=medium',
        22);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/d7a0378f2d2b43bcafb0c16b1228ecec/juna--the-dreamwalker-17e73.jpg?resize=1&w=480&h=270&quality=medium',
        22);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/cefd4a4f43b340669aa8b575fb27eadd/the-etherscape-1cb2k.png?resize=1&w=360&h=480&quality=medium',
        23);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/cefd4a4f43b340669aa8b575fb27eadd/the-etherscape-logo-kcww4.png?resize=1&w=480&h=270&quality=medium',
        23);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/cefd4a4f43b340669aa8b575fb27eadd/the-etherscape-jr6qw.png?resize=1&w=480&h=270&quality=medium',
        23);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/cefd4a4f43b340669aa8b575fb27eadd/the-etherscape-1ywt7.png?resize=1&w=480&h=270&quality=medium',
        23);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/cefd4a4f43b340669aa8b575fb27eadd/the-etherscape-i6g9h.png?resize=1&w=480&h=270&quality=medium',
        23);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/cefd4a4f43b340669aa8b575fb27eadd/the-etherscape-jci3f.png?resize=1&w=480&h=270&quality=medium',
        23);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/cefd4a4f43b340669aa8b575fb27eadd/the-etherscape-bk6md.png?resize=1&w=480&h=270&quality=medium',
        23);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/cefd4a4f43b340669aa8b575fb27eadd/the-etherscape-155br.png?resize=1&w=480&h=270&quality=medium',
        23);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/7f75e3ddeb8f4784b822ed51340a40b6/arhaekon-18ejn.jpg?resize=1&w=360&h=480&quality=medium',
        24);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/7f75e3ddeb8f4784b822ed51340a40b6/arhaekon-logo-1u4a8.png?resize=1&w=480&h=270&quality=medium',
        24);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/7f75e3ddeb8f4784b822ed51340a40b6/arhaekon-1ntw9.png?resize=1&w=480&h=270&quality=medium',
        24);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/7f75e3ddeb8f4784b822ed51340a40b6/arhaekon-1nihj.png?resize=1&w=480&h=270&quality=medium',
        24);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/7f75e3ddeb8f4784b822ed51340a40b6/arhaekon-1mbwz.png?resize=1&w=480&h=270&quality=medium',
        24);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/7f75e3ddeb8f4784b822ed51340a40b6/arhaekon-zspc4.png?resize=1&w=480&h=270&quality=medium',
        24);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/7f75e3ddeb8f4784b822ed51340a40b6/arhaekon-bgju7.png?resize=1&w=480&h=270&quality=medium',
        24);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/7f75e3ddeb8f4784b822ed51340a40b6/arhaekon-1we5k.png?resize=1&w=480&h=270&quality=medium',
        24);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/4dfae34e0ace43ac83f3d0e2b81b3c16/dragon-chronicles-black-tears-1dbuo.png?resize=1&w=360&h=480&quality=medium',
        25);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/4dfae34e0ace43ac83f3d0e2b81b3c16/dragon-chronicles-black-tears-logo-1gm8j.png?resize=1&w=480&h=270&quality=medium',
        25);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/4dfae34e0ace43ac83f3d0e2b81b3c16/dragon-chronicles-black-tears-19rnu.png?resize=1&w=480&h=270&quality=medium',
        25);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/4dfae34e0ace43ac83f3d0e2b81b3c16/dragon-chronicles-black-tears-1g0c1.png?resize=1&w=480&h=270&quality=medium',
        25);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/4dfae34e0ace43ac83f3d0e2b81b3c16/dragon-chronicles-black-tears-1k4g8.png?resize=1&w=480&h=270&quality=medium',
        25);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/4dfae34e0ace43ac83f3d0e2b81b3c16/dragon-chronicles-black-tears-f28nc.png?resize=1&w=480&h=270&quality=medium',
        25);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/4dfae34e0ace43ac83f3d0e2b81b3c16/dragon-chronicles-black-tears-ldgnj.jpg?resize=1&w=480&h=270&quality=medium',
        25);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/4dfae34e0ace43ac83f3d0e2b81b3c16/dragon-chronicles-black-tears-1gl6c.jpg?resize=1&w=480&h=270&quality=medium',
        25);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/dc77f1f941e14360b61093b761cb34ba/dark-and-darker-1h8fw.jpg?resize=1&w=360&h=480&quality=medium',
        26);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/dc77f1f941e14360b61093b761cb34ba/dark-and-darker-logo-1fd51.png?resize=1&w=480&h=270&quality=medium',
        26);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/dc77f1f941e14360b61093b761cb34ba/dark-and-darker-1vxxo.png?resize=1&w=480&h=270&quality=medium',
        26);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/dc77f1f941e14360b61093b761cb34ba/dark-and-darker-1vxxo.png?resize=1&w=480&h=270&quality=medium',
        26);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/dc77f1f941e14360b61093b761cb34ba/dark-and-darker-1uir6.png?resize=1&w=480&h=270&quality=medium',
        26);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/dc77f1f941e14360b61093b761cb34ba/dark-and-darker-1h860.png?resize=1&w=480&h=270&quality=medium',
        26);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/dc77f1f941e14360b61093b761cb34ba/dark-and-darker-1tqvb.png?resize=1&w=480&h=270&quality=medium',
        26);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/dc77f1f941e14360b61093b761cb34ba/dark-and-darker-1mp58.png?resize=1&w=480&h=270&quality=medium',
        26);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/7d5e43be93184f7989b95be4049ef2d6/tomb-nightmares-1ixrl.png?resize=1&w=360&h=480&quality=medium',
        27);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/7d5e43be93184f7989b95be4049ef2d6/tomb-nightmares-logo-1d2id.png?resize=1&w=480&h=270&quality=medium',
        27);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/7d5e43be93184f7989b95be4049ef2d6/tomb-nightmares-1u3w5.png?resize=1&w=480&h=270&quality=medium',
        27);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/7d5e43be93184f7989b95be4049ef2d6/tomb-nightmares-1qu8n.png?resize=1&w=480&h=270&quality=medium',
        27);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/7d5e43be93184f7989b95be4049ef2d6/tomb-nightmares-hsncr.png?resize=1&w=480&h=270&quality=medium',
        27);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/7d5e43be93184f7989b95be4049ef2d6/tomb-nightmares-1ff51.png?resize=1&w=480&h=270&quality=medium',
        27);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/7d5e43be93184f7989b95be4049ef2d6/tomb-nightmares-bcauk.png?resize=1&w=480&h=270&quality=medium',
        27);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/7d5e43be93184f7989b95be4049ef2d6/tomb-nightmares-doynx.png?resize=1&w=480&h=270&quality=medium',
        27);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/bbbc19e075d74714a9ae297e00c08506/surfing-legends-1dvoq.png?resize=1&w=360&h=480&quality=medium',
        28);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/bbbc19e075d74714a9ae297e00c08506/surfing-legends-logo-rf0lu.png?resize=1&w=480&h=270&quality=medium',
        28);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/bbbc19e075d74714a9ae297e00c08506/surfing-legends-12vxy.jpg?resize=1&w=480&h=270&quality=medium',
        28);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/bbbc19e075d74714a9ae297e00c08506/surfing-legends-qic23.jpg?resize=1&w=480&h=270&quality=medium',
        28);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/bbbc19e075d74714a9ae297e00c08506/surfing-legends-1lj79.jpg?resize=1&w=480&h=270&quality=medium',
        28);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/bbbc19e075d74714a9ae297e00c08506/surfing-legends-dtx7e.jpg?resize=1&w=480&h=270&quality=medium',
        28);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/bbbc19e075d74714a9ae297e00c08506/surfing-legends-zzo5n.jpg?resize=1&w=480&h=270&quality=medium',
        28);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/bbbc19e075d74714a9ae297e00c08506/surfing-legends-13o0z.jpg?resize=1&w=480&h=270&quality=medium',
        28);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/ae3f962fab4d42eba2dbefa4a1e76ff6/voidwrought-9gwxt.png?resize=1&w=360&h=480&quality=medium',
        29);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/ae3f962fab4d42eba2dbefa4a1e76ff6/voidwrought-logo-qz89k.png?resize=1&w=480&h=270&quality=medium',
        29);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/ae3f962fab4d42eba2dbefa4a1e76ff6/voidwrought-33u59.png?resize=1&w=480&h=270&quality=medium',
        29);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/ae3f962fab4d42eba2dbefa4a1e76ff6/voidwrought-13dm3.png?resize=1&w=480&h=270&quality=medium',
        29);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/ae3f962fab4d42eba2dbefa4a1e76ff6/voidwrought-1ivxm.png?resize=1&w=480&h=270&quality=medium',
        29);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/ae3f962fab4d42eba2dbefa4a1e76ff6/voidwrought-f8bg0.png?resize=1&w=480&h=270&quality=medium',
        29);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/ae3f962fab4d42eba2dbefa4a1e76ff6/voidwrought-1gbd6.jpg?resize=1&w=480&h=270&quality=medium',
        29);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/ae3f962fab4d42eba2dbefa4a1e76ff6/voidwrought-16her.png?resize=1&w=480&h=270&quality=medium',
        29);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/55bf9c955ee34bcc9f060a6a73108bc2/labyrinth-destroyer-1-iizu0.png?resize=1&w=360&h=480&quality=medium',
        30);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/55bf9c955ee34bcc9f060a6a73108bc2/labyrinth-destroyer-1-logo-uv30r.PNG?resize=1&w=480&h=270&quality=medium',
        30);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/55bf9c955ee34bcc9f060a6a73108bc2/labyrinth-destroyer-1-logo-uv30r.PNG?resize=1&w=480&h=270&quality=medium',
        30);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/55bf9c955ee34bcc9f060a6a73108bc2/labyrinth-destroyer-1-logo-uv30r.PNG?resize=1&w=480&h=270&quality=medium',
        30);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/55bf9c955ee34bcc9f060a6a73108bc2/labyrinth-destroyer-1-logo-uv30r.PNG?resize=1&w=480&h=270&quality=medium',
        30);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/55bf9c955ee34bcc9f060a6a73108bc2/labyrinth-destroyer-1-logo-uv30r.PNG?resize=1&w=480&h=270&quality=medium',
        30);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/55bf9c955ee34bcc9f060a6a73108bc2/labyrinth-destroyer-1-logo-uv30r.PNG?resize=1&w=480&h=270&quality=medium',
        30);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/55bf9c955ee34bcc9f060a6a73108bc2/labyrinth-destroyer-1-logo-uv30r.PNG?resize=1&w=480&h=270&quality=medium',
        30);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/b51d6cbcd4804d77bc97276c2b7767c7/interlude-lcz5v.png?resize=1&w=360&h=480&quality=medium',
        31);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/b51d6cbcd4804d77bc97276c2b7767c7/interlude-logo-khpej.png?resize=1&w=480&h=270&quality=medium',
        31);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/b51d6cbcd4804d77bc97276c2b7767c7/interlude-yrnu6.png?resize=1&w=480&h=270&quality=medium',
        31);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/b51d6cbcd4804d77bc97276c2b7767c7/interlude-1vbpm.png?resize=1&w=480&h=270&quality=medium',
        31);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/b51d6cbcd4804d77bc97276c2b7767c7/interlude-uogyb.png?resize=1&w=480&h=270&quality=medium',
        31);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/b51d6cbcd4804d77bc97276c2b7767c7/interlude-x0kgw.png?resize=1&w=480&h=270&quality=medium',
        31);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/b51d6cbcd4804d77bc97276c2b7767c7/interlude-x0kgw.png?resize=1&w=480&h=270&quality=medium',
        31);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/b51d6cbcd4804d77bc97276c2b7767c7/interlude-x0kgw.png?resize=1&w=480&h=270&quality=medium',
        31);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/636a4fbb1cbb4f4d9448c744d20c68e2/honeycomb-10ow2.png?resize=1&w=360&h=480&quality=medium',
        32);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/636a4fbb1cbb4f4d9448c744d20c68e2/honeycomb-logo-uhfz2.png?resize=1&w=480&h=270&quality=medium',
        32);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/636a4fbb1cbb4f4d9448c744d20c68e2/honeycomb-rgxrn.jpg?resize=1&w=480&h=270&quality=medium',
        32);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/636a4fbb1cbb4f4d9448c744d20c68e2/honeycomb-mixwp.jpg?resize=1&w=480&h=270&quality=medium',
        32);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/636a4fbb1cbb4f4d9448c744d20c68e2/honeycomb-1mnob.jpg?resize=1&w=480&h=270&quality=medium',
        32);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/636a4fbb1cbb4f4d9448c744d20c68e2/honeycomb-1rfja.jpg?resize=1&w=480&h=270&quality=medium',
        32);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/636a4fbb1cbb4f4d9448c744d20c68e2/honeycomb-f4t70.jpg?resize=1&w=480&h=270&quality=medium',
        32);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/636a4fbb1cbb4f4d9448c744d20c68e2/honeycomb-1i9hs.jpg?resize=1&w=480&h=270&quality=medium',
        32);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://cdn1.epicgames.com/spt-assets/da136e41d7ef40c99ce0f8b73fb57c36/the-magical-mixture-mill-1p6b6.jpg?resize=1&w=360&h=480&quality=medium',
        33);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo',
        'https://cdn1.epicgames.com/spt-assets/da136e41d7ef40c99ce0f8b73fb57c36/the-magical-mixture-mill-logo-173dy.png?resize=1&w=480&h=270&quality=medium',
        33);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://cdn1.epicgames.com/spt-assets/da136e41d7ef40c99ce0f8b73fb57c36/the-magical-mixture-mill-logo-173dy.png?resize=1&w=480&h=270&quality=medium',
        33);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://cdn1.epicgames.com/spt-assets/da136e41d7ef40c99ce0f8b73fb57c36/the-magical-mixture-mill-logo-173dy.png?resize=1&w=480&h=270&quality=medium',
        33);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://cdn1.epicgames.com/spt-assets/da136e41d7ef40c99ce0f8b73fb57c36/the-magical-mixture-mill-logo-173dy.png?resize=1&w=480&h=270&quality=medium',
        33);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://cdn1.epicgames.com/spt-assets/da136e41d7ef40c99ce0f8b73fb57c36/the-magical-mixture-mill-logo-173dy.png?resize=1&w=480&h=270&quality=medium',
        33);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://cdn1.epicgames.com/spt-assets/da136e41d7ef40c99ce0f8b73fb57c36/the-magical-mixture-mill-logo-173dy.png?resize=1&w=480&h=270&quality=medium',
        33);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://cdn1.epicgames.com/spt-assets/da136e41d7ef40c99ce0f8b73fb57c36/the-magical-mixture-mill-logo-173dy.png?resize=1&w=480&h=270&quality=medium',
        33);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2456350/ss_8df4dcbd56b7dc0fe8390ac2dac7c41741927183.1920x1080.jpg?t=1730176325',
        34);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2456350/header.jpg?t=1730176325',
        34);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2456350/ss_0b0ac85eb2bfe242bbb6d6609cc8a6ab701aa6d9.600x338.jpg?t=1730176325',
        34);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2456350/ss_8e5f825be4b659f1a7447506028a373bcabec429.600x338.jpg?t=1730176325',
        34);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2456350/ss_242f319ef066267c61759ece581c647276c307ea.600x338.jpg?t=1730176325',
        34);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2456350/ss_58a9bf62cf1aed1357a26ff51ed9ea93da17a631.600x338.jpg?t=1730176325',
        34);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2456350/ss_52a1c421f4ebf570448e1fd72e4597edced757ed.600x338.jpg?t=1730176325',
        34);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2456350/ss_7e94fe2f3c45d95ccd4598d72c4511c824aafb6e.600x338.jpg?t=1730176325',
        34);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('thumbnail',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2358720/ss_968bbc9caceb7d798bd0c393e1e9b4c44ed6d835.600x338.jpg?t=1732286900',
        35);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('logo', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2358720/header.jpg?t=1732286900',
        35);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p1',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2358720/ss_86c4b7462bba219a0d0b89931a35812b9f188976.600x338.jpg?t=1732286900',
        35);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p2',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2358720/ss_d9391ab31a4d15dddf7ba4949bfa44f5d9170580.600x338.jpg?t=1732286900',
        35);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p3',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2358720/ss_524a39da392ee83dde091033562bc719d46b5838.600x338.jpg?t=1732286900',
        35);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p4',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2358720/ss_968bbc9caceb7d798bd0c393e1e9b4c44ed6d835.600x338.jpg?t=1732286900',
        35);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p5',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2358720/ss_415397426d4c939ebb8a93ac66831f28ee7199be.600x338.jpg?t=1732286900',
        35);
INSERT INTO public.media (media_name, media_url, sys_id_game)
VALUES ('p6',
        'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2358720/ss_63477e8ce2c0582b81c6ed576377d78e692b5642.600x338.jpg?t=1732286900',
        35);

---voucher
truncate table voucher cascade;

ALTER TABLE voucher
    ALTER COLUMN sys_id_voucher DROP IDENTITY;

INSERT INTO public.voucher (sys_id_voucher, code_voucher, discount_name, discount_percent, start_date, end_date,
                            description, quantity, is_active, voucher_banner, max_discount)
VALUES (1, 'fallout', 'Fallout Day Sale', 50, '2024-10-20', '2024-12-31',
        'Celebrate Fallout Day with discounts on Fallout games and more', 77, true,
        'https://cdn2.unrealengine.com/falloutday-sale-2024-epic-keyart-1920x1080-01-1920x1080-1c74e27f2de9.jpg?resize=1&w=854&h=480&quality=medium',
        500000);
INSERT INTO public.voucher (sys_id_voucher, code_voucher, discount_name, discount_percent, start_date, end_date,
                            description, quantity, is_active, voucher_banner, max_discount)
VALUES (2, 'elder', 'Elder Festival', 44, '2024-10-20', '2024-12-24', 'Elder Festival for Halloween holiday', 55, true,
        'https://cdn2.unrealengine.com/egs-eso-witches-festival-2024-breaker-1920x1080-817df397bcf1.jpg?resize=1&w=854&h=480&quality=medium',
        777777);
INSERT INTO public.voucher (sys_id_voucher, code_voucher, discount_name, discount_percent, start_date, end_date,
                            description, quantity, is_active, voucher_banner, max_discount)
VALUES (3, 'about-to', 'Brand new seasons', 69, '2024-10-20', '2024-12-25', 'New nuke of voucher seasons', 65, true,
        'https://cdn2.unrealengine.com/egs-nis-america-publisher-sale-2024-breaker-1920x1080-44eaff94888a.jpg?resize=1&w=854&h=480&quality=medium',
        444444);

ALTER TABLE voucher
    ALTER COLUMN sys_id_voucher ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE game
    ALTER COLUMN sys_id_game RESTART WITH 36;

ALTER TABLE category
    ALTER COLUMN sys_id_category RESTART WITH 36;

ALTER TABLE voucher
    ALTER COLUMN sys_id_voucher RESTART WITH 4;

update media
set media_url = 'http://localhost:9999/images/35/thumbnail.jpg'
where sys_id_game = 35
  and media_name = 'thumbnail';

update media
set media_url = 'http://localhost:9999/images/34/thumbnail.png'
where sys_id_game = 34
  and media_name = 'thumbnail';

--- new
update users
set join_time = '2024-12-17 21:20:53.000000'
where sys_id_user = 1;

update users
set join_time = '2024-11-17 21:20:37.000000'
where sys_id_user = 2;
CREATE OR REPLACE FUNCTION get_user_statistics()
    RETURNS TABLE
            (
                users_this_month INT,
                percent_increase NUMERIC
            )
AS
$$
BEGIN
    RETURN QUERY
        WITH current_month AS (SELECT COUNT(*)::INT AS count
                               FROM users
                               WHERE DATE_TRUNC('month', join_time) = DATE_TRUNC('month', CURRENT_DATE)),
             previous_month AS (SELECT COUNT(*)::INT AS count
                                FROM users
                                WHERE DATE_TRUNC('month', join_time) =
                                      DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '1 month')
        SELECT current_month.count AS users_this_month,
               CASE
                   WHEN previous_month.count = 0 THEN 100
                   ELSE ROUND((current_month.count - previous_month.count) * 100.0 / previous_month.count, 1)
                   END             AS percent_increase
        FROM current_month,
             previous_month;
END;
$$ LANGUAGE plpgsql;

SELECT *
FROM get_user_statistics();

CREATE OR REPLACE FUNCTION get_transaction_statistics()
    RETURNS TABLE
            (
                transactions_this_month INT,
                percent_increase        NUMERIC
            )
AS
$$
BEGIN
    RETURN QUERY
        WITH current_month AS (SELECT COUNT(*)::INT AS count
                               FROM transaction_history
                               WHERE amount <= 0
                                 AND status = TRUE
                                 AND DATE_TRUNC('month', payment_time) = DATE_TRUNC('month', CURRENT_DATE)),
             previous_month AS (SELECT COUNT(*)::INT AS count
                                FROM transaction_history
                                WHERE amount <= 0
                                  AND status = TRUE
                                  AND DATE_TRUNC('month', payment_time) =
                                      DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '1 month')
        SELECT current_month.count AS transactions_this_month,
               CASE
                   WHEN previous_month.count = 0 THEN 100
                   ELSE ROUND((current_month.count - previous_month.count) * 100.0 / previous_month.count, 1)
                   END             AS percent_increase
        FROM current_month,
             previous_month;
END;
$$ LANGUAGE plpgsql;

SELECT *
FROM get_transaction_statistics();

CREATE OR REPLACE FUNCTION get_transaction_sums()
    RETURNS TABLE
            (
                total_negative_amount NUMERIC,
                today_negative_amount NUMERIC
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT SUM(CASE
                       WHEN amount <= 0
                           AND status = TRUE THEN amount * -1
                       ELSE 0 END)::NUMERIC AS total_negative_amount,
               SUM(CASE
                       WHEN amount <= 0
                           AND status = TRUE AND DATE_TRUNC('day', payment_time) = CURRENT_DATE THEN amount * -1
                       ELSE 0 END)::NUMERIC AS today_negative_amount
        FROM transaction_history;
END;
$$ LANGUAGE plpgsql;

SELECT *
FROM get_transaction_sums();

INSERT INTO public.transaction_history (payment_time, description, amount, status, user_name, user_balance)
VALUES ('2024-12-17 23:13:55.000000', 'Test transaction 1', -100, true, 'kaiz', 1000),
       ('2024-12-16 22:13:55.000000', 'Test transaction 2', -200, true, 'kaiz', 2000),
       ('2024-12-15 21:13:55.000000', 'Test transaction 3', -300, true, 'kaiz', 3000),
       ('2024-12-14 20:13:55.000000', 'Test transaction 4', -400, true, 'kaiz', 4000),
       ('2024-12-13 19:13:55.000000', 'Test transaction 5', -500, true, 'kaiz', 5000),
       ('2024-12-12 18:13:55.000000', 'Test transaction 6', -600, true, 'kaiz', 6000),
       ('2024-12-11 17:13:55.000000', 'Test transaction 7', -700, true, 'kaiz', 7000),
       ('2024-12-10 16:13:55.000000', 'Test transaction 8', -800, true, 'kaiz', 8000),
       ('2024-12-09 15:13:55.000000', 'Test transaction 9', -900, true, 'kaiz', 9000),
       ('2024-12-08 14:13:55.000000', 'Test transaction 10', -1000, true, 'kaiz', 10000),
       ('2024-12-07 13:13:55.000000', 'Test transaction 11', -1100, true, 'kaiz', 11000),
       ('2024-12-06 12:13:55.000000', 'Test transaction 12', -1200, true, 'kaiz', 12000),
       ('2024-12-05 11:13:55.000000', 'Test transaction 13', -1300, true, 'kaiz', 13000),
       ('2024-12-04 10:13:55.000000', 'Test transaction 14', -1400, true, 'kaiz', 14000),
       ('2024-12-03 09:13:55.000000', 'Test transaction 15', -1500, true, 'kaiz', 15000),
       ('2024-12-02 08:13:55.000000', 'Test transaction 16', -1600, true, 'kaiz', 16000),
       ('2024-12-01 07:13:55.000000', 'Test transaction 17', -1700, true, 'kaiz', 17000),
       ('2024-11-30 06:13:55.000000', 'Test transaction 18', -1800, true, 'kaiz', 18000),
       ('2024-11-29 05:13:55.000000', 'Test transaction 19', -1900, true, 'kaiz', 19000),
       ('2024-11-28 04:13:55.000000', 'Test transaction 20', -2000, true, 'kaiz', 20000),
       ('2024-11-27 03:13:55.000000', 'Test transaction 21', -2100, true, 'kaiz', 21000),
       ('2024-11-26 02:13:55.000000', 'Test transaction 22', -2200, true, 'kaiz', 22000),
       ('2024-11-25 01:13:55.000000', 'Test transaction 23', -2300, true, 'kaiz', 23000),
       ('2024-11-24 00:13:55.000000', 'Test transaction 24', -2400, true, 'kaiz', 24000),
       ('2024-11-23 23:13:55.000000', 'Test transaction 25', -2500, true, 'kaiz', 25000),
       ('2024-11-22 22:13:55.000000', 'Test transaction 26', -2600, true, 'kaiz', 26000),
       ('2024-11-21 21:13:55.000000', 'Test transaction 27', -2700, true, 'kaiz', 27000),
       ('2024-11-20 20:13:55.000000', 'Test transaction 28', -2800, true, 'kaiz', 28000),
       ('2024-11-19 19:13:55.000000', 'Test transaction 29', -2900, true, 'kaiz', 29000),
       ('2024-11-18 18:13:55.000000', 'Test transaction 30', -3000, true, 'kaiz', 30000);

drop function if exists get_transaction_summary;
CREATE OR REPLACE FUNCTION get_transaction_summary()
    RETURNS TABLE
            (
                formatted_date    TEXT,
                transaction_count INTEGER,
                new_users_count   INTEGER
            )
AS
$$
DECLARE
    start_date DATE := CURRENT_DATE + 1;
    end_date   DATE;
BEGIN
    FOR i IN 0..5
        LOOP
            end_date := start_date - INTERVAL '5 days';
            RETURN QUERY
                SELECT TO_CHAR(start_date - 1, 'Mon DD') AS formatted_date,
                       COUNT(*)::INTEGER                 AS transaction_count,
                       (SELECT COUNT(*)::INTEGER
                        FROM users
                        WHERE join_time > end_date
                          AND join_time <= start_date)   AS new_users_count
                FROM transaction_history
                WHERE amount <= 0
                  AND status = TRUE
                  AND payment_time > end_date
                  AND payment_time <= start_date;
            start_date := end_date;
        END LOOP;
END;
$$ LANGUAGE plpgsql;
-- To call the function and get the results
SELECT *
FROM get_transaction_summary();

drop table if exists chatroom;
CREATE TABLE ChatChannel
(
    id           SERIAL PRIMARY KEY,
    channel_name VARCHAR(255) unique NOT NULL, -- Tên kênh
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status       VARCHAR(50)                   -- Trạng thái kênh (ví dụ: open, closed)
);

drop table if exists message;
CREATE TABLE Message
(
    id           SERIAL PRIMARY KEY,
    channel_name VARCHAR(255),
    sender_name  VARCHAR(255), -- Username lấy từ JWT
    sender_role  VARCHAR(50),  -- Role lấy từ JWT
    content      TEXT,         -- Nội dung tin nhắn
    sent_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (channel_name) REFERENCES ChatChannel (channel_name) ON DELETE CASCADE
);