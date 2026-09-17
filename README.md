# DiChat（递聊）

> 基于 yudao-cloud 二次开发的 **即时通讯（IM）+ 社交支付** 全栈开源项目。

DiChat 在芋道（yudao）后台管理框架之上，构建了一套面向社交场景的即时通讯能力，并打通了红包、转账、钱包余额等社交支付闭环，可作为 IM 系统、客服系统或社交 App 后端的参考实现。

---

## ✨ 项目介绍

DiChat 的定位是「带支付能力的社交 IM」，核心由两大部分组成：

- **即时通讯（IM）**：单聊 / 群聊消息收发、会话管理、基于 WebSocket 的实时消息推送（支持 Redis Pub/Sub 多实例广播）、机器人关键词自动回复与一键转人工、客服工作台 SSE 实时推送。
- **社交支付**：钱包账户、模拟充值、提现申请、红包（普通 / 拼手气）、实时转账（含备注）、支付密码、单笔 / 日累计限额、并发防超领，以及完整的资金流水（交易明细）与对账能力。

项目沿用 yudao-cloud 的分层规范与基础设施（统一返回 `CommonResult`、多租户、数据权限、分布式锁等），后端以 Spring Cloud Alibaba 微服务架构组织，客户端配套 [dichat-im-uniapp](https://github.com/hanson-cloud/dichat-im-uniapp)（uni-app + Vue3 移动端）。

---

## 🧱 应用技术栈

### 后端（本仓库）

| 分类 | 技术 |
|------|------|
| 核心框架 | Spring Boot 3、Spring Cloud Alibaba |
| 微服务组件 | Nacos（注册 / 配置中心）、Sentinel（限流熔断）、Seata（分布式事务） |
| 数据层 | MyBatis-Plus、MySQL 8.x、Redis 6.x |
| 实时通信 | WebSocket（自研 Starter）、Redis Pub/Sub（多实例消息广播） |
| 接口文档 | Knife4j / Swagger |
| 定时任务 | XXL-JOB（yudao `starter-job`） |
| 安全 / 鉴权 | Spring Security（yudao 体系）、JWT |
| 构建工具 | Maven（多模块） |

### 客户端 App（配套仓库 dichat-im-uniapp）

| 分类 | 技术 |
|------|------|
| 框架 | uni-app（Vue 3 `<script setup>` + TypeScript + Pinia） |
| 开发工具 | HBuilderX |
| 编辑器 | Quill |
| 实时音视频 | LiveKit（群组通话 / 视频 PiP） |
| 视觉风格 | Y2K 复古未来主义 |

---

## 📦 模块结构

```
dichat/
├── dichat-dependencies/          # Maven BOM / 父 POM（统一依赖版本）
├── dichat-framework/             # 通用 Starter 集合（共 16 个）
│   ├── dichat-common/
│   ├── dichat-spring-boot-starter-web/          # Web / 统一返回
│   ├── dichat-spring-boot-starter-security/     # 安全 / 鉴权
│   ├── dichat-spring-boot-starter-mybatis/      # 数据访问
│   ├── dichat-spring-boot-starter-redis/        # 缓存 / 分布式锁
│   ├── dichat-spring-boot-starter-websocket/    # 实时通信
│   ├── dichat-spring-boot-starter-mq/           # 消息队列
│   ├── dichat-spring-boot-starter-rpc/          # Feign 远程调用
│   ├── dichat-spring-boot-starter-tenant/       # 多租户
│   ├── dichat-spring-boot-starter-job/          # 定时任务（XXL-JOB）
│   └── ...（含 excel / ip / protection / env / monitor / test / data-permission 等）
├── dichat-gateway/               # API 网关（Spring Cloud Gateway）
├── dichat-server/                # 聚合启动模块
├── dichat-module-im/             # IM 核心模块（api + server）
├── dichat-module-ai/             # AI / 机器人模块（api + server）
├── dichat-module-infra/          # 基础设施模块（api + server）
├── dichat-module-mall/           # 商城 / 商品模块（product / promotion / statistics / trade）
├── dichat-module-member/         # 会员模块（server）
├── dichat-module-pay/            # 支付模块（server）
└── dichat-module-system/         # 系统模块（server）
```

---

## 🚀 启动方式

### 1. 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.x
- Redis 6.x
- Nacos 2.x（注册中心 + 配置中心）
- （可选）Sentinel、Seata、XXL-JOB 控制台

### 2. 初始化数据库

建表 DDL 随各模块提供，位于各 `*-server` 子模块的 `src/test/resources/sql/create_tables.sql`（IM、系统、支付、会员、商城 product / promotion / trade 等模块各有一份），WebSocket Starter 另含 `ws_offline_message.sql`。将所需模块的 `create_tables.sql` 导入你的 MySQL 实例即可完成表结构初始化。

> 说明：本仓库**不随附**数据库创建脚本（`CREATE DATABASE`）以及 Nacos、XXL-JOB 等中间件的库表初始化 SQL（相关 `script/` 基础设施脚本未纳入仓库，见下方「安全与开源说明」）。请自行创建业务数据库（如 `dichat_im` / `dichat_system` / `dichat_pay` / `dichat_member` / `dichat_ai`），并按 Nacos、XXL-JOB 官方文档初始化其所需库表。

### 3. 启动中间件

本仓库**不提供**一键式 Docker / 中间件编排脚本（`script/` 目录未纳入仓库）。请自行准备以下依赖（本地安装或官方镜像均可）：

- MySQL 8.x（业务存储）
- Redis 6.x（缓存 / 分布式锁 / 多实例消息广播）
- Nacos 2.x（注册中心 + 配置中心）
- （可选）Sentinel 控制台、Seata 服务端、XXL-JOB Admin

依赖就绪后，在 Nacos 或各模块的 `application.yaml` 中配置数据源、Redis、注册中心地址即可。

> ⚠️ 涉及密码、密钥、Token 的配置项在公开版本中已统一替换为占位符 `CHANGE_ME`，部署前请替换为你的真实配置；本地开发用的 `application-dev.yaml` / `application-local.yaml` 等含密钥文件未纳入仓库，请自行创建。详见下方「安全与开源说明」。

### 4. 编译与启动

```bash
# 在项目根目录编译（跳过测试可加快速度）
mvn clean install -DskipTests

# 依次启动（至少包含以下核心服务）：
# 1. dichat-gateway        网关
# 2. dichat-module-infra   基础设施（文件 / 系统）
# 3. dichat-module-im      IM 核心
# 其余模块（ai / mall / member / pay / system）按需启动
```

各模块的启动类位于 `*-server` 子模块的 `src/main/java` 下。也可以通过 `dichat-server` 聚合模块统一启动。

### 5. 接口文档

服务启动后，访问对应模块的 Knife4j 文档地址，例如：

```
http://localhost:<端口>/doc.html
```

### 6. 客户端 App（可选）

若需运行配套移动端，参见 [dichat-im-uniapp](https://github.com/hanson-cloud/dichat-im-uniapp)：使用 HBuilderX 打开，配置后端地址后运行到浏览器 / 真机。

---

## 🔒 安全与开源说明

- 本仓库**不包含**任何数据库数据文件（`*.ibd` / `*.sdi` 等）、数据库创建与中间件初始化 SQL、本地密钥配置、Docker / 基础设施编排脚本（`script/` 目录）、以及内部文档。
- 所有敏感配置项已脱敏为 `CHANGE_ME` 占位符，请部署时自行替换。
- 请勿将含真实密钥的 `application-dev.yaml` / `application-local.yaml` 提交到公开仓库。

---

## 📄 开源协议

本项目基于 [MIT License](./LICENSE) 开源，可自由用于学习、二次开发与商业项目（请保留版权声明）。

---

## 🙏 致谢

- [yudao-cloud](https://github.com/yudaocode/yudao-cloud) —— 本项目的底层后台管理框架。
