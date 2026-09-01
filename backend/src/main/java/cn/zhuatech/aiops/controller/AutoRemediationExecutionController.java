/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.controller;

import cn.zhuatech.aiops.common.ApiResponse;
import cn.zhuatech.aiops.service.AutoRemediationExecutionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enterprise/aiops")
public class AutoRemediationExecutionController {
    private final AutoRemediationExecutionService service;
    public AutoRemediationExecutionController(AutoRemediationExecutionService service) { this.service = service; }
    @PostMapping("/auto-remediation-execution")
    public ApiResponse<AutoRemediationExecutionService.Assessment> assess(
            @Valid @RequestBody AutoRemediationExecutionService.Request request) {
        return ApiResponse.ok(service.assess(request));
    }
}
