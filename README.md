# RelicAdmin 前端优化说明


## 一、改动概览

| 模块 | 说明 |
|------|------|
| 个人中心 · 角色展示 | 角色栏改为调用与「角色管理」相同的数据源，按 `displayName` 显示 |
| 个人中心 · 界面优化 | 双栏响应式布局、标签页分区、头像区与全局样式统一 |
| 个人中心 · 修改密码 | 新密码与原密码相同时提示「新密码不能与原密码相同」并阻止提交；`PUT /admin/admin-user/current/password` 及用户端改密接口同步校验 |
| 全局 UI（延续） | Element Plus 管理后台风格：布局、主题、列表页、分页等 |

---

## 二、个人中心 · 角色对应

### 2.1 背景

原先个人中心「角色」栏写死为「管理员」，与「角色管理」页面及数据库中的真实角色不一致。

### 2.2 实现思路

角色名称与 **角色管理** 使用同一套数据来源：

1. **`GET /admin/role/list`**  
   获取全部角色，使用其中的 `displayName`（显示名称），与角色管理表格「显示名称」列一致。

2. **`GET /admin/admin-user/page`**  
   当前登录接口 `GET /admin/admin-user/current` 不返回 `roleId`，因此在前端通过分页接口按当前用户名查询本人记录，读取与「管理员管理」列表相同的 `roleId` 字段。

3. **前端映射**  
   用 `roleId` 在角色列表中匹配，渲染 `el-tag`；无角色时显示「未分配角色」。

### 2.3 涉及文件

```
relic-frontend/src/views/ProfileView.vue    # 加载角色并展示标签
relic-frontend/src/utils/adminRole.js       # 角色解析工具函数
relic-frontend/src/api/role.js              # getRoleList（已有）
relic-frontend/src/api/adminUser.js         # getCurrentAdmin、getAdminUserPage（已有）
```

### 2.4 新增工具函数（adminRole.js）

| 函数 | 作用 |
|------|------|
| `findRoleById(roleId, roles)` | 在角色列表中按 id 查找 |
| `roleTagFromList(roleId, roles)` | 解析为 `{ label, type }`，label 优先取 `displayName` |
| `buildProfileRoleTags(roleId, roles)` | 个人中心单角色标签数组 |

### 2.5 展示效果

- 已分配角色：显示与角色管理中一致的名称（如「超级审核员」「内容审核员」）
- 未分配角色：灰色「未分配角色」标签
- 标签颜色仍按角色 id 区分（与管理员列表风格接近）

---

## 三、个人中心 · 界面优化

### 3.1 布局结构

```
┌─────────────────────────────────────────────────────┐
│  el-page-header · 个人中心                           │
├──────────────┬──────────────────────────────────────┤
│  左侧概览卡   │  右侧功能面板（el-tabs）               │
│  · 渐变横幅   │  ┌─ 基本信息 ─┬─ 安全设置 ─┐          │
│  · 头像/姓名  │  │  资料表单    │  修改密码    │          │
│  · 状态/角色  │  └─────────────┴─────────────┘          │
│  · 登录信息   │                                       │
└──────────────┴──────────────────────────────────────┘
```

- **左侧（约 7/24 列）**：账号概览，含头像、姓名、@用户名、状态与角色标签、最后登录 / IP / 注册时间
- **右侧（约 17/24 列）**：`el-tabs` 分为「基本信息」与「安全设置」，避免上下堆叠多个大卡片
- **响应式**：`xs/sm` 全宽纵向排列；`md` 及以上恢复双栏

### 3.2 交互与体验

| 项 | 说明 |
|----|------|
| 页面 loading | 加载个人信息与角色数据时整页 loading |
| 头像上传 | 横向头像区 + 说明文字；编辑模式下可上传；成功后左侧概览头像同步更新 |
| 基本信息 | 姓名 / 手机两列并排，邮箱单行；仅编辑模式可改 |
| 安全设置 | 顶部 info 提示「修改密码后需重新登录」 |
| 修改密码 | 新密码与原密码一致时，表单项提示「新密码不能与原密码相同」且无法提交；改密成功后清除 Token 并跳转登录页 |
| 时间格式 | `2026-05-23T17:07:00` 自动格式化为 `2026-05-23 17:07:00` |

### 3.3 样式

个人中心样式集中在全局主题文件中，与仪表盘、列表页共用 Element Plus 设计变量：

```
relic-frontend/src/styles/admin-theme.css   # .profile-page 相关样式块
```

主要包含：概览卡横幅、头像阴影、元信息列表、头像编辑区、表单宽度、移动端适配等。

---

## 四、相关全局 UI（同批次前端优化）

以下为同一阶段对管理后台前端的整体视觉统一，便于对照：

| 文件 | 内容 |
|------|------|
| `src/layout/MainLayout.vue` | 可折叠侧栏、顶栏面包屑、用户下拉 |
| `src/views/LoginView.vue` | 卡片式登录页 |
| `src/styles/admin-theme.css` | 列表卡片、筛选区、分页、表格等统一样式 |
| `src/components/PageContainer.vue` | 可选页面容器（标题 + 描述 + 内容） |
| `src/App.vue` | `el-config-provider` 中文 locale |

部分列表页已统一 `el-card shadow="never"`、分页 `background` 等 Element Plus 推荐用法。

