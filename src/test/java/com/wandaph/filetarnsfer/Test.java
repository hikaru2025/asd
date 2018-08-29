package com.wandaph.filetarnsfer;

import com.wandaph.filetarnsfer.model.request.FileTransferRequest;
import com.wandaph.filetarnsfer.utils.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Test {

    private static final String SUFF1 = "/file/upload/contractuploadbase64";
    private static final String SUFF2 = "/file/upload/contractuploadinputstream";
    private static final String privatekey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANEjYmQYeP0Tztot\n" +
            "KwOjp4ERtZVtdo7QYFeGlyMCpcsE2+Erq08EuAc+XOaJ86+xmDpIxIjIxoDBgKTN\n" +
            "JIbJjLu0fxQ7viBl5wp6un6Zdk03U8Lij+h5gxBxuGTiqArxs6r3R/DCp6iXm0JR\n" +
            "EACoWS32L9o+MqJn/5GgB5HHWDdjAgMBAAECgYBTtp82w9kCLICbMoNKRKLS9jzC\n" +
            "IHvZdK1ru5MZz4B/wGUnP6ylx5TGSX80RLoHdMYjZmdM/Vj/xvTwgrEBRwIOkzRb\n" +
            "1Bpj6QbSSRIgXvq2I1pR6s+ToHP5zMbbLVjYFjqANlX8QZsjKSmNve/WrH9xXWL2\n" +
            "75+BIy783paWz5bfmQJBAPBkdHU1AREGudBy35kktaPkbQm2QIBwZ5ZklNdtAc0Z\n" +
            "uMyFSLmiKxx5KjvhsGATXHn2ePwBHEDtIxFLzriYuyUCQQDet3S0tFBz6Vk+fauU\n" +
            "RQMRP9ZMsOFJFp6BU4U2UKghiohdYvdvnt60OXAU+GaMYyeABt2gokTFukzu5qXF\n" +
            "HyXnAkApXzCHx3KCiqTKEisXQTEdnr+BT2N0d2Pshyzykmkf8lBSqpGkmRtQUC4L\n" +
            "lkDzmAFvYBLcVBiXt/g0DtbqACcdAkBd9sS0YMb4wGC4rsivG5a5tlcxsAa8kWB6\n" +
            "a8RuMAVfcVUrIVAHtd176CfTkSGi5UqLB1qGJTAixAElYgZablPNAkAzz8Usyo2M\n" +
            "ZZaau+0kvuwtsbXskNIH4E1ATe5lV4BS81Wh8t3eMGJjXXofpZz3rqCS2XL025Ak\n" +
            "bj868FuHxJPG";

    public static void main(String[] args) throws Exception{

        //String url1 = "http://10.53.156.187:9016/file/upload/contractuploadinputstream"+SUFF1;
        String url1 = "http://localhost:9016"+SUFF1;
        String url2 = "http://10.53.156.187:9016"+SUFF2;
        test1(url1);
        //test2(url2);
    }

    public static void test1(String url) throws Exception{

        FileTransferClient.fileTransferBase64(url,getMap());
    }

    public static void test2(String url) throws Exception{
        File file = new File("D:\\Users\\lipeng\\IdeaProjects\\wandaph-filetransfer\\src\\main\\resources\\application.yml");
        FileTransferClient.fileTransferInputStream(url,file,getMap());
    }




    public static Map<String,String> getMap() throws Exception{

        Map<String,String> map = new HashMap<>();
        map.put("data",getData());
        map.put("channel","CHN_WDD_0001");
        map.put("requestTime","20180606103005");
        map.put("sign",getSign());
        return map;
    }

    public static String getSign() throws Exception{

        return RSAUtils.rsaSign(getData(),privatekey);
    }

    public static String getData() throws Exception{

        FileTransferRequest request = new FileTransferRequest();
        request.setFileType("pdf");
        request.setLoanNo("aaaaaaa");
        request.setFileStr(Base64Utils.encode(FileTransferClient.getBytes("D:\\Users\\lipeng\\IdeaProjects\\wandaph-filetransfer\\src\\main\\resources\\application.yml")));
        return JsonUtil.toJsonString(request);
    }
}
