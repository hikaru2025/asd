package com.wandaph.filetarnsfer.utils;

import com.hsjry.lang.common.base.HsjryPageRequest;
import com.hsjry.lang.common.base.HsjryRequest;
import com.hsjry.lang.common.base.HsjryUserRequest;
import com.hsjry.lang.common.base.enums.EnumModuleCode;
import com.hsjry.lang.common.util.UUIDUtil;

import java.util.Date;

/**
 * @author lipeng
 * @Title: BaseReqBuilder
 * @ProjectName wandaph-filetransfer
 * @Description: TODO
 * @date 2018/8/2716:49
 */
public class BaseReqBuilder {
    private static final String CHANNELNO = ResourceUtil.getValue("filetransfer.channel");

    public static HsjryRequest buildBaseRequest() {
        return buildBaseRequest(UUIDUtil.getUUID());
    }

    public static HsjryRequest buildBaseRequest(String serialNum) {
        HsjryRequest base = new HsjryRequest();
        // TODO
        base.setIdemSerialNo(serialNum);
        base.setSerialNo(serialNum);
        base.setAsk(EnumModuleCode.CONSOLE);
        base.setChannelNo(CHANNELNO);
        base.setTenantId(ResourceUtil.getValue("tenantId"));
        base.setTransDateTime(new Date());
        return base;
    }

    public static HsjryPageRequest buildBasePageRequest(String serialNum, int pageNo, int pageSize) {
        HsjryPageRequest req = new HsjryPageRequest((pageNo - 1) * pageSize, pageSize);
        req.setSerialNo(serialNum);
        req.setIdemSerialNo(serialNum);
        req.setAsk(EnumModuleCode.CONSOLE);
        req.setChannelNo(CHANNELNO);
        req.setTenantId(ResourceUtil.getValue("tenantId"));
        req.setTransDateTime(new Date());
        return req;
    }

    public static HsjryPageRequest buildBasePageRequest(int pageNo, int pageSize) {
        return buildBasePageRequest(UUIDUtil.getUUID(), pageNo, pageSize);
    }

}
