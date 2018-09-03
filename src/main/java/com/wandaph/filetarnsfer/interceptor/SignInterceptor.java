package com.wandaph.filetarnsfer.interceptor;

import com.hsjry.lang.log.Log;
import com.hsjry.lang.log.TenantLog;
import com.wandaph.filetarnsfer.config.FiletransferConfig;
import com.wandaph.filetarnsfer.model.entity.Filetransfer;
import com.wandaph.filetarnsfer.model.enums.FileTransferStatusEnums;
import com.wandaph.filetarnsfer.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author lipeng
 * @Title: SignInterceptor
 * @ProjectName wandaph-filetransfer
 * @Description: TODO
 * @date 2018/8/248:22
 */
@Component
public class SignInterceptor implements HandlerInterceptor {

    private static final Log log = TenantLog.get(SignInterceptor.class);

    @Autowired
    private FiletransferConfig filetransferConfig;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

         String data = (String)httpServletRequest.getParameter("data");
         String channel = (String)httpServletRequest.getParameter("channel");
         String requestTime = (String)httpServletRequest.getParameter("requestTime");
         String sign = (String)httpServletRequest.getParameter("sign");

         //参数检查
         if(StringUtils.isEmpty(data) ||
                 StringUtils.isEmpty(channel) ||
                 StringUtils.isEmpty(requestTime) ||
                 StringUtils.isEmpty(sign)){
             response(httpServletResponse,"参数异常",FileTransferStatusEnums.PARM_ERR.getCode(),FileTransferStatusEnums.PARM_ERR.getDesc(),null);
             return false;
         }

         //通过渠道编号找对应的公钥
        List<Filetransfer> filetransfers = filetransferConfig.getFiletransfer();
        String publickey = null;
        if(!CollectionUtils.isEmpty(filetransfers)){

             for (Filetransfer filetransfer:filetransfers) {

                 if(filetransfer != null){

                     if(channel.equals(filetransfer.getChannel())){
                         publickey = filetransfer.getPublickey();
                         break;
                     }
                 }
             }
             if (StringUtils.isEmpty(publickey)){
                 response(httpServletResponse,"渠道编号匹配公钥错误",FileTransferStatusEnums.CHANNEL_TO_PUBLICKEY.getCode(),FileTransferStatusEnums.CHANNEL_TO_PUBLICKEY.getDesc(),null);
                 return  false;
             }
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
