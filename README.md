# 智旅AI - AI智能旅游规划平台

基于Vue 2 + Spring Boot构建的AI智能旅游规划平台，集成高德地图、DeepSeek AI等服务。

## 技术栈

### 前端
- Vue 2.7
- Vite
- Element UI
- Axios

### 后端
- Spring Boot 2.7
- Spring Security
- MyBatis Plus
- SQLite
- JWT

## 功能特性

- ✅ AI智能攻略生成
- ✅ 高德地图导航
- ✅ 景点天气查询
- ✅ 用户注册登录
- ✅ 景点图片上传
- ✅ 用户评论系统

## 快速开始

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

后端服务运行在 `http://localhost:8080`

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务运行在 `http://localhost:8081`

## API配置

在 `backend/src/main/resources/application.yml` 中配置：

- 高德地图API Key
- DeepSeek AI API Key

## 项目结构

```
├── backend/          # Spring Boot后端
│   ├── src/main/java/com/travel/ai/
│   ├── src/main/resources/
│   └── pom.xml
├── frontend/         # Vue前端
│   ├── src/
│   └── package.json
└── .gitignore
```
