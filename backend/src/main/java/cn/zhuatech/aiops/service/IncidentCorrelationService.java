/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 将告警信号归并为可解释的事件等级和处置建议。 */
@Service
public class IncidentCorrelationService {
    public CorrelationResult correlate(CorrelationRequest request) {
        int score = Math.min(100,
            request.signals().size() * 8
                + request.affectedServices() * 12
                + (request.errorRate() >= 0.1 ? 25 : request.errorRate() >= 0.03 ? 12 : 0)
                + (request.latencyRatio() >= 3 ? 20 : request.latencyRatio() >= 1.5 ? 10 : 0)
                + (request.recentChange() ? 15 : 0));
        String severity = score >= 75 ? "P1" : score >= 50 ? "P2" : score >= 25 ? "P3" : "P4";
        String suspectedCause = request.recentChange() ? "近期变更与异常时间窗口高度重合"
            : request.errorRate() >= 0.1 ? "错误率突增，需要检查依赖与容量" : "需要继续收集拓扑和日志证据";
        List<String> actions = new ArrayList<>();
        if (request.recentChange()) actions.add("核对最近发布并准备回滚");
        if (request.latencyRatio() >= 1.5) actions.add("检查慢调用链与资源饱和度");
        if (request.affectedServices() > 1) actions.add("按服务拓扑定位共同上游依赖");
        if (actions.isEmpty()) actions.add("持续观察指标并补充日志样本");
        return new CorrelationResult(severity, score, suspectedCause, List.copyOf(actions), request.signals().size());
    }

    public record CorrelationRequest(
        @NotEmpty(message = "请至少提交一个告警信号") List<String> signals,
        @Positive(message = "受影响服务数必须大于 0") int affectedServices,
        @DecimalMin(value = "0.0", message = "错误率不能为负数") double errorRate,
        @DecimalMin(value = "0.0", message = "时延倍率不能为负数") double latencyRatio,
        boolean recentChange
    ) {}

    public record CorrelationResult(String severity, int correlationScore, String suspectedCause, List<String> actions, int signalCount) {}
}
