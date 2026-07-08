# TravelAI 智能旅游推荐与规划系统 — 简历项目经历

> 应聘岗位：Java 初级开发工程师

---

## 项目名称

**TravelAI 智能旅游推荐与规划系统**

## 技术栈

**后端**：Java 8、Spring Boot 2.7、Spring Security、Spring Data JPA、Spring WebFlux（WebClient）、JWT、Maven  
**数据库**：MySQL 8.0、Redis（缓存预留）  
**前端**：Vue3、TypeScript、Element Plus、Axios、Pinia、Vite  
**AI 服务**：DeepSeek API（大语言模型集成）  
**开发工具**：IntelliJ IDEA、Git、Postman、MySQL Workbench  
**其他**：RESTful API 设计、微服务架构、Docker

## 项目描述

- **项目背景与核心功能**：该项目是一个基于微服务架构的智能旅游规划平台，旨在通过 AI 大模型帮助用户自动生成个性化旅行行程。系统采用 Spring Boot 构建用户服务、行程服务、推荐服务、聊天服务 4 个独立微服务，实现用户注册登录（JWT 认证）、AI 行程生成（集成 DeepSeek API）、行程全生命周期管理（CRUD + 状态流转 + 分享）、AI 对话助手等核心功能，前端使用 Vue3 + TypeScript 构建交互界面。

- **个人职责与技术实现**：独立完成前后端全栈开发。后端基于 Spring Boot 2.7 搭建多模块 Maven 微服务工程，使用 Spring Data JPA 完成 7 张核心业务表的数据持久化与关联映射（用户-行程-每日活动-预算明细），通过 Spring Security + JWT 实现统一身份认证与接口鉴权，使用 WebClient 调用 DeepSeek API 并设计降级策略（API 不可用时自动返回模拟数据，保障服务可用性）；前端通过 Axios 拦截器统一处理 Token 注入与 401 自动跳转，使用 TypeScript 定义完整接口类型体系。

- **技术难点与成果优化**：解决多项工程实践问题——通过外部 .env 文件管理 API 密钥避免硬编码，使用 BCrypt 加密用户密码保障安全，设计全局异常处理器（@RestControllerAdvice）统一 API 响应格式；排查并修复了前端分页参数 1-based 与后端 JPA 0-based 不一致导致数据查不到的问题、Spring Security 未启用 CORS 导致跨域 403 的问题，以及 Vue 模板中 TypeScript 类型注解导致的编译警告问题。最终系统成功实现 AI 30 秒内生成包含每日景点、交通、预算的完整行程方案，代码已开源至 GitHub。

---

> **备注**：项目源码已托管于 GitHub：https://github.com/Hinacore/TravelAI
> 
> **文档资料**：项目附带完整的需求文档、技术架构文档、配置说明文档，体现规范的工程化开发流程。
