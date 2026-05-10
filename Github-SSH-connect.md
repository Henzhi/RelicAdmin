# Git 推送失败与 SSH 配置完全教程（从零到成功）

本教程基于真实踩坑经历：**VSCode 推送时 GnuTLS 报错 → Ubuntu 服务器 SSH 配置 → 创建新仓库绕过旧仓库问题 → Windows 本地 SSH 配置并成功推送 Java 项目**。适用于 Linux 服务器和 Windows 本地两种环境。

---

## 一、问题现象与核心原因

### 典型错误 1：GnuTLS recv error (-110)
```
fatal: unable to access 'https://github.com/...': GnuTLS recv error (-110): The TLS connection was non-properly terminated.
```
- **原因**：HTTPS 协议下的 TLS 握手被网络（防火墙/代理）中断，或系统 CA 证书问题。

### 典型错误 2：SSH 连接超时 / Permission denied
```
ssh: connect to host github.com port 22: Connection timed out
git@github.com: Permission denied (publickey).
```
- **原因**：22 端口被网络封锁；或未配置 SSH 密钥；或公钥未添加到 GitHub。

### 典型错误 3：No such remote 'origin'
```
error: No such remote 'origin'
```
- **原因**：当前目录不是 Git 仓库，或从未关联远程仓库。

### 核心结论
- **推荐使用 SSH 协议**，避免 HTTPS 的 TLS 问题。
- 如果 22 端口被封，改用 **443 端口** 连接 `ssh.github.com`。
- 每台机器（Ubuntu 服务器、Windows 电脑）需**独立生成 SSH 密钥对**，并将**公钥**添加到 GitHub 账户。

---

## 二、准备工作：GitHub 账户与仓库

1. 登录 GitHub，点击右上角 `+` → **New repository**。
2. 填写仓库名（如 `seitem`）。
3. **不要**勾选 “Add a README” / “.gitignore” / “license”（保持完全空仓库）。
4. 创建后，复制仓库的 **SSH 地址**：`git@github.com:你的用户名/仓库名.git`

---

## 三、Ubuntu 服务器端配置（解决 GnuTLS 错误）

### 步骤 1：生成 SSH 密钥（在服务器终端执行）
```bash
ssh-keygen -t ed25519 -C "你的GitHub邮箱@example.com"
```
- 一路回车（使用默认路径 `~/.ssh/id_ed25519`，不设密码）。

### 步骤 2：添加公钥到 GitHub
```bash
cat ~/.ssh/id_ed25519.pub
```
- 复制输出的全部内容。
- 打开 GitHub → Settings → SSH and GPG keys → New SSH key → 粘贴 → Add。

### 步骤 3：测试 SSH 连接（默认 22 端口）
```bash
ssh -T git@github.com
```
- 若出现 `Permission denied` 或超时，跳到**步骤 4**（改用 443 端口）。
- 若成功显示 `Hi username! ...`，直接跳到**步骤 5**。

### 步骤 4：解决 22 端口超时（改用 443 端口）
编辑 SSH 配置文件：
```bash
vim ~/.ssh/config
```
添加以下内容：
```
Host github.com
    HostName ssh.github.com
    User git
    Port 443
    IdentityFile ~/.ssh/id_ed25519
```
保存后再次测试：
```bash
ssh -T git@github.com
```
应该成功。

### 步骤 5：推送本地代码（以 Odoo 模块 myaddons 为例）
```bash
git init
git add .
git commit -m "初始提交 myaddons 模块"
git remote add origin git@github.com:Henzhi/Odoo18-MyAddons.git
git branch -M main                # 与 GitHub 默认分支名统一
git push -u origin main
```
> 若第一次提交时提示 `Author identity unknown`，先配置：
> ```bash
> git config --global user.name "你的GitHub用户名"
> git config --global user.email "你的GitHub邮箱"
> ```

---

## 四、Windows 本地配置（推送 Java 项目）

### 步骤 1：生成 SSH 密钥（在 PowerShell 或 Git Bash 中）
```powershell
ssh-keygen -t ed25519 -C "你的GitHub邮箱@example.com"
```
- 按回车使用默认路径 `C:\Users\你的用户名\.ssh\id_ed25519`。
- 按回车跳过密码短语。

### 步骤 2：添加公钥到 GitHub
```powershell
cat ~/.ssh/id_ed25519.pub
```
- 复制输出 → GitHub Settings → SSH and GPG keys → New SSH key → 粘贴 → Add。

### 步骤 3：测试连接（同样可能遇到端口 22 超时）
```powershell
ssh -T git@github.com
```
- 若超时，同样配置 `~/.ssh/config` 文件（路径 `C:\Users\你的用户名\.ssh\config`）：
```
Host github.com
    HostName ssh.github.com
    User git
    Port 443
    IdentityFile ~/.ssh/id_ed25519
```
- 再次测试，应成功显示 `Hi username! ...`

### 步骤 4：推送现有 Java 项目（如 seitem）
```powershell
cd C:\Code\Java-study-item\seitem\seitem
git init
git add .
git commit -m "Initial commit"
git remote add origin git@github.com:Henzhi/seitem.git
git branch -M main
git push -u origin main
```

### 步骤 5：后续日常更新
每次修改代码后：
```powershell
git add .
git commit -m "修改说明"
git push
```

---

## 五、常见问题与解决方法

| 错误现象 | 可能原因 | 解决方案 |
|---------|----------|----------|
| `GnuTLS recv error (-110)` | HTTPS 协议 TLS 握手失败 | 改用 SSH 协议（本教程全部使用 SSH） |
| `Permission denied (publickey)` | 公钥未添加或私钥路径不对 | 检查 `~/.ssh/id_ed25519.pub` 已添加到 GitHub；检查 `~/.ssh/config` 中 `IdentityFile` 路径 |
| `Connection timed out` (port 22) | 网络封禁 22 端口 | 按上述方法改用 443 端口连接 `ssh.github.com` |
| `No such remote 'origin'` | 未关联远程仓库 | 执行 `git remote add origin git@github.com:用户名/仓库名.git` |
| `failed to push some refs` | 远程仓库有本地没有的提交（如 README） | 先 `git pull origin main --allow-unrelated-histories`，合并后再 `git push` |
| `Author identity unknown` | 未配置 Git 用户名/邮箱 | `git config --global user.name "..."` `git config --global user.email "..."` |
| `src refspec main does not match any` | 没有本地提交 | 先 `git commit -m "..."` |
| `ssh: Could not resolve hostname github.com` | DNS 解析问题 | 检查网络，或修改 hosts 文件：`140.82.112.3 github.com` |

---

## 六、最佳实践总结

1. **每个设备生成独立的 SSH 密钥**，公钥均添加到 GitHub（允许多个）。
2. **优先使用 SSH 协议**，遇到端口封锁则改用 443 端口。
3. **新仓库保持空状态**（不勾选 README），避免首次推送需要合并历史。
4. **本地首次推送前**：`git init` → `git add .` → `git commit -m "init"` → `git remote add origin ...` → `git push -u origin main`。
5. **日常三连**：`git add .` → `git commit -m "msg"` → `git push`。

通过以上步骤，你可以彻底解决“VSCode 推送慢 / GnuTLS 错误 / SSH 超时”等问题，顺利将代码推送到 GitHub。
通过以上步骤，你可以彻底解决“VSCode 推送慢 / GnuTLS 错误 / SSH 超时”等问题，顺利将代码推送到 GitHub。