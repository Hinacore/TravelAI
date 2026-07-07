# TravelAI 智能旅游推荐与规划系统 — 配置说明文档

> 版本：1.0.0\
> 更新日期：2026-07-08\
> 状态：正式发布

***

## 1. 环境要求

### 1.1 开发环境要求

| 软件      | 版本要求 | 说明             |
| ------- | ---- | -------------- |
| JDK     | 1.8+ | Java 运行时环境     |
| Maven   | 3.6+ | Java 构建工具      |
| Node.js | 18+  | 前端构建环境         |
| npm     | 9+   | 前端包管理器         |
| MySQL   | 8.0+ | 关系型数据库         |
| Redis   | 6.x+ | 缓存服务（预留，当前可省略） |

### 1.2 推荐开发工具

| 工具                        | 用途         |
| ------------------------- | ---------- |
| IntelliJ IDEA / VS Code   | 后端/前端开发    |
| Trae IDE                  | 全栈开发（当前使用） |
| Navicat / MySQL Workbench | 数据库管理      |
| Postman                   | API 测试     |

***

## 2. 前端项目配置

### 2.1 项目路径

```
e:\AAA\test\frontend\
```

### 2.2 安装依赖

```bash
cd e:\AAA\test\frontend
npm install
```

### 2.3 启动命令

```bash
# 开发模式（热更新，默认端口 5173）
npm run dev

# 生产构建（先 TypeScript 类型检查，再 Vite 构建）
npm run build

# 预览生产构建结果
npm run preview
```

### 2.4 开发环境配置

#### 2.4.1 Vite 配置文件

**文件路径**：`e:\AAA\test\frontend\vite.config.ts`

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,        // 开发服务器端口
    open: false        // 是否自动打开浏览器
  }
})
```

#### 2.4.2 TypeScript 配置

**文件路径**：`e:\AAA\test\frontend\tsconfig.json`

引用了两个子配置：

- `tsconfig.app.json` — 应用代码配置
- `tsconfig.node.json` — Node.js 环境配置

#### 2.4.3 API 服务地址配置

**文件路径**：`e:\AAA\test\frontend\src\api\index.ts`

```typescript
const userApiClient = createApi('http://localhost:8091')      // 用户服务
const tripApiClient = createApi('http://localhost:8092')      // 行程服务
const recommendApiClient = createApi('http://localhost:8093') // 推荐服务
const chatApiClient = createApi('http://localhost:8094')      // 聊天服务
```

> **如需修改后端服务地址**，请修改以上 4 行代码中的 URL。

#### 2.4.4 Element Plus 中文语言包配置

**文件路径**：`e:\AAA\test\frontend\src\main.ts`

```typescript
import zhCn from 'element-plus/es/locale/lang/zh-cn'

app.use(ElementPlus, {
  locale: zhCn
})
```

***

## 3. 后端项目配置

### 3.1 项目路径

```
e:\AAA\test\backend\
```

### 3.2 构建命令

```bash
# 编译所有模块（根目录执行）
cd e:\AAA\test\backend
mvn clean compile

# 打包所有模块
mvn clean package -DskipTests

# 安装 common 模块到本地 Maven 仓库（其他模块依赖它）
cd e:\AAA\test\backend\travelai-common
mvn install -DskipTests -q
```

### 3.3 启动命令

每个微服务需要单独启动，建议在 4 个不同的终端中执行：

```bash
# 终端1：启动用户服务 (:8091)
cd e:\AAA\test\backend\travelai-user
mvn spring-boot:run

# 终端2：启动行程服务 (:8092)
cd e:\AAA\test\backend\travelai-trip
mvn spring-boot:run

# 终端3：启动推荐服务 (:8093)
cd e:\AAA\test\backend\travelai-recommend
mvn spring-boot:run

# 终端4：启动聊天服务 (:8094)
cd e:\AAA\test\backend\travelai-chat
mvn spring-boot:run
```

> **注意**：首次启动或 common 模块有更新时，必须先执行 `mvn install` 安装 common 模块。

### 3.4 数据库配置

#### 3.4.1 数据库连接信息

所有微服务使用相同的数据库配置：

| 配置项   | 值                |
| ----- | ---------------- |
| 数据库地址 | `localhost:3306` |
| 数据库名  | `travelai`       |
| 用户名   | `root`           |
| 密码    | `1234`           |

#### 3.4.2 数据库密码修改文件路径

**需要修改的文件清单**（4个模块的 application.yml）：

| 文件路径                                                                        | 行号     |
| --------------------------------------------------------------------------- | ------ |
| `e:\AAA\test\backend\travelai-user\src\main\resources\application.yml`      | 第 10 行 |
| `e:\AAA\test\backend\travelai-trip\src\main\resources\application.yml`      | 第 10 行 |
| `e:\AAA\test\backend\travelai-recommend\src\main\resources\application.yml` | 第 10 行 |
| `e:\AAA\test\backend\travelai-chat\src\main\resources\application.yml`      | 第 10 行 |

**修改方式**：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/travelai?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的数据库密码    # ← 修改这里
    driver-class-name: com.mysql.cj.jdbc.Driver
```

