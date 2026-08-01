# AIOps API 摘要

版权所有 © 2026 上海如静知华信息科技有限公司。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录并获取 JWT |
| GET | `/api/admin/dashboard` | 智能运维运行态势 |
| GET | `/api/admin/work-orders` | 智能事件清单 |
| GET | `/api/shopfloor/dashboard` | 一线值守工作台 |
| POST | `/api/shopfloor/work-orders/{id}/reports` | 提交事件处置记录 |
| POST | `/api/shopfloor/ai-preview` | 调用可替换 AI Provider 生成根因建议 |
| POST | `/api/shopfloor/incident-correlation` | 关联多源告警并输出事件等级、疑似原因与处置动作 |
