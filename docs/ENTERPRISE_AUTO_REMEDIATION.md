# 企业级 AIOps 自动修复执行门禁

`POST /api/enterprise/aiops/auto-remediation-execution` 校验运行手册、影响范围、变更窗口、预演、回滚、命令白名单、特权授权、集群健康、事故冲突和影响实例上限。

接口返回 `EXECUTE / SUPERVISED / BLOCKED`。关键服务只允许在人工批准和监督下分批执行；生产系统还应接入变更单、指标验证、自动熔断和完整命令审计。
