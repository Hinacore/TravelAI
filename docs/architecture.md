# TravelAI 智能旅游推荐与规划系统 — 技术架构文档

> 版本：1.0.0  
> 更新日期：2026-07-08  
> 状态：正式发布

---

## 1. 系统架构概述

### 1.1 架构风格

TravelAI 采用**前后端分离 + 微服务**架构：

- **前端**：Vue3 SPA（单页应用），通过 Axios 调用后端各微服务 API
- **后端**：Spring Boot 2.7 微服务集群，每个服务独立部署、独立端口
- **数据库**：MySQL 8.0 单实例，所有微服务共享同一个 `travelai` 数据库
- **缓存**：Redis（配置预留，当前未深度使用）
- **AI 服务**：DeepSeek API（外部服务，通过 HTTP 调用）

### 1.2 系统架构图

```
┌─────────────────────────────────────────────────────────┐
│                    用户浏览器 (Chrome/Edge/Firefox)       │
│                   Vue3 + Element Plus + TypeScript        │
└───────┬──────────────────────────────────────────────────┘
        │ HTTP/JSON (CORS)
        │
┌───────▼──────────────────────────────────────────────────┐
│                    前端服务 (Vite Dev Server)              │
│                    http://localhost:5173                   │
└───┬───────┬───────┬───────┬──────────────────────────────┘
    │       │       │       │
    │ 8091  │ 8092  │ 8093  │ 8094
    ▼       ▼       ▼       ▼
┌───────┐┌───────┐┌───────┐┌───────┐
│用户服务││行程服务││推荐服务││聊天服务│
│ :8091 ││ :8092 ││ :8093 ││ :8094 │
└───┬───┘└───┬───┘└───┬───┘└───┬───┘
    │        │        │        │
    │  JPA   │  JPA   │ WebClient│ JPA + WebClient
    │        │        │        │
    ▼        ▼        ▼        ▼
┌──────────────────┐  ┌──────────────────┐
│  MySQL 8.0       │  │ DeepSeek API     │
│  database:travelai│  │ https://api.deepseek.com │
│  port: 3306      │  │ model: deepseek-v4-flash  │
└──────────────────┘  └──────────────────┘
```

---

## 2. 技术栈选型

### 2.1 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | ^3.5.13 | 前端框架（Composition API + `<script setup>`） |
| TypeScript | ~5.8.3 | 类型安全 |
| Vite | ^6.3.5 | 构建工具和开发服务器 |
| Vue Router | ^4.6.4 | 路由管理 |
| Pinia | ^3.0.4 | 状态管理 |
| Element Plus | ^2.14.2 | UI 组件库 |
| Axios | ^1.18.1 | HTTP 请求 |
| vue-tsc | ^2.2.8 | Vue TypeScript 类型检查 |

### 2.2 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.18 | 微服务框架 |
| Spring Security | 2.7.18 | 认证授权 |
| Spring Data JPA | 2.7.18 | ORM 和数据访问 |
| Spring WebFlux | 2.7.18 | WebClient（调用 AI API） |
| MySQL Connector/J | 8.0.16 | MySQL 驱动 |
| JJWT | 0.9.1 | JWT Token 生成和解析 |
| Lombok | 1.18.30 | 简化 Java 代码 |
| Knife4j | 3.0.3 | API 文档 |
| Logback | 2.7.18 | 日志框架 |

### 2.3 基础设施

| 技术 | 版本 | 用途 |
|------|------|------|
| JDK | 1.8+ | Java 运行时 |
| Maven | 3.x | 构建工具 |
| Node.js | 18+ | 前端构建环境 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.x | 缓存（预留） |

---

## 3. 模块划分

### 3.1 后端模块结构

