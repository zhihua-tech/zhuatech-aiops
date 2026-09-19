/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.service;

import cn.zhuatech.aiops.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class SloErrorBudgetServiceTest {
    private final SloErrorBudgetService service = new SloErrorBudgetService();

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void allowsDeploymentWithHealthyBudget() {
        var result = service.evaluate(request(50, 20, 100, false, false, false));
        assertThat(result.decision()).isEqualTo(SloErrorBudgetService.Decision.HEALTHY);
        assertThat(result.deploymentAllowed()).isTrue();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void requiresReviewNearBudgetLimit() {
        var result = service.evaluate(request(800, 40, 200, false, false, false));
        assertThat(result.decision()).isEqualTo(SloErrorBudgetService.Decision.REVIEW_CHANGES);
        assertThat(result.budgetConsumedPercent()).isEqualTo(80d);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void freezesChangesWhenBudgetIsExhausted() {
        var result = service.evaluate(request(1_200, 200, 900, false, false, false));
        assertThat(result.decision()).isEqualTo(SloErrorBudgetService.Decision.FREEZE_CHANGES);
        assertThat(result.budgetRemainingPercent()).isZero();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void rollsBackActiveChangeDuringSevOne() {
        var result = service.evaluate(request(100, 20, 100, true, true, true));
        assertThat(result.decision()).isEqualTo(SloErrorBudgetService.Decision.ROLLBACK);
        assertThat(result.actions()).hasSize(2);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void rejectsImpossibleCounters() {
        assertThatThrownBy(() -> service.evaluate(new SloErrorBudgetService.BudgetRequest(
                "orders", 43_200, 100, 101, 99.9, 5, 100, 1, 60, 100, 1,
                false, false, false))).isInstanceOf(BusinessException.class);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private SloErrorBudgetService.BudgetRequest request(long failed, long shortFailed, long longFailed,
                                                        boolean sev1, boolean change, boolean rollback) {
        return new SloErrorBudgetService.BudgetRequest("orders", 43_200, 1_000_000, failed, 99.9,
                5, 100_000, shortFailed, 60, 500_000, longFailed, sev1, change, rollback);
    }
}
