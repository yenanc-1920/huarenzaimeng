# 华人在孟 V1.0 — 云托管定时触发器配置清单
# 配置位置：微信云托管控制台 → 服务 → 定时触发器
# 设计决策：不使用容器内crond，由云托管平台统一调度（避免多实例重复执行）

| 触发器名称   | Cron表达式       | 触发URL                                        | 说明                    |
|-------------|-----------------|-----------------------------------------------|------------------------|
| 汇率同步     | 0 */4 * * *     | http://localhost:8080/api/internal/sync/fx-rate      | 每4小时同步USD/CNY汇率   |
| 商品同步     | 0 1 * * *       | http://localhost:8080/api/internal/sync/products     | 每日UTC 01:00(北京09:00) |
| 余额监控     | */10 * * * *    | http://localhost:8080/api/internal/monitor/balance   | 每10分钟检查Reloadly余额 |
| MNP恢复     | */5 * * * *     | http://localhost:8080/api/internal/recovery/mnp      | 每5分钟探测MNP接口恢复   |
| 节假日提醒   | 0 1 * * *       | http://localhost:8080/api/internal/remind/holiday    | 每日UTC 01:00检查待观月   |
| 订单超时     | * * * * *       | http://localhost:8080/api/internal/timeout/scan      | 每分钟扫描超时订单       |

# 鉴权：所有触发器请求需携带 Header X-Internal-Secret，值为环境变量 INTERNAL_API_SECRET
# 云托管定时触发器自动携带平台内部鉴权信息，后端 InternalApiInterceptor 做双重校验
