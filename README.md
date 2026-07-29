# 华人在孟

“华人在孟”是面向在孟加拉国华人的微信小程序与运营系统。主仓库是唯一持续开发和部署来源。

## 目录

- `mini/`：Vue 3 + uni-app 微信小程序
- `admin/`：Vue 3 + Ant Design Vue 管理后台
- `server/`：Java 17 + Spring Boot 服务端
- `sql/`：MySQL 初始化脚本
- `docs/`：产品、设计、测试和发布资料

同级的日期快照目录仅作历史归档，不再作为开发或部署来源。

## 本地构建

```powershell
cd mini
npm.cmd ci
npm.cmd run build:mp-weixin
```

微信开发者工具导入 `mini/dist/build/mp-weixin`。

```powershell
cd admin
npm.cmd ci
npm.cmd run build
```

管理后台部署产物需同步到 `server/src/main/resources/static/console`，CI 会校验两者一致。

```powershell
cd server
mvn clean package -DskipTests -B
mvn test -B
```

## 云托管

- 流水线分支：`develop`
- 目标目录：`server`
- Dockerfile：`Dockerfile`
- 服务端口：`8080`
- 管理后台入口：`/api/console/`
- 存活探针：`/api/actuator/health/liveness`
- 就绪探针：`/api/actuator/health/readiness`

## 小程序云托管调用

微信小程序构建通过 `wx.cloud.callContainer` 访问云托管：

- 云环境：`prod-d3g9ntdmsdf9d7877`
- 服务名：`huarenzaimeng-server`
- 服务端上下文：`/api`
- 小程序业务接口前缀：`/api/wx`
- 云托管自动注入 `x-wx-openid`，后端据此创建或识别游客用户

如云托管服务改名或迁移环境，请同步修改 `mini/src/api/request.js` 中的 `CLOUD_ENV` 与 `CLOUD_SERVICE`。

## 当前已知缺口

- 管理后台目前主要使用模拟数据，尚未完成真实 API 联调
- 微信支付和退款链路尚未达到生产可用状态

不要提交 `project.private.config.json`、`.qoder/`、密钥或本地环境配置。