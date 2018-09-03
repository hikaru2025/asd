package com.wandaph.filetarnsfer.interceptor;

import com.hsjry.lang.log.Log;
import com.hsjry.lang.log.TenantLog;
import com.wandaph.filetarnsfer.model.enums.FileTransferStatusEnums;
import com.wandaph.filetarnsfer.utils.FileTransferUtils;
import com.wandaph.filetarnsfer.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author lipeng
 * @Title: SignInterceptor
 * @ProjectName wandaph-filetransfer
 * @Description: TODO
 * @date 2018/8/248:22
 */
@Component
public class WddContractSignInterceptor implements HandlerInterceptor {

    private static final Log log = TenantLog.get(WddContractSignInterceptor.class);

    @Value("${filetransfer.wdd.channel}")
    private String channel;

    @Value("${filetransfer.wdd.chn_wdd_0001_publickey}")
    private String publickey;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

         String data = (String)httpServletRequest.getParameter("data");
         String channel1 = (String)httpServletRequest.getParameter("channel");
         String requestTime = (String)httpServletRequest.getParameter("requestTime");
         String sign = (String)httpServletRequest.getParameter("sign");

         //参数检查
         if(StringUtils.isEmpty(data) ||
                 StringUtils.isEmpty(channel1) ||
                 StringUtils.isEmpty(requestTime) ||
                 StringUtils.isEmpty(sign)){
             response(httpServletResponse,"参数异常",FileTransferStatusEnums.PARM_ERR.getCode(),FileTransferStatusEnums.PARM_ERR.getDesc(),null);
             return false;
         }

         //校验渠道编号
         if(!channel1.equals(channel)){
             response(httpServletResponse,"渠道编号不匹配",FileTransferStatusEnums.CHANNEL_NOEXIST.getCode(),FileTransferStatusEnums.CHANNEL_NOEXIST.getDesc(),null);
             return false;
         }

        try {
            //开始验签
            log.info("文件上传参数验签。。。。。。");

            if(!FileTransferUtils.checkSign(data,sign,publickey)){

                response(httpServletResponse,"验签失败",FileTransferStatusEnums.CHECK_SIGN_FAIL.getCode(),FileTransferStatusEnums.CHECK_SIGN_FAIL.getDesc(),null);
                return false;
            }

            log.info("文件上传参数验签成功：开始处理业务。。。。。。");
        } catch (RuntimeException e) {
            response(httpServletResponse,"验签参数解析异常",FileTransferStatusEnums.CHECK_SIGN_FAIL.getCode(),FileTransferStatusEnums.CHECK_SIGN_FAIL.getDesc(),e);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private void response(HttpServletResponse httpServletResponse,String errDesc,String code,String desc,Exception e) throws Exception{

        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        String response = JsonUtil.toJsonString(FileTransferUtils.handleFail(errDesc, code, desc, e));
        PrintWriter out = httpServletResponse.getWriter();
        out.append(response);
    }
}
