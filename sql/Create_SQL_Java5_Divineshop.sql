USE master;
GO

ALTER DATABASE Java5_DivineShop SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
DROP DATABASE IF EXISTS Java5_DivineShop;
GO

CREATE DATABASE Java5_DivineShop;
GO

USE Java5_DivineShop;
GO

-- Tạo bảng Account
Create table [Account](
    id              int IDENTITY (1,1) PRIMARY KEY,
    username        varchar(50) NOT NULL UNIQUE,
    email           varchar(255) NOT NULL UNIQUE,
    hash_password		varchar(255) NOT NULL,
    is_enabled			bit DEFAULT 1
)

Create table [User](
    Sys_id_user int IDENTITY (1,1) primary key,
    ten_dang_nhap nvarchar(255) NOT NULL UNIQUE,
    email nvarchar(255) NOT NULL UNIQUE,
    ho_va_ten nvarchar(255),
    so_du nvarchar(255) DEFAULT 0,
    ngay_tham_gia date,
    anh_dai_dien nvarchar(max)
)

Create table [Roles](
    id_role int IDENTITY (1,1) primary key,
    username varchar(50) NOT NULL  ,
    username_user nvarchar(255) NOT NULL  ,
    role varchar(50) NOT NULL  ,
    FOREIGN KEY (username) REFERENCES account(username),
    FOREIGN KEY (username_user) REFERENCES [User](ten_dang_nhap)
)

-- Tạo bảng Discount
CREATE TABLE [Voucher] (
    Sys_id_voucher INT IDENTITY(1,1) PRIMARY KEY,
    code_voucher NVARCHAR(8) UNIQUE,
    ten_giam_gia NVARCHAR(255),
    percent_giam_gia FLOAT,
    ngay_bat_dau DATE,
    ngay_ket_thuc DATE,
    mota NVARCHAR(255)
);

CREATE TABLE [voucher_used] (
    Sys_id_voucher_used INT IDENTITY(1,1) PRIMARY KEY,
    Sys_id_user INT NOT NULL,
    Sys_id_voucher INT NOT NULL,
    ngay_su_dung DATE NOT NULL,
    FOREIGN KEY (Sys_id_user) REFERENCES [User](Sys_id_user),
    FOREIGN KEY (Sys_id_voucher) REFERENCES [voucher](Sys_id_voucher)
);

-- Tạo bảng Category
CREATE TABLE [Category] (
    Sys_id_category INT IDENTITY(1,1) PRIMARY KEY,
    ten_the_loai NVARCHAR(MAX)
);

-- Tạo bảng Product
CREATE TABLE [Product] (
    Sys_id_product INT IDENTITY(1,1) PRIMARY KEY,
    ma_san_pham NVARCHAR(255),
    ten_san_pham NVARCHAR(255),
    tinh_trang BIT,
    the_loai NVARCHAR(255),
    gia_san_pham FLOAT,
    percent_giam_gia FLOAT,
    anh_san_pham NVARCHAR(255),
    slug NVARCHAR(255) UNIQUE NOT NULL,
    danh_muc NVARCHAR(255),
    mota NVARCHAR(MAX),
    active_san_pham BIT DEFAULT 1,
    Sys_id_discount INT,
    soluong int,
    soluongmua int,
    soluotthich int
    FOREIGN KEY (Sys_id_discount) REFERENCES [voucher](Sys_id_voucher)    
);

-- Tạo bảng CategoryDetail
CREATE TABLE [category_detail] (
    Sys_id_category INT NOT NULL,
    Sys_id_product INT NOT NULL,
    FOREIGN KEY (Sys_id_category) REFERENCES [Category] (Sys_id_category),
    FOREIGN KEY (Sys_id_product) REFERENCES [Product] (Sys_id_product),
    PRIMARY KEY (Sys_id_category, Sys_id_product)
);

