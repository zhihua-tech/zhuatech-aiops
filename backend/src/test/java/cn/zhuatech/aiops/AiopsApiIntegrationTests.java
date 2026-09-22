/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops;
import org.junit.jupiter.api.*; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.http.MediaType; import org.springframework.test.web.servlet.MockMvc; import java.util.regex.*; import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@SpringBootTest @AutoConfigureMockMvc class AiopsApiIntegrationTests {
    @Autowired MockMvc mvc; private String operatorToken; private String plannerToken;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @BeforeEach void login()throws Exception{operatorToken=token("operator","Demo@2026");plannerToken=token("planner","Demo@2026");}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private String token(String u,String p)throws Exception{String json=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\""+u+"\",\"password\":\""+p+"\"}")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();Matcher matcher=Pattern.compile("\\\"token\\\":\\\"([^\\\"]+)\\\"").matcher(json);if(!matcher.find())throw new AssertionError("登录响应中缺少 token");return matcher.group(1);}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void operatorCanReadShopfloorDashboard()throws Exception{mvc.perform(get("/api/shopfloor/dashboard").header("Authorization","Bearer "+operatorToken)).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data.metrics[0].label").value("关联信号数量"));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void plannerCanReadWorkRecords()throws Exception{mvc.perform(get("/api/admin/work-orders").header("Authorization","Bearer "+plannerToken)).andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(3));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void operatorCanSubmitProductionReport()throws Exception{mvc.perform(post("/api/shopfloor/work-orders/1/reports").header("Authorization","Bearer "+operatorToken).contentType(MediaType.APPLICATION_JSON).content("{\"operationName\":\"根因确认\",\"goodQty\":2,\"defectQty\":1,\"remark\":\"证据完整\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("反馈提交成功")).andExpect(jsonPath("$.data.completedQty").value(399));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void operatorCanCorrelateIncidentSignals()throws Exception{mvc.perform(post("/api/shopfloor/incident-correlation").header("Authorization","Bearer "+operatorToken).contentType(MediaType.APPLICATION_JSON).content("{\"signals\":[\"HTTP_5XX\",\"DB_TIMEOUT\",\"LATENCY\"],\"affectedServices\":3,\"errorRate\":0.14,\"latencyRatio\":3.2,\"recentChange\":true}")).andExpect(status().isOk()).andExpect(jsonPath("$.data.severity").value("P1")).andExpect(jsonPath("$.data.correlationScore").value(100)).andExpect(jsonPath("$.data.signalCount").value(3)).andExpect(jsonPath("$.data.actions.length()").value(3));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void operatorCanRouteAndGroupAlerts()throws Exception{mvc.perform(post("/api/shopfloor/alert-routing").header("Authorization","Bearer "+operatorToken).contentType(MediaType.APPLICATION_JSON).content("{\"serviceName\":\"order-api\",\"alertName\":\"High Latency\",\"occurrencesInTenMinutes\":8,\"affectedInstances\":2,\"maintenanceWindow\":false,\"customerImpact\":false,\"knownDuplicate\":true}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.data.route").value("GROUP"))
        .andExpect(jsonPath("$.data.groupKey").value("order-api:high-latency"))
        .andExpect(jsonPath("$.data.suppressionMinutes").value(10))
        .andExpect(jsonPath("$.data.groupedOccurrences").value(8));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void operatorCanEvaluateSloErrorBudget()throws Exception{mvc.perform(post("/api/enterprise/aiops/slo-error-budget").header("Authorization","Bearer "+operatorToken).contentType(MediaType.APPLICATION_JSON).content("{\"serviceName\":\"orders\",\"windowMinutes\":43200,\"totalRequests\":1000000,\"failedRequests\":800,\"targetAvailabilityPercent\":99.9,\"shortWindowMinutes\":5,\"shortWindowRequests\":100000,\"shortWindowFailures\":40,\"longWindowMinutes\":60,\"longWindowRequests\":500000,\"longWindowFailures\":200,\"activeSev1\":false,\"changeInProgress\":false,\"rollbackReady\":true}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").value("REVIEW_CHANGES"))
        .andExpect(jsonPath("$.data.budgetConsumedPercent").value(80.0));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void anonymousRequestIsDenied()throws Exception{mvc.perform(get("/api/admin/dashboard")).andExpect(status().isUnauthorized());}
}
