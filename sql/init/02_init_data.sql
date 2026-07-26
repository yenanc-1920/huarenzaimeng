-- ============================================================
-- 华人在孟 V1.0 初始化数据
-- 执行前提：01_create_tables.sql 已执行
-- ============================================================

USE huarenzaimeng;

-- -----------------------------------------------------------
-- 管理员账号 (admin / admin123)
-- BCrypt hash of 'admin123'
-- -----------------------------------------------------------
INSERT INTO t_admin (username, password, role, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE username = username;

-- -----------------------------------------------------------
-- 系统配置项
-- -----------------------------------------------------------
INSERT INTO t_sys_config (config_key, config_value, value_type, description) VALUES
('risk.openid.daily.limit', '9', 'NUMBER', '同一openid每日充值次数上限'),
('risk.phone.daily.limit', '5', 'NUMBER', '同一手机号每日充值次数上限'),
('risk.order.max.amount', '500', 'NUMBER', '单笔订单最大金额(CNY)'),
('risk.daily.max.amount', '1000', 'NUMBER', '同一openid每日累计最大金额(CNY)'),
('price.global.loss.rate', '0.03', 'NUMBER', '全局损耗率(3%)'),
('price.global.profit.rate', '0.15', 'NUMBER', '全局利润率(15%)'),
('price.change.threshold', '0.02', 'NUMBER', '价格变动阻断阈值(2%)'),
('fx.cache.expire.hours', '24', 'NUMBER', '汇率缓存过期时间(小时)'),
('fx.disable.hours', '48', 'NUMBER', '汇率超时自动禁用时间(小时)'),
('fx.premium.rate', '0.02', 'NUMBER', '汇率溢价(2%)'),
('mnp.timeout.degrade.count', '3', 'NUMBER', 'MNP连续超时降级次数'),
('mnp.cache.minutes', '5', 'NUMBER', 'MNP校验结果缓存时间(分钟)'),
('worker.retry.intervals', '30,60,120', 'STRING', 'Worker重试间隔(秒)'),
('worker.timeout.minutes', '15', 'NUMBER', '充值超时标记EXCEPTION(分钟)'),
('refund.circuit.break.count', '3', 'NUMBER', '退款熔断连续失败次数'),
('refund.pre.funded.hours', '2', 'NUMBER', '垫资退款触发时间(PROCESSING超N小时)'),
('balance.warn.threshold', '50', 'NUMBER', 'Reloadly余额一级告警阈值(USD)'),
('order.timeout.minutes', '30', 'NUMBER', '待支付订单超时关闭(分钟)')
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);