```
backend/
├── pom.xml                          # 父 POM，管理所有子模块
├── travelai-common/                 # 公共模块
│   └── com.travelai.common/
│       ├── config/                  # 公共配置（EnvConfig, CorsConfig）
│       ├── security/                # 安全工具（JwtUtil, JwtFilter）
│       ├── exception/               # 全局异常处理
│       └── result/                  # 统一响应封装（Result）
├── travelai-user/                   # 用户服务 (:8091)
│   └── com.travelai.user/
│       ├── controller/              # UserController
│       ├── service/                 # UserService
│       ├── entity/                  # User
│       ├── repository/              # UserRepository
│       ├── dto/                     # 请求/响应 DTO
│       └── config/                  # SecurityConfig
├── travelai-trip/                   # 行程服务 (:8092)
│   └── com.travelai.trip/
│       ├── controller/              # TripController
│       ├── service/                 # TripService
│       ├── entity/                  # Trip, TripDay, TripActivity, TripBudget
│       ├── repository/              # TripRepository, TripDayRepository, ...
│       ├── dto/                     # CreateTripRequest, TripResponse, ...
│       └── config/                  # SecurityConfig
├── travelai-recommend/              # 推荐服务 (:8093)
│   └── com.travelai.recommend/
│       ├── controller/              # RecommendController
│       ├── service/                 # RecommendService, AIClient
│       ├── dto/                     # GenerateTripRequest, AITripResponse, ...
│       └── config/                  # SecurityConfig, WebClientConfig
├── travelai-chat/                   # 聊天服务 (:8094)
│   └── com.travelai.chat/
│       ├── controller/              # ChatController
│       ├── service/                 # ChatService, AIClient
│       ├── entity/                  # Conversation, Message
│       ├── repository/              # ConversationRepository, MessageRepository
│       └── config/                  # SecurityConfig, WebClientConfig
├── travelai-api/                    # API 网关（预留，当前未使用）
├── travelai-ai-mock/                # AI 模拟服务（预留，当前未使用）
├── travelai-app/                    # 应用聚合（预留，当前未使用）
└── sql/
    └── init.sql                     # 数据库初始化脚本
```

### 3.2 前端模块结构

```
frontend/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── index.html
└── src/
    ├── main.ts                      # 应用入口
    ├── App.vue                      # 根组件（布局 + 导航栏）
    ├── router/
    │   └── index.ts                 # 路由配置 + 路由守卫
    ├── stores/
    │   └── user.ts                  # 用户状态管理（Pinia）
    ├── api/
    │   ├── index.ts                 # API 客户端配置和接口定义
    │   └── types.ts                 # TypeScript 类型定义
    └── views/
        ├── Home.vue                 # 首页
        ├── Login.vue                # 登录页
        ├── Register.vue             # 注册页
        ├── Recommend.vue            # AI 行程推荐页
        ├── Chat.vue                 # AI 助手对话页
        ├── TripList.vue             # 行程管理列表页
        ├── TripDetail.vue           # 行程详情页
        └── Profile.vue              # 个人信息页
```

### 3.3 各微服务职责

| 服务 | 端口 | 职责 |
|------|------|------|
| travelai-user | 8091 | 用户注册、登录、信息管理、JWT 认证 |
| travelai-trip | 8092 | 行程 CRUD、状态管理、分享功能 |
| travelai-recommend | 8093 | 调用 DeepSeek API 生成行程方案 |
| travelai-chat | 8094 | AI 对话、消息管理、历史记录 |
| travelai-common | — | 公共组件：JWT 工具、异常处理、CORS 配置、Env 配置 |

---

## 4. 接口设计

### 4.1 统一响应格式

所有 API 返回统一的 JSON 响应结构：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": 1783444881818
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码（200成功，401未授权，403禁止，404未找到，500服务器错误） |
| message | String | 响应消息 |
| data | Object | 响应数据 |
| timestamp | Long | 时间戳 |

### 4.2 用户服务接口 (:8091)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/users/register` | 用户注册 | 否 |
| POST | `/api/v1/users/login` | 用户登录 | 否 |
| GET | `/api/v1/users/info` | 获取当前用户信息 | 是 |
| PUT | `/api/v1/users/info` | 更新用户信息 | 是 |
| PUT | `/api/v1/users/password` | 修改密码 | 是 |

