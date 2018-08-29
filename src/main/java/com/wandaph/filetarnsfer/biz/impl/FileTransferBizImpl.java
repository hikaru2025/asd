package com.wandaph.filetarnsfer.biz.impl;

import com.alibaba.rocketmq.client.producer.SendStatus;
import com.hsjry.lang.common.util.DateUtil;
import com.hsjry.lang.fs.service.HsjryFileService;
import com.hsjry.lang.log.Log;
import com.hsjry.lang.log.TenantLog;
import com.hsjry.mq.SendResult;
import com.wandaph.filetarnsfer.biz.FileTransferBiz;
import com.wandaph.filetarnsfer.controller.FileTransferController;
import com.wandaph.filetarnsfer.model.enums.FileTransferStatusEnums;
import com.wandaph.filetarnsfer.model.request.FileTransferRequest;
import com.wandaph.filetarnsfer.model.response.FileTransferResponse;
import com.wandaph.filetarnsfer.rocketmq.producer.ContractMqProducerCore;
import com.wandaph.filetarnsfer.utils.FileTransferUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Date;

@Service
public class FileTransferBizImpl implements FileTransferBiz{

    private static final Log log = TenantLog.get(FileTransferController.class);

    @Resource
    private ContractMqProducerCore contractMqProducerCore;

    @Resource
    private HsjryFileService hsjryFileService;

    @Override
    public FileTransferResponse contractUploadBase64(FileTransferRequest request) {

        if(StringUtils.isEmpty(request.getFileStr()) ||
                StringUtils.isEmpty(request.getLoanNo()) ||
                StringUtils.isEmpty(request.getFileType())){
            return FileTransferUtils.handleFail("请求参数异常", FileTransferStatusEnums.PARAM_MISS.getCode(),FileTransferStatusEnums.PARM_ERR.getDesc(),null);
        }
        try {
            String date = DateUtil.getDate(new Date(), "yyyyMMdd");
            //上传文件
            FileTransferResponse response = FileTransferUtils.uploadContract(hsjryFileService,request,date);

            if(response.getCode().equals(FileTransferStatusEnums.SUCCESS.getCode())){
                //发送消息
                contractMqProducerCore.addAsynPaymentProducer(request.getLoanNo(), response.getUrl());
            }
            return  response;
        }catch (Exception e){
            log.error("文件上传失败：errorCode={}，errorMsg={}，{}", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc());
            return FileTransferUtils.handleFail("文件上传失败", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc(),null);
        }
    }

    @Override
    public FileTransferResponse contractUploadInputSteam(FileTransferRequest request, InputStream inputStream) {

        if(StringUtils.isEmpty(request.getLoanNo()) || StringUtils.isEmpty(request.getFileType())){
            return FileTransferUtils.handleFail("请求参数异常",FileTransferStatusEnums.PARAM_MISS.getCode(),FileTransferStatusEnums.PARM_ERR.getDesc(),null);
        }
        try {
            String date = DateUtil.getDate(new Date(), "yyyyMMdd");
            //上传文件
            FileTransferResponse response = FileTransferUtils.uploadContract(hsjryFileService,inputStream, request, date);
            if(response.getCode().equals(FileTransferStatusEnums.SUCCESS.getCode())){
                //发送消息
                contractMqProducerCore.addAsynPaymentProducer(request.getLoanNo(), response.getUrl());
            }
            return response;
        }catch (Exception e){
            log.error("文件上传失败：errorCode={}，errorMsg={}，{}", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc());
            return FileTransferUtils.handleFail("文件上传失败", FileTransferStatusEnums.FILE_UPLOAD_FAIL.getCode(),FileTransferStatusEnums.FILE_UPLOAD_FAIL.getDesc(),null);
        }
    }


}