---

## 五、本地运行与验证

### 启动前端

```powershell
cd relic-frontend
npm install
npm run dev
```

访问：`http://localhost:5173`

### 验证个人中心

1. 使用管理员账号登录
2. 进入 **个人中心**（侧栏或顶栏用户菜单）
3. 确认左侧「角色」与 **角色管理 → 显示名称** 一致
4. 切换「基本信息 / 安全设置」标签页，检查布局与编辑、改密流程
5. 「安全设置」中将新密码设为与原密码相同，应提示「新密码不能与原密码相同」且无法提交

### 构建

```powershell
cd relic-frontend
npm run build
```

---

## 六、已知说明

1. **角色 id 来源**  
   当前仍依赖分页接口中的单个 `roleId` 字段；若账号绑定多个角色，后端需扩展 `roleIds` / `roleNames` 后，前端可在 `adminRole.js` 中扩展多标签展示。

2. **`/current` 无 roleId**  
   属现有接口返回字段限制；本次在前端用分页接口补全，未改后端。

3. **改密接口**  
   个人中心与管理员改密表单均校验新密码不可与原密码相同；涉及 `ProfileView.vue`、`AdminUserListView.vue` 及 `MessageConstant.PASSWORD_SAME_AS_OLD` 对应后端实现。

## 八、2026-06-02 更新

### 8.1 管理员列表 · 操作列优化

- 操作按钮改为 **link 文字按钮**，按功能区分颜色（编辑/分配角色、禁用/启用、重置密码、删除）
- 使用 `.table-actions` 容器统一 **12px 间距**，避免按钮挤在一起
- 涉及：`AdminUserListView.vue`、`admin-theme.css`

### 8.2 顶栏与个人头像

- 新增默认头像资源 `public/default-avatar.svg` 与工具 `utils/avatar.js`
- 顶栏右上角显示当前管理员头像（无头像时显示默认图）
- 登录、进入后台、个人中心加载/上传头像后，通过 `auth.updateUserInfo` 同步顶栏
- 涉及：`MainLayout.vue`、`stores/auth.js`、`ProfileView.vue`

### 8.3 前端架构精简

| 新增 | 说明 |
|------|------|
| `composables/usePagination.js` | 列表分页、加载、翻页统一逻辑 |
| `composables/useDateRangeQuery.js` | 时间范围筛选参数 |
| `composables/useUserBehaviorList.js` | 用户行为类列表组合逻辑 |
| `utils/format.js` | 时间格式化 `formatDateTime` |
| `utils/status.js` | 用户/管理员状态标签 |
| `components/ListPagination.vue` | 全局分页组件 |
| `components/UserBehaviorSearchBar.vue` | 行为页共用搜索条 |
| `config/menu.js` | 侧栏菜单配置（单一数据源） |

**布局调整：**

- `MainLayout.vue` 侧栏菜单改为读取 `config/menu.js`，补充「审核统计」入口
- 布局级 `el-page-header` 默认关闭，由 `PageContainer` 统一页面标题，避免双标题
- 旧路由 `/favorites` 重定向至 `/user-behavior/favorites`

**已迁移示例页面：**

- `UserFavoriteAdminView.vue`、`UserFollowAdminView.vue`（`PageContainer` + composable）
- `admin-theme.css` 新增 `.search-bar`、`.table-actions` 全局样式

### 8.4 改动文件清单（本日）

```
relic-frontend/
├── public/default-avatar.svg
├── src/
│   ├── composables/          # usePagination、useDateRangeQuery、useUserBehaviorList
│   ├── config/menu.js      # 侧栏菜单配置
│   ├── components/         # ListPagination、UserBehaviorSearchBar
│   ├── utils/              # avatar.js、format.js、status.js
│   ├── stores/auth.js      # 登录保存 avatarUrl、updateUserInfo
│   ├── layout/MainLayout.vue
│   ├── router/index.js
│   ├── main.js
│   ├── views/
│   │   ├── AdminUserListView.vue
│   │   ├── ProfileView.vue
│   │   ├── UserFavoriteAdminView.vue
│   │   └── UserFollowAdminView.vue
│   └── styles/admin-theme.css
└── README.md
```

---

## 九、2026-06-04 更新

### 9.1 图片预览被栏遮挡

**问题**：文物图片管理、用户收藏等列表中，点击缩略图大图预览时，预览层或底部工具栏会被表格右侧固定「操作」列、主内容区滚动容器挡住。

**原因**：`el-image` 默认未 `preview-teleported`，预览节点渲染在表格 DOM 内，受 `overflow` 与固定列层级影响。

**处理**：

- `ArtifactImageView.vue`、`UserFavoriteListView.vue`：为 `el-image` 增加 `preview-teleported`、`:z-index="3200"`
- `admin-theme.css`：提高 `.el-image-viewer__wrapper` 层级

**验证**：进入「内容管理 → 文物管理 → 图片」，点击预览，确认全屏遮罩与底部缩放/旋转工具栏不被遮挡。

### 9.2 改动文件

```
relic-frontend/src/views/ArtifactImageView.vue
relic-frontend/src/views/UserFavoriteListView.vue
relic-frontend/src/styles/admin-theme.css
```

---

*文档更新时间：2026-06-04*