### 4.3 行程服务接口 (:8092)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/trips` | 创建行程 | 是 |
| GET | `/api/v1/trips` | 查询行程列表（分页、搜索、排序） | 是 |
| GET | `/api/v1/trips/{id}` | 查询行程详情 | 是 |
| PUT | `/api/v1/trips/{id}` | 更新行程 | 是 |
| DELETE | `/api/v1/trips/{id}` | 删除行程 | 是 |
| PATCH | `/api/v1/trips/{id}/status` | 更新行程状态 | 是 |
| POST | `/api/v1/trips/{id}/share` | 生成分享链接 | 是 |
| GET | `/api/v1/trips/share/{token}` | 查看分享的行程 | 否 |

**行程列表查询参数**：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | Integer | 0 | 页码（从0开始） |
| size | Integer | 10 | 每页条数 |
| keyword | String | null | 搜索关键词（匹配目的地和名称） |
| sortBy | String | createdAt | 排序字段 |
| sortDirection | String | DESC | 排序方向（ASC/DESC） |

### 4.4 推荐服务接口 (:8093)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/recommend/trip` | AI 生成行程 | 否 |
| POST | `/api/v1/recommend/trip/{tripId}/optimize` | 优化已有行程 | 否 |

### 4.5 聊天服务接口 (:8094)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/chat/conversations` | 创建对话 | 否 |
| GET | `/api/v1/chat/conversations` | 获取对话列表 | 否 |
| GET | `/api/v1/chat/conversations/{id}` | 获取对话详情（含消息） | 否 |
| DELETE | `/api/v1/chat/conversations/{id}` | 删除对话 | 否 |
| POST | `/api/v1/chat/conversations/{id}/messages` | 发送消息 | 否 |
| POST | `/api/v1/chat/messages` | 自动创建对话并发送消息 | 否 |

---

## 5. 数据库设计

### 5.1 ER 关系图

```
users (1) ──── (N) trips
                    │
                    ├── (N) trip_days
                    │         │
                    │         └── (N) trip_activities
                    │
                    └── (N) trip_budget

users (1) ──── (N) conversations
                    │
                    └── (N) messages
```

### 5.2 表结构详细设计

#### 5.2.1 users — 用户表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码（BCrypt加密） |
| email | VARCHAR(100) | UNIQUE | 邮箱 |
| phone | VARCHAR(20) | UNIQUE | 手机号 |
| avatar | VARCHAR(255) | NULL | 头像URL |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'USER' | 角色（ADMIN/USER） |
| status | INT | NOT NULL, DEFAULT 1 | 状态（0禁用/1启用） |
| created_at | DATETIME | NOT NULL, DEFAULT NOW() | 创建时间 |
| updated_at | DATETIME | ON UPDATE NOW() | 更新时间 |

#### 5.2.2 trips — 行程表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 行程ID |
| user_id | BIGINT | NOT NULL, FK→users.id | 用户ID |
| name | VARCHAR(100) | NOT NULL | 行程名称 |
| destination | VARCHAR(100) | NOT NULL | 目的地 |
| budget | DECIMAL(10,2) | NOT NULL | 预算金额 |
| days | INT | NOT NULL | 旅行天数 |
| start_date | DATE | NULL | 出发日期 |
| description | TEXT | NULL | 行程描述 |
| status | INT | NOT NULL, DEFAULT 0 | 状态（0草稿/1进行中/2已完成/3已取消） |
| share_token | VARCHAR(64) | UNIQUE | 分享Token |
| share_status | INT | NOT NULL, DEFAULT 0 | 分享状态（0私密/1公开） |
| version | INT | NOT NULL, DEFAULT 1 | 版本号（乐观锁） |
| created_at | DATETIME | NOT NULL, DEFAULT NOW() | 创建时间 |
| updated_at | DATETIME | ON UPDATE NOW() | 更新时间 |

#### 5.2.3 trip_days — 每日行程表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 每日行程ID |
| trip_id | BIGINT | NOT NULL, FK→trips.id | 行程ID |
| day_number | INT | NOT NULL | 第几天 |
| summary | VARCHAR(200) | NULL | 当日总结 |
| created_at | DATETIME | NOT NULL, DEFAULT NOW() | 创建时间 |

