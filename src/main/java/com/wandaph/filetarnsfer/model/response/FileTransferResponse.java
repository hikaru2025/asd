package com.wandaph.filetarnsfer.model.response;

import java.io.Serializable;

public class FileTransferResponse implements Serializable{

    private static final long serialVersionUID = 7015384072925266411L;

    private String code;
    private String msg;
    private String responseTime;
    private String url;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public FileTransferResponse(String code, String msg, String responseTime) {
        this.code = code;
        this.msg = msg;
        this.responseTime = responseTime;
    }

    public FileTransferResponse() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FileTransferResponse(String code, String msg, String responseTime, String url) {
        this.code = code;
        this.msg = msg;
        this.responseTime = responseTime;
        this.url = url;
    }
}