#### 3.4.3 数据库初始化

**SQL 脚本路径**：`e:\AAA\test\backend\sql\init.sql`

```bash
# 登录 MySQL 并执行初始化脚本
mysql -u root -p < e:\AAA\test\backend\sql\init.sql
```

此脚本将：

- 创建 `travelai` 数据库
- 创建所有 7 张表（users, trips, trip\_days, trip\_activities, trip\_budget, conversations, messages）
- 插入默认管理员账户（用户名: admin，密码: admin）

#### 3.4.4 Redis 配置

**密码修改位置**：每个模块的 `application.yml` 第 27 行：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 123456    # ← 修改为你的 Redis 密码
```

> **注意**：如 Redis 无密码，将 `password` 行删除或留空。

### 3.5 JWT 配置

**配置位置**：每个模块的 `application.yml`：

```yaml
jwt:
  secret: travelai-secret-key-2026    # JWT 密钥，建议修改
  expiration: 7200000                  # Token 有效期（毫秒），7200000 = 2小时
```

> **安全提醒**：生产环境务必修改 `jwt.secret` 为复杂的随机字符串。

### 3.6 各微服务端口配置

| 服务   | 配置文件路径                                                  | 端口   |
| ---- | ------------------------------------------------------- | ---- |
| 用户服务 | `travelai-user/src/main/resources/application.yml`      | 8091 |
| 行程服务 | `travelai-trip/src/main/resources/application.yml`      | 8092 |
| 推荐服务 | `travelai-recommend/src/main/resources/application.yml` | 8093 |
| 聊天服务 | `travelai-chat/src/main/resources/application.yml`      | 8094 |

修改端口：

```yaml
server:
  port: 8091    # ← 修改端口号
```

***

## 4. API 密钥（AI Key）配置

### 4.1 密钥存放路径

```
e:\AAA\test\AI_KEY\DEEPSEEK_API_KEY.env
```

### 4.2 环境变量文件内容

base\_url (OpenAI)=https\://api.deepseek.com

DEEPSEEK_APIKEY=你的api_ key

| 变量名                 | 说明         |
| ------------------- | ---------- |
| `base_url (OpenAI)` | API 基础 URL |
| `DEEPSEEK_API_KEY`  |  API 密钥    |

### 4.3 获取 API Key

1. 访问 DeepSeek 开放平台：<https://platform.deepseek.com/>
2. 注册并登录账号
3. 在 API Keys 页面创建新的 API Key
4. 复制 Key 值（格式：`sk-xxxxxxxx`）
5. 替换 `.env` 文件中的 `DEEPSEEK_API_KEY` 值

### 4.4 后端读取逻辑

**文件路径**：`e:\AAA\test\backend\travelai-common\src\main\java\com\travelai\common\config\EnvConfig.java`

读取流程：

```
1. 启动时执行 @PostConstruct init() 方法
2. 计算路径：当前工作目录 → 上两级 → AI_KEY/DEEPSEEK_API_KEY.env
   即：e:\AAA\test\backend\travelai-xxx → e:\AAA\test\AI_KEY\DEEPSEEK_API_KEY.env