#### 5.2.4 trip_activities — 行程活动表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 活动ID |
| trip_day_id | BIGINT | NOT NULL, FK→trip_days.id | 每日行程ID |
| type | VARCHAR(20) | NOT NULL | 类型（MORNING/AFTERNOON/EVENING） |
| title | VARCHAR(100) | NOT NULL | 活动标题 |
| location | VARCHAR(200) | NULL | 地点 |
| duration | VARCHAR(50) | NULL | 预计时长 |
| ticket_price | DECIMAL(8,2) | NULL | 门票价格 |
| transportation | VARCHAR(200) | NULL | 交通方式 |
| description | TEXT | NULL | 详细描述 |
| budget | DECIMAL(8,2) | NULL | 预算 |

#### 5.2.5 trip_budget — 预算明细表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 预算明细ID |
| trip_id | BIGINT | NOT NULL, FK→trips.id | 行程ID |
| category | VARCHAR(20) | NOT NULL | 类别（住宿/餐饮/交通/门票/其他） |
| amount | DECIMAL(10,2) | NOT NULL | 金额 |
| description | VARCHAR(200) | NULL | 说明 |

#### 5.2.6 conversations — 对话记录表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 对话ID |
| user_id | BIGINT | NULL | 用户ID（允许匿名） |
| title | VARCHAR(100) | NULL | 对话标题 |
| context | TEXT | NULL | 对话上下文 |
| status | INT | NOT NULL, DEFAULT 1 | 状态（0结束/1进行中） |
| created_at | DATETIME | NOT NULL, DEFAULT NOW() | 创建时间 |
| updated_at | DATETIME | ON UPDATE NOW() | 更新时间 |

#### 5.2.7 messages — 消息记录表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 消息ID |
| conversation_id | BIGINT | NOT NULL, FK→conversations.id | 对话ID |
| sender | VARCHAR(20) | NOT NULL | 发送者（USER/AI） |
| content | TEXT | NOT NULL | 消息内容 |
| created_at | DATETIME | NOT NULL, DEFAULT NOW() | 创建时间 |

---

## 6. 安全策略

### 6.1 认证流程

```
1. 用户登录 → UserService 验证用户名密码
2. 验证通过 → JwtUtil 生成 Token（包含 userId, username, role）
3. 返回 Token 给前端
4. 前端存储 Token 到 localStorage
5. 后续请求携带 Authorization: Bearer <token>
6. 后端 JwtFilter 解析和验证 Token
7. 验证通过 → 设置 SecurityContext → 进入业务逻辑
8. 验证失败 → 返回 401
```

### 6.2 Spring Security 配置

每个微服务模块都配置了独立的 `SecurityConfig`：

- **公开路径**：注册、登录、推荐、聊天接口
- **保护路径**：行程管理、用户信息接口（需要 JWT 认证）
- **CORS 配置**：允许所有源跨域访问（开发环境）
- **CSRF**：禁用（REST API 不需要）

### 6.3 API 密钥安全

- DeepSeek API Key 存储在项目外部的 `.env` 文件中
- 路径：`e:\AAA\test\AI_KEY\DEEPSEEK_API_KEY.env`
- 后端通过 `EnvConfig.java` 读取，禁止硬编码到代码中
- `.env` 文件不纳入版本控制

### 6.4 密码安全

- 使用 BCrypt 算法加密存储
- 密码强度要求：≥6位，包含大小写字母和数字
- 修改密码需验证旧密码

---

## 7. AI 集成设计

### 7.1 DeepSeek API 调用流程

```
RecommendService / ChatService
    → 构建 Prompt（含用户需求 + JSON 格式要求）
    → AIClient 通过 WebClient 调用 DeepSeek API
    → POST https://api.deepseek.com/v1/chat/completions
    → Headers: Authorization: Bearer <API_KEY>
    → Body: { model, messages, temperature, max_tokens }
    → 解析 AI 返回的 JSON
    → 返回结构化数据
```

### 7.2 降级策略

