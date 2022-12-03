package org.caterpillar.rpa.account.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.MD5;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.caterpillar.rpa.account.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/account/api")
public class AccountController {

    @Resource
    private AccountService accountService;
    @RequestMapping(value = "getEncryptKey", method = {RequestMethod.GET, RequestMethod.POST})
    public String getAccountEncryptKey(HttpServletRequest request, HttpServletResponse response){
        Map<String, String> mapParam = new HashMap<>();
        Enumeration<String> enum1 = request.getParameterNames();
        while(enum1.hasMoreElements()){
            String key = enum1.nextElement();
            mapParam.put(key, request.getParameter(key));
        }
        String encryptKey = accountService.getAccountEncryptKey(mapParam);
        return encryptKey;
    }


}