-- Tạo bảng Order
CREATE TABLE [Order] (
    Sys_id_cart_payment INT IDENTITY(1,1) PRIMARY KEY,
    ma_don_hang NVARCHAR(255),
    ngay_lap_don DATE,
    trang_thai_thanh_toan BIT,
    tong_tien_san_pham FLOAT,
    tien_thanh_toan FLOAT,
    so_luong_mua INT,
    Sys_id_product INT,
    Sys_id_user INT,
    FOREIGN KEY (Sys_id_product) REFERENCES [Product] (Sys_id_product),
    FOREIGN KEY (Sys_id_user) REFERENCES [User] (Sys_id_user)
);
Go
Create table [Payment](
    Sys_id_payment int IDENTITY (1,1) PRIMARY KEY ,
    thoi_gian DATE,
    mota nvarchar(255),
    sotien float,
    trangthai bit default 0,
    tenuser nvarchar(255) not null ,
    FOREIGN KEY (tenuser) REFERENCES [User] (ten_dang_nhap)
)
GO

-- Tạo trigger trg_InsertUserAfterAccountInsert
CREATE TRIGGER trg_InsertUserAfterAccountInsert
ON [Account]
AFTER INSERT
AS
BEGIN
    INSERT INTO [User] (ten_dang_nhap, email, ho_va_ten, so_du, ngay_tham_gia, anh_dai_dien)
    SELECT
        username AS ten_dang_nhap,
        email,
        NULL AS ho_va_ten,
        NULL AS so_du,
        GETDATE() AS ngay_tham_gia,
        'anh-khach-hanggg.png' AS anh_dai_dien
    FROM
        inserted;
END;
GO


-- Tạo bảng Comment
CREATE TABLE [Comment] (
    Sys_id_comment INT IDENTITY(1,1) PRIMARY KEY,
    noi_dung NVARCHAR(255) NOT NULL,
    ngay_binh_luan DATE NOT NULL DEFAULT GETDATE(),
    Sys_id_user INT NOT NULL,
    Sys_id_product INT NOT NULL,
    FOREIGN KEY (Sys_id_user) REFERENCES [User](Sys_id_user),
    FOREIGN KEY (Sys_id_product) REFERENCES [Product](Sys_id_product)
);

-- Tạo bảng Reply
CREATE TABLE [Reply] (
    Sys_id_reply INT IDENTITY(1,1) PRIMARY KEY,
    noi_dung NVARCHAR(MAX) NOT NULL,
    ngay_tra_loi DATE NOT NULL DEFAULT GETDATE(),
    Sys_id_user INT NOT NULL,
    Sys_id_comment INT NOT NULL,
    FOREIGN KEY (Sys_id_user) REFERENCES [User](Sys_id_user),
    FOREIGN KEY (Sys_id_comment) REFERENCES [Comment](Sys_id_comment)
);



INSERT INTO [Comment] (noi_dung, ngay_binh_luan, Sys_id_user, Sys_id_product) VALUES
(N'Bình luận về sản phẩm 1', GETDATE(), 1, 1),
(N'Bình luận về sản phẩm 2', GETDATE(), 2, 2);

INSERT INTO [Reply] (noi_dung, ngay_tra_loi, Sys_id_user, Sys_id_comment) VALUES
(N'Trả lời bình luận 1 bởi user3', GETDATE(), 1, 1),
(N'Trả lời bình luận 1 bởi user4', GETDATE(), 2, 1),
(N'Trả lời bình luận 1 bởi user5', GETDATE(), 3, 1),
(N'Trả lời bình luận 1 bởi user6', GETDATE(), 4, 1),
(N'Trả lời bình luận 1 bởi user10', GETDATE(), 1, 1),
(N'Trả lời bình luận 1 bởi user11', GETDATE(), 3, 1),
(N'Trả lời bình luận 1 bởi user12', GETDATE(), 2, 1),
(N'Trả lời bình luận 2 bởi user13', GETDATE(), 4, 2),
(N'Trả lời bình luận 2 bởi user14', GETDATE(), 4, 2),
(N'Trả lời bình luận 2 bởi user19', GETDATE(), 2, 2),
(N'Trả lời bình luận 2 bởi user20', GETDATE(), 2, 2),
(N'Trả lời bình luận 2 bởi user21', GETDATE(), 4, 2);