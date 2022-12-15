package org.caterpillar.rpa.browser.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.util.StringUtil;
import org.jeecg.modules.online.cgform.enhance.CgformEnhanceJavaInter;
import org.jeecg.modules.online.config.exception.BusinessException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("browserEnhanceBean")
public class BrowserEnhanceBean implements CgformEnhanceJavaInter {

    @Resource
    private BrowserInfoService browserInfoService;

    @Override
    public void execute(String tableName, JSONObject json) throws BusinessException {
        // 1. 检查名字
        String name = json.getString("name");
        if(ObjectUtil.isEmpty(name)){
            name = RandomUtil.randomString(5);
            json.put("name", name);
        }
        // 2. 检查UA
        String ua = json.getString("ua");
        if(ObjectUtil.isEmpty(ua)){
            ua = randomUserAgent(json);
            if(ObjectUtil.isNotEmpty(ua)){
                json.put("ua", ua);
            }
        }
    }

    private String randomUserAgent(JSONObject json){
        String ua = null;
        String browser = json.getString("browser");
        ua = browserInfoService.randGetUserAgent(null, browser);
        return ua;
    }
}
