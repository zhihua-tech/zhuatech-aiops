/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.workbench;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cn.zhuatech.aiops.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@SpringBootTest
@AutoConfigureMockMvc
class AiWorkbenchSecurityIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired JwtService jwt;

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void securityFilterRejectsAnonymous() throws Exception {
    mvc.perform(
            post("/api/workbench/alert-window-dedup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"windowSeconds\":60,\"alerts\":[{\"id\":\"A1\",\"environment\":\"prod\",\"service\":\"orders\",\"fingerprint\":\"timeout\",\"at\":\"2026-10-23T10:00:00Z\",\"severity\":\"WARNING\"},{\"id\":\"A2\",\"environment\":\"prod\",\"service\":\"orders\",\"fingerprint\":\"timeout\",\"at\":\"2026-10-23T10:00:30Z\",\"severity\":\"CRITICAL\"},{\"id\":\"A3\",\"environment\":\"prod\",\"service\":\"orders\",\"fingerprint\":\"timeout\",\"at\":\"2026-10-23T10:01:10Z\",\"severity\":\"WARNING\"}]}"))
        .andExpect(status().isUnauthorized());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void securityFilterRejectsInvalidBearer() throws Exception {
    mvc.perform(
            post("/api/workbench/alert-window-dedup")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"windowSeconds\":60,\"alerts\":[{\"id\":\"A1\",\"environment\":\"prod\",\"service\":\"orders\",\"fingerprint\":\"timeout\",\"at\":\"2026-10-23T10:00:00Z\",\"severity\":\"WARNING\"},{\"id\":\"A2\",\"environment\":\"prod\",\"service\":\"orders\",\"fingerprint\":\"timeout\",\"at\":\"2026-10-23T10:00:30Z\",\"severity\":\"CRITICAL\"},{\"id\":\"A3\",\"environment\":\"prod\",\"service\":\"orders\",\"fingerprint\":\"timeout\",\"at\":\"2026-10-23T10:01:10Z\",\"severity\":\"WARNING\"}]}"))
        .andExpect(status().isUnauthorized());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void authenticatedCallerCanCompute() throws Exception {
    mvc.perform(
            post("/api/workbench/alert-window-dedup")
                .header("Authorization", "Bearer " + jwt.generate("operator"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"windowSeconds\":60,\"alerts\":[{\"id\":\"A1\",\"environment\":\"prod\",\"service\":\"orders\",\"fingerprint\":\"timeout\",\"at\":\"2026-10-23T10:00:00Z\",\"severity\":\"WARNING\"},{\"id\":\"A2\",\"environment\":\"prod\",\"service\":\"orders\",\"fingerprint\":\"timeout\",\"at\":\"2026-10-23T10:00:30Z\",\"severity\":\"CRITICAL\"},{\"id\":\"A3\",\"environment\":\"prod\",\"service\":\"orders\",\"fingerprint\":\"timeout\",\"at\":\"2026-10-23T10:01:10Z\",\"severity\":\"WARNING\"}]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void authenticatedCallerGetsValidationError() throws Exception {
    mvc.perform(
            post("/api/workbench/alert-window-dedup")
                .header("Authorization", "Bearer " + jwt.generate("operator"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }
}
