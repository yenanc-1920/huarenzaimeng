-- ============================================================
-- 华人在孟 V1.0 DDL Script
-- Compatible: MySQL 5.7+ / TDSQL-C (CynosDB)
-- Charset: utf8mb4 | Timezone: UTC
-- ============================================================

CREATE DATABASE IF NOT EXISTS huarenzaimeng DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE huarenzaimeng;

-- -----------------------------------------------------------
-- 1. t_user 用户表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    openid VARCHAR(64) NOT NULL COMMENT '微信openid',
    user_type VARCHAR(16) NOT NULL DEFAULT 'VISITOR' COMMENT '用户类型: VISITOR/MEMBER',
    nickname VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    avatar_url VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    phone VARCHAR(20) DEFAULT NULL COMMENT '绑定手机号',
    last_login_at DATETIME DEFAULT NULL COMMENT '最后登录时间(UTC)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(UTC)',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间(UTC)',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -----------------------------------------------------------
-- 2. t_user_favorite_phone 常用号码
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_user_favorite_phone (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    openid VARCHAR(64) NOT NULL COMMENT '用户openid',
    phone VARCHAR(20) NOT NULL COMMENT '手机号码',
    operator_id BIGINT UNSIGNED DEFAULT NULL COMMENT '运营商ID',
    label VARCHAR(32) DEFAULT NULL COMMENT '备注标签',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常用号码';

-- -----------------------------------------------------------
-- 3. t_operator 运营商
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_operator (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    reloadly_operator_id BIGINT NOT NULL COMMENT 'Reloadly运营商ID',
    name VARCHAR(64) NOT NULL COMMENT '运营商名称',
    logo_url VARCHAR(512) DEFAULT NULL COMMENT 'Logo URL',
    country VARCHAR(32) NOT NULL DEFAULT 'Bangladesh' COMMENT '国家',
    phone_prefix VARCHAR(128) DEFAULT NULL COMMENT '号码段前缀(逗号分隔)',
    bundle TINYINT NOT NULL DEFAULT 0 COMMENT '是否支持流量包: 0否 1是',
    fx_rate DECIMAL(12,6) DEFAULT NULL COMMENT 'USD兑BDT汇率',
    fx_rate_updated_at DATETIME DEFAULT NULL COMMENT '汇率更新时间(UTC)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0停用 1启用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_reloadly_operator_id (reloadly_operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营商';

-- -----------------------------------------------------------
-- 4. t_product 商品
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_product (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    operator_id BIGINT UNSIGNED NOT NULL COMMENT '运营商ID',
    topup_type VARCHAR(16) NOT NULL COMMENT '充值类型: AIRTIME/BUNDLE',
    reloadly_product_id BIGINT DEFAULT NULL COMMENT 'Reloadly商品ID',
    name VARCHAR(128) NOT NULL COMMENT '商品名称',
    usd_cost DECIMAL(10,4) NOT NULL COMMENT 'USD成本',
    bdt_amount DECIMAL(10,2) DEFAULT NULL COMMENT 'BDT到账金额',
    cny_price DECIMAL(10,2) NOT NULL COMMENT 'CNY售价',
    price_mode VARCHAR(8) NOT NULL DEFAULT 'AUTO' COMMENT '定价模式: AUTO/FIXED',
    loss_rate DECIMAL(5,4) NOT NULL DEFAULT 0.0300 COMMENT '损耗率',
    profit_rate DECIMAL(5,4) NOT NULL DEFAULT 0.1500 COMMENT '利润率',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0下架 1上架',
    reloadly_updated_at DATETIME DEFAULT NULL COMMENT 'Reloadly侧更新时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_operator_topup (operator_id, topup_type, usd_cost),
    KEY idx_operator_id (operator_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品';

-- -----------------------------------------------------------
-- 5. t_product_history 商品历史快照
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_product_history (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    usd_cost DECIMAL(10,4) NOT NULL COMMENT 'USD成本',
    cny_price DECIMAL(10,2) NOT NULL COMMENT 'CNY售价',
    price_mode VARCHAR(8) NOT NULL COMMENT '定价模式',
    change_reason VARCHAR(128) DEFAULT NULL COMMENT '变更原因',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品历史快照';

-- -----------------------------------------------------------
-- 6. t_order 充值订单
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    order_no VARCHAR(32) NOT NULL COMMENT '订单号(HM+时间戳+随机数)',
    openid VARCHAR(64) NOT NULL COMMENT '用户openid',
    phone VARCHAR(20) NOT NULL COMMENT '充值手机号',
    operator_id BIGINT UNSIGNED NOT NULL COMMENT '运营商ID',
    product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    topup_type VARCHAR(16) NOT NULL COMMENT '充值类型',
    usd_cost DECIMAL(10,4) NOT NULL COMMENT 'USD成本',
    bdt_amount DECIMAL(10,2) DEFAULT NULL COMMENT 'BDT到账金额',
    cny_price DECIMAL(10,2) NOT NULL COMMENT 'CNY售价(用户实付)',
    order_status VARCHAR(20) NOT NULL DEFAULT 'PENDING_PAY' COMMENT '订单状态',
    status_desc VARCHAR(64) DEFAULT NULL COMMENT '异常子类型描述',
    mnp_verified TINYINT NOT NULL DEFAULT 1 COMMENT 'MNP校验: 0未校验 1已校验',
    wx_transaction_id VARCHAR(64) DEFAULT NULL COMMENT '微信支付交易号',
    wx_prepay_id VARCHAR(128) DEFAULT NULL COMMENT '微信预支付ID',
    reloadly_transaction_id BIGINT DEFAULT NULL COMMENT 'Reloadly交易ID',
    paid_at DATETIME DEFAULT NULL COMMENT '支付时间(UTC)',
    charged_at DATETIME DEFAULT NULL COMMENT '充值完成时间(UTC)',
    retry_count INT NOT NULL DEFAULT 0 COMMENT '重试次数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_openid (openid),
    KEY idx_phone (phone),
    KEY idx_status (order_status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值订单';

-- -----------------------------------------------------------
-- 7. t_refund 退款记录
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_refund (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    refund_no VARCHAR(32) NOT NULL COMMENT '退款单号',
    order_no VARCHAR(32) NOT NULL COMMENT '关联订单号',
    openid VARCHAR(64) NOT NULL COMMENT '用户openid',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额(CNY)',
    refund_type VARCHAR(16) NOT NULL COMMENT '退款类型: AUTO/MANUAL/PRE_FUNDED',
    refund_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '退款状态',
    wx_refund_id VARCHAR(64) DEFAULT NULL COMMENT '微信退款单号',
    reason VARCHAR(256) DEFAULT NULL COMMENT '退款原因',
    operator_id BIGINT UNSIGNED DEFAULT NULL COMMENT '操作管理员ID',
    refunded_at DATETIME DEFAULT NULL COMMENT '退款完成时间(UTC)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_refund_no (refund_no),
    KEY idx_order_no (order_no),
    KEY idx_openid (openid),
    KEY idx_refund_status (refund_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录';

-- -----------------------------------------------------------
-- 8. t_news 资讯
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_news (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    title VARCHAR(128) NOT NULL COMMENT '标题',
    summary VARCHAR(256) DEFAULT NULL COMMENT '摘要',
    content TEXT COMMENT '正文(HTML)',
    cover_url VARCHAR(512) DEFAULT NULL COMMENT '封面图URL',
    images TEXT DEFAULT NULL COMMENT '图片列表(JSON数组)',
    audit_status VARCHAR(16) NOT NULL DEFAULT 'PASS' COMMENT '审核状态: PENDING/PASS/REJECT',
    is_top TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶: 0否 1是',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0下架 1发布',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    published_at DATETIME DEFAULT NULL COMMENT '发布时间(UTC)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_status_top (status, is_top),
    KEY idx_published_at (published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯';

-- -----------------------------------------------------------
-- 9. t_company 企业黄页
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_company (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT '企业名称',
    category VARCHAR(32) NOT NULL COMMENT '分类: FOOD/LOGISTICS/SERVICES/OTHER',
    description TEXT DEFAULT NULL COMMENT '简介',
    logo_url VARCHAR(512) DEFAULT NULL COMMENT 'Logo URL',
    logo_audit_status VARCHAR(16) NOT NULL DEFAULT 'PASS' COMMENT 'Logo审核状态',
    phone VARCHAR(32) DEFAULT NULL COMMENT '联系电话',
    wechat VARCHAR(64) DEFAULT NULL COMMENT '微信号',
    address VARCHAR(256) DEFAULT NULL COMMENT '地址',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0下架 1展示',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_category (category),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业黄页';

-- -----------------------------------------------------------
-- 10. t_holiday 节假日
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_holiday (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL COMMENT '节假日名称',
    name_en VARCHAR(64) DEFAULT NULL COMMENT '英文名称',
    holiday_date DATE NOT NULL COMMENT '日期',
    confirm_status VARCHAR(16) NOT NULL DEFAULT 'CONFIRMED' COMMENT '确认状态: CONFIRMED/PENDING_MOON',
    description VARCHAR(256) DEFAULT NULL COMMENT '说明',
    year INT NOT NULL COMMENT '年份',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_year_date (year, holiday_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节假日';

-- -----------------------------------------------------------
-- 11. t_sys_config 系统配置
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_sys_config (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    config_key VARCHAR(64) NOT NULL COMMENT '配置键',
    config_value VARCHAR(1024) NOT NULL COMMENT '配置值',
    value_type VARCHAR(16) NOT NULL DEFAULT 'STRING' COMMENT '值类型: STRING/NUMBER/BOOLEAN/JSON',
    description VARCHAR(128) DEFAULT NULL COMMENT '说明',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置';

-- -----------------------------------------------------------
-- 12. t_admin 管理员
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS t_admin (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    username VARCHAR(32) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码(BCrypt)',
    role VARCHAR(16) NOT NULL DEFAULT 'ADMIN' COMMENT '角色: ADMIN(预留多角色)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    last_login_at DATETIME DEFAULT NULL COMMENT '最后登录时间(UTC)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员';
