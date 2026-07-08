# TravelAI 智能旅游推荐与规划系统

> 基于人工智能的智能旅游规划平台，帮助用户快速生成个性化旅行方案

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-1.8+-blue.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![Vue Version](https://img.shields.io/badge/Vue-3.x-green.svg)](https://vuejs.org/)
[![MySQL Version](https://img.shields.io/badge/MySQL-8.0+-orange.svg)](https://www.mysql.com/)

---

## 目录

- [项目概述](#项目概述)
- [核心功能](#核心功能)
- [技术架构](#技术架构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [Docker Compose 部署](#docker-compose-部署)
- [使用指南](#使用指南)
- [API 文档](#api-文档)
- [项目结构](#项目结构)
- [配置说明](#配置说明)
- [贡献规范](#贡献规范)
- [联系方式](#联系方式)
- [更新日志](#更新日志)

---

## 项目概述

TravelAI 是一个基于人工智能的旅游推荐与规划系统，旨在帮助用户通过自然语言交互和智能算法，快速生成个性化的旅行行程方案。

### 核心价值

- **智能行程规划**：基于 DeepSeek 大语言模型，30秒内生成完整旅行方案
- **一站式管理**：行程的创建、编辑、状态跟踪、分享全生命周期管理
- **AI 对话助手**：自然语言对话式 AI 助手，解答旅游相关问题
- **用户友好界面**：现代化 Web 界面，支持响应式布局

### 业务痛点

| 痛点 | 解决方案 |
|------|----------|
| 行程规划耗时 | AI 自动生成详细行程方案 |
| 预算难以控制 | 系统化预算分配工具 |
| 个性化不足 | 基于用户偏好的定制化推荐 |
| 行程管理分散 | 统一的行程管理平台 |

---

## 核心功能

### 🎯 用户管理

- 用户注册与登录（JWT 认证）
- 个人信息管理
- 密码修改

### 🗺️ AI 行程推荐

- 根据目的地、预算、天数生成完整行程
- 每日行程安排（上午/下午/晚上）
- 预算分配明细（住宿/餐饮/交通/门票/其他）
- 行程优化功能

### 📋 行程管理

- 行程 CRUD 操作
- 分页查询与搜索
- 状态管理（草稿/进行中/已完成/已取消）
- 行程分享功能

### 💬 AI 助手对话

- 实时对话交互
- 对话历史管理
- 上下文感知回复

---

## 技术架构

> 详细架构设计请参考 [架构文档](docs/architecture.md)

### 架构风格

采用**前后端分离 + 微服务**架构：

```
┌─────────────────────────────────────────────────────────┐
│                    用户浏览器                           │
│              Vue3 + Element Plus + TypeScript            │
└───────┬──────────────────────────────────────────────────┘
        │ HTTP/JSON (CORS)
        │
┌───────▼──────────────────────────────────────────────────┐
│                 前端服务 (Vite Dev Server)                 │
│                   http://localhost:5173                   │
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
│   MySQL 8.0      │  │  DeepSeek API    │
│ database:travelai│  │ api.deepseek.com │
│     port: 3306   │  │ model: v4-flash  │
└──────────────────┘  └──────────────────┘
```

### 技术栈

| 分类 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **前端** | Vue 3 | ^3.5 | 前端框架 |
| | TypeScript | ~5.8 | 类型安全 |
| | Vite | ^6.3 | 构建工具 |
| | Element Plus | ^2.14 | UI 组件库 |
| | Vue Router | ^4.6 | 路由管理 |
| | Pinia | ^3.0 | 状态管理 |
| | Axios | ^1.18 | HTTP 请求 |
| **后端** | Spring Boot | 2.7.18 | 微服务框架 |
| | Spring Security | 2.7.18 | 认证授权 |
| | Spring Data JPA | 2.7.18 | ORM |
| | Spring WebFlux | 2.7.18 | WebClient |
| | JJWT | 0.9.1 | JWT Token |
| | Lombok | 1.18.30 | 简化代码 |
| | Knife4j | 3.0.3 | API 文档 |
| **基础设施** | MySQL | 8.0+ | 关系型数据库 |
| | Redis | 6.x+ | 缓存（预留） |
| | DeepSeek API | v4 | AI 服务 |

### 微服务模块

| 服务 | 端口 | 职责 |
|------|------|------|
| travelai-user | 8091 | 用户注册、登录、信息管理 |
| travelai-trip | 8092 | 行程 CRUD、状态管理、分享 |
| travelai-recommend | 8093 | AI 生成行程方案 |
| travelai-chat | 8094 | AI 对话、消息管理 |
| travelai-common | — | 公共组件（JWT、异常处理、CORS） |

---

## 环境要求

### 开发环境

| 软件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 1.8+ | Java 运行时环境 |
| Maven | 3.6+ | Java 构建工具 |
| Node.js | 18+ | 前端构建环境 |
| npm | 9+ | 前端包管理器 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.x+ | 缓存服务（可选） |
| Docker | 20+ | Docker Compose 部署（可选） |

### 推荐开发工具

| 工具 | 用途 |
|------|------|
| IntelliJ IDEA | 后端开发 |
| VS Code | 前端开发 |
| Navicat / MySQL Workbench | 数据库管理 |
| Postman | API 测试 |

---

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/Hinacore/TravelAI.git
cd TravelAI
```

### 2. 配置数据库

```bash
# 创建数据库并执行初始化脚本
mysql -u root -p < backend/sql/init.sql
```

> **⚠️ 安全警告**：数据库初始化脚本会创建默认管理员账户，**生产环境部署后请立即修改默认密码**！

### 3. 配置 API Key

```bash
# 复制环境变量模板
cp AI_KEY/.env.example AI_KEY/DEEPSEEK_API_KEY.env

# 编辑并填入你的 DeepSeek API Key
# 文件路径：AI_KEY/DEEPSEEK_API_KEY.env
```

### 4. 修改数据库密码

编辑以下 4 个文件中的数据库密码配置：

- `backend/travelai-user/src/main/resources/application.yml`
- `backend/travelai-trip/src/main/resources/application.yml`
- `backend/travelai-recommend/src/main/resources/application.yml`
- `backend/travelai-chat/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    password: [你的数据库密码]
```

### 5. 构建后端

```bash
cd backend

# 编译所有模块
mvn clean compile

# 安装公共模块
cd travelai-common
mvn install -DskipTests -q
```

### 6. 启动后端服务

在 **4 个独立终端**中分别启动：

```bash
# 终端1：用户服务 (:8091)
cd backend/travelai-user && mvn spring-boot:run

# 终端2：行程服务 (:8092)
cd backend/travelai-trip && mvn spring-boot:run

# 终端3：推荐服务 (:8093)
cd backend/travelai-recommend && mvn spring-boot:run

# 终端4：聊天服务 (:8094)
cd backend/travelai-chat && mvn spring-boot:run
```

### 7. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 8. 访问系统

打开浏览器访问：**http://localhost:5173**

---

## Docker Compose 部署

项目提供 Docker Compose 配置，支持一键部署整个系统。

### 1. 配置 API Key

```bash
# 复制环境变量模板
cp AI_KEY/.env.example AI_KEY/DEEPSEEK_API_KEY.env

# 编辑并填入你的 DeepSeek API Key
```

### 2. 修改 Docker Compose 配置（可选）

如需修改数据库密码或其他配置，编辑 `docker-compose.yml`：

```yaml
services:
  mysql:
    environment:
      MYSQL_ROOT_PASSWORD: [你的数据库密码]
  
  user-service:
    environment:
      SPRING_DATASOURCE_PASSWORD: [你的数据库密码]
```

### 3. 启动所有服务

```bash
# 构建并启动所有服务（首次启动需要较长时间）
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 4. 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（谨慎操作）
docker-compose down -v
```

### 服务访问地址

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:3000 |
| 用户服务 | http://localhost:8091 |
| 行程服务 | http://localhost:8092 |
| 推荐服务 | http://localhost:8093 |
| 聊天服务 | http://localhost:8094 |
| API 网关 | http://localhost:8090 |

---

## 使用指南

### 用户注册与登录

1. 访问首页，点击"登录"或"注册"
2. 注册新账户：输入用户名、邮箱、密码
3. 登录系统：使用注册的账户信息

### AI 生成行程

1. 登录后进入"AI 行程推荐"页面
2. 填写：目的地、预算、旅行天数、出发日期
3. 点击"AI 生成行程"
4. 查看生成的详细行程方案
5. 点击"保存行程"将方案保存到数据库

### 行程管理

1. 进入"行程管理"页面查看所有行程
2. 支持搜索、排序、分页
3. 可编辑、删除、分享行程
4. 可修改行程状态（草稿/进行中/已完成/已取消）

### AI 助手对话

1. 进入"AI 助手"页面
2. 创建新对话或选择历史对话
3. 输入问题，AI 实时回复

---

## API 文档

> 详细 API 设计请参考 [架构文档](docs/architecture.md)

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": 1783444881818
}
```

### 用户服务 (:8091)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/users/register` | 用户注册 | 否 |
| POST | `/api/v1/users/login` | 用户登录 | 否 |
| GET | `/api/v1/users/info` | 获取当前用户信息 | 是 |
| PUT | `/api/v1/users/info` | 更新用户信息 | 是 |
| PUT | `/api/v1/users/password` | 修改密码 | 是 |

### 行程服务 (:8092)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/trips` | 创建行程 | 是 |
| GET | `/api/v1/trips` | 查询行程列表 | 是 |
| GET | `/api/v1/trips/{id}` | 查询行程详情 | 是 |
| PUT | `/api/v1/trips/{id}` | 更新行程 | 是 |
| DELETE | `/api/v1/trips/{id}` | 删除行程 | 是 |
| PATCH | `/api/v1/trips/{id}/status` | 更新行程状态 | 是 |
| POST | `/api/v1/trips/{id}/share` | 生成分享链接 | 是 |

### 推荐服务 (:8093)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/recommend/trip` | AI 生成行程 | 否 |
| POST | `/api/v1/recommend/trip/{tripId}/optimize` | 优化行程 | 否 |

### 聊天服务 (:8094)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/chat/conversations` | 创建对话 | 否 |
| GET | `/api/v1/chat/conversations` | 获取对话列表 | 否 |
| POST | `/api/v1/chat/conversations/{id}/messages` | 发送消息 | 否 |
| DELETE | `/api/v1/chat/conversations/{id}` | 删除对话 | 否 |

> 详细 API 文档请访问各服务的 Knife4j 页面：`http://localhost:8091/doc.html`

---

## 项目结构

```
TravelAI/
├── AI_KEY/                           # API 密钥配置
├── backend/                          # 后端微服务代码
│   ├── travelai-user/                # 用户服务 (:8091)
│   ├── travelai-trip/                # 行程服务 (:8092)
│   ├── travelai-recommend/           # 推荐服务 (:8093)
│   ├── travelai-chat/                # 聊天服务 (:8094)
│   ├── travelai-common/              # 公共模块
│   └── sql/                          # SQL 初始化脚本
├── frontend/                         # 前端代码
│   └── src/
│       ├── views/                    # 页面组件
│       ├── api/                      # API 接口定义
│       ├── router/                   # 路由配置
│       └── stores/                   # Pinia 状态管理
├── docs/                             # 项目文档
│   ├── requirements.md               # 需求文档
│   ├── architecture.md               # 架构文档
│   └── configuration.md              # 配置文档
├── docker-compose.yml                # Docker Compose 配置
├── .gitignore
├── LICENSE
└── README.md
```

### 文档说明

| 文档 | 说明 |
|------|------|
| [requirements.md](docs/requirements.md) | 需求文档（功能需求、非功能需求、业务流程） |
| [architecture.md](docs/architecture.md) | 架构文档（系统架构、技术栈、接口设计、数据库设计） |
| [configuration.md](docs/configuration.md) | 配置文档（环境要求、启动流程、常见问题排查） |

---

## 配置说明

> 详细配置指南请参考 [配置文档](docs/configuration.md)

### 配置文件位置

| 配置项 | 文件路径 |
|--------|----------|
| MySQL 密码 | `backend/*/src/main/resources/application.yml` |
| Redis 密码 | `backend/*/src/main/resources/application.yml` |
| JWT 密钥 | `backend/*/src/main/resources/application.yml` |
| DeepSeek API Key | `AI_KEY/DEEPSEEK_API_KEY.env` |
| 前端 API 地址 | `frontend/src/api/index.ts` |

### 环境变量

`.env` 文件格式：

```
base_url (OpenAI)=https://api.deepseek.com
DEEPSEEK_APIKEY=[你的api_key]
```

### 重要安全提示

- **禁止**将 API Key 硬编码到代码中
- **禁止**将 `.env` 文件提交到版本控制
- 生产环境务必修改 JWT 密钥为复杂随机字符串
- 默认管理员密码在部署后应立即修改

---

## 贡献规范

### 代码风格

- 后端：遵循 Java 编码规范（Google Style）
- 前端：使用 Prettier 自动格式化
- 所有代码需通过 TypeScript 类型检查

### 分支管理

- `main`：主分支，稳定版本
- `develop`：开发分支，日常开发
- `feature/xxx`：功能分支，开发新功能
- `bugfix/xxx`：修复分支，修复 bug

### 提交规范

```
<type>(<scope>): <description>

<optional body>

<optional footer>
```

**类型说明**：

| 类型 | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | 修复 bug |
| `docs` | 文档更新 |
| `style` | 代码格式调整 |
| `refactor` | 重构代码 |
| `test` | 测试相关 |
| `chore` | 构建/工具配置 |

### 开发流程

1. 从 `develop` 分支创建新分支
2. 完成开发并编写测试
3. 提交代码并创建 Pull Request
4. 通过代码审查后合并到 `develop`
5. 定期从 `develop` 合并到 `main`

---

## 联系方式

- **项目地址**：[https://github.com/Hinacore/TravelAI](https://github.com/Hinacore/TravelAI)
- **问题反馈**：[提交 Issue](https://github.com/Hinacore/TravelAI/issues)
- **贡献代码**：[提交 Pull Request](https://github.com/Hinacore/TravelAI/pulls)

---

## 更新日志

### v1.0.1 (2026-07-08)

- 初始版本发布
- 完成用户管理模块
- 完成 AI 行程推荐模块
- 完成行程管理模块
- 完成 AI 助手对话模块
- 集成 DeepSeek API
- 实现 JWT 认证
- 完成前后端联调
- 支持 Docker Compose 一键部署

---

