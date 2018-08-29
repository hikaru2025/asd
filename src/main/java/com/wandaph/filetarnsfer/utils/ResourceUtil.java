package com.wandaph.filetarnsfer.utils;

import com.hsjry.lang.log.Log;
import com.hsjry.lang.log.TenantLog;

import java.util.Properties;

public class ResourceUtil {

    private static Log        log = TenantLog.get(ResourceUtil.class);

    private static Properties config;

    static {
        try {
            config = ApplicationContextUtil.getBean("configproperties");
        } catch (Exception e) {
            log.error("获取configproperties bean异常", e);
        }

    }

    public static String getValue(String key) {
        try {
            return config.getProperty(key);
        } catch (Exception e) {
            log.error("获取[{}]失败", key, e);
        }
        return null;
    }

    public static String getTenantId(){
        return getValue("tenantId");
    }
    
    public static String getChannelNo(){
        return getValue("channelNo");
    }

    public static String getJdchannelno(){
        return getValue("jd.channelno");
    }
}
