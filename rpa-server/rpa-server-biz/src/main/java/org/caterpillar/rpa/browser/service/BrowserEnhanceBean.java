package org.caterpillar.rpa.browser.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.jeecg.modules.online.cgform.enhance.CgformEnhanceJavaInter;
import org.jeecg.modules.online.config.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("browserEnhanceBean")
public class BrowserEnhanceBean implements CgformEnhanceJavaInter {

    @Resource
    private BrowserInfoService browserInfoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void execute(String tableName, JSONObject json) throws BusinessException {
        // 1. 检查名字
        String name = json.getString("name");
        if(ObjectUtil.isEmpty(name)){
            name = RandomUtil.randomString(5);
            updateObjectValue(tableName, json.getString("id"), "name", name);
        }
        // 2. 检查UA
        String system = json.getString("os_type");
        String browser = json.getString("browser");
        String ua = json.getString("ua");
        if(ObjectUtil.isEmpty(ua)){
            ua = randomUserAgent(system, browser);
            if(ObjectUtil.isNotEmpty(ua)){
                updateObjectValue(tableName, json.getString("id"), "ua", ua);
                system = BrowserInfoService.getOsTypeByUA(ua);
                updateObjectValue(tableName, json.getString("id"), "os_type", system);
            }
        }
    }

    boolean updateObjectValue(String tableName, String id, String field, String value){
        String sql = "update "+tableName+" set "+field+"='"+value+"' where id='"+id+"'";
        try{
            jdbcTemplate.execute(sql);
        }catch (Throwable t){
            log.warn("update fail in browserEnhanceBean, sql="+sql, t.getMessage());
            return false;
        }
        return true;
    }

    private String randomUserAgent(String system, String browser){
        String ua = null;
        ua = browserInfoService.randGetUserAgent(system, browser);
        return ua;
    }
}
