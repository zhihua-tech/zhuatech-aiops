/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aiops.workbench;

import static cn.zhuatech.aiops.workbench.WorkbenchInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);
    keys(r, "windowSeconds", "alerts");
    int window = integer(r, "windowSeconds", 1, 86400);
    var alerts = new ArrayList<>(rows(r, "alerts", 1, 500));
    Set<String> ids = new HashSet<>();
    Map<String, Integer> levels = Map.of("INFO", 0, "WARNING", 1, "CRITICAL", 2);
    for (var a : alerts) {
      keys(a, "id", "environment", "service", "fingerprint", "at", "severity");
      unique(ids, id(a, "id"));
      id(a, "environment");
      id(a, "service");
      id(a, "fingerprint");
      instant(text(a, "at", 40));
      if (!levels.containsKey(text(a, "severity", 10))) throw new IllegalArgumentException("严重度错误");
    }
    alerts.sort(
        Comparator.<Map<String, Object>, java.time.Instant>comparing(
                a -> instant(a.get("at").toString()))
            .thenComparing(a -> a.get("id").toString()));
    Map<List<String>, Map<String, Object>> active = new HashMap<>();
    List<Map<String, Object>> out = new ArrayList<>();
    for (var a : alerts) {
      var key =
          List.of(
              a.get("environment").toString(),
              a.get("service").toString(),
              a.get("fingerprint").toString());
      var at = instant(a.get("at").toString());
      var g = active.get(key);
      if (g == null
          || java.time.Duration.between(instant(g.get("firstAt").toString()), at).getSeconds()
              >= window) {
        g = new LinkedHashMap<>();
        g.put("environment", key.get(0));
        g.put("service", key.get(1));
        g.put("fingerprint", key.get(2));
        g.put("firstAt", at.toString());
        g.put("severity", a.get("severity"));
        g.put("ids", new ArrayList<String>());
        out.add(g);
        active.put(key, g);
      }
      @SuppressWarnings("unchecked")
      var members = (List<String>) g.get("ids");
      members.add(a.get("id").toString());
      g.put("count", members.size());
      g.put("lastAt", at.toString());
      if (levels.get(a.get("severity").toString()) > levels.get(g.get("severity").toString()))
        g.put("severity", a.get("severity"));
    }
    return Map.of(
        "inputCount",
        alerts.size(),
        "groupCount",
        out.size(),
        "suppressedDuplicates",
        alerts.size() - out.size(),
        "groups",
        out);
  }
}
