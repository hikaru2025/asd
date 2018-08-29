package com.wandaph.filetarnsfer.utils;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.hsjry.lang.common.util.DateUtil;
import com.hsjry.lang.fs.service.HsjryFileService;
import com.hsjry.lang.fs.util.FileUploadResp;
import com.hsjry.lang.fs.util.FileUtil;
import com.hsjry.lang.log.Log;
import com.hsjry.lang.log.TenantLog;
import com.wandaph.filetarnsfer.model.enums.FileTransferStatusEnums;
import com.wandaph.filetarnsfer.model.request.FileTransferRequest;
import com.wandaph.filetarnsfer.model.response.FileTransferResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class FileTransferUtils {

    private static final Log log = TenantLog.get(FileTransferUtils.class);

    /**
     * 根据请求获取对应请求参数，并转化
     */
    public static <T> T getRequestData(HttpServletRequest request, Class<T> clazz) {
        try {
            String data = (String) request.getAttribute(FileTransferConstant.DATA);
            return JsonUtil.parser(data,clazz);
        } catch (Exception e) {
            log.info("[万达贷导流万e贷：合同传输]获取请求参数异常：{}", e);
        }
        return null;
    }

    /**
     * 失败处理
     */
    public static FileTransferResponse handleFail(String errorDesc,String errorCode, String errorMsg, Exception ex) {

        if (ex != null) {
            log.error(errorDesc+"：errorCode={}，errorMsg={}，{}", errorCode,errorMsg,ex);
        }else {
            log.error(errorDesc+"：errorCode={}，errorMsg={}，{}", errorCode,errorMsg);
        }
        return new FileTransferResponse(errorCode,errorMsg,new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    }

    /**
     * 成功处理
     */
    public static FileTransferResponse handleSuccess() {

        return new FileTransferResponse(FileTransferStatusEnums.SUCCESS.getCode(),FileTransferStatusEnums.SUCCESS.getDesc(),new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    }

    /**
     * 成功处理
     */
    public static FileTransferResponse handleSuccess(String url) {

        return new FileTransferResponse(FileTransferStatusEnums.SUCCESS.getCode(),FileTransferStatusEnums.SUCCESS.getDesc(),new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),url);
    }

    /**
     * 验签
     * @param data
     * @param sign
     * @param publicKey
     * @return
     */
    public static boolean checkSign(String data,String sign,String publicKey) {

        try {
            return RSAUtils.doCheck(Base64Utils.decode(data), Base64Utils.decode(sign),publicKey);
        }catch (Exception e){
            log.error("验签异常：errorCode={}，errorMsg={}，{}", FileTransferStatusEnums.CHECK_SIGN_FAIL.getCode(),FileTransferStatusEnums.CHECK_SIGN_FAIL.getDesc(),e);
            return false;
        }
    }


    /**
     * 接收Base64位的解密字符串
     * @param
     * @param
     */
    public static FileTransferResponse uploadContract(HsjryFileService hsjryFileService, FileTransferRequest request, String date){

        String filePath = FileTransferConstant.FILE_SAVE_PATH+ File.separator+date;
        String fileName = FileTransferConstant.FILE_SAVE_PREFIX+request.getLoanNo()+"."+request.getFileType();

        InputStream fileInputStream=null;
        try {

            //Base64转码
            byte[] fileBytes = Base64Utils.decode(request.getFileStr());

            // 打开一个新的输入流
            fileInputStream = new ByteArrayInputStream(fileBytes);

            log.info("合同文件开始传输。。。。。。");
            //上传到本地
            FileUploadResp res = hsjryFileService.uploadFile(fileInputStream,fileName,filePath);

            if(res.isSuccess()){
                log.info("文件上传到文件件服务器成功[{}]",res.getUrl());
                return handleSuccess(res.getUrl());
            }else{
                log.error("文件上传失败：errorCode={}，errorMsg={}，{}", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc());
                return handleFail("文件上传失败", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc(),null);
            }
        }catch (Exception e){
            log.error("文件上传失败：errorCode={}，errorMsg={}，{}", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc(),e);
            return handleFail("文件上传失败", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc(),null);
        }
    }

    /**
     * 以流的形式保存文件
     * @param
     * @param
     */
    public static FileTransferResponse uploadContract(HsjryFileService hsjryFileService,InputStream inputStream ,FileTransferRequest request,String date){

        String filePath = FileTransferConstant.FILE_SAVE_PATH+ File.separator+date;
        String fileName = FileTransferConstant.FILE_SAVE_PREFIX+request.getLoanNo()+"."+request.getFileType();

        try{
            log.info("合同文件开始传输。。。。。。");
            //上传到服务器
            FileUploadResp res = hsjryFileService.uploadFile(inputStream,fileName,filePath);

            if(res.isSuccess()){
                log.info("文件上传到文件服务器成功[{}]",res.getUrl());
                return handleSuccess(res.getUrl());
            }else{
                log.error("文件上传失败：errorCode={}，errorMsg={}，{}", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc());
                return handleFail("文件上传失败", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc(),null);
            }
        }catch (Exception e){
            log.error("文件上传失败：errorCode={}，errorMsg={}，{}", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc(),e);
            return handleFail("文件上传失败", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc(),null);
        }
    }

    public static SendStatus sendMessage(DefaultMQProducer producer, String key, String value) throws Exception{
        Message msg = new Message(FileTransferConstant.MSG_TOPIC,FileTransferConstant.MSG_TAGS,key,value.getBytes());
        SendResult result = producer.send(msg);
        return result.getSendStatus();
    }

}
