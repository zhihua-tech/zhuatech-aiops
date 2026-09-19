/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.controller;

import cn.zhuatech.aiops.common.ApiResponse;
import cn.zhuatech.aiops.service.AutoRemediationExecutionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/enterprise/aiops")
public class AutoRemediationExecutionController {
    private final AutoRemediationExecutionService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public AutoRemediationExecutionController(AutoRemediationExecutionService service) { this.service = service; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/auto-remediation-execution")
    public ApiResponse<AutoRemediationExecutionService.Assessment> assess(
            @Valid @RequestBody AutoRemediationExecutionService.Request request) {
        return ApiResponse.ok(service.assess(request));
    }
}
