package com.wandaph.filetarnsfer.utils;

import com.hsjry.lang.fs.util.FileUploadResp;
import com.hsjry.lang.fs.util.FileUtil;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * @Auther: cwj
 * @Date: 2018/5/22 15:23
 * @Description:
 */
public class SftpUtils {
    private static final Logger logger = LoggerFactory
            .getLogger(SftpUtils.class);
    private Session session = null;
    private ChannelSftp channel = null;


    /**
     * 连接sftp服务器
     *
     * @param serverIP 服务IP
     * @param port     端口
     * @param userName 用户名
     * @param password 密码
     * @throws SocketException SocketException
     * @throws IOException     IOException
     * @throws JSchException   JSchException
     */
    public void connectServer(String serverIP, int port, String userName, String password) throws SocketException, IOException, JSchException {
        JSch jsch = new JSch();
        // 根据用户名，主机ip，端口获取一个Session对象
        session = jsch.getSession(userName, serverIP, port);
        // 设置密码
        session.setPassword(password);
        // 为Session对象设置properties
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        // 通过Session建立链接
        session.connect();
        // 打开SFTP通道
        channel = (ChannelSftp) session.openChannel("sftp");
        // 建立SFTP通道的连接
        channel.connect();

    }

    /**
     * 自动关闭资源
     */
    public void disconnect() {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

    public List<ChannelSftp.LsEntry> getDirList(String path) throws SftpException {
        List<ChannelSftp.LsEntry> list = new ArrayList<ChannelSftp.LsEntry>();
        if (channel != null) {
            Vector vv = channel.ls(path);
            if (vv == null && vv.size() == 0) {
                return list;
            } else {
                Object[] aa = vv.toArray();
                for (int i = 0; i < aa.length; i++) {
                    ChannelSftp.LsEntry temp = (ChannelSftp.LsEntry) aa[i];
                    list.add(temp);

                }
            }
        }
        return list;
    }

    /**
     * 下载文件
     */
    public void downloadFile(String remotePathFile, String localDir,String localFileName) throws SftpException, IOException {
        File file = new File(localDir);
        if(!file.exists()){
            file.mkdirs();
        }
        FileOutputStream os = new FileOutputStream(new File(localDir,localFileName));
        if (channel == null){
            throw new IOException("sftp server not login");
        }
        channel.get(remotePathFile, os);
    }

    /**
     * 上传文件
     *
     * @param remoteFile 远程文件
     * @param localFile
     * @throws SftpException
     * @throws IOException
     */
    public void uploadFile(String remoteFile, String localFile) throws SftpException, IOException {
        FileInputStream in = new FileInputStream(new File(localFile));
        if (channel == null){
            throw new IOException("sftp server not login");
        }
        String path=remoteFile.substring(0,remoteFile.lastIndexOf(File.separator));
        channel.mkdir(path);
        channel.put(in, remoteFile);
    }

    /**
     * 下载base文件并解码到本地文件
     * @param remotePath
     * @param fileName
     * @param fileDir
     * @param localFileName
     * @return 返回相对地址
     * @throws Exception
     */
    public String downNeedDesFileToLocal(String remotePath,String fileName,String fileDir,String localFileName)throws Exception{

        InputStream ins = downFile(remotePath,fileName);
        if(null == ins){
            return null;
        }
        FileUploadResp resp = FileUtil.uploadFile(ins,localFileName,fileDir);
        return resp.getUrl();
    }
    /**
     * 下载远程文件到本地流
     *
     * @param remotePath
     * @param fileName
     * @return
     * @throws Exception
     */
    public InputStream downFile(String remotePath, String fileName)
            throws Exception {
    	logger.info("downFile|下载远程文件到本地流|remotePath:"+remotePath+
                "|fileName:"+fileName);

        remotePath = getAbsolutePath(remotePath);
        if (!existFile(remotePath, fileName)) {
            logger.info("downFile|下载远程文件到本地流|文件不存在|path:"+remotePath+"|file:",fileName+"|return:null");
            return null;
        }
        this.channel.cd(remotePath);
        return this.channel.get(fileName);
    }

    /**
     * 获取文件绝对路径
     *
     * @param path
     * @return
     */
    private String getAbsolutePath(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }
    
    /**
     * 目录下文件是否存在
     * @param remotePath
     * @param fileName
     * @return
     */
    public boolean existFile(String remotePath, String fileName) {
        try {
            if (!existDir(remotePath)) {
                return false;
            }

            String fullName = remotePath + fileName;
            InputStream in = this.channel.get(fullName);
            logger.info("existFile|判断文件是否存在|remotePath:"+ remotePath+
                    "|fileName"+ fileName+ "return:"+(in != null));
            return in != null;
        } catch (Exception e) {
            logger.info("existFile|判断文件是否存在|remotePath:"+ remotePath+
                    "|fileName"+ fileName+ "|异常|", e);
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 判断远程目录是否存在
     *
     * @param remotePath
     * @return
     * @throws SftpException
     * @throws JSchException
     */
    private boolean existDir(String remotePath) throws SftpException,
            JSchException {
        try {
            SftpATTRS attrs = this.channel.lstat(remotePath);
            return attrs.isDir();
        } catch (Exception e) {
            logger.info("existDir|"+remotePath+"不存在");
            return false;
        }
    }

}
