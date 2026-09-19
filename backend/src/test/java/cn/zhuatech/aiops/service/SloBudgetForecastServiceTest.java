/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class SloBudgetForecastServiceTest {
    private final SloBudgetForecastService service = new SloBudgetForecastService(new SloErrorBudgetService());

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void forecastsBudgetBreachFromLongWindowFailureRate() {
        var result = service.forecast(new SloBudgetForecastService.ForecastRequest(budget(2, 3), 40));
        assertEquals(SloBudgetForecastService.ForecastDecision.FORECAST_BREACH, result.forecastDecision());
        assertEquals(8d, result.remainingFailureAllowance());
        assertEquals(0.3d, result.observedFailuresPerMinute());
        assertEquals(26.67d, result.estimatedMinutesToExhaustion());
        assertFalse(result.deploymentAllowed());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void noRecentFailuresRemainStableWithoutFalseExhaustionTime() {
        var result = service.forecast(new SloBudgetForecastService.ForecastRequest(budget(2, 0), 30));
        assertEquals(SloBudgetForecastService.ForecastDecision.STABLE, result.forecastDecision());
        assertNull(result.estimatedMinutesToExhaustion());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void exhaustedBudgetCannotDeploy() {
        var result = service.forecast(new SloBudgetForecastService.ForecastRequest(budget(10, 1), 10));
        assertEquals(SloBudgetForecastService.ForecastDecision.EXHAUSTED, result.forecastDecision());
        assertFalse(result.deploymentAllowed());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void invalidForecastHorizonIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.forecast(
                new SloBudgetForecastService.ForecastRequest(budget(2, 1), 0)));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private SloErrorBudgetService.BudgetRequest budget(long failures, long longWindowFailures) {
        return new SloErrorBudgetService.BudgetRequest("order-api", 1440, 1000, failures, 99,
                5, 500, 0, 10, 1000, longWindowFailures,
                false, false, true);
    }
}
