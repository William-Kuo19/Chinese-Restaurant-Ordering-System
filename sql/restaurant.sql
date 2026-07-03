-- 中式餐館點餐管理系統 Database V4
-- MySQL 8.0

DROP DATABASE IF EXISTS ordersystem;
CREATE DATABASE ordersystem
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE ordersystem;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS order_detail;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS member;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE member (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(100),
    role ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(30) NOT NULL UNIQUE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(80) NOT NULL,
    category_id INT NOT NULL,
    price INT NOT NULL,
    description VARCHAR(255),
    image VARCHAR(255),
    status ENUM('販售中','停售') NOT NULL DEFAULT '販售中',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(category_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_product_category (category_id),
    INDEX idx_product_name (product_name),
    INDEX idx_product_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(30) NOT NULL UNIQUE,
    member_id INT NOT NULL,
    dine_type ENUM('內用','外帶') NOT NULL DEFAULT '內用',
    table_no VARCHAR(20),
    remark VARCHAR(255),
    total INT NOT NULL DEFAULT 0,
    status ENUM('待製作','製作中','已完成','已取消') NOT NULL DEFAULT '待製作',
    order_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_member FOREIGN KEY (member_id) REFERENCES member(member_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_orders_member (member_id),
    INDEX idx_orders_order_time (order_time),
    INDEX idx_orders_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_detail (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    qty INT NOT NULL,
    price INT NOT NULL,
    subtotal INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_detail_order FOREIGN KEY (order_id) REFERENCES orders(order_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_detail_product FOREIGN KEY (product_id) REFERENCES product(product_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_detail_order (order_id),
    INDEX idx_detail_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO member (username, password, name, phone, email, role) VALUES
('admin', '1234', '系統管理員', '0900000000', 'admin@example.com', 'ADMIN'),
('user', '1234', '一般會員', '0911111111', 'user@example.com', 'USER');

INSERT INTO category (category_name, sort_order) VALUES
('主食', 1),
('麵類', 2),
('小菜', 3),
('湯品', 4),
('飲料', 5),
('甜點', 6),
('套餐', 7);

INSERT INTO product (product_name, category_id, price, description, image, status) VALUES
-- 主食
('滷肉飯', 1, 45, '經典台式滷肉飯', 'foods/lu_rou_fan.png', '販售中'),
('雞肉飯', 1, 50, '香嫩雞肉飯', 'foods/ji_rou_fan.png', '販售中'),
('爌肉飯', 1, 80, '肥瘦適中的爌肉飯', 'foods/kong_rou_fan.png', '販售中'),
('排骨飯', 1, 95, '酥炸排骨飯', 'foods/pai_gu_fan.png', '販售中'),
('雞腿飯', 1, 110, '大雞腿便當', 'foods/ji_tui_fan.png', '販售中'),
('三杯雞飯', 1, 120, '九層塔三杯雞飯', 'foods/san_bei_ji_fan.png', '販售中'),
('宮保雞丁飯', 1, 110, '微辣宮保雞丁飯', 'foods/gong_bao_ji_ding_fan.png', '販售中'),
('咖哩雞飯', 1, 105, '濃郁咖哩雞飯', 'foods/curry_chicken_rice.png', '販售中'),
('牛肉燴飯', 1, 120, '滑順牛肉燴飯', 'foods/beef_rice.png', '販售中'),
('蝦仁炒飯', 1, 100, '香氣十足蝦仁炒飯', 'foods/shrimp_fried_rice.png', '販售中'),
('火腿炒飯', 1, 85, '經典火腿炒飯', 'foods/ham_fried_rice.png', '販售中'),
('牛肉炒飯', 1, 110, '牛肉香炒飯', 'foods/beef_fried_rice.png', '販售中'),
-- 麵類
('牛肉麵', 2, 140, '招牌牛肉麵', 'foods/beef_noodle.png', '販售中'),
('紅燒牛肉麵', 2, 150, '紅燒湯頭牛肉麵', 'foods/braised_beef_noodle.png', '販售中'),
('榨菜肉絲麵', 2, 85, '清爽榨菜肉絲麵', 'foods/pork_noodle.png', '販售中'),
('陽春麵', 2, 55, '簡單美味陽春麵', 'foods/plain_noodle.png', '販售中'),
('麻醬麵', 2, 65, '濃香麻醬麵', 'foods/sesame_noodle.png', '販售中'),
('乾拌麵', 2, 70, '家常乾拌麵', 'foods/dry_noodle.png', '販售中'),
('鍋燒意麵', 2, 90, '豐富配料鍋燒意麵', 'foods/pot_noodle.png', '販售中'),
('烏龍麵', 2, 90, 'Q彈烏龍麵', 'foods/udon.png', '販售中'),
('海鮮烏龍麵', 2, 130, '海鮮風味烏龍麵', 'foods/seafood_udon.png', '販售中'),
('餛飩麵', 2, 85, '鮮肉餛飩麵', 'foods/wonton_noodle.png', '販售中'),
-- 小菜
('滷蛋', 3, 15, '入味滷蛋', 'foods/braised_egg.png', '販售中'),
('豆干', 3, 20, '滷味豆干', 'foods/tofu_dry.png', '販售中'),
('海帶', 3, 20, '滷味海帶', 'foods/kelp.png', '販售中'),
('滷味拼盤', 3, 60, '綜合滷味拼盤', 'foods/braised_combo.png', '販售中'),
('燙青菜', 3, 40, '時令燙青菜', 'foods/vegetable.png', '販售中'),
('涼拌小黃瓜', 3, 40, '清爽涼拌小黃瓜', 'foods/cucumber.png', '販售中'),
('泡菜', 3, 35, '酸辣泡菜', 'foods/kimchi.png', '販售中'),
('皮蛋豆腐', 3, 50, '經典皮蛋豆腐', 'foods/preserved_egg_tofu.png', '販售中'),
('水餃', 3, 70, '手工水餃10顆', 'foods/dumplings.png', '販售中'),
('蔥油餅', 3, 45, '香酥蔥油餅', 'foods/scallion_pancake.png', '販售中'),
-- 湯品
('貢丸湯', 4, 35, '新竹貢丸湯', 'foods/meatball_soup.png', '販售中'),
('魚丸湯', 4, 35, '鮮甜魚丸湯', 'foods/fishball_soup.png', '販售中'),
('味噌湯', 4, 30, '日式味噌湯', 'foods/miso_soup.png', '販售中'),
('紫菜蛋花湯', 4, 35, '紫菜蛋花湯', 'foods/seaweed_egg_soup.png', '販售中'),
('酸辣湯', 4, 45, '酸辣開胃湯', 'foods/hot_sour_soup.png', '販售中'),
('玉米濃湯', 4, 45, '香甜玉米濃湯', 'foods/corn_soup.png', '販售中'),
('蛤蜊湯', 4, 60, '鮮味蛤蜊湯', 'foods/clam_soup.png', '販售中'),
-- 飲料
('紅茶', 5, 30, '冰紅茶', 'foods/black_tea.png', '販售中'),
('綠茶', 5, 30, '冰綠茶', 'foods/green_tea.png', '販售中'),
('奶茶', 5, 40, '香濃奶茶', 'foods/milk_tea.png', '販售中'),
('冬瓜茶', 5, 35, '古早味冬瓜茶', 'foods/winter_melon_tea.png', '販售中'),
('烏梅汁', 5, 40, '酸甜烏梅汁', 'foods/plum_juice.png', '販售中'),
('檸檬紅茶', 5, 45, '檸檬紅茶', 'foods/lemon_black_tea.png', '販售中'),
('百香綠茶', 5, 45, '百香果綠茶', 'foods/passion_green_tea.png', '販售中'),
('可樂', 5, 35, '冰可樂', 'foods/cola.png', '販售中'),
('雪碧', 5, 35, '冰雪碧', 'foods/sprite.png', '販售中'),
('礦泉水', 5, 20, '瓶裝水', 'foods/water.png', '販售中'),
-- 甜點
('豆花', 6, 45, '傳統豆花', 'foods/douhua.png', '販售中'),
('愛玉', 6, 40, '清爽愛玉', 'foods/aiyu.png', '販售中'),
('紅豆湯', 6, 50, '暖心紅豆湯', 'foods/red_bean_soup.png', '販售中'),
('綠豆湯', 6, 45, '消暑綠豆湯', 'foods/mung_bean_soup.png', '販售中'),
('仙草蜜', 6, 45, '仙草蜜', 'foods/grass_jelly.png', '販售中'),
('芝麻湯圓', 6, 55, '芝麻湯圓', 'foods/tangyuan.png', '販售中'),
-- 套餐（v0.9.3：先以一般商品方式管理，套餐內容寫在 description）
('招牌雞腿套餐', 7, 160, '雞腿飯＋燙青菜＋紅茶', 'foods/set_signature_chicken_leg.png', '販售中'),
('經典排骨套餐', 7, 170, '排骨飯＋滷蛋＋蛋花湯', 'foods/set_classic_pork_rib.png', '販售中'),
('牛肉麵套餐', 7, 185, '牛肉麵＋海帶＋奶茶', 'foods/set_beef_noodle.png', '販售中'),
('滷肉飯套餐', 7, 145, '滷肉飯＋燙青菜＋貢丸湯', 'foods/set_lu_rou_fan.png', '販售中'),
('雞肉飯套餐', 7, 150, '雞肉飯＋豆干＋紅茶', 'foods/set_chicken_rice.png', '販售中'),
('火腿炒飯套餐', 7, 165, '火腿炒飯＋滷蛋＋綠茶', 'foods/set_ham_fried_rice.png', '販售中'),
('牛肉炒飯套餐', 7, 185, '牛肉炒飯＋燙青菜＋酸辣湯', 'foods/set_beef_fried_rice.png', '販售中'),
('水餃套餐', 7, 175, '水餃(10顆)＋燙青菜＋酸辣湯', 'foods/set_dumplings.png', '販售中'),
('蔥油餅套餐', 7, 150, '蔥油餅＋滷蛋＋奶茶', 'foods/set_scallion_pancake.png', '販售中'),
('素食套餐', 7, 170, '香菇燴飯＋燙青菜＋玉米濃湯', 'foods/set_vegetarian.png', '販售中'),
('商務套餐', 7, 185, '排骨飯＋皮蛋豆腐＋可樂', 'foods/set_business.png', '販售中'),
('豪華牛肉麵套餐', 7, 220, '牛肉麵＋燙青菜＋滷蛋＋奶茶', 'foods/set_deluxe_beef_noodle.png', '販售中');

INSERT INTO orders (order_no, member_id, dine_type, table_no, remark, total, status, order_time) VALUES
('ORD202607020001', 1, '內用', 'A03', '少辣', 185, '已完成', NOW()),
('ORD202607020002', 2, '外帶', NULL, '不要香菜', 170, '已完成', NOW()),
('ORD202607020003', 1, '內用', 'B05', NULL, 145, '待製作', NOW());

INSERT INTO order_detail (order_id, product_id, qty, price, subtotal) VALUES
(1, 1, 1, 45, 45),
(1, 13, 1, 140, 140),
(2, 57, 1, 170, 170),
(3, 59, 1, 145, 145);

-- 驗證用查詢
-- SELECT username, password, role FROM member;
-- SELECT c.category_name, COUNT(*) product_count FROM category c LEFT JOIN product p ON c.category_id = p.category_id GROUP BY c.category_id, c.category_name ORDER BY c.sort_order;
-- SELECT COUNT(*) AS total_products FROM product;
