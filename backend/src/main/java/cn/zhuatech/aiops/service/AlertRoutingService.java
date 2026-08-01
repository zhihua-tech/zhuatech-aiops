/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.aiops.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** 对重复告警进行分组、维护窗口抑制和客户影响升级判断。 */
@Service
public class AlertRoutingService {
    public RoutingResult route(RoutingRequest request) {
        List<String> reasons = new ArrayList<>();
        String route;
        int suppressionMinutes;
        if (request.customerImpact() || request.affectedInstances() >= 5) {
            route = "PAGE";
            suppressionMinutes = 0;
            reasons.add(request.customerImpact() ? "已确认客户影响" : "受影响实例达到升级阈值");
        } else if (request.maintenanceWindow() && !request.customerImpact()) {
            route = "SUPPRESS";
            suppressionMinutes = 30;
            reasons.add("告警发生在已登记维护窗口内");
        } else if (request.knownDuplicate() || request.occurrencesInTenMinutes() >= 3) {
            route = "GROUP";
            suppressionMinutes = 10;
            reasons.add("相同指纹告警在短时间内重复出现");
        } else {
            route = "QUEUE";
            suppressionMinutes = 0;
            reasons.add("进入值班队列等待人工确认");
        }
        String groupKey = (request.serviceName() + ":" + request.alertName()).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9:_-]", "-");
        return new RoutingResult(route, groupKey, suppressionMinutes, request.occurrencesInTenMinutes(), List.copyOf(reasons),
            "PAGE".equals(route) ? "立即通知值班人员并创建事件" : "SUPPRESS".equals(route) ? "维护结束后重新评估" : "合并证据后继续观察");
    }

    public record RoutingRequest(
        @NotBlank(message = "请输入服务名称") String serviceName,
        @NotBlank(message = "请输入告警名称") String alertName,
        @Positive int occurrencesInTenMinutes,
        @Positive int affectedInstances,
        boolean maintenanceWindow,
        boolean customerImpact,
        boolean knownDuplicate
    ) {}

    public record RoutingResult(String route, String groupKey, int suppressionMinutes, int groupedOccurrences, List<String> reasons, String nextAction) {}
}
