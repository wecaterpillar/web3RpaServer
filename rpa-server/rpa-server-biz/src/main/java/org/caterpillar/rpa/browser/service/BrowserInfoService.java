package org.caterpillar.rpa.browser.service;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BrowserInfoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;



    Map<String, List<JSONObject>> queryDictValues(String keys){


        return null;
    }

    JSONObject randGetUserAgent2(String systems, String browsers){
        String userAgent = null;
        return null;
    }


    public String randGetUserAgent(String system, String browser){
        WeightRandom<JSONObject> wr = getUaConfigWeightRandom(system,browser);
        JSONObject uaConfig = wr.next();
        String ua = uaConfig.getStr("ua_template");

        // 系统小版本
        String strOsVers = uaConfig.getStr("mini_version_os");
        if(ObjectUtil.isNotEmpty(strOsVers)){
            String[] osVers = strOsVers.replaceAll("\\r\\n","\\n").replaceAll("\\n\\r","\\n").replaceAll("\\r","\\n").split("\\n");
            if(osVers.length>1){
                int select = RandomUtil.randomInt(osVers.length);
                if(ua.indexOf(osVers[select])==-1){
                    for(int i=0; i< osVers.length; i++){
                        if(ua.indexOf(osVers[i])>0){
                            ua.replaceAll(osVers[i], osVers[select]);
                            break;
                        }
                    }
                }
            }
        }
        // 浏览器小版本
        String strBsVers = uaConfig.getStr("mini_version_browser");
        if(ObjectUtil.isNotEmpty(strBsVers)){
            String[] bsVers = strBsVers.replaceAll("\\r\\n","\\n").replaceAll("\\n\\r","\\n").replaceAll("\\r","\\n").split("\\n");
            if(bsVers.length>1){
                int select = RandomUtil.randomInt(bsVers.length);
                if(ua.indexOf(bsVers[select])==-1){
                    for(int i=0; i< bsVers.length; i++){
                        if(ua.indexOf(bsVers[i])>0){
                            ua.replaceAll(bsVers[i], bsVers[select]);
                            break;
                        }
                    }
                }
            }
        }
        return ua;
    }


    public WeightRandom getUaConfigWeightRandom(String system,String browser){
        List<JSONObject> uaConfigList = getUaConfigs(system, browser);
        List<WeightRandom.WeightObj<JSONObject>> list  = new ArrayList<>();
        for(JSONObject uaConfig:  uaConfigList){
            list.add(new WeightRandom.WeightObj<>(uaConfig, uaConfig.getDouble("weight")));
        }
        WeightRandom wr = RandomUtil.weightRandom(list);
        return  wr;
    }

    @Cacheable(cacheNames = "uaConfig#1h", key = "'default'")
    public List<JSONObject> getUaConfigs(String system,String browser){
        List<JSONObject> result = new ArrayList<>();
        String sql = "select * from w3_ua_config where weight >0 ";
        // 查询系统类型
        if(ObjectUtil.isNotEmpty(system) || !"all".equalsIgnoreCase(system)){
            sql += " and ";
            String[] sysArray = system.split(",");
            if(sysArray.length==1) {
                sql += " ua_template like '%"+sysArray[0]+"%' ";
            }else if(sysArray.length>1){
                for(int i=0; i<sysArray.length; i++){
                    if(i==0){
                        sql += "( ua_template like '%"+sysArray[0]+"%' ";
                    }else{
                        sql += "or ua_template like '%"+sysArray[i]+"%' ";
                    }
                    if(i==sysArray.length-1){
                        sql += ")";
                    }
                }
            }
        }
        // 查询浏览器类型
        if(ObjectUtil.isNotEmpty(browser)){
            // 暂不实现
        }
        List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql);
        for(Map item: list){
            JSONObject json = new JSONObject();
            json.putAll(item);
            result.add(json);
        }
        return result;
    }

}
