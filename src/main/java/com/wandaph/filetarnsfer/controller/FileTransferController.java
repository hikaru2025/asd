package com.wandaph.filetarnsfer.controller;

import com.hsjry.lang.log.Log;
import com.hsjry.lang.log.TenantLog;
import com.wandaph.filetarnsfer.biz.FileTransferBiz;
import com.wandaph.filetarnsfer.model.enums.FileTransferStatusEnums;
import com.wandaph.filetarnsfer.model.request.FileTransferRequest;
import com.wandaph.filetarnsfer.utils.FileTransferUtils;
import com.wandaph.filetarnsfer.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
public class FileTransferController {

    private static final Log log = TenantLog.get(FileTransferController.class);

    @Autowired
    private FileTransferBiz fileTransferBiz;

    /**
     * 合同文件上传
     * @param
     * @return
     */
    @RequestMapping(value = "/upload/contractuploadbase64",method = RequestMethod.POST)
    @ResponseBody
    public Object contractUploadBase64(HttpServletRequest request){

        String data = (String)request.getParameter("data");
        String channel = (String)request.getParameter("channel");
        String requestTime = (String)request.getParameter("requestTime");
        String sign = (String)request.getParameter("sign");

        //参数检查
        if(StringUtils.isEmpty(data) ||
                StringUtils.isEmpty(channel) ||
                StringUtils.isEmpty(requestTime) ||
                StringUtils.isEmpty(sign)){
            return FileTransferUtils.handleFail("请求参数异常",FileTransferStatusEnums.PARAM_MISS.getCode(),FileTransferStatusEnums.PARM_ERR.getDesc(),null);
        }

        log.info("合同传输请求参数入参{}",data);
        //参数解析
        FileTransferRequest fileTransferRequest = null;
        try {

            fileTransferRequest = JsonUtil.parser(data, FileTransferRequest.class);
        }catch (RuntimeException e){

            return FileTransferUtils.handleFail("data参数解析异常",FileTransferStatusEnums.PARM_PARSE_ERR.getCode(),FileTransferStatusEnums.PARM_PARSE_ERR.getDesc(),e);
        }

        //文件上传
        try {

            return fileTransferBiz.contractUploadBase64(fileTransferRequest);
        }catch (Exception e){
            log.error("文件上传失败：errorCode={}，errorMsg={}，{}", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc());
            return FileTransferUtils.handleFail("文件上传失败", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc(),null);
        }
    }

    /**
     * 合同文件上传
     * @param request
     * @return
     */
    @RequestMapping(value = "/upload/contractuploadinputstream",method = RequestMethod.POST)
    @ResponseBody
    public Object contractUploadInputStream(@RequestParam MultipartFile file, HttpServletRequest request){

        String data = (String)request.getParameter("data");
        String channel = (String)request.getParameter("channel");
        String requestTime = (String)request.getParameter("requestTime");
        String sign = (String)request.getParameter("sign");

        //参数检查
        if(StringUtils.isEmpty(data) ||
                StringUtils.isEmpty(channel) ||
                StringUtils.isEmpty(requestTime) ||
                StringUtils.isEmpty(sign)){
            return FileTransferUtils.handleFail("请求参数异常",FileTransferStatusEnums.PARAM_MISS.getCode(),FileTransferStatusEnums.PARM_ERR.getDesc(),null);
        }

        //参数解析
        FileTransferRequest fileTransferRequest = null;
        try {

            fileTransferRequest = JsonUtil.parser(data, FileTransferRequest.class);
        }catch (RuntimeException e){

            return FileTransferUtils.handleFail("data参数解析异常",FileTransferStatusEnums.PARM_PARSE_ERR.getCode(),FileTransferStatusEnums.PARM_PARSE_ERR.getDesc(),e);
        }

        //文件上传
        try {

            return fileTransferBiz.contractUploadInputSteam(fileTransferRequest,file.getInputStream());
        }catch (Exception e){
            log.error("文件上传失败：errorCode={}，errorMsg={}，{}", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc());
            return FileTransferUtils.handleFail("文件上传失败", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc(),null);
        }
    }

}
