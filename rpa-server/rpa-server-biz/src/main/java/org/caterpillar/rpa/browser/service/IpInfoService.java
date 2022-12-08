package org.caterpillar.rpa.browser.service;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * IP地址信息库
 *
 * 整合本地IP库和第三方查询API
 */
@Service
public class IpInfoService {

    public JSONObject getIpData(String ip){
        // ip_data(country, region, city, timezone, latitude, longitude)
        JSONObject ipData = new JSONObject();
        ipData.set("version","2.0");
        ipData.set("country","cn");
        ipData.set("region","sh");
        ipData.set("city","shanghai");
        ipData.set("timezone","Asia/Shanghai");
        ipData.set("latitude","31.230496");
        ipData.set("longitude","121.474769");
        return ipData;
    }
}
