package com.wandaph.filetarnsfer.model.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lipeng
 * @Title: ContractTransfer
 * @ProjectName wandaph-filetransfer
 * @Description: TODO
 * @date 2018/8/248:32
 */
@Entity
@Table(name="contract_transfer")
public class ContractTransfer {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "loan_no")
    private String loanNo;

    @Column(name = "channel")
    private String channel;

    @Column(name = "contract_url")
    private String contractUrl;

    @Column(name = "createtime")
    private Date createtime;

    @Column(name = "updatetime")
    private Date updatetime;

    @Column(name = "publickey")
    private String publickey;

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoanNo() {
        return loanNo;
    }

    public void setLoanNo(String loanNo) {
        this.loanNo = loanNo;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public ContractTransfer(String loanNo, String channel, String contractUrl, Date createtime, Date updatetime) {
        this.loanNo = loanNo;
        this.channel = channel;
        this.contractUrl = contractUrl;
        this.createtime = createtime;
        this.updatetime = updatetime;
    }

    public ContractTransfer() {
    }
}
