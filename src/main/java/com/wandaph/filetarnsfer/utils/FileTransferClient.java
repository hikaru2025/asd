package com.wandaph.filetarnsfer.utils;

import com.hsjry.lang.log.Log;
import com.hsjry.lang.log.TenantLog;
import com.wandaph.filetarnsfer.controller.FileTransferController;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * @author lipeng
 * @Title: FileTransferClient
 * @ProjectName wandaph-filetransfer
 * @Description: TODO
 * @date 2018/8/2710:51
 */
public class FileTransferClient {

    private static final Log log = TenantLog.get(FileTransferClient.class);

    /**
     * 流的形式传输文件
     * @param url
     * @param file
     * @param params
     * @return
     * @throws IOException
     */
    public static String fileTransferInputStream(String url, File file, Map<String,String> params) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        //封装参数
        if(params != null){
            for (Map.Entry<String,String> entry:params.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.TEXT_PLAIN);
            }
        }

        // 把文件加到HTTP的post请求中
        builder.addBinaryBody(
                "file",
                new FileInputStream(file),
                ContentType.APPLICATION_OCTET_STREAM,
                file.getName()
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        HttpEntity responseEntity = response.getEntity();
        String sResponse= EntityUtils.toString(responseEntity, "UTF-8");
        log.info("响应结果response{}{}",sResponse);
        return sResponse;
    }

    /**
     * Base64传输文件
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String fileTransferBase64(String url, Map<String,String> params) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        //封装参数
        if(params != null){
            for (Map.Entry<String,String> entry:params.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.TEXT_PLAIN);
            }
        }
        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        HttpEntity responseEntity = response.getEntity();
        String sResponse= EntityUtils.toString(responseEntity, "UTF-8");
        log.info("响应结果response{}{}",sResponse);
        return sResponse;
    }

    /**
     * 文件转字节数组
     * @param filePath
     * @return
     * @throws Exception
     */
    public static byte[] getBytes(String filePath) throws Exception{
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            if(fis != null){
                fis.close();
            }
            if(bos != null){
                bos.close();
            }
        }
        return buffer;
    }
}
