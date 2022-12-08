package org.caterpillar.rpa.browser.controller;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.caterpillar.rpa.browser.service.IpInfoService;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/browser/api")
public class BrowserController {

    @Resource
    private IpInfoService ipInfoService;

    @GetMapping("/get-visitor-ip")
    Result getVisitorIp(HttpServletRequest request){
        JSONObject visitorData = new JSONObject();
        // TODO 先返回静态数据，待实现
        // ua, language, ip
        String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_0_1) AppleWebKit/537.36 (KHTML, like Gecko) adspower_global/4.10.18 Chrome/87.0.4280.141 Electron/11.3.0 Safari/537.36 isGlobal isBeta adspower/4.10.18";
        visitorData.set("ua", ua);
        String language = "en-US";
        visitorData.set("language", language);
        // gmt-offset: ""
        // ip_data(country, region, city, timezone, latitude, longitude)
        String ip = "222.67.103.200";
        JSONObject ipData = ipInfoService.getIpData(ip);
        visitorData.set("ip", ip);
        visitorData.set("ip_data", ipData);

        return Result.ok(visitorData);
    }
}
