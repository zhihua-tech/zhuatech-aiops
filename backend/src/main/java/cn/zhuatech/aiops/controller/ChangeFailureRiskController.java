/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.controller;import cn.zhuatech.aiops.common.ApiResponse;import cn.zhuatech.aiops.service.ChangeFailureRiskService;import jakarta.validation.Valid;import org.springframework.web.bind.annotation.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api/aiops/insights/change-failure-risk") public class ChangeFailureRiskController{private final ChangeFailureRiskService service;/**
                                                                                                                                                                    * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                    */
public ChangeFailureRiskController(ChangeFailureRiskService service){this.service=service;}/**
                                                                                                                                                                                                                                                               * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                               */
@PostMapping ApiResponse<ChangeFailureRiskService.Result> evaluate(@Valid @RequestBody ChangeFailureRiskService.Request r){return ApiResponse.ok(service.evaluate(r));}}
