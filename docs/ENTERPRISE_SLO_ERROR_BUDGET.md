# 企业 SLO 错误预算与变更控制

Copyright © 2026 上海如静知华信息科技有限公司 · <https://www.zhuatech.cn/>

`POST /api/enterprise/aiops/slo-error-budget` 根据服务目标可用性、周期请求量与失败量计算错误预算消耗，并使用短窗口和长窗口失败率计算多窗口燃烧率。

决策包含 `HEALTHY`、`REVIEW_CHANGES`、`FREEZE_CHANGES`、`INCIDENT` 和 `ROLLBACK`：预算健康时允许按标准流程发布；达到预警线时要求 SRE 复核；预算耗尽或快速燃烧时冻结变更；活动中的一级事件会启动事件响应，若同时存在进行中的变更和已验证回滚方案则直接建议回滚。

响应提供预算消耗/剩余百分比、两个窗口的燃烧率、是否允许发布、原因和动作清单。生产环境可把该接口接入发布流水线、变更审批与值班告警，实现以 SLO 为依据的发布治理。
