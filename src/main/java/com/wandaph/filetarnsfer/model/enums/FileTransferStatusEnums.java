package com.wandaph.filetarnsfer.model.enums;

public enum FileTransferStatusEnums {

    SUCCESS("0000", "成功", "成功返回信息"),
    PARAM_MISS("0001", "缺少参数", "入参错误提示"),
    PARM_ERR("0002","参数错误", "入参错误提示"),
    SERVER_UNUSABLE("0003","服务不可用","服务器错误提示"),
    CHECK_SIGN_FAIL("0004","验签失败","验签提示"),
    FILE_UPLOAD_FAIL("0005","文件上传服务器失败","文件上传提示"),
    FILE_OVERSIZE("0006","文件过大","文件上传提示"),
    MSG_SEND_FAIL("0007","消息发送失败","消息发送提示"),
    PARM_PARSE_ERR("0008","参数解析错误","入参错误提示"),
    CHANNEL_NOEXIST("0009","渠道号不存在","入参错误提示"),
    PUBLICKEY_NOEXIST("0010","公钥不存在，请提供公钥给调用方","验签提示"),
    CHANNEL_TO_PUBLICKEY("0011","渠道编号匹配公钥错误","渠道匹配公钥");



    private final String code;
    private final String desc;
    private final String remark;

    private FileTransferStatusEnums(String code, String desc, String remark) {
        this.code = code;
        this.desc = desc;
        this.remark = remark;
    }

    public static FileTransferStatusEnums find(String code) {
        FileTransferStatusEnums[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            FileTransferStatusEnums frs = var1[var3];
            if (frs.getCode().equals(code)) {
                return frs;
            }
        }

        return null;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getRemark() {
        return this.remark;
    }

}
