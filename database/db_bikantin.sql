-- =====================================================
-- Database BI-KANTIN (Kantin Online) - Projek PBO
-- Jalankan di phpMyAdmin (tab SQL) atau:
--   C:\xampp\mysql\bin\mysql.exe -u root < database\db_bikantin.sql
-- PERHATIAN: script ini menghapus & membuat ulang tabel.
-- =====================================================

CREATE DATABASE IF NOT EXISTS db_bikantin_pbo;
USE db_bikantin_pbo;

DROP TABLE IF EXISTS order_details;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menus;
DROP TABLE IF EXISTS users;

-- Akun pengguna (Admin, Siswa, Kasir). Hapus = soft delete (deleted_at).
CREATE TABLE users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nama        VARCHAR(100) NOT NULL,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    CHAR(64)     NOT NULL,              -- hash SHA-256
    role        ENUM('admin','siswa','kasir') NOT NULL DEFAULT 'siswa',
    status      ENUM('Aktif','Non-aktif')     NOT NULL DEFAULT 'Aktif',
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP NULL DEFAULT NULL
);

-- Menu makanan/minuman. Hapus = soft delete supaya riwayat pesanan tetap utuh.
CREATE TABLE menus (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nama        VARCHAR(100) NOT NULL,
    kategori    ENUM('Makanan','Minuman','Snack') NOT NULL,
    harga       INT NOT NULL,
    deskripsi   TEXT,
    foto        VARCHAR(255) NULL,
    tersedia    TINYINT(1) NOT NULL DEFAULT 1,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP NULL DEFAULT NULL
);

-- Pesanan (header). 1 user (siswa) punya banyak orders.
CREATE TABLE orders (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    user_id            INT NOT NULL,
    total              INT NOT NULL,
    catatan            VARCHAR(255) NULL,
    status_pesanan     ENUM('Menunggu Konfirmasi','Sedang Dimasak','Siap Diambil','Selesai')
                       NOT NULL DEFAULT 'Menunggu Konfirmasi',
    status_pembayaran  ENUM('Belum Bayar','Lunas') NOT NULL DEFAULT 'Belum Bayar',
    kasir_id           INT NULL,                    -- kasir yang memvalidasi pembayaran
    created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dibayar_at         TIMESTAMP NULL DEFAULT NULL,
    FOREIGN KEY (user_id)  REFERENCES users(id),
    FOREIGN KEY (kasir_id) REFERENCES users(id)
);

-- Detail pesanan. 1 order punya banyak order_details, 1 menu muncul di banyak order_details.
CREATE TABLE order_details (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    order_id  INT NOT NULL,
    menu_id   INT NOT NULL,
    jumlah    INT NOT NULL,
    harga     INT NOT NULL,                         -- harga saat dipesan
    subtotal  INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id)  REFERENCES menus(id)
);

-- ===== Data awal =====
-- admin / admin123, kasir / kasir123, budi / budi123
INSERT INTO users (nama, username, email, password, role) VALUES
('Administrator Kantin', 'admin', 'admin@bikantin.sch.id', SHA2('admin123', 256), 'admin'),
('Ibu Kasir',            'kasir', 'kasir@bikantin.sch.id', SHA2('kasir123', 256), 'kasir'),
('Budi Santoso',         'budi',  'budi@siswa.sch.id',     SHA2('budi123', 256),  'siswa');

INSERT INTO menus (nama, kategori, harga, deskripsi) VALUES
('Nasi Goreng Spesial', 'Makanan', 15000, 'Nasi goreng dengan telur dan ayam suwir'),
('Mie Ayam',            'Makanan', 12000, 'Mie ayam dengan pangsit'),
('Ayam Geprek',         'Makanan', 14000, 'Ayam geprek sambal bawang + nasi'),
('Es Teh Manis',        'Minuman',  4000, 'Teh manis dingin'),
('Es Jeruk',            'Minuman',  6000, 'Jeruk peras segar'),
('Risoles Mayo',        'Snack',    3000, 'Risoles isi mayones dan sosis');