```java
try {
    // 调用 DeepSeek API
    response = aiClient.callDeepSeek(prompt);
} catch (Exception e) {
    log.warn("AI API 调用失败，使用模拟数据: {}", e.getMessage());
    // 返回预设的模拟行程/回复
    response = generateMockResponse();
}
```

### 7.3 Prompt 工程

行程生成 Prompt 结构：

```
你是一个专业的旅游规划师，擅长根据用户的需求生成详细的旅行行程。
请根据以下信息为用户生成一份详细的旅游规划：
- 目的地城市：{city}
- 预算：{budget}元
- 旅行天数：{days}天
要求：
1. 每天的行程安排（上午、下午、晚上）
2. 每个景点的详细介绍
3. 交通建议
4. 预算分配明细
5. 注意事项
请以JSON格式输出，结构如下：
{ ... JSON 模板 ... }
```

---

## 8. 前端架构设计

### 8.1 路由设计

| 路径 | 组件 | 认证 | 说明 |
|------|------|------|------|
| `/` | Home.vue | 否 | 首页 |
| `/login` | Login.vue | 否 | 登录页 |
| `/register` | Register.vue | 否 | 注册页 |
| `/recommend` | Recommend.vue | 是 | AI 行程推荐 |
| `/chat` | Chat.vue | 是 | AI 助手对话 |
| `/trips` | TripList.vue | 是 | 行程管理列表 |
| `/trips/:id` | TripDetail.vue | 是 | 行程详情 |
| `/profile` | Profile.vue | 是 | 个人信息 |
| `/:pathMatch(.*)*` | — | 否 | 404 重定向到首页 |

### 8.2 状态管理

使用 Pinia 管理用户状态：

```typescript
// stores/user.ts
- token: string          // JWT Token
- userInfo: UserInfo     // 用户信息
- setToken()             // 设置 Token（同步到 localStorage）
- setUserInfo()          // 设置用户信息（同步到 localStorage）
- logout()               // 登出（清除 Token 和用户信息）
- initFromStorage()      // 从 localStorage 恢复状态
- isLoggedIn()           // 检查登录状态
```

### 8.3 API 客户端设计

每个微服务对应一个独立的 Axios 实例：

```typescript
const userApiClient = createApi('http://localhost:8091')      // 用户服务
const tripApiClient = createApi('http://localhost:8092')      // 行程服务
const recommendApiClient = createApi('http://localhost:8093') // 推荐服务
const chatApiClient = createApi('http://localhost:8094')      // 聊天服务
```

每个实例配置：
- 请求拦截器：自动添加 `Authorization` 头
- 响应拦截器：检测 401 状态码，自动跳转登录页
- 超时时间：30 秒

### 8.4 TypeScript 类型系统

核心类型定义在 `src/api/types.ts`：

- `ApiResponse<T>` — 统一响应类型
- `UserInfo` — 用户信息
- `LoginResponse` — 登录响应
- `Trip` — 行程信息
- `Conversation` — 对话信息
- `Message` — 消息信息
- `AITripResponse` — AI 行程生成响应
- `RecommendForm` — 推荐请求表单

---

## 9. 错误处理设计

### 9.1 后端异常处理

使用 `@RestControllerAdvice` 全局异常处理器：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    - BusinessException → 400 + 自定义错误消息
    - AuthenticationException → 401
    - AccessDeniedException → 403
    - MethodArgumentNotValidException → 400 + 字段错误
    - Exception → 500 + "系统繁忙，请稍后再试"
}
```

### 9.2 前端错误处理

- API 调用使用 try-catch 包装
- 网络错误显示友好提示
- 401 错误自动清除 Token 并跳转登录页
- 表单验证错误实时显示

---

## 10. 日志设计

### 10.1 后端日志

使用 SLF4J + Logback：

```java
log.info("用户登录成功: {}", username);
log.warn("AI API 调用失败，使用模拟数据: {}", e.getMessage());
log.error("数据库操作失败: {}", e.getMessage(), e);
```

### 10.2 前端日志

```typescript
console.error('加载行程失败:', error)
console.error('保存行程失败:', error)
```
