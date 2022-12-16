package org.caterpillar.rpa.browser.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.caterpillar.rpa.browser.service.BrowserInfoService;
import org.caterpillar.rpa.browser.service.IpInfoService;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 浏览器以及网络相关API
 */
@Slf4j
@Api(tags="浏览器browser")
@RestController
@RequestMapping("/browser/api")
public class W3BrowserController {

    @Resource
    private IpInfoService ipInfoService;

    @Resource
    private BrowserInfoService browserInfoService;

    /**
     * 获取IP信息
     * 临时代理
     *  https://rpa2b.w3bb.cc/sys/config/ip/get-visitor-ip
     * @param request
     * @return
     */
    @GetMapping("get-visitor-ip")
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

    @GetMapping("ua-version-list")
    Result getUaVersionList(HttpServletRequest request){
        // 输入参数：
        // system_version: Mac OS X
        // browser: chrome
        // system:
        // 输出参数：
        // "version":["106","105","104","103","102","101","100","99","98","97","96","95","94","93","92","91","90","89","88"]
        return null;
    }

    @ApiOperation(value="随机userAgent", notes="随机userAgent")
    @GetMapping("rand-user-agent")
    Result getRandUserAgent(HttpServletRequest request){
        String system = request.getParameter("system");
        String browser = request.getParameter("browser");
        String ua = browserInfoService.randGetUserAgent(system, browser);
        JSONObject data = new JSONObject();
        data.set("ua", ua);
        return  Result.ok(data);
    }
    @ApiOperation(value="更新浏览器platform", notes="更新浏览器platform")
    @GetMapping("update-browser-platform")
    Result updateBrowserByPlatform(HttpServletRequest request){
        String platform = request.getParameter("platform");
        if(ObjectUtil.isEmpty(platform)){
            return Result.error("platform is must fill");
        }
        String osType = BrowserInfoService.getOsTypeByPlatform(platform);
        String id = request.getParameter("id");
        if(ObjectUtil.isNotEmpty(id)){
            browserInfoService.updateObjectValue("w3_browser", id, "os_type", osType);
        }
        return Result.ok(osType);
    }
}
