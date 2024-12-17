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
    RETURNS TABLE (
        total_negative_amount NUMERIC,
        today_negative_amount NUMERIC
    )
AS
$$
BEGIN
    RETURN QUERY
    SELECT
        SUM(CASE WHEN amount <= 0
            AND status = TRUE THEN amount * -1 ELSE 0 END)::NUMERIC AS total_negative_amount,
        SUM(CASE WHEN amount <= 0
            AND status = TRUE AND DATE_TRUNC('day', payment_time) = CURRENT_DATE THEN amount * -1 ELSE 0 END)::NUMERIC AS today_negative_amount
    FROM
        transaction_history;
END;
$$ LANGUAGE plpgsql;

SELECT *
FROM get_transaction_sums();

INSERT INTO public.transaction_history (payment_time, description, amount, status, user_name, user_balance) VALUES
('2024-12-17 23:13:55.000000', 'Test transaction 1', -100, true, 'kaiz', 1000),
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
    RETURNS TABLE (
        formatted_date TEXT,
        transaction_count INTEGER,
        new_users_count INTEGER
    )
AS
$$
DECLARE
    start_date DATE := CURRENT_DATE + 1;
    end_date DATE;
BEGIN
    FOR i IN 0..5 LOOP
        end_date := start_date - INTERVAL '5 days';
        RETURN QUERY
        SELECT
            TO_CHAR(start_date - 1, 'Mon DD') AS formatted_date,
            COUNT(*)::INTEGER AS transaction_count,
            (SELECT COUNT(*)::INTEGER
             FROM users
             WHERE join_time > end_date
             AND join_time <= start_date) AS new_users_count
        FROM
            transaction_history
        WHERE
            amount <= 0
            AND status = TRUE
            AND payment_time > end_date
            AND payment_time <= start_date;
        start_date := end_date;
    END LOOP;
END;
$$ LANGUAGE plpgsql;
-- To call the function and get the results
SELECT * FROM get_transaction_summary();

drop table if exists chatroom;
CREATE TABLE ChatChannel (
                             id SERIAL PRIMARY KEY,
                             channel_name VARCHAR(255) unique NOT NULL, -- Tên kênh
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             status VARCHAR(50) -- Trạng thái kênh (ví dụ: open, closed)
);

drop table if exists message;
CREATE TABLE Message (
                         id SERIAL PRIMARY KEY,
                         channel_name VARCHAR(255) ,
                         sender_name VARCHAR(255), -- Username lấy từ JWT
                         sender_role VARCHAR(50), -- Role lấy từ JWT
                         content TEXT , -- Nội dung tin nhắn
                         sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (channel_name) REFERENCES ChatChannel(channel_name) ON DELETE CASCADE
);