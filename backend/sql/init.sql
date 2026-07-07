CREATE DATABASE IF NOT EXISTS travelai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE travelai;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色（ADMIN/USER）',
    status INT NOT NULL DEFAULT 1 COMMENT '状态（0禁用/1启用）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '行程ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '行程名称',
    destination VARCHAR(100) NOT NULL COMMENT '目的地',
    budget DECIMAL(10,2) NOT NULL COMMENT '预算金额',
    days INT NOT NULL COMMENT '旅行天数',
    start_date DATE COMMENT '出发日期',
    description TEXT COMMENT '行程描述',
    status INT NOT NULL DEFAULT 0 COMMENT '状态（0草稿/1已发布/2已完成）',
    share_token VARCHAR(64) UNIQUE COMMENT '分享Token',
    share_status INT NOT NULL DEFAULT 0 COMMENT '分享状态（0私密/1公开）',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行程表';

CREATE TABLE IF NOT EXISTS trip_days (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '每日行程ID',
    trip_id BIGINT NOT NULL COMMENT '行程ID',
    day_number INT NOT NULL COMMENT '第几天',
    summary VARCHAR(200) COMMENT '当日总结',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_trip_id (trip_id),
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日行程表';

CREATE TABLE IF NOT EXISTS trip_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '活动ID',
    trip_day_id BIGINT NOT NULL COMMENT '每日行程ID',
    type VARCHAR(20) NOT NULL COMMENT '类型（MORNING/AFTERNOON/EVENING）',
    title VARCHAR(100) NOT NULL COMMENT '活动标题',
    location VARCHAR(200) COMMENT '地点',
    duration VARCHAR(50) COMMENT '预计时长',
    ticket_price DECIMAL(8,2) COMMENT '门票价格',
    transportation VARCHAR(200) COMMENT '交通方式',
    description TEXT COMMENT '详细描述',
    budget DECIMAL(8,2) COMMENT '预算',
    INDEX idx_trip_day_id (trip_day_id),
    FOREIGN KEY (trip_day_id) REFERENCES trip_days(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行程活动表';

CREATE TABLE IF NOT EXISTS trip_budget (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预算明细ID',
    trip_id BIGINT NOT NULL COMMENT '行程ID',
    category VARCHAR(20) NOT NULL COMMENT '类别（住宿/餐饮/交通/门票/其他）',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    description VARCHAR(200) COMMENT '说明',
    INDEX idx_trip_id (trip_id),
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预算明细表';

CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '对话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) COMMENT '对话标题',
    context TEXT COMMENT '对话上下文',
    status INT NOT NULL DEFAULT 1 COMMENT '状态（0结束/1进行中）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话记录表';

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    conversation_id BIGINT NOT NULL COMMENT '对话ID',
    sender VARCHAR(20) NOT NULL COMMENT '发送者（USER/AI）',
    content TEXT NOT NULL COMMENT '消息内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_conversation_id (conversation_id),
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息记录表';

INSERT INTO users (username, password, email, phone, role, status) VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', 'admin@travelai.com', '13800138000', 'ADMIN', 1);