3. 读取 .env 文件，解析 Properties
4. 提取 DEEPSEEK_API_KEY 和 base_url (OpenAI)
5. 如果文件不存在，回退到 application.yml 中的配置值
6. 如果都没有，使用模拟数据（不调用真实 API）
```

### 4.5 重要注意事项

- **禁止**将 API Key 硬编码到任何 Java 源代码或前端代码中
- **禁止**将 `.env` 文件提交到 Git 版本控制
- `.env` 文件必须放在项目根目录的 `AI_KEY` 文件夹中
- 如果部署到服务器，需确保运行 Maven 的用户有权限读取该文件

***

## 5. 需要开发者手动修改的配置清单

### 5.1 首次部署必须修改的配置

| 序号 | 配置项              | 文件路径                          | 当前默认值                      | 修改说明                    |
| -- | ---------------- | ----------------------------- | -------------------------- | ----------------------- |
| 1  | MySQL 密码         | 4个模块的 application.yml         | `123456`                   | 改为你的实际 MySQL 密码         |
| 2  | Redis 密码         | 4个模块的 application.yml         | `123456`                   | 改为你的实际 Redis 密码（无密码则删除） |
| 3  | JWT 密钥           | 4个模块的 application.yml         | `travelai-secret-key-2026` | 生产环境务必修改                |
| 4  | DeepSeek API Key | `AI_KEY/DEEPSEEK_API_KEY.env` | `sk-4bbe...`               | 替换为你自己的 API Key         |

### 5.2 可选修改的配置

| 序号 | 配置项        | 文件路径                        | 当前默认值            | 修改说明                |
| -- | ---------- | --------------------------- | ---------------- | ------------------- |
| 1  | 前端开发端口     | `frontend/vite.config.ts`   | `5173`           | 如端口冲突可修改            |
| 2  | 后端服务端口     | 4个模块的 application.yml       | 8091-8094        | 如端口冲突可修改            |
| 3  | 前端 API 地址  | `frontend/src/api/index.ts` | `localhost:809x` | 部署到服务器时需修改          |
| 4  | JWT 过期时间   | 4个模块的 application.yml       | `7200000`(2小时)   | 按需调整                |
| 5  | JPA DDL 模式 | 4个模块的 application.yml       | `update`         | 生产环境建议改为 `validate` |
| 6  | SQL 日志     | 4个模块的 application.yml       | `true`           | 生产环境建议关闭            |

***

## 6. 完整启动流程

### 6.1 首次部署流程

```
步骤1：安装环境
  → 安装 JDK 1.8+
  → 安装 Maven 3.6+
  → 安装 Node.js 18+
  → 安装 MySQL 8.0+
  → 安装 Redis 6.x（可选）

步骤2：配置数据库
  → 修改4个模块 application.yml 中的 MySQL 密码
  → 执行 init.sql 初始化数据库
  → mysql -u root -p < e:\AAA\test\backend\sql\init.sql

步骤3：配置 API Key
  → 确认 e:\AAA\test\AI_KEY\DEEPSEEK_API_KEY.env 文件存在
  → 替换其中的 API Key 为你自己的

步骤4：构建后端
  → cd e:\AAA\test\backend
  → mvn clean compile
  → cd travelai-common && mvn install -DskipTests -q

步骤5：启动后端服务（4个终端）
  → 终端1: cd travelai-user && mvn spring-boot:run
  → 终端2: cd travelai-trip && mvn spring-boot:run
  → 终端3: cd travelai-recommend && mvn spring-boot:run
  → 终端4: cd travelai-chat && mvn spring-boot:run

步骤6：启动前端
  → cd e:\AAA\test\frontend
  → npm install
  → npm run dev

步骤7：访问系统
  → 打开浏览器访问 http://localhost:5173
  → 使用 admin/admin 登录，或注册新账户
```

### 6.2 日常开发启动流程

```
1. 启动 MySQL 服务
2. 启动后端4个微服务
3. 启动前端开发服务器 (npm run dev)
4. 访问 http://localhost:5173
```

### 6.3 停止服务

```bash
# 停止所有 Java 进程
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force

# 停止前端开发服务器
# 在前端终端按 Ctrl+C
```

***

## 7. 常见问题排查

### 7.1 端口被占用

```bash
# 查看端口占用
netstat -ano | findstr :8091

# 杀死占用进程
taskkill /PID <进程ID> /F
```

### 7.2 common 模块找不到

```
错误：NoClassDefFoundError: com.travelai.common.config.EnvConfig
解决：
  cd e:\AAA\test\backend\travelai-common
  mvn install -DskipTests -q
  然后重新启动报错的服务
```

### 7.3 数据库连接失败

```
检查项：
  1. MySQL 服务是否启动
  2. 数据库密码是否正确
  3. travelai 数据库是否存在
  4. application.yml 中的连接 URL 是否正确
```

### 7.4 DeepSeek API 调用失败

```
检查项：
  1. AI_KEY/DEEPSEEK_API_KEY.env 文件是否存在
  2. API Key 是否有效
  3. 网络是否能访问 https://api.deepseek.com
  4. 查看后端日志是否有 "DeepSeek API key loaded successfully"
```

### 7.5 前端 CORS 错误

```
检查项：
  1. 后端 SecurityConfig 是否配置了 .cors()
  2. CorsConfig 是否允许前端域名和端口
  3. 后端服务是否正常启动
```

### 7.6 前端 403 错误

```
原因：Spring Security 拦截了请求
解决：
  1. 检查 SecurityConfig 中的公开路径配置
  2. 确认请求路径在 permitAll() 列表中
  3. 检查请求是否携带有效的 JWT Token
```

***

## 8. 默认账户

| 用户名   | 密码    | 角色    | 说明                   |
| ----- | ----- | ----- | -------------------- |
| admin | admin | ADMIN | 默认管理员账户（init.sql 创建） |

> **安全提醒**：生产环境部署后请立即修改默认密码。

