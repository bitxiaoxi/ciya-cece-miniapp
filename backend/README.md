# AI 测词汇量后端 MVP

独立后端模块位置：`backend/`。

## 技术栈
- Spring Boot 2.7.18
- Java 8
- Maven
- MySQL 8
- MyBatis-Plus + XML Mapper
- Lombok
- Spring Validation

## 目录说明
- `src/main/java/com/ciya/cece/assessment/controller`：接口层
- `src/main/java/com/ciya/cece/assessment/service`：服务接口
- `src/main/java/com/ciya/cece/assessment/service/impl`：服务实现
- `src/main/java/com/ciya/cece/assessment/mapper`：Mapper 接口
- `src/main/resources/mapper`：Mapper XML
- `src/main/resources/schema.sql`：建表脚本
- `src/main/resources/data.sql`：初始化数据

## 数据库初始化
1. 创建数据库：
```sql
CREATE DATABASE ciya_cece_assessment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
2. 执行脚本：
```bash
mysql -uroot -p ciya_cece_assessment < src/main/resources/schema.sql
mysql -uroot -p ciya_cece_assessment < src/main/resources/data.sql
```

## application.yml
默认使用以下环境变量：

```yaml
ASSESSMENT_SERVER_PORT=8080
ASSESSMENT_DB_URL=jdbc:mysql://127.0.0.1:3306/ciya_cece_assessment?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
ASSESSMENT_DB_USERNAME=root
ASSESSMENT_DB_PASSWORD=root
```

## 启动项目
```bash
cd backend
mvn clean test
mvn -DskipTests spring-boot:run
```

## 接口列表

### 学生档案
- `POST /api/students`
```json
{
  "studentName": "林小芽",
  "gradeCode": "P2",
  "birthYear": 2018
}
```
- `GET /api/students/{id}`
- `GET /api/students`
- `PUT /api/students/{id}`

### 学段规则
- `GET /api/assessment/stages`

### 开始测评
- `POST /api/assessment/start`
```json
{
  "studentId": 1,
  "selectedStageCode": "P3_4",
  "aiEnabled": true
}
```

### 获取下一题
- `GET /api/assessment/{sessionNo}/next-question`

### 提交答案
- `POST /api/assessment/{sessionNo}/answer`
```json
{
  "itemId": 21,
  "selectedOptionId": 1083,
  "answerStatus": "CORRECT",
  "responseTimeMs": 3200
}
```

`answerStatus=UNCERTAIN` 或 `SKIP` 时，后端保留该状态；其他情况以后端对正确选项的复核结果为准。

### 完成测评
- `POST /api/assessment/{sessionNo}/finish`

### 获取结果
- `GET /api/assessment/{sessionNo}/result`

### 获取历史
- `GET /api/assessment/history?studentId=1`

## 当前占位实现
- AI 模型调用：使用 `FakeAiExplainService`，只重写结果解释文案，不参与数值判定。
- 题库调优：`difficulty_score`、`discrimination_score` 为 MVP 种子值，后续可以根据真实答题数据继续校准。
- 结果映射：`result_mapping_json` 已启用 JSON 解析，但词汇量区间仍为 MVP 样例规则，可继续精细化。
