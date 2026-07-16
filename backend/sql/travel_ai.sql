CREATE DATABASE IF NOT EXISTS travel_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE travel_ai;

CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键自增',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号，唯一',
    password VARCHAR(100) NOT NULL COMMENT '加密密码',
    nickname VARCHAR(50) DEFAULT '用户' COMMENT '用户昵称',
    avatar VARCHAR(255) DEFAULT '' COMMENT '头像图片地址',
    phone VARCHAR(11) DEFAULT '' COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '0 禁用 1 正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `scenic` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    scenic_name VARCHAR(100) NOT NULL COMMENT '景点名称',
    address VARCHAR(200) DEFAULT '' COMMENT '详细地址（高德地图定位）',
    longitude DECIMAL(10,7) DEFAULT 0 COMMENT '经度',
    latitude DECIMAL(10,7) DEFAULT 0 COMMENT '纬度',
    category VARCHAR(50) DEFAULT '' COMMENT '分类：山水/古镇/乐园/人文/美食',
    intro TEXT DEFAULT '' COMMENT '景点简介',
    price DECIMAL(10,2) DEFAULT 0 COMMENT '门票价格',
    open_time VARCHAR(100) DEFAULT '' COMMENT '开放时间',
    img_urls TEXT DEFAULT '' COMMENT '多张实地图片URL，逗号分隔',
    hot INT DEFAULT 0 COMMENT '热度值（排序）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
    INDEX idx_category (category),
    INDEX idx_hot (hot)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点表';

CREATE TABLE IF NOT EXISTS `scenic_comment` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '评论用户ID，关联user表',
    scenic_id BIGINT NOT NULL COMMENT '评论所属景点ID，关联scenic表',
    content TEXT NOT NULL COMMENT '评论内容',
    like_num INT DEFAULT 0 COMMENT '点赞数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    INDEX idx_user_id (user_id),
    INDEX idx_scenic_id (scenic_id),
    FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
    FOREIGN KEY (scenic_id) REFERENCES scenic(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户评论表';

CREATE TABLE IF NOT EXISTS `travel_strategy` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '生成攻略的用户ID',
    start_point VARCHAR(200) DEFAULT '' COMMENT '出发起点',
    end_point VARCHAR(200) NOT NULL COMMENT '目的地',
    travel_days INT DEFAULT 3 COMMENT '出行天数',
    budget VARCHAR(50) DEFAULT '' COMMENT '预算档位',
    preference VARCHAR(200) DEFAULT '' COMMENT '用户偏好',
    ai_content LONGTEXT DEFAULT '' COMMENT 'AI生成完整旅游方案/攻略',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI旅游攻略表';

INSERT INTO `user` (username, password, nickname, status) VALUES 
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '管理员', 1),
('test', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '测试用户', 1);

INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES
('张家界天门山', '湖南省张家界市永定区天门山索道下站', 110.4739, 29.1146, '山水', '天门山是张家界永定区海拔最高的山，因自然奇观天门洞而得名。天门洞南北对穿，门高131.5米，宽57米，深60米，拔地依天，宛若一道通天的门户。', 278, '08:00-18:00', 'https://images.unsplash.com/photo-1510771463146-e89e6e86560e?w=800,https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800,https://images.unsplash.com/photo-1534081333815-ae5019106622?w=800', 100),
('丽江古城', '云南省丽江市古城区', 100.2345, 26.8604, '古镇', '丽江古城是中国历史文化名城，世界文化遗产。古城内街道依山傍水修建，以红色角砾岩铺就，有四方街、木府、五凤楼等景点。', 0, '全天开放', 'https://images.unsplash.com/photo-1503919545889-4868ce80f31e?w=800,https://images.unsplash.com/photo-1523413651479-597eb2da0ad6?w=800,https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=800', 98),
('上海迪士尼乐园', '上海市浦东新区川沙新镇', 121.7892, 31.1433, '乐园', '上海迪士尼乐园是中国内地首座迪士尼主题乐园，包含米奇大街、奇想花园、梦幻世界、探险岛、宝藏湾、明日世界和玩具总动员等七大主题园区。', 719, '09:00-21:00', 'https://images.unsplash.com/photo-1534430480872-3498386e7856?w=800,https://images.unsplash.com/photo-1523374339868-249d0b37e5bb?w=800,https://images.unsplash.com/photo-1542831371-29b0f74f9713?w=800', 95),
('故宫博物院', '北京市东城区景山前街4号', 116.3972, 39.9163, '人文', '故宫博物院是中国明清两代的皇家宫殿，旧称紫禁城，位于北京中轴线的中心。是世界上现存规模最大、保存最为完整的木质结构古建筑群。', 60, '08:30-17:00', 'https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=800,https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=800,https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800', 99),
('西湖', '浙江省杭州市西湖区', 120.1551, 30.2741, '山水', '西湖是中国著名的风景名胜区，被誉为人间天堂。西湖十景包括苏堤春晓、断桥残雪、曲院风荷、花港观鱼等著名景点。', 0, '全天开放', 'https://images.unsplash.com/photo-1559494069-d894196f37ea?w=800,https://images.unsplash.com/photo-1544547593-a572a8821506?w=800,https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800', 97),
('成都宽窄巷子', '四川省成都市青羊区', 104.0638, 30.6637, '古镇', '宽窄巷子是成都遗留下来的较成规模的清朝古街道，由宽巷子、窄巷子和井巷子三条平行排列的城市老式街道及其之间的四合院群落组成。', 0, '全天开放', 'https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=800,https://images.unsplash.com/photo-1532293870145-7e994195d0d5?w=800,https://images.unsplash.com/photo-1484980972926-edee96e0960d?w=800', 90),
('三亚亚龙湾', '海南省三亚市吉阳区', 109.5132, 18.2587, '山水', '亚龙湾是中国最南端的滨海旅游度假区，与“天涯海角”齐名，享有“天下第一湾”之美誉。这里的海水清澈见底，可以清晰地看见珊瑚。', 免费, '全天开放', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800,https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=800,https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800', 96),
('兵马俑', '陕西省西安市临潼区', 109.2763, 34.3853, '人文', '秦始皇兵马俑是世界第八大奇迹，是秦始皇陵的陪葬坑。兵马俑坑规模宏大，三个坑共约有8000余件陶俑、陶马。', 120, '08:30-18:00', 'https://images.unsplash.com/photo-1512641406448-6574e777bec6?w=800,https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=800,https://images.unsplash.com/photo-1512641406448-6574e777bec6?w=800', 94);