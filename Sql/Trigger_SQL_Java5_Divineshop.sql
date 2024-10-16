use Java5_DivineShop
go


CREATE TRIGGER trg_InsertRoleAfterUserInsert
ON [Account]
AFTER INSERT
AS
BEGIN
    INSERT INTO [Roles] (username, username_user, role)
    SELECT
        username AS username,
        username AS username_user,
        'ROLE_EMPLOYEE' AS role
    FROM
        inserted;
END;
GO



insert into [Account] (username, email, hash_password)
values ('chauphat','hieuphung2111@gmail.com','$2a$12$OG.FutSWkTsI02p50tItseyHrZjb1zzaiRBoTus3A6oAYYASup6fi')

SELECT * FROM [User] WHERE email = 'hieuphung2111@gmail.com';

update Roles set role = 'ROLE_ADMIN' where username = 'chauphat'