# RelicAdmin 前后端接口文档

> **版本**: v2.0\
> **最后更新**: 2026-05-10\
> **适用环境**: 开发 / 测试 / 生产\
> **基础URL**: `http://localhost:8080`

***

## 目录

1. [接口规范（必读）](#1-接口规范必读)
   - 1.1 [统一响应格式](#11-统一响应格式)
   - 1.2 [分页响应格式](#12-分页响应格式)
   - 1.3 [认证方式](#13-认证方式)
   - 1.4 [请求头要求](#14-请求头要求)
   - 1.5 [数据格式规范](#15-数据格式规范)
   - 1.6 [接口版本控制](#16-接口版本控制)
   - 1.7 [调用频率限制](#17-调用频率限制)
2. [错误码汇总](#2-错误码汇总)
3. [认证模块 - Admin](#3-认证模块---admin)
4. [用户管理模块](#4-用户管理模块)
5. [角色管理模块](#5-角色管理模块)
6. [权限管理模块](#6-权限管理模块)
7. [博物馆管理模块](#7-博物馆管理模块)
8. [朝代管理模块](#8-朝代管理模块)
9. [艺术家管理模块](#9-艺术家管理模块)
10. [地点管理模块](#10-地点管理模块)
11. [文物管理模块](#11-文物管理模块)
12. [文物多图片模块](#12-文物多图片模块)
13. [用户收藏模块](#13-用户收藏模块)
14. [用户点赞模块](#14-用户点赞模块)
15. [评论模块](#15-评论模块)
16. [用户动态模块](#16-用户动态模块)
17. [用户关注模块](#17-用户关注模块)
18. [用户上传模块](#18-用户上传模块)
19. [审核管理模块](#19-审核管理模块)
20. [违规处罚模块](#20-违规处罚模块)
21. [申诉管理模块](#21-申诉管理模块)
22. [公告管理模块](#22-公告管理模块)
23. [通知模块](#23-通知模块)
24. [敏感词库模块](#24-敏感词库模块)
25. [爬取任务模块](#25-爬取任务模块)
26. [系统配置模块](#26-系统配置模块)
27. [备份管理模块](#27-备份管理模块)
28. [日志管理模块](#28-日志管理模块)
29. [文件上传（OSS）模块](#29-文件上传oss模块)
30. [数据统计模块](#30-数据统计模块)
31. [附录](#31-附录)

***

## 1. 接口规范（必读）

### 1.1 统一响应格式

所有接口均返回 **JSON** 格式，统一数据结构如下：

```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

| 字段   | 类型            | 说明                                                                                           |
| ---- | ------------- | -------------------------------------------------------------------------------------------- |
| code | Integer       | **200** = 成功；**401** = 未认证；**403** = 权限不足；**404** = 资源不存在；**400** = 参数校验失败；**500** = 服务器内部错误 |
| msg  | String        | 提示信息，成功时为空字符串 `""`，失败时为具体错误原因                                                                |
| data | Object / null | 响应数据体，无数据时为 `null`                                                                           |

**成功示例（无数据）：**

```json
{ "code": 200, "msg": "", "data": null }
```

**失败示例：**

```json
{ "code": 500, "msg": "密码错误", "data": null }
```

***

### 1.2 分页响应格式

列表类接口统一使用分页查询，返回格式如下：

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 100,
    "records": [],
    "page": 1,
    "pageSize": 10
  }
}
```

| 字段       | 类型      | 说明           |
| -------- | ------- | ------------ |
| total    | Long    | 符合筛选条件的总记录数  |
| records  | Array   | 当前页数据列表      |
| page     | Integer | 当前页码（从 1 开始） |
| pageSize | Integer | 每页数据条数       |

***

### 1.3 认证方式

本项目采用 **JWT（JSON Web Token）** 无状态认证，分为两套独立的认证体系：

#### 1.3.1 管理端认证（Admin）

| 项目          | 说明                                         |
| ----------- | ------------------------------------------ |
| Token 请求头名称 | `token`                                    |
| 签名秘钥        | `itcast`（配置于 `relic.jwt.admin-secret-key`） |
| 有效期         | 7200000 毫秒（2 小时）                           |
| 拦截路径        | `/admin/**`                                |
| 白名单路径       | `/admin/employee/login`                    |

#### 1.3.2 用户端认证（User）

| 项目          | 说明                                        |
| ----------- | ----------------------------------------- |
| Token 请求头名称 | `authentication`                          |
| 签名秘钥        | `seitem`（配置于 `relic.jwt.user-secret-key`） |
| 有效期         | 7200000 毫秒（2 小时）                          |
| 拦截路径        | `/user/**`                                |
| 白名单路径       | `/user/user/login`、`/user/shop/status`    |

**Token 过期处理**：前端应拦截 HTTP 401 状态码，自动跳转登录页。

***

### 1.4 请求头要求

所有需要认证的接口必须携带对应 Token：

| 请求头 Key          | 适用端 | 说明                                   |
| ---------------- | --- | ------------------------------------ |
| `token`          | 管理端 | Admin JWT Token                      |
| `authentication` | 用户端 | User JWT Token                       |
| `Content-Type`   | 公共  | 固定值 `application/json;charset=UTF-8` |

**管理端示例：**

```
POST /admin/artifact/page
Content-Type: application/json;charset=UTF-8
token: eyJhbGciOiJIUzI1NiJ9...
```

**用户端示例：**

```
GET /user/artifact/detail/1
Content-Type: application/json;charset=UTF-8
authentication: eyJhbGciOiJIUzI1NiJ9...
```

***

### 1.5 数据格式规范

#### 1.5.1 JSON 请求/响应格式

- **Content-Type**: `application/json;charset=UTF-8`
- **编码**: UTF-8
- **空值处理**: `null` 字段在序列化时不省略（即返回 `"field": null`）

#### 1.5.2 时间日期格式

| 类型                  | 格式                 | 示例                 |
| ------------------- | ------------------ | ------------------ |
| LocalDate（日期）       | `yyyy-MM-dd`       | `2026-05-10`       |
| LocalDateTime（日期时间） | `yyyy-MM-dd HH:mm` | `2026-05-10 14:30` |

#### 1.5.3 布尔值

本项目中 TINYINT(0/1) 数据库字段在 Java 实体中使用 `Integer` 类型映射，**整型值 0 表示 false，1 表示 true**。

#### 1.5.4 空值 / 可选字段

- 可选字段若前端不传，后端接收为 `null`，不得传空字符串 `""` 代替 `null`
- 整型数值字段若不需要，请求体中不传该键或传 `null`

#### 1.5.5 枚举值规范

字符串枚举值统一使用**英文小写**：

| 枚举字段                 | 可用值                                      |
| -------------------- | ---------------------------------------- |
| 用户状态 `status`        | `active` / `disabled` / `banned`         |
| 审核状态 `status`        | `pending` / `approved` / `rejected`      |
| 地点类型 `type`          | `country` / `province` / `city` / `site` |
| 媒体类型 `mediaType`     | `image` / `audio` / `video`              |
| 日志级别 `logLevel`      | `DEBUG` / `INFO` / `WARN` / `ERROR`      |
| 操作类型 `operationType` | `INSERT` / `UPDATE` / `DELETE`           |
| 爬取状态 `status`        | `running` / `success` / `failed`         |
| 备份类型 `backupType`    | `full` / `incremental` / `export`        |
| 处罚类型 `penaltyType`   | `warning` / `temp_ban` / `permanent_ban` |
| 违规等级 `severityLevel` | `1`(轻微) / `2`(一般) / `3`(严重) / `4`(特别严重)  |

***

### 1.6 接口版本控制

- **当前版本**: v2.0
- **版本策略**: URL 路径前缀版本控制
- **URL 格式**: `/{version}/{module}/{action}`，当前版本未在 URL 中显式声明，后续可通过 `interceptor` 或 `gateway` 统一加入 `/v2/` 前缀
- **兼容性**: 向后兼容，废弃接口至少保留一个大版本周期

***

### 1.7 调用频率限制

| 限制级别     | 限制规则                | 适用对象        |
| -------- | ------------------- | ----------- |
| **匿名用户** | 同一 IP：60 次 / 分钟     | 登录、注册、公开查询  |
| **普通用户** | 同一 Token：120 次 / 分钟 | 浏览、收藏、点赞、评论 |
| **管理员**  | 同一 Token：300 次 / 分钟 | 后台管理操作      |

***

## 2. 错误码汇总

| HTTP Code | 业务 Code | 错误信息（msg）      | 说明          |
| --------- | ------- | -------------- | ----------- |
| 200       | 200     | `""`           | 请求成功        |
| 200       | 200     | `"操作成功"`       | 请求成功（有提示）   |
| 400       | 500     | `"参数校验失败"`     | 请求参数不合法     |
| 401       | 500     | `"用户未登录"`      | Token 缺失或无效 |
| 401       | 500     | `"登录失败"`       | 登录凭证错误      |
| 400       | 500     | `"密码错误"`       | 密码不匹配       |
| 404       | 500     | `"账号不存在"`      | 账号未注册       |
| 403       | 500     | `"账号被锁定"`      | 账号已封禁       |
| 409       | 500     | `"账号已存在"`      | 唯一约束冲突      |
| 403       | 500     | `"权限不足"`       | 角色无操作权限     |
| 404       | 500     | `"角色不存在"`      | 角色ID无效      |
| 409       | 500     | `"角色名称已存在"`    | 角色名重复       |
| 404       | 500     | `"敏感词不存在"`     | 敏感词ID无效     |
| 409       | 500     | `"敏感词已存在"`     | 敏感词已入库      |
| 404       | 500     | `"审核内容不存在"`    | 审核记录ID无效    |
| 400       | 500     | `"拒绝时必须填写原因"`  | 审核拒绝理由未填    |
| 404       | 500     | `"文物不存在"`      | 文物ID无效      |
| 400       | 500     | `"导入文件格式错误"`   | 批量导入文件格式不支持 |
| 500       | 500     | `"文件上传失败"`     | OSS上传异常     |
| 500       | 500     | `"密码修改失败"`     | 修改密码操作异常    |
| 500       | 500     | `"备份失败"`       | 数据库备份异常     |
| 400       | 500     | `"数据恢复需要二次确认"` | 恢复操作未二次确认   |
| 500       | 500     | `"未知错误"`       | 服务器内部未知异常   |

***

## 3. 认证模块 - Admin

### 3.1 管理员登录

**接口描述**: 管理员使用用户名和密码登录，成功返回 JWT Token。

| 项目      | 内容                      |
| ------- | ----------------------- |
| **URL** | `/admin/employee/login` |
| **方法**  | `POST`                  |
| **认证**  | 无需认证（白名单）               |

**请求参数（Body - JSON）**:

```json
{
  "username": "admin",
  "password": "123456"
}
```

| 参数       | 类型     | 必填 | 默认值 | 说明                |
| -------- | ------ | -- | --- | ----------------- |
| username | String | 是  | -   | 管理员用户名            |
| password | String | 是  | -   | 明文密码（传输层应HTTPS加密） |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

| 字段       | 类型      | 说明                        |
| -------- | ------- | ------------------------- |
| id       | Integer | 管理员用户ID                   |
| username | String  | 用户名                       |
| nickname | String  | 昵称                        |
| token    | String  | JWT Token（请求头 `token` 携带） |

**错误信息**:

| msg     | 说明           |
| ------- | ------------ |
| `账号不存在` | 用户名在系统中未找到   |
| `密码错误`  | 密码验证失败       |
| `账号被锁定` | 用户状态为 banned |
| `登录失败`  | 其他异常         |

**请求示例**:

```bash
curl -X POST http://localhost:8080/admin/employee/login \
  -H "Content-Type: application/json;charset=UTF-8" \
  -d '{"username":"admin","password":"123456"}'
```

***

### 3.2 管理员退出登录

**接口描述**: 管理员安全退出，后端清除当前会话上下文。

| 项目      | 内容                       |
| ------- | ------------------------ |
| **URL** | `/admin/employee/logout` |
| **方法**  | `POST`                   |
| **认证**  | 需要 Admin Token           |

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": null
}
```

***

## 4. 用户管理模块

> **注意**: 以下 `/admin/**` 接口需 Admin Token，`/user/**` 接口需 User Token。

### 4.1 用户注册

| 项目      | 内容                    |
| ------- | --------------------- |
| **URL** | `/user/user/register` |
| **方法**  | `POST`                |
| **认证**  | 无需认证                  |

**请求参数（Body - JSON）**:

```json
{
  "username": "zhangsan",
  "password": "aBc@123456",
  "email": "zhangsan@example.com",
  "phone": "13800138000",
  "nickname": "张三",
  "source": "web"
}
```

| 参数       | 类型     | 必填 | 默认值   | 说明                 |
| -------- | ------ | -- | ----- | ------------------ |
| username | String | 是  | -     | 用户名，长度 4\~50 字符    |
| password | String | 是  | -     | 明文密码，长度 6\~20 字符   |
| email    | String | 否  | null  | 邮箱地址               |
| phone    | String | 否  | null  | 手机号                |
| nickname | String | 否  | null  | 用户昵称               |
| source   | String | 否  | `web` | 注册来源：`web` / `app` |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "id": 10,
    "username": "zhangsan",
    "nickname": "张三"
  }
}
```

***

### 4.2 用户登录

| 项目      | 内容                 |
| ------- | ------------------ |
| **URL** | `/user/user/login` |
| **方法**  | `POST`             |
| **认证**  | 无需认证（白名单）          |

**请求参数（Body - JSON）**:

```json
{
  "username": "zhangsan",
  "password": "aBc@123456"
}
```

| 参数       | 类型     | 必填 | 默认值 | 说明  |
| -------- | ------ | -- | --- | --- |
| username | String | 是  | -   | 用户名 |
| password | String | 是  | -   | 密码  |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "id": 10,
    "username": "zhangsan",
    "nickname": "张三",
    "avatarUrl": "/uploads/avatar/10.jpg",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

| 字段        | 类型      | 说明                                      |
| --------- | ------- | --------------------------------------- |
| id        | Integer | 用户ID                                    |
| username  | String  | 用户名                                     |
| nickname  | String  | 昵称                                      |
| avatarUrl | String  | 头像URL                                   |
| token     | String  | User JWT Token（请求头 `authentication` 携带） |

***

### 4.3 获取当前用户信息

| 项目      | 内容                |
| ------- | ----------------- |
| **URL** | `/user/user/info` |
| **方法**  | `GET`             |
| **认证**  | 需要 User Token     |

**请求参数**: 无（从 Token 解析 user\_id）

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "id": 10,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "avatarUrl": "/uploads/avatar/10.jpg",
    "nickname": "张三",
    "status": "active",
    "registeredAt": "2026-05-10 10:30",
    "lastLogin": "2026-05-10 14:20",
    "source": "web"
  }
}
```

***

### 4.4 更新用户信息

| 项目      | 内容                  |
| ------- | ------------------- |
| **URL** | `/user/user/update` |
| **方法**  | `PUT`               |
| **认证**  | 需要 User Token       |

**请求参数（Body - JSON）**:

```json
{
  "nickname": "张三丰",
  "email": "zhangsanfeng@example.com",
  "phone": "13900139000"
}
```

| 参数       | 类型     | 必填 | 默认值  | 说明                |
| -------- | ------ | -- | ---- | ----------------- |
| nickname | String | 否  | null | 昵称（不更新则不传或传 null） |
| email    | String | 否  | null | 邮箱                |
| phone    | String | 否  | null | 手机号               |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": null
}
```

***

### 4.5 修改密码

| 项目      | 内容                    |
| ------- | --------------------- |
| **URL** | `/user/user/password` |
| **方法**  | `PUT`                 |
| **认证**  | 需要 User Token         |

**请求参数（Body - JSON）**:

```json
{
  "oldPassword": "aBc@123456",
  "newPassword": "XyZ@654321"
}
```

| 参数          | 类型     | 必填 | 默认值 | 说明           |
| ----------- | ------ | -- | --- | ------------ |
| oldPassword | String | 是  | -   | 原密码          |
| newPassword | String | 是  | -   | 新密码，长度 6\~20 |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": null
}
```

***

### 4.6 上传用户头像

| 项目               | 内容                    |
| ---------------- | --------------------- |
| **URL**          | `/user/user/avatar`   |
| **方法**           | `POST`                |
| **认证**           | 需要 User Token         |
| **Content-Type** | `multipart/form-data` |

**请求参数（Form-Data）**:

| 参数   | 类型   | 必填 | 默认值 | 说明                    |
| ---- | ---- | -- | --- | --------------------- |
| file | File | 是  | -   | 图片文件（jpg / png，≤ 2MB） |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "avatarUrl": "/uploads/avatar/10_20260510143022.jpg"
  }
}
```

***

### 4.7 管理员 - 分页查询用户列表

| 项目      | 内容                 |
| ------- | ------------------ |
| **URL** | `/admin/user/page` |
| **方法**  | `GET`              |
| **认证**  | 需要 Admin Token     |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明                                    |
| -------- | ------- | -- | ---- | ------------------------------------- |
| page     | Integer | 否  | 1    | 当前页码                                  |
| pageSize | Integer | 否  | 10   | 每页条数                                  |
| username | String  | 否  | null | 用户名模糊搜索                               |
| nickname | String  | 否  | null | 昵称模糊搜索                                |
| status   | String  | 否  | null | 状态筛选：`active` / `disabled` / `banned` |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 256,
    "records": [
      {
        "id": 10,
        "username": "zhangsan",
        "nickname": "张三",
        "email": "zhangsan@example.com",
        "phone": "13800138000",
        "avatarUrl": "/uploads/avatar/10.jpg",
        "status": "active",
        "registeredAt": "2026-05-10 10:30",
        "lastLogin": "2026-05-10 14:20",
        "source": "web"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 4.8 管理员 - 封禁 / 解封用户

| 项目      | 内容                         |
| ------- | -------------------------- |
| **URL** | `/admin/user/ban/{userId}` |
| **方法**  | `PUT`                      |
| **认证**  | 需要 Admin Token             |

**请求参数（Path + Body）**:

| 参数     | 类型      | 必填 | 默认值 | 说明        |
| ------ | ------- | -- | --- | --------- |
| userId | Integer | 是  | -   | 路径参数，用户ID |

```json
{
  "status": "banned",
  "banReason": "发布违规内容"
}
```

| 参数        | 类型     | 必填 | 默认值  | 说明                               |
| --------- | ------ | -- | ---- | -------------------------------- |
| status    | String | 是  | -    | `active` / `disabled` / `banned` |
| banReason | String | 否  | null | 封禁原因（status=banned 时建议填写）        |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": null
}
```

***

### 4.9 管理员 - 禁止/解除用户评论权限

| 项目      | 内容                                     |
| ------- | -------------------------------------- |
| **URL** | `/admin/user/comment-disable/{userId}` |
| **方法**  | `PUT`                                  |
| **认证**  | 需要 Admin Token                         |

**请求参数（Body - JSON）**:

```json
{
  "commentDisabled": 1
}
```

| 参数              | 类型      | 必填 | 默认值 | 说明            |
| --------------- | ------- | -- | --- | ------------- |
| commentDisabled | Integer | 是  | -   | 0=允许评论，1=禁止评论 |

***

### 4.10 管理员 - 禁止/解除用户上传权限

| 项目      | 内容                                    |
| ------- | ------------------------------------- |
| **URL** | `/admin/user/upload-disable/{userId}` |
| **方法**  | `PUT`                                 |
| **认证**  | 需要 Admin Token                        |

**请求参数（Body - JSON）**:

```json
{
  "uploadDisabled": 1
}
```

| 参数             | 类型      | 必填 | 默认值 | 说明            |
| -------------- | ------- | -- | --- | ------------- |
| uploadDisabled | Integer | 是  | -   | 0=允许上传，1=禁止上传 |

***

## 5. 角色管理模块

> 所有接口需 **Admin Token**。

### 5.1 分页查询角色列表

| 项目      | 内容                 |
| ------- | ------------------ |
| **URL** | `/admin/role/page` |
| **方法**  | `GET`              |
| **认证**  | 需要 Admin Token     |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明       |
| -------- | ------- | -- | ---- | -------- |
| page     | Integer | 否  | 1    | 当前页码     |
| pageSize | Integer | 否  | 10   | 每页条数     |
| name     | String  | 否  | null | 角色标识模糊搜索 |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 5,
    "records": [
      {
        "id": 1,
        "name": "super_admin",
        "displayName": "超级管理员",
        "description": "拥有系统全部权限",
        "createdAt": "2026-01-01 10:00",
        "updatedAt": "2026-05-01 14:30"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 5.2 获取所有角色（不分页）

| 项目      | 内容                 |
| ------- | ------------------ |
| **URL** | `/admin/role/list` |
| **方法**  | `GET`              |
| **认证**  | 需要 Admin Token     |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": [
    { "id": 1, "name": "super_admin", "displayName": "超级管理员" },
    { "id": 2, "name": "admin", "displayName": "管理员" },
    { "id": 3, "name": "editor", "displayName": "编辑者" }
  ]
}
```

***

### 5.3 创建角色

| 项目      | 内容             |
| ------- | -------------- |
| **URL** | `/admin/role`  |
| **方法**  | `POST`         |
| **认证**  | 需要 Admin Token |

**请求参数（Body - JSON）**:

```json
{
  "name": "editor",
  "displayName": "编辑者",
  "description": "负责文物数据录入和编辑"
}
```

| 参数          | 类型     | 必填 | 默认值  | 说明      |
| ----------- | ------ | -- | ---- | ------- |
| name        | String | 是  | -    | 角色标识，唯一 |
| displayName | String | 是  | -    | 显示名称    |
| description | String | 否  | null | 角色描述    |

***

### 5.4 更新角色

| 项目      | 内容                 |
| ------- | ------------------ |
| **URL** | `/admin/role/{id}` |
| **方法**  | `PUT`              |
| **认证**  | 需要 Admin Token     |

**请求参数（Path + Body）**:

| 参数 | 类型      | 必填 | 默认值 | 说明        |
| -- | ------- | -- | --- | --------- |
| id | Integer | 是  | -   | 路径参数，角色ID |

```json
{
  "displayName": "高级编辑者",
  "description": "负责文物数据录入、编辑和审核"
}
```

***

### 5.5 删除角色

| 项目      | 内容                 |
| ------- | ------------------ |
| **URL** | `/admin/role/{id}` |
| **方法**  | `DELETE`           |
| **认证**  | 需要 Admin Token     |

| 参数 | 类型      | 必填 | 默认值 | 说明        |
| -- | ------- | -- | --- | --------- |
| id | Integer | 是  | -   | 路径参数，角色ID |

***

### 5.6 为角色分配权限

| 项目      | 内容                                 |
| ------- | ---------------------------------- |
| **URL** | `/admin/role/{roleId}/permissions` |
| **方法**  | `PUT`                              |
| **认证**  | 需要 Admin Token                     |

**请求参数（Path + Body）**:

| 参数     | 类型      | 必填 | 默认值 | 说明        |
| ------ | ------- | -- | --- | --------- |
| roleId | Integer | 是  | -   | 路径参数，角色ID |

```json
{
  "permissionIds": [1, 2, 3, 5, 7]
}
```

| 参数            | 类型         | 必填 | 默认值 | 说明          |
| ------------- | ---------- | -- | --- | ----------- |
| permissionIds | Integer\[] | 是  | -   | 权限ID数组，全量覆盖 |

***

### 5.7 查询角色已有权限

| 项目      | 内容                                 |
| ------- | ---------------------------------- |
| **URL** | `/admin/role/{roleId}/permissions` |
| **方法**  | `GET`                              |
| **认证**  | 需要 Admin Token                     |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": [
    { "id": 1, "name": "artifact:view", "displayName": "查看文物", "module": "文物管理" },
    { "id": 2, "name": "artifact:edit", "displayName": "编辑文物", "module": "文物管理" }
  ]
}
```

***

## 6. 权限管理模块

> 所有接口需 **Admin Token**。

### 6.1 分页查询权限列表

| 项目      | 内容                       |
| ------- | ------------------------ |
| **URL** | `/admin/permission/page` |
| **方法**  | `GET`                    |
| **认证**  | 需要 Admin Token           |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明       |
| -------- | ------- | -- | ---- | -------- |
| page     | Integer | 否  | 1    | 当前页码     |
| pageSize | Integer | 否  | 10   | 每页条数     |
| name     | String  | 否  | null | 权限标识模糊搜索 |
| module   | String  | 否  | null | 所属模块筛选   |

***

### 6.2 获取所有权限列表（不分页）

| 项目      | 内容                       |
| ------- | ------------------------ |
| **URL** | `/admin/permission/list` |
| **方法**  | `GET`                    |
| **认证**  | 需要 Admin Token           |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": [
    { "id": 1, "name": "artifact:view", "displayName": "查看文物", "module": "文物管理" },
    { "id": 2, "name": "artifact:edit", "displayName": "编辑文物", "module": "文物管理" },
    { "id": 3, "name": "artifact:delete", "displayName": "删除文物", "module": "文物管理" }
  ]
}
```

***

### 6.3 创建权限

| 项目      | 内容                  |
| ------- | ------------------- |
| **URL** | `/admin/permission` |
| **方法**  | `POST`              |
| **认证**  | 需要 Admin Token      |

```json
{
  "name": "user:ban",
  "displayName": "封禁用户",
  "module": "用户管理"
}
```

***

### 6.4 更新权限

| 项目      | 内容                       |
| ------- | ------------------------ |
| **URL** | `/admin/permission/{id}` |
| **方法**  | `PUT`                    |
| **认证**  | 需要 Admin Token           |

### 6.5 删除权限

| 项目      | 内容                       |
| ------- | ------------------------ |
| **URL** | `/admin/permission/{id}` |
| **方法**  | `DELETE`                 |
| **认证**  | 需要 Admin Token           |

***

### 6.6 管理员 - 为用户分配角色

| 项目      | 内容                           |
| ------- | ---------------------------- |
| **URL** | `/admin/user/{userId}/roles` |
| **方法**  | `PUT`                        |
| **认证**  | 需要 Admin Token               |

```json
{
  "roleIds": [1, 3]
}
```

| 参数      | 类型         | 必填 | 默认值 | 说明          |
| ------- | ---------- | -- | --- | ----------- |
| userId  | Integer    | 是  | -   | 路径参数，用户ID   |
| roleIds | Integer\[] | 是  | -   | 角色ID数组，全量覆盖 |

***

## 7. 博物馆管理模块

> 数据接口使用 User Token，管理接口使用 Admin Token。

### 7.1 分页查询博物馆列表

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/museum/page` |
| **方法**  | `GET`                |
| **认证**  | 需要 Admin Token       |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明        |
| -------- | ------- | -- | ---- | --------- |
| page     | Integer | 否  | 1    | 当前页码      |
| pageSize | Integer | 否  | 10   | 每页条数      |
| name     | String  | 否  | null | 博物馆名称模糊搜索 |
| country  | String  | 否  | null | 国家筛选      |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 50,
    "records": [
      {
        "id": 1,
        "name": "故宫博物院",
        "shortName": "故宫",
        "country": "中国",
        "city": "北京",
        "website": "https://www.dpm.org.cn",
        "collectionUrl": "https://www.dpm.org.cn/collection",
        "createdAt": "2026-01-01 10:00",
        "updatedAt": "2026-05-01 14:00"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 7.2 获取博物馆详情

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/museum/{id}` |
| **方法**  | `GET`                |
| **认证**  | 需要 Admin Token       |

***

### 7.3 用户端 - 获取博物馆列表

| 项目      | 内容                  |
| ------- | ------------------- |
| **URL** | `/user/museum/list` |
| **方法**  | `GET`               |
| **认证**  | 需要 User Token       |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明      |
| -------- | ------- | -- | ---- | ------- |
| page     | Integer | 否  | 1    | 当前页码    |
| pageSize | Integer | 否  | 10   | 每页条数    |
| country  | String  | 否  | null | 按国家筛选   |
| keyword  | String  | 否  | null | 按名称模糊搜索 |

***

### 7.4 新增博物馆

| 项目      | 内容              |
| ------- | --------------- |
| **URL** | `/admin/museum` |
| **方法**  | `POST`          |
| **认证**  | 需要 Admin Token  |

```json
{
  "name": "大英博物馆",
  "shortName": "大英",
  "country": "英国",
  "city": "伦敦",
  "website": "https://www.britishmuseum.org",
  "collectionUrl": "https://www.britishmuseum.org/collection"
}
```

***

### 7.5 更新博物馆

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/museum/{id}` |
| **方法**  | `PUT`                |
| **认证**  | 需要 Admin Token       |

### 7.6 删除博物馆

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/museum/{id}` |
| **方法**  | `DELETE`             |
| **认证**  | 需要 Admin Token       |

***

## 8. 朝代管理模块

### 8.1 分页查询朝代列表

| 项目      | 内容                    |
| ------- | --------------------- |
| **URL** | `/admin/dynasty/page` |
| **方法**  | `GET`                 |
| **认证**  | 需要 Admin Token        |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明        |
| -------- | ------- | -- | ---- | --------- |
| page     | Integer | 否  | 1    | 当前页码      |
| pageSize | Integer | 否  | 10   | 每页条数      |
| nameZh   | String  | 否  | null | 朝代中文名模糊搜索 |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 24,
    "records": [
      {
        "id": 1,
        "nameZh": "商朝",
        "nameEn": "Shang Dynasty",
        "startYear": -1600,
        "endYear": -1046,
        "description": "又称殷商，中国第二个朝代",
        "createdAt": "2026-01-01 10:00"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 8.2 获取所有朝代（不分页）

| 项目      | 内容                    |
| ------- | --------------------- |
| **URL** | `/admin/dynasty/list` |
| **方法**  | `GET`                 |
| **认证**  | 需要 Admin Token        |

***

### 8.3 新增朝代

| 项目      | 内容               |
| ------- | ---------------- |
| **URL** | `/admin/dynasty` |
| **方法**  | `POST`           |
| **认证**  | 需要 Admin Token   |

```json
{
  "nameZh": "明朝",
  "nameEn": "Ming Dynasty",
  "startYear": 1368,
  "endYear": 1644,
  "description": "中国历史上最后一个由汉族建立的大一统王朝"
}
```

***

### 8.4 更新朝代

| 项目      | 内容                    |
| ------- | --------------------- |
| **URL** | `/admin/dynasty/{id}` |
| **方法**  | `PUT`                 |
| **认证**  | 需要 Admin Token        |

### 8.5 删除朝代

| 项目      | 内容                    |
| ------- | --------------------- |
| **URL** | `/admin/dynasty/{id}` |
| **方法**  | `DELETE`              |
| **认证**  | 需要 Admin Token        |

***

## 9. 艺术家管理模块

### 9.1 分页查询艺术家列表

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/artist/page` |
| **方法**  | `GET`                |
| **认证**  | 需要 Admin Token       |

**请求参数（Query）**:

| 参数        | 类型      | 必填 | 默认值  | 说明      |
| --------- | ------- | -- | ---- | ------- |
| page      | Integer | 否  | 1    | 当前页码    |
| pageSize  | Integer | 否  | 10   | 每页条数    |
| nameZh    | String  | 否  | null | 中文名模糊搜索 |
| nameEn    | String  | 否  | null | 英文名模糊搜索 |
| dynastyId | Integer | 否  | null | 朝代ID筛选  |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 100,
    "records": [
      {
        "id": 1,
        "nameZh": "吴道子",
        "nameEn": "Wu Daozi",
        "birthYear": 680,
        "deathYear": 759,
        "dynastyId": 7,
        "biography": "唐代著名画家，被尊称为画圣",
        "baiduUrl": "https://baike.baidu.com/item/吴道子",
        "wikiUrl": "https://en.wikipedia.org/wiki/Wu_Daozi",
        "createdAt": "2026-01-01 10:00",
        "updatedAt": "2026-05-01 14:00"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 9.2 获取艺术家详情

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/artist/{id}` |
| **方法**  | `GET`                |
| **认证**  | 需要 Admin Token       |

### 9.3 用户端 - 获取艺术家列表

| 项目      | 内容                  |
| ------- | ------------------- |
| **URL** | `/user/artist/list` |
| **方法**  | `GET`               |
| **认证**  | 需要 User Token       |

| 参数        | 类型      | 必填 | 默认值  | 说明     |
| --------- | ------- | -- | ---- | ------ |
| page      | Integer | 否  | 1    | 当前页码   |
| pageSize  | Integer | 否  | 10   | 每页条数   |
| dynastyId | Integer | 否  | null | 朝代ID筛选 |
| keyword   | String  | 否  | null | 名称模糊搜索 |

### 9.4 新增艺术家

| 项目      | 内容              |
| ------- | --------------- |
| **URL** | `/admin/artist` |
| **方法**  | `POST`          |
| **认证**  | 需要 Admin Token  |

### 9.5 更新艺术家

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/artist/{id}` |
| **方法**  | `PUT`                |
| **认证**  | 需要 Admin Token       |

### 9.6 删除艺术家

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/artist/{id}` |
| **方法**  | `DELETE`             |
| **认证**  | 需要 Admin Token       |

***

## 10. 地点管理模块

### 10.1 地点树形列表

| 项目      | 内容                     |
| ------- | ---------------------- |
| **URL** | `/admin/location/tree` |
| **方法**  | `GET`                  |
| **认证**  | 需要 Admin Token         |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": [
    {
      "id": 1,
      "nameZh": "中国",
      "nameEn": "China",
      "type": "country",
      "children": [
        {
          "id": 2,
          "nameZh": "北京市",
          "nameEn": "Beijing",
          "type": "city",
          "parentId": 1,
          "children": [
            {
              "id": 3,
              "nameZh": "故宫",
              "nameEn": "Forbidden City",
              "type": "site",
              "parentId": 2,
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```

***

### 10.2 获取地点扁平列表

| 项目      | 内容                     |
| ------- | ---------------------- |
| **URL** | `/admin/location/list` |
| **方法**  | `GET`                  |
| **认证**  | 需要 Admin Token         |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明                                            |
| -------- | ------- | -- | ---- | --------------------------------------------- |
| type     | String  | 否  | null | 类型筛选：`country` / `province` / `city` / `site` |
| parentId | Integer | 否  | null | 上级地点ID筛选                                      |

***

### 10.3 新增地点

| 项目      | 内容                |
| ------- | ----------------- |
| **URL** | `/admin/location` |
| **方法**  | `POST`            |
| **认证**  | 需要 Admin Token    |

```json
{
  "nameZh": "陕西省",
  "nameEn": "Shaanxi Province",
  "parentId": 1,
  "type": "province"
}
```

***

### 10.4 更新地点

| 项目      | 内容                     |
| ------- | ---------------------- |
| **URL** | `/admin/location/{id}` |
| **方法**  | `PUT`                  |
| **认证**  | 需要 Admin Token         |

### 10.5 删除地点

| 项目      | 内容                     |
| ------- | ---------------------- |
| **URL** | `/admin/location/{id}` |
| **方法**  | `DELETE`               |
| **认证**  | 需要 Admin Token         |

***

## 11. 文物管理模块

### 11.1 分页查询文物列表

| 项目      | 内容                     |
| ------- | ---------------------- |
| **URL** | `/admin/artifact/page` |
| **方法**  | `GET`                  |
| **认证**  | 需要 Admin Token         |

**请求参数（Query）**:

| 参数        | 类型      | 必填 | 默认值  | 说明                            |
| --------- | ------- | -- | ---- | ----------------------------- |
| page      | Integer | 否  | 1    | 当前页码                          |
| pageSize  | Integer | 否  | 10   | 每页条数                          |
| titleZh   | String  | 否  | null | 中文名称模糊搜索                      |
| titleEn   | String  | 否  | null | 英文名称模糊搜索                      |
| type      | String  | 否  | null | 文物类型筛选（Painting / Ceramics 等） |
| dynastyId | Integer | 否  | null | 朝代ID筛选                        |
| museumId  | Integer | 否  | null | 博物馆ID筛选                       |
| material  | String  | 否  | null | 材质筛选                          |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 5000,
    "records": [
      {
        "id": 1,
        "objectId": "MET_DP12345",
        "titleZh": "千里江山图",
        "titleEn": "A Thousand Li of Rivers and Mountains",
        "timePeriod": "北宋",
        "dynastyId": 10,
        "type": "Painting",
        "material": "绢本设色",
        "description": "宋代王希孟创作的绢本设色画...",
        "dimensions": "51.5 × 1191.5 cm",
        "museumId": 3,
        "locationId": 5,
        "detailUrl": "https://www.metmuseum.org/art/collection/DP12345",
        "imageUrl": "https://images.metmuseum.org/CRDImages/as/original/DP12345.jpg",
        "imagePath": "/uploads/artifact/DP12345.jpg",
        "creditLine": "Gift of John D. Rockefeller Jr.",
        "accessionNumber": "25.120.1",
        "crawlDate": "2026-05-01",
        "imageValidated": 1,
        "lastUpdated": "2026-05-10 12:00",
        "createdAt": "2026-05-01 08:00"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 11.2 获取文物详情

| 项目      | 内容                     |
| ------- | ---------------------- |
| **URL** | `/admin/artifact/{id}` |
| **方法**  | `GET`                  |
| **认证**  | 需要 Admin Token         |

**响应数据包含关联信息**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "id": 1,
    "objectId": "MET_DP12345",
    "titleZh": "千里江山图",
    "titleEn": "A Thousand Li of Rivers and Mountains",
    "timePeriod": "北宋",
    "dynastyId": 10,
    "dynastyName": "宋朝",
    "type": "Painting",
    "material": "绢本设色",
    "description": "宋代王希孟创作的绢本设色画...",
    "dimensions": "51.5 × 1191.5 cm",
    "museumId": 3,
    "museumName": "大都会艺术博物馆",
    "locationId": 5,
    "locationName": "纽约",
    "detailUrl": "https://www.metmuseum.org/art/collection/DP12345",
    "imageUrl": "https://images.metmuseum.org/CRDImages/as/original/DP12345.jpg",
    "imagePath": "/uploads/artifact/DP12345.jpg",
    "creditLine": "Gift of John D. Rockefeller Jr.",
    "accessionNumber": "25.120.1",
    "crawlDate": "2026-05-01",
    "imageValidated": 1,
    "images": [
      {
        "id": 1,
        "imageUrl": "https://images.metmuseum.org/CRDImages/as/original/DP12345.jpg",
        "imagePath": "/uploads/artifact/DP12345.jpg",
        "isPrimary": 1,
        "sortOrder": 1
      }
    ],
    "artists": [
      {
        "artistId": 15,
        "artistNameZh": "王希孟",
        "relationshipType": "creator"
      }
    ],
    "locations": [
      {
        "locationId": 10,
        "locationNameZh": "开封",
        "role": "制作地"
      }
    ]
  }
}
```

***

### 11.3 用户端 - 文物列表（分页 + 搜索）

| 项目      | 内容                    |
| ------- | --------------------- |
| **URL** | `/user/artifact/list` |
| **方法**  | `GET`                 |
| **认证**  | 需要 User Token         |

**请求参数（Query）**:

| 参数        | 类型      | 必填 | 默认值         | 说明                           |
| --------- | ------- | -- | ----------- | ---------------------------- |
| page      | Integer | 否  | 1           | 当前页码                         |
| pageSize  | Integer | 否  | 20          | 每页条数                         |
| keyword   | String  | 否  | null        | 关键词搜索（匹配中英文名）                |
| dynastyId | Integer | 否  | null        | 朝代ID                         |
| type      | String  | 否  | null        | 文物类型                         |
| museumId  | Integer | 否  | null        | 博物馆ID                        |
| material  | String  | 否  | null        | 材质                           |
| sortBy    | String  | 否  | `createdAt` | 排序字段：`createdAt` / `titleZh` |
| sortOrder | String  | 否  | `desc`      | 排序方向：`asc` / `desc`          |

***

### 11.4 用户端 - 文物详情

| 项目      | 内容                           |
| ------- | ---------------------------- |
| **URL** | `/user/artifact/detail/{id}` |
| **方法**  | `GET`                        |
| **认证**  | 需要 User Token                |

返回数据格式同 [11.2 获取文物详情](#112-获取文物详情)。

***

### 11.5 新增文物

| 项目      | 内容                |
| ------- | ----------------- |
| **URL** | `/admin/artifact` |
| **方法**  | `POST`            |
| **认证**  | 需要 Admin Token    |

```json
{
  "objectId": "MET_DP99999",
  "titleZh": "溪山行旅图",
  "titleEn": "Travelers Among Mountains and Streams",
  "timePeriod": "北宋",
  "dynastyId": 10,
  "type": "Painting",
  "material": "绢本水墨",
  "description": "范宽代表作...",
  "dimensions": "206.3 × 103.3 cm",
  "museumId": 1,
  "locationId": 3,
  "detailUrl": "https://example.com/collection/DP99999",
  "imageUrl": "https://example.com/images/DP99999.jpg",
  "creditLine": "故宫旧藏",
  "accessionNumber": "故00001234",
  "crawlDate": "2026-05-10"
}
```

***

### 11.6 更新文物

| 项目      | 内容                     |
| ------- | ---------------------- |
| **URL** | `/admin/artifact/{id}` |
| **方法**  | `PUT`                  |
| **认证**  | 需要 Admin Token         |

参数同新增，只传需更新的字段。

***

### 11.7 删除文物

| 项目      | 内容                     |
| ------- | ---------------------- |
| **URL** | `/admin/artifact/{id}` |
| **方法**  | `DELETE`               |
| **认证**  | 需要 Admin Token         |

***

### 11.8 批量导入文物（Excel）

| 项目               | 内容                       |
| ---------------- | ------------------------ |
| **URL**          | `/admin/artifact/import` |
| **方法**           | `POST`                   |
| **认证**           | 需要 Admin Token           |
| **Content-Type** | `multipart/form-data`    |

| 参数   | 类型   | 必填 | 默认值 | 说明                     |
| ---- | ---- | -- | --- | ---------------------- |
| file | File | 是  | -   | Excel 文件（.xlsx，≤ 10MB） |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "totalRows": 200,
    "successCount": 198,
    "failCount": 2,
    "failDetails": [
      { "row": 15, "reason": "博物馆ID不存在" },
      { "row": 87, "reason": "必填字段缺失: titleEn" }
    ]
  }
}
```

***

### 11.9 批量导出文物（Excel）

| 项目                 | 内容                                                                  |
| ------------------ | ------------------------------------------------------------------- |
| **URL**            | `/admin/artifact/export`                                            |
| **方法**             | `GET`                                                               |
| **认证**             | 需要 Admin Token                                                      |
| **响应Content-Type** | `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` |

**请求参数（Query）**:

| 参数        | 类型      | 必填 | 默认值  | 说明       |
| --------- | ------- | -- | ---- | -------- |
| museumId  | Integer | 否  | null | 按博物馆筛选导出 |
| dynastyId | Integer | 否  | null | 按朝代筛选导出  |
| type      | String  | 否  | null | 按类型筛选导出  |

返回文件流，文件名：`文物导出_20260510143022.xlsx`

***

## 12. 文物多图片模块

### 12.1 获取文物所有图片

| 项目      | 内容                                    |
| ------- | ------------------------------------- |
| **URL** | `/admin/artifact/{artifactId}/images` |
| **方法**  | `GET`                                 |
| **认证**  | 需要 Admin Token                        |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": [
    {
      "id": 1,
      "artifactId": 1,
      "imageUrl": "https://example.com/img1.jpg",
      "imagePath": "/uploads/artifact/img1.jpg",
      "isPrimary": 1,
      "sortOrder": 1
    },
    {
      "id": 2,
      "artifactId": 1,
      "imageUrl": "https://example.com/img2.jpg",
      "imagePath": "/uploads/artifact/img2.jpg",
      "isPrimary": 0,
      "sortOrder": 2
    }
  ]
}
```

***

### 12.2 新增文物图片

| 项目      | 内容                                    |
| ------- | ------------------------------------- |
| **URL** | `/admin/artifact/{artifactId}/images` |
| **方法**  | `POST`                                |
| **认证**  | 需要 Admin Token                        |

```json
{
  "imageUrl": "https://example.com/img3.jpg",
  "imagePath": "/uploads/artifact/img3.jpg",
  "isPrimary": 0,
  "sortOrder": 3
}
```

***

### 12.3 更新文物图片

| 项目      | 内容                                              |
| ------- | ----------------------------------------------- |
| **URL** | `/admin/artifact/{artifactId}/images/{imageId}` |
| **方法**  | `PUT`                                           |
| **认证**  | 需要 Admin Token                                  |

### 12.4 删除文物图片

| 项目      | 内容                                              |
| ------- | ----------------------------------------------- |
| **URL** | `/admin/artifact/{artifactId}/images/{imageId}` |
| **方法**  | `DELETE`                                        |
| **认证**  | 需要 Admin Token                                  |

### 12.5 设置主图

| 项目      | 内容                                                      |
| ------- | ------------------------------------------------------- |
| **URL** | `/admin/artifact/{artifactId}/images/{imageId}/primary` |
| **方法**  | `PUT`                                                   |
| **认证**  | 需要 Admin Token                                          |

***

## 13. 用户收藏模块

> 以下接口需 **User Token**。

### 13.1 收藏文物

| 项目      | 内容               |
| ------- | ---------------- |
| **URL** | `/user/favorite` |
| **方法**  | `POST`           |
| **认证**  | 需要 User Token    |

```json
{
  "artifactId": 1,
  "groupName": "宋代绘画"
}
```

| 参数         | 类型      | 必填 | 默认值  | 说明     |
| ---------- | ------- | -- | ---- | ------ |
| artifactId | Integer | 是  | -    | 文物ID   |
| groupName  | String  | 否  | null | 收藏分组名称 |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": null
}
```

***

### 13.2 取消收藏

| 项目      | 内容                            |
| ------- | ----------------------------- |
| **URL** | `/user/favorite/{artifactId}` |
| **方法**  | `DELETE`                      |
| **认证**  | 需要 User Token                 |

| 参数         | 类型      | 必填 | 默认值 | 说明        |
| ---------- | ------- | -- | --- | --------- |
| artifactId | Integer | 是  | -   | 路径参数，文物ID |

***

### 13.3 查询收藏列表

| 项目      | 内容                    |
| ------- | --------------------- |
| **URL** | `/user/favorite/list` |
| **方法**  | `GET`                 |
| **认证**  | 需要 User Token         |

**请求参数（Query）**:

| 参数        | 类型      | 必填 | 默认值  | 说明    |
| --------- | ------- | -- | ---- | ----- |
| page      | Integer | 否  | 1    | 当前页码  |
| pageSize  | Integer | 否  | 10   | 每页条数  |
| groupName | String  | 否  | null | 按分组筛选 |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 5,
    "records": [
      {
        "artifactId": 1,
        "titleZh": "千里江山图",
        "titleEn": "A Thousand Li of Rivers and Mountains",
        "imageUrl": "https://example.com/img1.jpg",
        "groupName": "宋代绘画",
        "createdAt": "2026-05-10 14:30"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 13.4 检查是否已收藏

| 项目      | 内容                                  |
| ------- | ----------------------------------- |
| **URL** | `/user/favorite/check/{artifactId}` |
| **方法**  | `GET`                               |
| **认证**  | 需要 User Token                       |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": { "favorited": true }
}
```

***

## 14. 用户点赞模块

### 14.1 点赞文物

| 项目      | 内容                                 |
| ------- | ---------------------------------- |
| **URL** | `/user/like/artifact/{artifactId}` |
| **方法**  | `POST`                             |
| **认证**  | 需要 User Token                      |

### 14.2 取消点赞文物

| 项目      | 内容                                 |
| ------- | ---------------------------------- |
| **URL** | `/user/like/artifact/{artifactId}` |
| **方法**  | `DELETE`                           |
| **认证**  | 需要 User Token                      |

### 14.3 点赞评论

| 项目      | 内容                               |
| ------- | -------------------------------- |
| **URL** | `/user/like/comment/{commentId}` |
| **方法**  | `POST`                           |
| **认证**  | 需要 User Token                    |

### 14.4 取消点赞评论

| 项目      | 内容                               |
| ------- | -------------------------------- |
| **URL** | `/user/like/comment/{commentId}` |
| **方法**  | `DELETE`                         |
| **认证**  | 需要 User Token                    |

### 14.5 点赞动态

| 项目      | 内容                         |
| ------- | -------------------------- |
| **URL** | `/user/like/post/{postId}` |
| **方法**  | `POST`                     |
| **认证**  | 需要 User Token              |

### 14.6 取消点赞动态

| 项目      | 内容                         |
| ------- | -------------------------- |
| **URL** | `/user/like/post/{postId}` |
| **方法**  | `DELETE`                   |
| **认证**  | 需要 User Token              |

***

## 15. 评论模块

### 15.1 发表评论

| 项目      | 内容              |
| ------- | --------------- |
| **URL** | `/user/comment` |
| **方法**  | `POST`          |
| **认证**  | 需要 User Token   |

```json
{
  "artifactId": 1,
  "parentId": null,
  "content": "这幅画真是太震撼了！"
}
```

| 参数         | 类型      | 必填 | 默认值  | 说明             |
| ---------- | ------- | -- | ---- | -------------- |
| artifactId | Integer | 是  | -    | 文物ID           |
| parentId   | Integer | 否  | null | 父评论ID（回复评论时使用） |
| content    | String  | 是  | -    | 评论内容，长度 1\~500 |

***

### 15.2 文物评论列表（分页）

| 项目      | 内容                                |
| ------- | --------------------------------- |
| **URL** | `/user/comment/list/{artifactId}` |
| **方法**  | `GET`                             |
| **认证**  | 需要 User Token                     |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值         | 说明                             |
| -------- | ------- | -- | ----------- | ------------------------------ |
| page     | Integer | 否  | 1           | 当前页码                           |
| pageSize | Integer | 否  | 10          | 每页条数                           |
| sortBy   | String  | 否  | `createdAt` | 排序字段：`createdAt` / `likeCount` |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 30,
    "records": [
      {
        "id": 100,
        "userId": 10,
        "userNickname": "张三",
        "userAvatar": "/uploads/avatar/10.jpg",
        "content": "这幅画真是太震撼了！",
        "likeCount": 5,
        "replies": [
          {
            "id": 101,
            "userId": 20,
            "userNickname": "李四",
            "userAvatar": "/uploads/avatar/20.jpg",
            "content": "确实，配色极为精美",
            "likeCount": 2
          }
        ],
        "status": "approved",
        "createdAt": "2026-05-10 14:30"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 15.3 删除评论

| 项目      | 内容                          |
| ------- | --------------------------- |
| **URL** | `/user/comment/{commentId}` |
| **方法**  | `DELETE`                    |
| **认证**  | 需要 User Token               |

> 只能删除自己的评论。

***

### 15.4 管理员 - 评论审核列表

| 项目      | 内容                          |
| ------- | --------------------------- |
| **URL** | `/admin/comment/audit/page` |
| **方法**  | `GET`                       |
| **认证**  | 需要 Admin Token              |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明                                  |
| -------- | ------- | -- | ---- | ----------------------------------- |
| page     | Integer | 否  | 1    | 当前页码                                |
| pageSize | Integer | 否  | 10   | 每页条数                                |
| status   | String  | 否  | null | `pending` / `approved` / `rejected` |

***

### 15.5 管理员 - 审核评论

| 项目      | 内容                                 |
| ------- | ---------------------------------- |
| **URL** | `/admin/comment/{commentId}/audit` |
| **方法**  | `PUT`                              |
| **认证**  | 需要 Admin Token                     |

```json
{
  "status": "approved",
  "rejectReason": null
}
```

| 参数           | 类型     | 必填                          | 默认值  | 说明                      |
| ------------ | ------ | --------------------------- | ---- | ----------------------- |
| status       | String | 是                           | -    | `approved` / `rejected` |
| rejectReason | String | 否(approved时) / 是(rejected时) | null | 拒绝原因                    |

***

## 16. 用户动态模块

### 16.1 发布动态

| 项目      | 内容            |
| ------- | ------------- |
| **URL** | `/user/post`  |
| **方法**  | `POST`        |
| **认证**  | 需要 User Token |

```json
{
  "content": "今天在故宫博物院看到了一幅超美的画！",
  "artifactId": 1,
  "museumId": 3,
  "imageUrls": "[\"https://example.com/post1.jpg\",\"https://example.com/post2.jpg\"]"
}
```

| 参数         | 类型      | 必填 | 默认值  | 说明                 |
| ---------- | ------- | -- | ---- | ------------------ |
| content    | String  | 是  | -    | 动态内容，长度 1\~2000    |
| artifactId | Integer | 否  | null | 关联文物ID             |
| museumId   | Integer | 否  | null | 关联博物馆ID            |
| imageUrls  | String  | 否  | null | 图片URL列表（JSON数组字符串） |

***

### 16.2 动态列表（首页动态流）

| 项目      | 内容                |
| ------- | ----------------- |
| **URL** | `/user/post/list` |
| **方法**  | `GET`             |
| **认证**  | 需要 User Token     |

**请求参数（Query）**:

| 参数           | 类型      | 必填 | 默认值 | 说明            |
| ------------ | ------- | -- | --- | ------------- |
| page         | Integer | 否  | 1   | 当前页码          |
| pageSize     | Integer | 否  | 10  | 每页条数          |
| followeeOnly | Integer | 否  | 0   | 1=仅看关注用户，0=全部 |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 500,
    "records": [
      {
        "id": 50,
        "userId": 10,
        "userNickname": "张三",
        "userAvatar": "/uploads/avatar/10.jpg",
        "content": "今天在故宫博物院看到了一幅超美的画！",
        "artifactId": 1,
        "artifactTitle": "千里江山图",
        "museumId": 3,
        "museumName": "故宫博物院",
        "imageUrls": ["https://example.com/post1.jpg", "https://example.com/post2.jpg"],
        "likeCount": 12,
        "commentCount": 3,
        "isLiked": false,
        "status": "approved",
        "createdAt": "2026-05-10 16:00"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 16.3 用户个人动态列表

| 项目      | 内容                         |
| ------- | -------------------------- |
| **URL** | `/user/post/user/{userId}` |
| **方法**  | `GET`                      |
| **认证**  | 需要 User Token              |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值 | 说明   |
| -------- | ------- | -- | --- | ---- |
| page     | Integer | 否  | 1   | 当前页码 |
| pageSize | Integer | 否  | 10  | 每页条数 |

***

### 16.4 动态详情

| 项目      | 内容                           |
| ------- | ---------------------------- |
| **URL** | `/user/post/detail/{postId}` |
| **方法**  | `GET`                        |
| **认证**  | 需要 User Token                |

### 16.5 删除动态

| 项目      | 内容                    |
| ------- | --------------------- |
| **URL** | `/user/post/{postId}` |
| **方法**  | `DELETE`              |
| **认证**  | 需要 User Token         |

> 只能删除自己的动态。

***

### 16.6 管理员 - 动态审核列表

| 项目      | 内容                       |
| ------- | ------------------------ |
| **URL** | `/admin/post/audit/page` |
| **方法**  | `GET`                    |
| **认证**  | 需要 Admin Token           |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明                                  |
| -------- | ------- | -- | ---- | ----------------------------------- |
| page     | Integer | 否  | 1    | 当前页码                                |
| pageSize | Integer | 否  | 10   | 每页条数                                |
| status   | String  | 否  | null | `pending` / `approved` / `rejected` |

***

### 16.7 管理员 - 审核动态

| 项目      | 内容                           |
| ------- | ---------------------------- |
| **URL** | `/admin/post/{postId}/audit` |
| **方法**  | `PUT`                        |
| **认证**  | 需要 Admin Token               |

```json
{
  "status": "approved",
  "rejectReason": null
}
```

***

## 19. 审核管理模块

> 所有接口需 **Admin Token**。

### 19.1 审核记录列表

| 项目      | 内容                  |
| ------- | ------------------- |
| **URL** | `/admin/audit/page` |
| **方法**  | `GET`               |
| **认证**  | 需要 Admin Token      |

**请求参数（Query）**:

| 参数                | 类型      | 必填 | 默认值  | 说明                                         |
| ----------------- | ------- | -- | ---- | ------------------------------------------ |
| page              | Integer | 否  | 1    | 当前页码                                       |
| pageSize          | Integer | 否  | 10   | 每页条数                                       |
| contentType       | String  | 否  | null | 内容类型：`comment` / `post` / `upload`         |
| manualAuditResult | String  | 否  | null | 人工审核结果：`pending` / `approved` / `rejected` |
| sourceType        | String  | 否  | null | 来源表名                                       |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 100,
    "records": [
      {
        "id": 1,
        "contentId": "50",
        "contentType": "post",
        "sourceType": "user_posts",
        "content": "今天在故宫博物院看到了一幅超美的画！",
        "submitterId": 10,
        "submitterName": "张三",
        "autoAuditResult": "pending",
        "manualAuditResult": "pending",
        "auditorId": null,
        "auditTime": null,
        "rejectReason": null,
        "createdAt": "2026-05-10 16:00"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 19.2 执行审核

| 项目      | 内容                  |
| ------- | ------------------- |
| **URL** | `/admin/audit/{id}` |
| **方法**  | `PUT`               |
| **认证**  | 需要 Admin Token      |

```json
{
  "manualAuditResult": "rejected",
  "rejectReason": "包含不当内容"
}
```

| 参数                | 类型     | 必填                        | 默认值  | 说明                      |
| ----------------- | ------ | ------------------------- | ---- | ----------------------- |
| manualAuditResult | String | 是                         | -    | `approved` / `rejected` |
| rejectReason      | String | 否(approved) / 是(rejected) | null | 拒绝原因                    |

***

## 20. 违规处罚模块

> 所有接口需 **Admin Token**。

### 20.1 违规类型列表

| 项目      | 内容                           |
| ------- | ---------------------------- |
| **URL** | `/admin/violation-type/list` |
| **方法**  | `GET`                        |
| **认证**  | 需要 Admin Token               |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": [
    { "id": 1, "typeCode": "spam_comment", "typeName": "垃圾评论", "severityLevel": 1, "defaultPenalty": "warning" },
    { "id": 2, "typeCode": "inappropriate_content", "typeName": "不当内容", "severityLevel": 2, "defaultPenalty": "temp_ban" },
    { "id": 3, "typeCode": "fake_identity", "typeName": "虚假身份", "severityLevel": 2, "defaultPenalty": "temp_ban" },
    { "id": 4, "typeCode": "copyright_violation", "typeName": "版权侵权", "severityLevel": 3, "defaultPenalty": "permanent_ban" },
    { "id": 5, "typeCode": "malicious_attack", "typeName": "恶意攻击", "severityLevel": 4, "defaultPenalty": "permanent_ban" }
  ]
}
```

***

### 20.2 处罚用户

| 项目      | 内容               |
| ------- | ---------------- |
| **URL** | `/admin/penalty` |
| **方法**  | `POST`           |
| **认证**  | 需要 Admin Token   |

```json
{
  "userId": 10,
  "penaltyType": "temp_ban",
  "reason": "发布不当内容",
  "expireTime": "2026-05-17 16:00"
}
```

| 参数          | 类型      | 必填 | 默认值  | 说明                                       |
| ----------- | ------- | -- | ---- | ---------------------------------------- |
| userId      | Integer | 是  | -    | 被处罚用户ID                                  |
| penaltyType | String  | 是  | -    | `warning` / `temp_ban` / `permanent_ban` |
| reason      | String  | 是  | -    | 处罚原因                                     |
| expireTime  | String  | 否  | null | 过期时间（临时封禁时必填）                            |
| remark      | String  | 否  | null | 备注                                       |

***

### 20.3 处罚记录列表

| 项目      | 内容                    |
| ------- | --------------------- |
| **URL** | `/admin/penalty/page` |
| **方法**  | `GET`                 |
| **认证**  | 需要 Admin Token        |

**请求参数（Query）**:

| 参数          | 类型      | 必填 | 默认值  | 说明          |
| ----------- | ------- | -- | ---- | ----------- |
| page        | Integer | 否  | 1    | 当前页码        |
| pageSize    | Integer | 否  | 10   | 每页条数        |
| userId      | Integer | 否  | null | 被处罚用户ID     |
| penaltyType | String  | 否  | null | 处罚类型        |
| status      | Integer | 否  | null | 1=生效中，0=已解除 |

***

### 20.4 解除处罚

| 项目      | 内容                           |
| ------- | ---------------------------- |
| **URL** | `/admin/penalty/{id}/revoke` |
| **方法**  | `PUT`                        |
| **认证**  | 需要 Admin Token               |

```json
{
  "remark": "用户已认识到错误，予以解除"
}
```

***

## 21. 申诉管理模块

### 21.1 用户提交申诉

| 项目      | 内容             |
| ------- | -------------- |
| **URL** | `/user/appeal` |
| **方法**  | `POST`         |
| **认证**  | 需要 User Token  |

```json
{
  "penaltyId": 5,
  "appealReason": "我没有发布不当内容，是系统误判",
  "evidence": "附件截图链接"
}
```

| 参数           | 类型     | 必填 | 默认值  | 说明       |
| ------------ | ------ | -- | ---- | -------- |
| penaltyId    | Long   | 是  | -    | 关联处罚记录ID |
| appealReason | String | 是  | -    | 申诉理由     |
| evidence     | String | 否  | null | 证据材料     |

***

### 21.2 我的申诉列表

| 项目      | 内容                  |
| ------- | ------------------- |
| **URL** | `/user/appeal/list` |
| **方法**  | `GET`               |
| **认证**  | 需要 User Token       |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值 | 说明   |
| -------- | ------- | -- | --- | ---- |
| page     | Integer | 否  | 1   | 当前页码 |
| pageSize | Integer | 否  | 10  | 每页条数 |

***

### 21.3 管理员 - 申诉列表

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/appeal/page` |
| **方法**  | `GET`                |
| **认证**  | 需要 Admin Token       |

| 参数       | 类型      | 必填 | 默认值  | 说明                                  |
| -------- | ------- | -- | ---- | ----------------------------------- |
| page     | Integer | 否  | 1    | 当前页码                                |
| pageSize | Integer | 否  | 10   | 每页条数                                |
| status   | String  | 否  | null | `pending` / `approved` / `rejected` |

***

### 21.4 管理员 - 处理申诉

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/appeal/{id}` |
| **方法**  | `PUT`                |
| **认证**  | 需要 Admin Token       |

```json
{
  "status": "approved",
  "reviewResult": "申诉成立",
  "reviewRemark": "经核实，确实存在系统误判"
}
```

| 参数           | 类型     | 必填 | 默认值  | 说明                               |
| ------------ | ------ | -- | ---- | -------------------------------- |
| status       | String | 是  | -    | `approved`（申诉成立）/ `rejected`（驳回） |
| reviewResult | String | 是  | -    | 复审结果描述                           |
| reviewRemark | String | 否  | null | 复审备注                             |

***

## 22. 公告管理模块

### 22.1 管理员 - 公告列表

| 项目      | 内容                         |
| ------- | -------------------------- |
| **URL** | `/admin/announcement/page` |
| **方法**  | `GET`                      |
| **认证**  | 需要 Admin Token             |

| 参数       | 类型      | 必填 | 默认值  | 说明        |
| -------- | ------- | -- | ---- | --------- |
| page     | Integer | 否  | 1    | 当前页码      |
| pageSize | Integer | 否  | 10   | 每页条数      |
| status   | Integer | 否  | null | 1=发布，0=下线 |

***

### 22.2 管理员 - 创建公告

| 项目      | 内容                    |
| ------- | --------------------- |
| **URL** | `/admin/announcement` |
| **方法**  | `POST`                |
| **认证**  | 需要 Admin Token        |

```json
{
  "title": "系统维护通知",
  "content": "系统将于本周六凌晨 2:00 - 4:00 进行维护升级...",
  "position": "global",
  "targetAudience": "all",
  "startTime": "2026-05-10 10:00",
  "endTime": "2026-05-17 10:00",
  "status": 1
}
```

| 参数             | 类型      | 必填 | 默认值      | 说明                                |
| -------------- | ------- | -- | -------- | --------------------------------- |
| title          | String  | 是  | -        | 公告标题                              |
| content        | String  | 是  | -        | 公告内容                              |
| position       | String  | 否  | `global` | `global` / `home` / `user_center` |
| targetAudience | String  | 否  | `all`    | `all` / `admin` / `users`         |
| startTime      | String  | 否  | null     | 展示开始时间                            |
| endTime        | String  | 否  | null     | 展示结束时间                            |
| status         | Integer | 是  | -        | 1=发布，0=下线                         |

***

### 22.3 管理员 - 更新公告

| 项目      | 内容                         |
| ------- | -------------------------- |
| **URL** | `/admin/announcement/{id}` |
| **方法**  | `PUT`                      |
| **认证**  | 需要 Admin Token             |

### 22.4 管理员 - 下线公告

| 项目      | 内容                                 |
| ------- | ---------------------------------- |
| **URL** | `/admin/announcement/{id}/offline` |
| **方法**  | `PUT`                              |
| **认证**  | 需要 Admin Token                     |

### 22.5 用户端 - 获取生效公告列表

| 项目      | 内容                        |
| ------- | ------------------------- |
| **URL** | `/user/announcement/list` |
| **方法**  | `GET`                     |
| **认证**  | 需要 User Token             |

| 参数       | 类型     | 必填 | 默认值  | 说明                                |
| -------- | ------ | -- | ---- | --------------------------------- |
| position | String | 否  | null | `global` / `home` / `user_center` |

***

## 23. 通知模块

### 23.1 获取我的通知列表

| 项目      | 内容                        |
| ------- | ------------------------- |
| **URL** | `/user/notification/list` |
| **方法**  | `GET`                     |
| **认证**  | 需要 User Token             |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明        |
| -------- | ------- | -- | ---- | --------- |
| page     | Integer | 否  | 1    | 当前页码      |
| pageSize | Integer | 否  | 10   | 每页条数      |
| isRead   | Integer | 否  | null | 0=未读，1=已读 |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 15,
    "records": [
      {
        "id": 100,
        "type": "audit_result",
        "title": "审核结果通知",
        "content": "您发布的动态已通过审核",
        "isRead": false,
        "extraData": "{\"postId\":50}",
        "createdAt": "2026-05-10 17:00"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 23.2 标记通知已读

| 项目      | 内容                        |
| ------- | ------------------------- |
| **URL** | `/user/notification/read` |
| **方法**  | `PUT`                     |
| **认证**  | 需要 User Token             |

```json
{
  "ids": [100, 101, 102]
}
```

| 参数  | 类型      | 必填 | 默认值  | 说明                    |
| --- | ------- | -- | ---- | --------------------- |
| ids | Long\[] | 否  | null | 通知ID数组，传 null 则标记全部已读 |

***

### 23.3 未读通知数量

| 项目      | 内容                                |
| ------- | --------------------------------- |
| **URL** | `/user/notification/unread-count` |
| **方法**  | `GET`                             |
| **认证**  | 需要 User Token                     |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": { "count": 3 }
}
```

***

## 24. 敏感词库模块

> 所有接口需 **Admin Token**。

### 24.1 分页查询敏感词

| 项目      | 内容                           |
| ------- | ---------------------------- |
| **URL** | `/admin/sensitive-word/page` |
| **方法**  | `GET`                        |
| **认证**  | 需要 Admin Token               |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明                                                                       |
| -------- | ------- | -- | ---- | ------------------------------------------------------------------------ |
| page     | Integer | 否  | 1    | 当前页码                                                                     |
| pageSize | Integer | 否  | 20   | 每页条数                                                                     |
| word     | String  | 否  | null | 敏感词模糊搜索                                                                  |
| category | String  | 否  | null | 分类筛选：`political` / `pornographic` / `violence` / `advertising` / `other` |
| status   | Integer | 否  | null | 1=启用，0=禁用                                                                |

***

### 24.2 新增敏感词

| 项目      | 内容                      |
| ------- | ----------------------- |
| **URL** | `/admin/sensitive-word` |
| **方法**  | `POST`                  |
| **认证**  | 需要 Admin Token          |

```json
{
  "word": "违规词示例",
  "category": "advertising"
}
```

***

### 24.3 批量导入敏感词

| 项目               | 内容                             |
| ---------------- | ------------------------------ |
| **URL**          | `/admin/sensitive-word/import` |
| **方法**           | `POST`                         |
| **认证**           | 需要 Admin Token                 |
| **Content-Type** | `multipart/form-data`          |

| 参数   | 类型   | 必填 | 默认值 | 说明          |
| ---- | ---- | -- | --- | ----------- |
| file | File | 是  | -   | 文本文件（每行一个词） |

***

### 24.4 更新敏感词

| 项目      | 内容                           |
| ------- | ---------------------------- |
| **URL** | `/admin/sensitive-word/{id}` |
| **方法**  | `PUT`                        |
| **认证**  | 需要 Admin Token               |

### 24.5 启用/禁用敏感词

| 项目      | 内容                                  |
| ------- | ----------------------------------- |
| **URL** | `/admin/sensitive-word/{id}/status` |
| **方法**  | `PUT`                               |
| **认证**  | 需要 Admin Token                      |

```json
{ "status": 0 }
```

***

### 24.6 删除敏感词

| 项目      | 内容                           |
| ------- | ---------------------------- |
| **URL** | `/admin/sensitive-word/{id}` |
| **方法**  | `DELETE`                     |
| **认证**  | 需要 Admin Token               |

***

## 25. 爬取任务模块

> 所有接口需 **Admin Token**。

### 25.1 爬取任务列表

| 项目      | 内容                       |
| ------- | ------------------------ |
| **URL** | `/admin/crawl-task/page` |
| **方法**  | `GET`                    |
| **认证**  | 需要 Admin Token           |

**请求参数（Query）**:

| 参数       | 类型      | 必填 | 默认值  | 说明                                    |
| -------- | ------- | -- | ---- | ------------------------------------- |
| page     | Integer | 否  | 1    | 当前页码                                  |
| pageSize | Integer | 否  | 10   | 每页条数                                  |
| museumId | Integer | 否  | null | 博物馆ID筛选                               |
| status   | String  | 否  | null | 状态筛选：`running` / `success` / `failed` |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 50,
    "records": [
      {
        "id": 1,
        "museumId": 1,
        "museumName": "大都会艺术博物馆",
        "startTime": "2026-05-10 08:00",
        "endTime": "2026-05-10 09:30",
        "status": "success",
        "itemsCrawled": 200,
        "itemsNew": 15,
        "itemsUpdated": 185,
        "errorMessage": null
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

***

### 25.2 手动触发爬取

| 项目      | 内容                          |
| ------- | --------------------------- |
| **URL** | `/admin/crawl-task/trigger` |
| **方法**  | `POST`                      |
| **认证**  | 需要 Admin Token              |

```json
{
  "museumId": 1
}
```

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": { "taskId": 51 }
}
```

***

## 26. 系统配置模块

> 所有接口需 **Admin Token**。

### 26.1 获取所有配置

| 项目      | 内容                          |
| ------- | --------------------------- |
| **URL** | `/admin/system-config/list` |
| **方法**  | `GET`                       |
| **认证**  | 需要 Admin Token              |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": [
    { "id": 1, "configKey": "site_name", "configValue": "RelicAdmin 文物管理系统", "description": "站点名称" },
    { "id": 2, "configKey": "comment_audit_enabled", "configValue": "true", "description": "评论审核开关" },
    { "id": 3, "configKey": "max_upload_size_mb", "configValue": "200", "description": "最大上传大小(MB)" }
  ]
}
```

***

### 26.2 更新配置

| 项目      | 内容                     |
| ------- | ---------------------- |
| **URL** | `/admin/system-config` |
| **方法**  | `PUT`                  |
| **认证**  | 需要 Admin Token         |

```json
{
  "configKey": "comment_audit_enabled",
  "configValue": "false",
  "description": "评论审核开关"
}
```

***

## 27. 备份管理模块

> 所有接口需 **Admin Token**。

### 27.1 备份记录列表

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/backup/page` |
| **方法**  | `GET`                |
| **认证**  | 需要 Admin Token       |

| 参数         | 类型      | 必填 | 默认值  | 说明                                |
| ---------- | ------- | -- | ---- | --------------------------------- |
| page       | Integer | 否  | 1    | 当前页码                              |
| pageSize   | Integer | 否  | 10   | 每页条数                              |
| status     | Integer | 否  | null | 0=进行中，1=已完成，2=失败                  |
| backupType | String  | 否  | null | `full` / `incremental` / `export` |

***

### 27.2 手动触发备份

| 项目      | 内容                     |
| ------- | ---------------------- |
| **URL** | `/admin/backup/create` |
| **方法**  | `POST`                 |
| **认证**  | 需要 Admin Token         |

```json
{
  "backupName": "全量备份-20260510",
  "backupType": "full",
  "scope": "全部数据"
}
```

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "id": 10,
    "backupName": "全量备份-20260510",
    "status": 0
  }
}
```

***

### 27.3 下载备份文件

| 项目      | 内容                            |
| ------- | ----------------------------- |
| **URL** | `/admin/backup/{id}/download` |
| **方法**  | `GET`                         |
| **认证**  | 需要 Admin Token                |
| **响应**  | 文件流                           |

***

### 27.4 删除备份

| 项目      | 内容                   |
| ------- | -------------------- |
| **URL** | `/admin/backup/{id}` |
| **方法**  | `DELETE`             |
| **认证**  | 需要 Admin Token       |

***

## 28. 日志管理模块

> 所有接口需 **Admin Token**。

### 28.1 操作日志列表

| 项目      | 内容                          |
| ------- | --------------------------- |
| **URL** | `/admin/operation-log/page` |
| **方法**  | `GET`                       |
| **认证**  | 需要 Admin Token              |

**请求参数（Query）**:

| 参数            | 类型      | 必填 | 默认值  | 说明                                  |
| ------------- | ------- | -- | ---- | ----------------------------------- |
| page          | Integer | 否  | 1    | 当前页码                                |
| pageSize      | Integer | 否  | 10   | 每页条数                                |
| userId        | Integer | 否  | null | 操作用户ID筛选                            |
| operationType | String  | 否  | null | 操作类型：`INSERT` / `UPDATE` / `DELETE` |
| targetType    | String  | 否  | null | 操作目标类型                              |
| startTime     | String  | 否  | null | 开始时间 `yyyy-MM-dd HH:mm`             |
| endTime       | String  | 否  | null | 结束时间 `yyyy-MM-dd HH:mm`             |

***

### 28.2 系统日志列表

| 项目      | 内容                       |
| ------- | ------------------------ |
| **URL** | `/admin/system-log/page` |
| **方法**  | `GET`                    |
| **认证**  | 需要 Admin Token           |

| 参数        | 类型      | 必填 | 默认值  | 说明                                  |
| --------- | ------- | -- | ---- | ----------------------------------- |
| page      | Integer | 否  | 1    | 当前页码                                |
| pageSize  | Integer | 否  | 10   | 每页条数                                |
| logLevel  | String  | 否  | null | `DEBUG` / `INFO` / `WARN` / `ERROR` |
| module    | String  | 否  | null | 模块名称                                |
| startTime | String  | 否  | null | 开始时间                                |
| endTime   | String  | 否  | null | 结束时间                                |

***

### 28.3 安全日志列表

| 项目      | 内容                         |
| ------- | -------------------------- |
| **URL** | `/admin/security-log/page` |
| **方法**  | `GET`                      |
| **认证**  | 需要 Admin Token             |

| 参数        | 类型      | 必填 | 默认值  | 说明                                           |
| --------- | ------- | -- | ---- | -------------------------------------------- |
| page      | Integer | 否  | 1    | 当前页码                                         |
| pageSize  | Integer | 否  | 10   | 每页条数                                         |
| eventType | String  | 否  | null | 事件类型（`login_failed` / `permission_change` 等） |
| userId    | Integer | 否  | null | 相关用户ID                                       |

***

### 28.4 用户行为日志列表

| 项目      | 内容                          |
| ------- | --------------------------- |
| **URL** | `/admin/user-behavior/page` |
| **方法**  | `GET`                       |
| **认证**  | 需要 Admin Token              |

| 参数           | 类型      | 必填 | 默认值  | 说明                                                                                     |
| ------------ | ------- | -- | ---- | -------------------------------------------------------------------------------------- |
| page         | Integer | 否  | 1    | 当前页码                                                                                   |
| pageSize     | Integer | 否  | 10   | 每页条数                                                                                   |
| userId       | Integer | 否  | null | 用户ID                                                                                   |
| behaviorType | String  | 否  | null | `login` / `comment` / `publish` / `upload` / `like` / `favorite` / `browse` / `follow` |
| startTime    | String  | 否  | null | 开始时间                                                                                   |
| endTime      | String  | 否  | null | 结束时间                                                                                   |

***

## 29. 文件上传（OSS）模块

### 29.1 通用文件上传

| 项目               | 内容                     |
| ---------------- | ---------------------- |
| **URL**          | `/admin/common/upload` |
| **方法**           | `POST`                 |
| **认证**           | 需要 Admin Token         |
| **Content-Type** | `multipart/form-data`  |

| 参数   | 类型   | 必填 | 默认值 | 说明         |
| ---- | ---- | -- | --- | ---------- |
| file | File | 是  | -   | 文件（≤ 50MB） |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "url": "https://oss.example.com/uploads/20260510/file_abc123.jpg",
    "fileName": "file_abc123.jpg"
  }
}
```

***

### 29.2 管理员浏览历史查询

| 项目      | 内容                           |
| ------- | ---------------------------- |
| **URL** | `/admin/browse-history/page` |
| **方法**  | `GET`                        |
| **认证**  | 需要 Admin Token               |

| 参数         | 类型      | 必填 | 默认值  | 说明   |
| ---------- | ------- | -- | ---- | ---- |
| page       | Integer | 否  | 1    | 当前页码 |
| pageSize   | Integer | 否  | 10   | 每页条数 |
| userId     | Integer | 否  | null | 用户ID |
| artifactId | Integer | 否  | null | 文物ID |

***

## 30. 数据统计模块

> 所有接口需 **Admin Token**。

### 30.1 仪表盘总览数据

| 项目      | 内容                            |
| ------- | ----------------------------- |
| **URL** | `/admin/statistics/dashboard` |
| **方法**  | `GET`                         |
| **认证**  | 需要 Admin Token                |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": {
    "totalUsers": 2560,
    "todayActiveUsers": 152,
    "totalArtifacts": 5000,
    "totalMuseums": 50,
    "totalComments": 12000,
    "pendingAudits": 35,
    "todayNewUsers": 12,
    "todayBrowseCount": 3200
  }
}
```

***

### 30.2 用户增长趋势

| 项目      | 内容                             |
| ------- | ------------------------------ |
| **URL** | `/admin/statistics/user-trend` |
| **方法**  | `GET`                          |
| **认证**  | 需要 Admin Token                 |

**请求参数（Query）**:

| 参数   | 类型      | 必填 | 默认值 | 说明   |
| ---- | ------- | -- | --- | ---- |
| days | Integer | 否  | 30  | 统计天数 |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": [
    { "date": "2026-04-11", "count": 5 },
    { "date": "2026-04-12", "count": 8 }
  ]
}
```

***

### 30.3 文物按博物馆统计

| 项目      | 内容                                     |
| ------- | -------------------------------------- |
| **URL** | `/admin/statistics/artifact-by-museum` |
| **方法**  | `GET`                                  |
| **认证**  | 需要 Admin Token                         |

**响应数据**:

```json
{
  "code": 200,
  "msg": "",
  "data": [
    { "museumId": 1, "museumName": "大都会艺术博物馆", "count": 2000 },
    { "museumId": 3, "museumName": "故宫博物院", "count": 1200 }
  ]
}
```

***

### 30.4 文物按类型统计

| 项目      | 内容                                   |
| ------- | ------------------------------------ |
| **URL** | `/admin/statistics/artifact-by-type` |
| **方法**  | `GET`                                |
| **认证**  | 需要 Admin Token                       |

### 30.5 文物按朝代统计

| 项目      | 内容                                      |
| ------- | --------------------------------------- |
| **URL** | `/admin/statistics/artifact-by-dynasty` |
| **方法**  | `GET`                                   |
| **认证**  | 需要 Admin Token                          |

***

## 31. 附录

### 31.1 接口汇总表

| 编号 | 模块  | 接口路径                                   | 方法     | 认证    |
| -- | --- | -------------------------------------- | ------ | ----- |
| 1  | 认证  | `/admin/employee/login`                | POST   | 无     |
| 2  | 认证  | `/admin/employee/logout`               | POST   | Admin |
| 3  | 用户  | `/user/user/register`                  | POST   | 无     |
| 4  | 用户  | `/user/user/login`                     | POST   | 无     |
| 5  | 用户  | `/user/user/info`                      | GET    | User  |
| 6  | 用户  | `/user/user/update`                    | PUT    | User  |
| 7  | 用户  | `/user/user/password`                  | PUT    | User  |
| 8  | 用户  | `/user/user/avatar`                    | POST   | User  |
| 9  | 用户  | `/admin/user/page`                     | GET    | Admin |
| 10 | 用户  | `/admin/user/ban/{userId}`             | PUT    | Admin |
| 11 | 用户  | `/admin/user/comment-disable/{userId}` | PUT    | Admin |
| 12 | 用户  | `/admin/user/upload-disable/{userId}`  | PUT    | Admin |
| 13 | 角色  | `/admin/role/page`                     | GET    | Admin |
| 14 | 角色  | `/admin/role/list`                     | GET    | Admin |
| 15 | 角色  | `/admin/role`                          | POST   | Admin |
| 16 | 角色  | `/admin/role/{id}`                     | PUT    | Admin |
| 17 | 角色  | `/admin/role/{id}`                     | DELETE | Admin |
| 18 | 角色  | `/admin/role/{roleId}/permissions`     | PUT    | Admin |
| 19 | 角色  | `/admin/role/{roleId}/permissions`     | GET    | Admin |
| 20 | 权限  | `/admin/permission/page`               | GET    | Admin |
| 21 | 权限  | `/admin/permission/list`               | GET    | Admin |
| 22 | 权限  | `/admin/permission`                    | POST   | Admin |
| 23 | 权限  | `/admin/permission/{id}`               | PUT    | Admin |
| 24 | 权限  | `/admin/permission/{id}`               | DELETE | Admin |
| 25 | 权限  | `/admin/user/{userId}/roles`           | PUT    | Admin |
| 26 | 博物馆 | `/admin/museum/page`                   | GET    | Admin |
| 27 | 博物馆 | `/admin/museum/{id}`                   | GET    | Admin |
| 28 | 博物馆 | `/user/museum/list`                    | GET    | User  |
| 29 | 博物馆 | `/admin/museum`                        | POST   | Admin |
| 30 | 博物馆 | `/admin/museum/{id}`                   | PUT    | Admin |
| 31 | 博物馆 | `/admin/museum/{id}`                   | DELETE | Admin |
| 32 | 朝代  | `/admin/dynasty/page`                  | GET    | Admin |
| 33 | 朝代  | `/admin/dynasty/list`                  | GET    | Admin |
| 34 | 朝代  | `/admin/dynasty`                       | POST   | Admin |
| 35 | 朝代  | `/admin/dynasty/{id}`                  | PUT    | Admin |
| 36 | 朝代  | `/admin/dynasty/{id}`                  | DELETE | Admin |
| 37 | 艺术家 | `/admin/artist/page`                   | GET    | Admin |
| 38 | 艺术家 | `/user/artist/list`                    | GET    | User  |
| 39 | 艺术家 | `/admin/artist/{id}`                   | GET    | Admin |
| 40 | 地点  | `/admin/location/tree`                 | GET    | Admin |
| 41 | 地点  | `/admin/location/list`                 | GET    | Admin |
| 42 | 文物  | `/admin/artifact/page`                 | GET    | Admin |
| 43 | 文物  | `/admin/artifact/{id}`                 | GET    | Admin |
| 44 | 文物  | `/user/artifact/list`                  | GET    | User  |
| 45 | 文物  | `/user/artifact/detail/{id}`           | GET    | User  |
| 46 | 文物  | `/admin/artifact`                      | POST   | Admin |
| 47 | 文物  | `/admin/artifact/{id}`                 | PUT    | Admin |
| 48 | 文物  | `/admin/artifact/{id}`                 | DELETE | Admin |
| 49 | 文物  | `/admin/artifact/import`               | POST   | Admin |
| 50 | 文物  | `/admin/artifact/export`               | GET    | Admin |
| 51 | 收藏  | `/user/favorite`                       | POST   | User  |
| 52 | 收藏  | `/user/favorite/{artifactId}`          | DELETE | User  |
| 53 | 收藏  | `/user/favorite/list`                  | GET    | User  |
| 54 | 收藏  | `/user/favorite/check/{artifactId}`    | GET    | User  |
| 55 | 点赞  | `/user/like/artifact/{id}`             | POST   | User  |
| 56 | 点赞  | `/user/like/artifact/{id}`             | DELETE | User  |
| 57 | 点赞  | `/user/like/comment/{id}`              | POST   | User  |
| 58 | 点赞  | `/user/like/post/{id}`                 | POST   | User  |
| 59 | 评论  | `/user/comment`                        | POST   | User  |
| 60 | 评论  | `/user/comment/list/{artifactId}`      | GET    | User  |
| 61 | 评论  | `/user/comment/{commentId}`            | DELETE | User  |
| 62 | 评论  | `/admin/comment/{id}/audit`            | PUT    | Admin |
| 63 | 动态  | `/user/post`                           | POST   | User  |
| 64 | 动态  | `/user/post/list`                      | GET    | User  |
| 65 | 动态  | `/admin/post/{id}/audit`               | PUT    | Admin |
| 66 | 关注  | `/user/follow/{id}`                    | POST   | User  |
| 67 | 关注  | `/user/follow/{id}`                    | DELETE | User  |
| 68 | 关注  | `/user/follow/following`               | GET    | User  |
| 69 | 关注  | `/user/follow/followers`               | GET    | User  |
| 70 | 上传  | `/user/upload`                         | POST   | User  |
| 71 | 上传  | `/admin/upload/{id}/audit`             | PUT    | Admin |
| 72 | 审核  | `/admin/audit/page`                    | GET    | Admin |
| 73 | 审核  | `/admin/audit/{id}`                    | PUT    | Admin |
| 74 | 处罚  | `/admin/penalty`                       | POST   | Admin |
| 75 | 处罚  | `/admin/penalty/page`                  | GET    | Admin |
| 76 | 申诉  | `/user/appeal`                         | POST   | User  |
| 77 | 申诉  | `/admin/appeal/{id}`                   | PUT    | Admin |
| 78 | 公告  | `/admin/announcement`                  | POST   | Admin |
| 79 | 公告  | `/user/announcement/list`              | GET    | User  |
| 80 | 通知  | `/user/notification/list`              | GET    | User  |
| 81 | 通知  | `/user/notification/read`              | PUT    | User  |
| 82 | 通知  | `/user/notification/unread-count`      | GET    | User  |
| 83 | 敏感词 | `/admin/sensitive-word/page`           | GET    | Admin |
| 84 | 敏感词 | `/admin/sensitive-word`                | POST   | Admin |
| 85 | 爬取  | `/admin/crawl-task/page`               | GET    | Admin |
| 86 | 爬取  | `/admin/crawl-task/trigger`            | POST   | Admin |
| 87 | 配置  | `/admin/system-config/list`            | GET    | Admin |
| 88 | 配置  | `/admin/system-config`                 | PUT    | Admin |
| 89 | 备份  | `/admin/backup/page`                   | GET    | Admin |
| 90 | 备份  | `/admin/backup/create`                 | POST   | Admin |
| 91 | 日志  | `/admin/operation-log/page`            | GET    | Admin |
| 92 | 日志  | `/admin/system-log/page`               | GET    | Admin |
| 93 | 日志  | `/admin/security-log/page`             | GET    | Admin |
| 94 | 日志  | `/admin/user-behavior/page`            | GET    | Admin |
| 95 | 上传  | `/admin/common/upload`                 | POST   | Admin |
| 96 | 统计  | `/admin/statistics/dashboard`          | GET    | Admin |
| 97 | 统计  | `/admin/statistics/user-trend`         | GET    | Admin |
| 98 | 统计  | `/admin/statistics/artifact-by-museum` | GET    | Admin |

***

### 31.2 前端开发注意事项

1. **Token 管理**
   - Admin Token 存储 Key：`admin_token`
   - User Token 存储 Key：`user_token`
   - 登录成功后立即存入 `localStorage`
   - 请求拦截器中自动从 `localStorage` 读取并注入请求头
2. **请求头封装（Axios 示例）**

```javascript
// Admin 请求实例
const adminApi = axios.create({ baseURL: '/admin' })
adminApi.interceptors.request.use(config => {
  config.headers['token'] = localStorage.getItem('admin_token')
  config.headers['Content-Type'] = 'application/json;charset=UTF-8'
  return config
})

// User 请求实例
const userApi = axios.create({ baseURL: '/user' })
userApi.interceptors.request.use(config => {
  config.headers['authentication'] = localStorage.getItem('user_token')
  config.headers['Content-Type'] = 'application/json;charset=UTF-8'
  return config
})
```

1. **401 全局处理**

```javascript
// 响应拦截器
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('user_token')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)
```

1. **分页统一处理**

```javascript
// 公共分页请求参数
const pageParams = { page: 1, pageSize: 10 }

// 公共分页响应处理
function handlePageResult(res) {
  return {
    total: res.data.total,
    list: res.data.records,
    page: res.data.page,
    pageSize: res.data.pageSize
  }
}
```

1. **文件上传特殊处理**

文件上传类的接口 Content-Type 为 `multipart/form-data`，需使用 `FormData` 封装：

```javascript
const formData = new FormData()
formData.append('file', file)
axios.post('/admin/common/upload', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})
```

***

### 31.3 后端 Controller 开发规范

后端开发建议遵循以下目录结构（Knife4j 分组）：

```
controller
├── admin/          → Knife4j 分组 "管理端接口"
│   ├── EmployeeController.java
│   ├── UserController.java
│   ├── ArtifactController.java
│   ├── MuseumController.java
│   └── ...
└── user/           → Knife4j 分组 "用户端接口"
    ├── UserController.java
    ├── ArtifactController.java
    └── ...
```

- `admin` 包下的 Controller URL 前缀 `/admin`
- `user` 包下的 Controller URL 前缀 `/user`

***

### 31.4 接口与数据库表映射

| 模块   | 主要表                                            | 关联表                                                     |
| ---- | ---------------------------------------------- | ------------------------------------------------------- |
| 认证   | `users`                                        | `user_roles`、`roles`                                    |
| 角色权限 | `roles`、`permissions`                          | `role_permissions`、`user_roles`                         |
| 博物馆  | `museums`                                      | -                                                       |
| 朝代   | `dynasties`                                    | -                                                       |
| 艺术家  | `artists`                                      | `artifact_artist`                                       |
| 地点   | `locations`                                    | `artifact_location`                                     |
| 文物   | `artifacts`                                    | `artifact_images`、`artifact_artist`、`artifact_location` |
| 收藏   | `user_favorites`                               | `artifacts`                                             |
| 点赞   | `artifact_likes`、`comment_likes`、`post_likes`  | -                                                       |
| 评论   | `user_comments`                                | `users`、`artifacts`                                     |
| 动态   | `user_posts`                                   | `users`、`artifacts`、`museums`                           |
| 关注   | `user_follows`                                 | `users`                                                 |
| 上传   | `user_uploads`                                 | -                                                       |
| 审核   | `audit_records`                                | `users`                                                 |
| 处罚   | `penalty_records`                              | `users`、`violation_types`                               |
| 申诉   | `appeal_records`                               | `penalty_records`                                       |
| 公告   | `announcements`                                | -                                                       |
| 通知   | `notifications`                                | `users`                                                 |
| 敏感词  | `sensitive_words`                              | -                                                       |
| 爬取   | `crawl_tasks`                                  | `museums`                                               |
| 配置   | `system_configs`                               | -                                                       |
| 备份   | `backup_records`                               | -                                                       |
| 日志   | `operation_logs`、`system_logs`、`security_logs` | -                                                       |
| 用户行为 | `user_behaviors`                               | -                                                       |
| 浏览历史 | `user_browse_history`                          | `users`、`artifacts`                                     |

***

### 31.5 变更记录

| 日期         | 版本   | 变更内容                 | 变更人 |
| ---------- | ---- | -------------------- | --- |
| 2026-05-10 | v2.0 | 初始版本，覆盖全部 31 个模块 API | -   |

***

> **文档结束** —— 如有疑问请参考对应模块源代码或联系后端负责人。

