package com.wandaph.filetarnsfer.rocketmq.consumer;

import com.hsjry.lang.log.Log;
import com.hsjry.lang.log.TenantLog;
import com.hsjry.mq.Action;
import com.hsjry.mq.ConsumeContext;
import com.hsjry.mq.Message;
import com.hsjry.mq.MessageListener;

/**
 * @author lipeng
 * @Title: ContractTransferConsumer
 * @ProjectName wandaph-filetransfer
 * @Description: TODO
 * @date 2018/8/2717:20
 */
public class ContractTransferMessageConsumer implements MessageListener {

    private Log log = TenantLog.get(this.getClass());

    @Override
    public Action consume(Message arg0, ConsumeContext context) {
        log.info("开始消费合同传输消息");
        String jsonStr = arg0.getObjBody(String.class);
/*        HsjryMQRequest<FileTransferMqRequest> mqRequest = JsonUtil.parser(jsonStr, HsjryMQRequest.class);
        FileTransferMqRequest fileTransferMqRequest = mqRequest.getBusinessRequest();*/
        System.out.println(jsonStr);
        return Action.CommitMessage;
    }
}
