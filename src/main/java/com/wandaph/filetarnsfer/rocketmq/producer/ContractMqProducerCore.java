package com.wandaph.filetarnsfer.rocketmq.producer;

/**
 * @author lipeng
 * @Title: ContractMqProducerCore
 * @ProjectName wandaph-filetransfer
 * @Description: TODO
 * @date 2018/8/2715:14
 */
public interface ContractMqProducerCore {

    void addAsynPaymentProducer(String key, String value);
}
