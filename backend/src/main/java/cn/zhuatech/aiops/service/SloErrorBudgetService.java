/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.service;

import cn.zhuatech.aiops.common.BusinessException;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 计算 SLO 错误预算、多窗口燃烧率，并给出变更冻结或回滚决策。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class SloErrorBudgetService {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public BudgetResult evaluate(BudgetRequest request) {
        validateCounts(request);
        double allowedErrorRate = 1d - request.targetAvailabilityPercent() / 100d;
        double allowedFailures = request.totalRequests() * allowedErrorRate;
        double consumedPercent = round(request.failedRequests() * 100d / allowedFailures);
        double remainingPercent = round(Math.max(0d, 100d - consumedPercent));
        double shortBurn = burnRate(request.shortWindowFailures(), request.shortWindowRequests(), allowedErrorRate);
        double longBurn = burnRate(request.longWindowFailures(), request.longWindowRequests(), allowedErrorRate);
        List<String> reasons = new ArrayList<>();
        List<String> actions = new ArrayList<>();

        if (request.activeSev1()) {
            reasons.add("存在活动中的一级生产事件");
            if (request.changeInProgress() && request.rollbackReady()) {
                actions.add("立即停止变更并执行已验证回滚方案");
                actions.add("恢复后重新计算错误预算并完成事件复盘");
                return result(Decision.ROLLBACK, false, consumedPercent, remainingPercent,
                        shortBurn, longBurn, reasons, actions);
            }
            actions.add("启动一级事件响应并冻结非必要变更");
            return result(Decision.INCIDENT, false, consumedPercent, remainingPercent,
                    shortBurn, longBurn, reasons, actions);
        }

        if (consumedPercent >= 100d) reasons.add("当前周期错误预算已经耗尽");
        if (shortBurn >= 14d && longBurn >= 6d) reasons.add("多窗口快速燃烧告警已触发");
        if (!reasons.isEmpty()) {
            actions.add("冻结生产变更并通知服务责任人");
            actions.add("建立稳定性改进项，预算恢复前仅允许紧急修复");
            return result(Decision.FREEZE_CHANGES, false, consumedPercent, remainingPercent,
                    shortBurn, longBurn, reasons, actions);
        }

        if (consumedPercent >= 75d || shortBurn >= 6d || longBurn >= 3d) {
            if (consumedPercent >= 75d) reasons.add("错误预算使用率达到 75% 预警线");
            if (shortBurn >= 6d || longBurn >= 3d) reasons.add("燃烧率达到发布复核阈值");
            actions.add("生产变更需由 SRE 复核并限制发布批次");
            return result(Decision.REVIEW_CHANGES, false, consumedPercent, remainingPercent,
                    shortBurn, longBurn, reasons, actions);
        }

        actions.add("允许按标准变更流程发布并持续监控多窗口燃烧率");
        return result(Decision.HEALTHY, true, consumedPercent, remainingPercent,
                shortBurn, longBurn, reasons, actions);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private void validateCounts(BudgetRequest request) {
        if (request.targetAvailabilityPercent() <= 0d || request.targetAvailabilityPercent() >= 100d) {
            throw new BusinessException("目标可用性必须大于 0 且小于 100");
        }
        if (request.failedRequests() > request.totalRequests()
                || request.shortWindowFailures() > request.shortWindowRequests()
                || request.longWindowFailures() > request.longWindowRequests()) {
            throw new BusinessException("失败请求数不能超过对应窗口总请求数");
        }
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private double burnRate(long failures, long requests, double allowedErrorRate) {
        return round((failures * 1d / requests) / allowedErrorRate);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private double round(double value) {
        return Math.round(value * 100d) / 100d;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private BudgetResult result(Decision decision, boolean deploymentAllowed, double consumed,
                                double remaining, double shortBurn, double longBurn,
                                List<String> reasons, List<String> actions) {
        return new BudgetResult(decision, deploymentAllowed, consumed, remaining, shortBurn, longBurn,
                List.copyOf(reasons), List.copyOf(actions));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record BudgetRequest(
            @NotBlank String serviceName,
            @Positive int windowMinutes,
            @Positive long totalRequests,
            @PositiveOrZero long failedRequests,
            @DecimalMin("90.0") @DecimalMax("99.999") double targetAvailabilityPercent,
            @Positive int shortWindowMinutes,
            @Positive long shortWindowRequests,
            @PositiveOrZero long shortWindowFailures,
            @Positive int longWindowMinutes,
            @Positive long longWindowRequests,
            @PositiveOrZero long longWindowFailures,
            boolean activeSev1,
            boolean changeInProgress,
            boolean rollbackReady
    ) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record BudgetResult(Decision decision, boolean deploymentAllowed,
                               double budgetConsumedPercent, double budgetRemainingPercent,
                               double shortWindowBurnRate, double longWindowBurnRate,
                               List<String> reasons, List<String> actions) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public enum Decision { HEALTHY, REVIEW_CHANGES, FREEZE_CHANGES, INCIDENT, ROLLBACK }
}
