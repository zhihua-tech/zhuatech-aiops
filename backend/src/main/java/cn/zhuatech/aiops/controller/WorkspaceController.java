/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.controller;

import cn.zhuatech.aiops.ai.AiProvider;
import cn.zhuatech.aiops.common.ApiResponse;
import cn.zhuatech.aiops.dto.AiopsDto.*;
import cn.zhuatech.aiops.service.AiopsService;
import cn.zhuatech.aiops.service.AlertRoutingService;
import cn.zhuatech.aiops.service.IncidentCorrelationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shopfloor")
@PreAuthorize("hasAnyRole('DOMAIN_USER','ADMIN')")
public class WorkspaceController {
    private final AiopsService service;
    private final AiProvider ai;
    private final IncidentCorrelationService correlation;
    private final AlertRoutingService alertRouting;

    public WorkspaceController(AiopsService service, AiProvider ai, IncidentCorrelationService correlation, AlertRoutingService alertRouting) {
        this.service = service;
        this.ai = ai;
        this.correlation = correlation;
        this.alertRouting = alertRouting;
    }

    @GetMapping("/dashboard")
    public ApiResponse<Dashboard> dashboard() { return ApiResponse.ok(service.shopfloorDashboard()); }

    @PostMapping("/work-orders/{id}/reports")
    public ApiResponse<ReportResult> report(@PathVariable Long id, @Valid @RequestBody ReportRequest request) {
        return ApiResponse.ok("反馈提交成功", service.report(id, request));
    }

    @PostMapping("/ai-preview")
    public ApiResponse<AiProvider.AiResult> preview(@RequestBody Map<String, String> body) {
        return ApiResponse.ok(ai.execute(body.getOrDefault("prompt", ""), Map.of("mode", "demo")));
    }

    @PostMapping("/incident-correlation")
    public ApiResponse<IncidentCorrelationService.CorrelationResult> correlate(@Valid @RequestBody IncidentCorrelationService.CorrelationRequest request) {
        return ApiResponse.ok("事件关联分析完成", correlation.correlate(request));
    }

    @PostMapping("/alert-routing")
    public ApiResponse<AlertRoutingService.RoutingResult> routeAlert(@Valid @RequestBody AlertRoutingService.RoutingRequest request) {
        return ApiResponse.ok("告警路由决策完成", alertRouting.route(request));
    }
}
