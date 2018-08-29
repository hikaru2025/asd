package com.wandaph.filetarnsfer.model.request;

import java.io.Serializable;

public class FileTransferRequest implements Serializable{

    private static final long serialVersionUID = 6286337316282080314L;

    private String loanNo;
    private String fileStr;
    private String fileType;

    public String getLoanNo() {
        return loanNo;
    }

    public void setLoanNo(String loanNo) {
        this.loanNo = loanNo;
    }

    public String getFileStr() {
        return fileStr;
    }

    public void setFileStr(String fileStr) {
        this.fileStr = fileStr;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

}
