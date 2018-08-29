package com.wandaph.filetarnsfer.rocketmq.producer.impl;

import com.alibaba.rocketmq.client.producer.SendStatus;
import com.google.gson.Gson;
import com.hsjry.lang.common.base.HsjryMQRequest;
import com.hsjry.lang.common.base.HsjryRequest;
import com.hsjry.lang.common.base.constant.HsjryMqBusiness;
import com.hsjry.lang.common.base.enums.EnumModuleCode;
import com.hsjry.lang.common.base.enums.EnumServiceScence;
import com.hsjry.lang.common.pojo.CompensateRequest;
import com.hsjry.lang.common.util.JsonUtil;
import com.hsjry.lang.common.util.StringUtil;
import com.hsjry.lang.common.util.UUIDUtil;
import com.hsjry.lang.log.Log;
import com.hsjry.lang.log.TenantLog;
import com.hsjry.mq.Message;
import com.hsjry.mq.Producer;
import com.hsjry.mq.SendResult;
import com.wandaph.filetarnsfer.model.request.FileTransferMqRequest;
import com.wandaph.filetarnsfer.rocketmq.producer.ContractMqProducerCore;
import com.wandaph.filetarnsfer.utils.BaseReqBuilder;
import com.wandaph.filetarnsfer.utils.FileTransferConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author lipeng
 * @Title: ContractTransferImpl
 * @ProjectName wandaph-filetransfer
 * @Description: TODO
 * @date 2018/8/2715:12
 */
public class ContractMqProducerCoreImpl implements ContractMqProducerCore{

    private Log logger     = TenantLog.get(ContractMqProducerCoreImpl.class);
    @Autowired
    @Qualifier("ContractTransferProducer")
    private Producer ContractTransferProducer;
    private final static int queryTimes = 3;

    @Override
    public void addAsynPaymentProducer(String loan,String url) {

        HsjryMQRequest<FileTransferMqRequest> mqRequest = new HsjryMQRequest<>();
        FileTransferMqRequest fileTransferMqRequest = new FileTransferMqRequest();
        fileTransferMqRequest.setLoanNo(loan);
        fileTransferMqRequest.setUrl(url);
        mqRequest.setBusinessRequest(fileTransferMqRequest);
        mqRequest.setHsjryRequest(null);
        logger.info("开始生产[合同传输]事件,参数为：{}", new Gson().toJson(mqRequest));
        SendResult result = ContractTransferProducer.send(FileTransferConstant.MSG_TOPIC, FileTransferConstant.MSG_TAGS,
                FileTransferConstant.MSG_TOPIC + "|" + FileTransferConstant.MSG_TAGS, JsonUtil.toJson(mqRequest));
        logger.info("[合同传输]消息发送结果为:{}", result);
        if (!SendStatus.SEND_OK.equals(result.getSendStatus())) {
            logger.info("[合同传输]生产事件失败，开始重新生产");
            for (int i = 0; i < queryTimes; i++) {
                SendResult sendResult = ContractTransferProducer.send(FileTransferConstant.MSG_TOPIC, FileTransferConstant.MSG_TAGS,
                        FileTransferConstant.MSG_TOPIC + "|" + FileTransferConstant.MSG_TAGS, mqRequest);

                if (!SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                    logger.warn("[合同传输]添加mq事件失败,请求参数为:{},第{}次添加失败:", new Gson().toJson(fileTransferMqRequest), i + 1);
                    continue;
                }else{
                    logger.info("[合同传输]添加mq事件任务成功。交易参数为：{}", new Gson().toJson(fileTransferMqRequest));
                    break;
                }
            }
        }
    }
}
