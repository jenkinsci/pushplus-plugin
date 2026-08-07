# pushplus Notification Plugin for Jenkins

[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/pushplus-plugin-jenkins.svg)](https://plugins.jenkins.io/pushplus-plugin-jenkins)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/pushplus-plugin-jenkins.svg?color=blue)](https://plugins.jenkins.io/pushplus-plugin-jenkins)

Send Jenkins build notifications to [pushplus](https://www.pushplus.plus/) push service, supporting WeChat, webhook, email, SMS, and more.

> Source: [pushplus/perk-pushplus-plugin-jenkins](https://github.com/pushplus/perk-pushplus-plugin-jenkins). After Jenkins hosting, the canonical repository will be under the `jenkinsci` organization.

## Features

- Push build result notifications (success / failure / aborted / unstable)
- Multiple delivery channels: WeChat Official Account, Webhook (WeCom, DingTalk, Feishu, etc.), WeCom App, Email, SMS, App
- Group messaging via topic codes
- Multi-recipient support

## Installation

1. Go to **Manage Jenkins > Manage Plugins > Available**
2. Search for **pushplus Notification**
3. Install and restart Jenkins

## Configuration

### Global Configuration

Go to **Manage Jenkins > Configure System**, find the **pushplus** section:

- **Token**: Your pushplus token (get it from [pushplus.plus](https://www.pushplus.plus/))
- **Jenkins URL**: Your Jenkins base URL for build result links

### Job Configuration

In your job's **Post-build Actions**, add **pushplus Notification**:

| Parameter | Description                                                                                        |
|-----------|----------------------------------------------------------------------------------------------------|
| **Channel** | Delivery channel: `wechat` (default), `webhook`, `cp`, `mail`, `sms`, `voice`, `extension`, `app`, `clawbot` |
| **Topic** | Group code for group messaging. Leave empty to send to yourself only.                              |
| **To** | Friend token (WeChat) or user ID (WeCom). Comma-separated for multiple recipients.                 |
| **Webhook** | Channel option / webhook code (used by webhook and other channels that need extra config).         |

### Pipeline Usage

```groovy
pushplus(topic: '', channel: 'wechat', webhook: '', to: '')
```

## Notification Content

Each notification includes:

- Build title (success / failure / aborted / unstable)
- Project name
- Build number
- Build user
- Build status
- Build duration
- Project URL
- Build log URL

## License

Licensed under the [MIT License](LICENSE).

---

## 推送服务 pushplus 插件

> Jenkins 插件，将构建结果推送到 [pushplus 推送加](https://www.pushplus.plus/) 服务

### 功能特性

#### 基础配置
- **Token 配置**：在 Jenkins 全局配置中设置 pushplus Token
- **Jenkins URL**：配置 Jenkins 访问地址，用于构建结果链接

#### 推送参数配置

1. **群组编码 (topic)**
   - 配置群组编码，可将消息推送到指定群组
   - 不填写则仅发送给自己

2. **发送渠道 (channel)**
   - **wechat** - 微信服务号（默认）
   - **webhook** - 第三方 webhook（企业微信、钉钉、飞书、Server酱、IFTTT 等）
   - **cp** - 企业微信应用
   - **mail** - 邮件
   - **sms** - 短信
   - **extension** - 插件
   - **app** - App
   - **clawbot** - 微信ClawBot

3. **Webhook 编码 (webhook)**
   - 仅在选择 webhook 渠道时有效
   - 可在 pushplus 官网获取或配置 webhook 地址

4. **好友令牌 (to)**
   - 微信服务号渠道：填写好友令牌
   - 企业微信渠道：填写企业微信用户 id
   - 多人接收：用逗号隔开
   - 不填写则仅发送给自己

### 使用方法

1. 在 Jenkins 全局配置中配置 pushplus Token 和 Jenkins URL
2. 在项目的构建后操作中添加 **pushplus Notification**（中文界面显示为「pushplus 推送通知」）步骤
3. 根据需要配置相应的推送参数
4. 保存配置后，每次构建完成都会自动推送通知

### 推送内容

构建通知包含以下信息：
- 构建标题（成功/失败/终止/不稳定）
- 项目名称
- 构建编号
- 构建用户
- 构建状态
- 构建耗时
- 项目 URL
- 构建日志 URL
