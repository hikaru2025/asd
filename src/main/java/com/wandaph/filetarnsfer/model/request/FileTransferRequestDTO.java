package com.wandaph.filetarnsfer.model.request;

import java.io.Serializable;

public class FileTransferRequestDTO implements Serializable{

    private static final long serialVersionUID = 6286337316282080314L;

    private String channel;
    private String requestTime;
    private String data;
    private String sign;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
