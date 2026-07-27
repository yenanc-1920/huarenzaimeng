# 贡献指南

## 分支策略

```
master (生产)
  └── develop (开发)
        ├── feature/xxx (功能)
        └── hotfix/xxx (紧急修复，从 master 拉出)
```

| 分支 | 用途 | 保护规则 |
|------|------|----------|
| `master` | 生产代码，仅通过 PR 合入 | 禁止直推，需 1 人 Approve + CI 通过 |
| `develop` | 开发集成，push 触发 dev 环境自动构建 | — |
| `feature/*` | 功能开发，从 develop 拉出 | — |
| `hotfix/*` | 紧急修复，从 master 拉出，合入 master + develop | 需 1 人 Approve |

## 开发流程

1. 从 `develop` 创建功能分支：`git checkout -b feature/recharge-flow develop`
2. 开发 + 本地验证
3. 推送并创建 PR → `develop`
4. CI 自动构建 + 测试
5. 至少 1 人 Code Review + Approve
6. Squash Merge 合入 develop
7. develop 自动部署 dev 环境
8. 测试通过后，创建 PR：`develop` → `master`
9. 合入 master 触发生产构建（需 Environment 审批）

## Commit 规范

格式：`<type>(<scope>): <subject>`

| type | 说明 |
|------|------|
| feat | 新功能 |
| fix | 修复 |
| refactor | 重构（不改变行为） |
| docs | 文档 |
| test | 测试 |
| chore | 构建/工具/依赖 |
| perf | 性能优化 |

scope 取值：`server` / `admin` / `mini` / `sql` / `docker` / `ci`

示例：
```
feat(server): 添加 Reloadly 充值接口对接
fix(mini): 修复金额显示字体未使用等宽字体
chore(ci): 优化 Docker 构建缓存策略
```

## PR 规范

- 标题：与 commit 同格式，如 `feat(server): 充值核心流程`
- 描述：包含变更摘要、测试方式、关联任务 ID
- 单个 PR 不超过 500 行变更（大功能拆分多个 PR）
- 必须关联 Issue 或任务 ID

## Code Review 要点

- 无硬编码密钥 / 敏感信息
- 新增接口有参数校验
- 数据库变更有 Flyway 迁移脚本
- 前端页面覆盖三态（空态/加载态/错误态）
- 金额计算使用 BigDecimal，禁止 float/double

## 环境说明

| 环境 | 触发方式 | 用途 |
|------|----------|------|
| dev | push to develop | 开发联调 |
| production | merge to master + 审批 | 生产 |

## 紧急修复流程

1. 从 `master` 拉出 `hotfix/xxx`
2. 修复 + 验证
3. PR → `master`（1 人 Approve）
4. 合入后立即部署
5. 同步 cherry-pick 到 `develop`
