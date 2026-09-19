/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class AutoRemediationExecutionService {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Assessment assess(Request request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        if (!request.approvedRunbook()) blockers.add("自动修复运行手册未批准");
        if (!request.blastRadiusKnown()) blockers.add("变更影响范围不明确");
        if (!request.changeWindowApproved()) blockers.add("执行窗口未获批准");
        if (!request.dryRunPassed()) blockers.add("预演或影子执行未通过");
        if (!request.rollbackReady()) blockers.add("自动回滚未就绪");
        if (!request.commandAllowlisted()) blockers.add("执行命令不在允许清单");
        if (!request.privilegedAccessApproved()) blockers.add("特权访问未批准");
        if (!request.quorumHealthy()) blockers.add("目标集群健康或仲裁状态异常");
        if (request.activeIncidentConflict()) blockers.add("存在冲突的活动事故处置");
        if (request.estimatedAffectedInstances() > request.maxAffectedInstances()) blockers.add("预计影响实例超过安全上限");
        if (request.affectedCriticalService() && !request.humanApprovalComplete()) blockers.add("关键服务修复缺少人工批准");
        if (!blockers.isEmpty()) {
            actions.add("阻断自动执行并转入人工变更评审");
            return new Assessment(Decision.BLOCKED, blockers, actions);
        }
        if (request.affectedCriticalService()) {
            actions.add("在人工监督下分批执行并实时验证服务指标");
            return new Assessment(Decision.SUPERVISED, blockers, actions);
        }
        actions.add("批准自动修复，并记录命令、结果、指标和回滚状态");
        return new Assessment(Decision.EXECUTE, blockers, actions);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Request(@NotBlank String actionId, boolean approvedRunbook, boolean blastRadiusKnown,
                          boolean affectedCriticalService, boolean changeWindowApproved,
                          boolean dryRunPassed, boolean rollbackReady, boolean commandAllowlisted,
                          boolean privilegedAccessApproved, boolean quorumHealthy,
                          boolean activeIncidentConflict, @Min(0) int estimatedAffectedInstances,
                          @Min(0) int maxAffectedInstances, boolean humanApprovalComplete) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Assessment(Decision decision, List<String> blockers, List<String> actions) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public enum Decision { EXECUTE, SUPERVISED, BLOCKED }
}
