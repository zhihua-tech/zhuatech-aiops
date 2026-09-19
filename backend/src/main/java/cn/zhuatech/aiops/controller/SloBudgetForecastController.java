/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.controller;

import cn.zhuatech.aiops.common.ApiResponse;
import cn.zhuatech.aiops.service.SloBudgetForecastService;
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
public class SloBudgetForecastController {
    private final SloBudgetForecastService service;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public SloBudgetForecastController(SloBudgetForecastService service) {
        this.service = service;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/slo-budget-forecast")
    public ApiResponse<SloBudgetForecastService.ForecastResult> forecast(
            @Valid @RequestBody SloBudgetForecastService.ForecastRequest request) {
        return ApiResponse.ok("SLO 错误预算预测完成", service.forecast(request));
    }
}
