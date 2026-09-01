/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AutoRemediationExecutionServiceTest {
    private final AutoRemediationExecutionService service = new AutoRemediationExecutionService();
    @Test void executesLowRiskRemediation() {
        var result = service.assess(new AutoRemediationExecutionService.Request("R1", true, true, false,
                true, true, true, true, true, true, false, 2, 5, false));
        assertThat(result.decision()).isEqualTo(AutoRemediationExecutionService.Decision.EXECUTE);
    }
    @Test void supervisesApprovedCriticalService() {
        var result = service.assess(new AutoRemediationExecutionService.Request("R2", true, true, true,
                true, true, true, true, true, true, false, 2, 5, true));
        assertThat(result.decision()).isEqualTo(AutoRemediationExecutionService.Decision.SUPERVISED);
    }
    @Test void blocksUnsafeExecution() {
        var result = service.assess(new AutoRemediationExecutionService.Request("R3", false, false, true,
                false, false, false, false, false, false, true, 10, 5, false));
        assertThat(result.blockers()).hasSize(11);
    }
}
