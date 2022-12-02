package org.caterpillar.rpa.account.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.MD5;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/account/api")
public class AccountController {

    @RequestMapping(value = "getEncryptKey", method = {RequestMethod.GET, RequestMethod.POST})
    public String getAccountEncryptKey(HttpServletRequest request, HttpServletResponse response){
        Map<String, String> mapParam = new HashMap<>();
        Enumeration<String> enum1 = request.getParameterNames();
        while(enum1.hasMoreElements()){
            String key = enum1.nextElement();
            mapParam.put(key, request.getParameter(key));
        }
        String encryptKey = getAccountEncryptKey(mapParam);
        return encryptKey;
    }

    /**
     *  加密机制
     *     以下为mysql版本的机制
     * -- 1. 规则说明
     * -- 账号数据库保存字段： 账号address/加盐saltMd5/加密后数据enResult
     * -- 1.1 加盐，不同项目规则不一样，由服务器加盐处理
     *     update cake_address
     *     set saltMd5=md5(SUBSTR(address,10,1));
     * -- 1.2 加密密码规则 项目密码 cake存放在项目配置中
     * -- enKey=md5(concat(address,saltMd5,'cake'));
     *
     * -- 2. 对称加密
     *     update cake_address
     *     set enResult = hex(AES_ENCRYPT(privateKey, md5(concat(address, saltMd5, 'cake'))));
     *
     * -- 3. 对称还原
     *     select address, AES_DECRYPT(unhex(enResult), md5(concat(address,saltMd5,'cake'))) as privateKey2, privateKey
     *     from cake_address;
     * @param mapParam
     * @return
     */
    public static String getAccountEncryptKey(Map<String, String> mapParam){
        String encryptKey = null;
        // scope: 1-web3 account 2-web2 account 3-project account
        // field: account,salt,type,group,project,tag
        // project code
        String project = MapUtil.getStr(mapParam, "project");
        if(ObjectUtils.isEmpty(project)
                && mapParam.containsKey("project_code")){
            project = MapUtil.getStr(mapParam, "project_code");
        }
        if(ObjectUtils.isEmpty(project)
                && mapParam.containsKey("project_id")){
            // todo project id -> project code
            String projectId = MapUtil.getStr(mapParam, "project_id");
        }
        String account = MapUtil.getStr(mapParam, "account");
        if(ObjectUtils.isEmpty(account)){
            account = MapUtil.getStr(mapParam, "address");
        }
        if(ObjectUtils.isEmpty(account)){
            account = MapUtil.getStr(mapParam, "username");
        }

        String salt = MapUtil.getStr(mapParam, "salt");
        if(ObjectUtils.isEmpty(salt)){
            salt = MapUtil.getStr(mapParam, "saltMd5");
        }
        if(ObjectUtils.isEmpty(salt)){
            if(isAddress(account)){
                // saltMd5=md5(SUBSTR(address,10,1))
                salt = md5(account.substring(9,10));
            }else{
                // saltMd5=md5(SUBSTR(username,-3,1))
                salt = md5(account.substring(account.length()-3,account.length()-2));
            }
        }

        // key priority?  encryptKey -> project -> group2 -> tag
        String inputKey = null;
        if(ObjectUtils.isNotEmpty(project)
                && mapParam.containsKey("username")){
            // project account: project
            inputKey = MapUtil.getStr(mapParam, "project");
        }else{
            // group2 -> type -> tag
            inputKey = MapUtil.getStr(mapParam, "group2");
            if(ObjectUtils.isEmpty(inputKey)){
                inputKey = MapUtil.getStr(mapParam, "type");
            }
            if(ObjectUtils.isEmpty(inputKey)){
                inputKey = MapUtil.getStr(mapParam, "tag");
            }
        }
        // enKey=md5(concat(address,saltMd5,key))
        encryptKey = md5(account+salt+inputKey);
        return encryptKey;
    }

    private static boolean isAddress(String account){
        // 0x8123888e87284ca732c27376332b03715193b20f
        if(account==null || account.length()<30){
            return  false;
        }
        if(!account.startsWith("0x")){
            return  false;
        }
        return true;
    }

    private static MD5 _md5;
    private static String md5(String str){
        if(_md5==null){
            _md5 = MD5.create();
        }
        return _md5.digestHex(str);
    }
}
