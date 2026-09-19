/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.controller;

import cn.zhuatech.aiops.common.ApiResponse;
import cn.zhuatech.aiops.service.SloErrorBudgetService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/enterprise/aiops")
public class SloErrorBudgetController {
    private final SloErrorBudgetService service;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public SloErrorBudgetController(SloErrorBudgetService service) {
        this.service = service;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/slo-error-budget")
    public ApiResponse<SloErrorBudgetService.BudgetResult> evaluate(
            @Valid @RequestBody SloErrorBudgetService.BudgetRequest request) {
        return ApiResponse.ok("SLO 错误预算决策完成", service.evaluate(request));
    }
}
