package com.wandaph.filetarnsfer.model.request;

import java.io.Serializable;

/**
 * @author lipeng
 * @Title: FileTransferMqRequest
 * @ProjectName wandaph-filetransfer
 * @Description: TODO
 * @date 2018/8/2716:52
 */
public class FileTransferMqRequest implements Serializable{

    private static final long serialVersionUID = 1L;

    private String loanNo;
    private String url;

    public String getLoanNo() {
        return loanNo;
    }

    public void setLoanNo(String loanNo) {
        this.loanNo = loanNo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
