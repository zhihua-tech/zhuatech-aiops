/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

/** 使用长窗口实际失败速率，估计固定 SLO 周期剩余错误预算的耗尽时间。 */
@Service
public class SloBudgetForecastService {
    private final SloErrorBudgetService budgetService;

    public SloBudgetForecastService(SloErrorBudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public ForecastResult forecast(ForecastRequest request) {
        if (request.forecastMinutes() <= 0 || request.budget() == null
                || request.budget().longWindowMinutes() <= 0
                || request.budget().totalRequests() <= 0
                || request.budget().longWindowRequests() <= 0) {
            throw new IllegalArgumentException("预测窗口和 SLO 采样窗口必须大于零");
        }
        var budget = budgetService.evaluate(request.budget());
        double allowedFailures = request.budget().totalRequests()
                * (1d - request.budget().targetAvailabilityPercent() / 100d);
        double remainingFailures = Math.max(0d, allowedFailures - request.budget().failedRequests());
        if (remainingFailures < 1e-8) remainingFailures = 0d;
        double failuresPerMinute = request.budget().longWindowFailures() * 1d
                / request.budget().longWindowMinutes();
        Double exhaustionMinutes = failuresPerMinute == 0d ? null : round(remainingFailures / failuresPerMinute);
        double projectedFailures = failuresPerMinute * request.forecastMinutes();
        double projectedRemaining = Math.max(0d, remainingFailures - projectedFailures);
        ForecastDecision decision = remainingFailures == 0d ? ForecastDecision.EXHAUSTED
                : failuresPerMinute == 0d ? ForecastDecision.STABLE
                : projectedFailures >= remainingFailures ? ForecastDecision.FORECAST_BREACH
                : projectedFailures * 2 >= remainingFailures ? ForecastDecision.WATCH
                : ForecastDecision.STABLE;
        return new ForecastResult(decision, budget.decision(), round(remainingFailures),
                round(failuresPerMinute), round(projectedFailures), round(projectedRemaining),
                exhaustionMinutes, decision == ForecastDecision.STABLE && budget.deploymentAllowed());
    }

    private double round(double value) {
        return Math.round(value * 100d) / 100d;
    }

    public record ForecastRequest(@NotNull @Valid SloErrorBudgetService.BudgetRequest budget,
                                  @Positive int forecastMinutes) {}

    public record ForecastResult(ForecastDecision forecastDecision,
                                 SloErrorBudgetService.Decision currentDecision,
                                 double remainingFailureAllowance,
                                 double observedFailuresPerMinute,
                                 double projectedFailures,
                                 double projectedRemainingFailureAllowance,
                                 Double estimatedMinutesToExhaustion,
                                 boolean deploymentAllowed) {}

    public enum ForecastDecision { STABLE, WATCH, FORECAST_BREACH, EXHAUSTED }
}
