# 经营数据分析系统

## 项目简介
本系统旨在整合外部系统（外勤365）的业务数据与基础数据，通过数据清洗、存储、分析与可视化，实现区域维度、员工维度、商品维度的经营数据透视。系统支持从OpenAPI和Excel文件双通道接入数据，提供多维度的经营分析报表与图表，覆盖PC端与移动端，辅助管理层决策。

## 技术栈
- 前端：Vue + Element Plus（PC端） + Vant（移动端） + ECharts（图表渲染）
- 后端：Spring Boot 3 + MyBatis Plus + Redis + Quartz
- 数据库：MySQL + MongoDB
- 部署：Docker + Nginx + Kubernetes

## 功能特性
1. 数据接入
   - OpenAPI实时数据接入
   - Excel文件批量导入
   - 数据清洗与校验

2. 数据分析
   - 区域维度分析
   - 员工维度分析
   - 商品维度分析

3. 数据可视化
   - 多维度图表展示
   - 自定义报表生成
   - 数据导出功能

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

### 数据库初始化
1. 创建数据库：
```sql
CREATE DATABASE insight_flow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p insight_flow < database/init.sql
```

### 后端服务启动
1. 修改配置：
编辑 `backend/src/main/resources/application.yml`，配置数据库连接等信息。

2. 启动服务：
```bash
cd backend
mvn spring-boot:run
```

### 前端开发
1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

## 项目结构
```
insight-flow/
├── backend/                # 后端服务
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/      # Java源代码
│   │   │   └── resources/ # 配置文件
│   │   └── test/          # 测试代码
│   └── pom.xml            # Maven配置
├── frontend/              # 前端项目
│   ├── src/              # 源代码
│   ├── public/           # 静态资源
│   └── package.json      # 依赖配置
├── database/             # 数据库脚本
└── README.md            # 项目文档
```

## API文档
启动后端服务后，访问：http://localhost:8080/api/swagger-ui/index.html

## 开发规范
1. 代码规范
   - 遵循阿里巴巴Java开发手册
   - 使用统一的代码格式化工具
   - 编写单元测试

2. 提交规范
   - feat: 新功能
   - fix: 修复bug
   - docs: 文档更新
   - style: 代码格式
   - refactor: 重构
   - test: 测试用例
   - chore: 其他修改

## 部署说明
1. 构建Docker镜像
```bash
docker build -t insight-flow:latest .
```

2. 启动容器
```bash
docker-compose up -d
```

## 贡献指南
1. Fork 本仓库
2. 创建特性分支
3. 提交代码
4. 创建 Pull Request

## 许可证
MIT License 