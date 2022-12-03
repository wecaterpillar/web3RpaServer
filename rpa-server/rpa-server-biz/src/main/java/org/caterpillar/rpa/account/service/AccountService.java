package org.caterpillar.rpa.account.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.MD5;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 账号服务
 *
 * AES加密解密密码生成
 *
 */
@Service
public class AccountService {

    /**
     * saltRuleDefault
     * will load from config
     */
    private  static String saltRuleDefault = "r1";

    /**
     *  加密机制R1
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
    public String getAccountEncryptKey(Map<String, String> mapParam){
        String encryptKey = null;
        // scope: 1-web3 account 2-web2 account 3-project account
        // field: account,salt,type,group,project,tag

        // 1. prepare
        // 1.1 project code
        String project = MapUtil.getStr(mapParam, "project");
        if(ObjectUtils.isEmpty(project)
                && mapParam.containsKey("project_code")){
            project = MapUtil.getStr(mapParam, "project_code");
        }
        if(ObjectUtils.isEmpty(project)
                && mapParam.containsKey("project_id")){
            // todo project id -> project code
            // 已支持project_code， 不再支持project_id
            String projectId = MapUtil.getStr(mapParam, "project_id");
        }

        // 1.2 account
        String account = MapUtil.getStr(mapParam, "account");
        if(ObjectUtils.isEmpty(account)){
            account = MapUtil.getStr(mapParam, "address");
        }
        if(ObjectUtils.isEmpty(account)){
            account = MapUtil.getStr(mapParam, "username");
        }

        // 1.3 salt
        String salt = MapUtil.getStr(mapParam, "salt");
        if(ObjectUtils.isEmpty(salt)){
            salt = MapUtil.getStr(mapParam, "saltMd5");
        }
        if(ObjectUtils.isEmpty(salt)){
            String saltRule = MapUtil.getStr(mapParam, "saltRule");
            salt = getSaltMd5(account, saltRule);
        }

        // 2 calculate encrypt key
        // 2.1 encrypt input key
        String inputKey = getEncryptInputKey(mapParam, project);
        // 2.2 calculate encrypt key
        encryptKey = getEncryptKey(account, salt, inputKey);

        return encryptKey;
    }

    /**
     * 获取AES密码（可用于加密解密）
     * @param account 账号(address或username)
     * @param saltMd5
     * @param inputKey 加密用密钥
     * @return
     */
    private static String getEncryptKey(String account, String saltMd5, String inputKey){
        String encryptKey = null;
        // enKey=md5(concat(address,saltMd5,key))
        encryptKey = md5(account+saltMd5+inputKey);
        return encryptKey;
    }

    private static String getEncryptInputKey(Map<String, String> mapParam, String project){
        // key priority?  encryptKey -> project -> group2 -> type -> tag
        String inputKey = null;
        if(ObjectUtils.isNotEmpty(project)
                && mapParam.containsKey("username")){
            // project account: project
            inputKey = project;
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
        return  inputKey;
    }

    /**
     * 获取账号account的salt(自定义规则，返回md5值)
     * @param account 账号(address或username)
     * @param saltRule
     * @return
     */
    private static String getSaltMd5(String account, String saltRule){
        String saltMd5 = null;
        if(ObjectUtil.isEmpty(saltRule)){
            saltRule = saltRuleDefault;
        }
        if("r2".equalsIgnoreCase(saltRule)){
            if(isAddress(account)){
                // address salt
                // mysql: saltMd5=md5(SUBSTR(address,10,1))
                saltMd5 = md5(account.substring(9,10));
            }else{
                // username salt   用户名过滤@及以后内容
                // mysql: saltMd5=md5(SUBSTR(username,-3,1))
                saltMd5 = md5(account.substring(account.length()-3,account.length()-2));
            }
        }else{
            // default is r1
            if(isAddress(account)){
                // address salt
                // mysql: saltMd5=md5(SUBSTR(address,10,1))
                saltMd5 = md5(account.substring(9,10));
            }else{
                // username salt
                // mysql: saltMd5=md5(SUBSTR(username,-3,1))
                saltMd5 = md5(account.substring(account.length()-3,account.length()-2));
            }
        }
        return saltMd5;
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
