package com.wandaph.filetarnsfer.utils;

import com.hsjry.lang.fs.util.FileUploadResp;
import com.hsjry.lang.fs.util.FileUtil;
import com.jcraft.jsch.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;
import java.util.Vector;

public class SftpUtil {

    private static final Logger logger = LoggerFactory
            .getLogger(SftpUtil.class);

    private ChannelSftp sftp;
    private static  String host;
    private static String username;
    private static String privateKeyFile;
    private static int port;
    private Session sshSession;


    static {
        host = ResourceUtil.getValue("jd.sftp.host");
        username = ResourceUtil.getValue("jd.sftp.username");
        privateKeyFile = ResourceUtil.getValue("jd.sftp.privateKeyFile");
        port = Integer.valueOf(ResourceUtil.getValue("jd.sftp.port"));
    }

    // 构造函数，根据私钥产生对象
    public SftpUtil() throws JSchException {
        init();
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

        this.sftp.cd(remotePath);
        return this.sftp.get(fileName);
    }

    /**
     * 下载文件到本地文件
     *
     * @param remotePath
     * @param fileName
     * @param localPath
     * @return
     * @throws SftpException
     */
    public void downFile(String remotePath, String fileName, String localPath)
            throws Exception {
        logger.info("downFile|下载文件|remotePath:"+remotePath+ "|fileName:"+
                fileName+ "|localPath:"+ localPath);

        // 创建本地文件
        localPath = getAbsolutePath(localPath);
        File dir = new File(localPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 判断远程文件夹
        remotePath = getAbsolutePath(remotePath);
        if (!existFile(remotePath, fileName)) {
            logger.info("downFile|下载远程文件到本地|文件不存在|path:"+remotePath+"|file:"+fileName+"|return:null");
            return;
        }
        this.sftp.cd(remotePath);
        this.sftp.get(fileName, new FileOutputStream(localPath + fileName));
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
        logger.info("downFile|下载文件|remotePath:"+remotePath+ "|fileName:"+
                fileName+ "|localPath:"+ fileDir + "|localFileName:"+ localFileName);

        InputStream ins = downFile(remotePath,fileName);
        if(null == ins){
            return null;
        }
        byte[] desFile = FileDesUtil.decryptFile(ins);
        InputStream inputStream = new ByteArrayInputStream(desFile);
        FileUploadResp resp = FileUtil.uploadFile(inputStream,localFileName,fileDir);

        return resp.getUrl();
    }


    /**
     * 批量下载文件
     *
     * @param remotePath
     * @param localPath
     * @return
     * @throws JSchException
     * @throws SftpException
     */
    public void batchDownFiles(String remotePath, String localPath)
            throws Exception {
        logger.info("batchDownFiles|批量下载文件|remotePath:"+ remotePath+
                "|localPath:"+localPath);

        if (!existDir(remotePath)) {
            logger.info("batchDownFiles|批量下载文件|文件目录不存在path:"+remotePath);
            return;
        }

        Vector fileList = this.sftp.ls(remotePath);
        if (fileList != null && fileList.size() > 0) {
            Iterator it = fileList.iterator();
            while (it.hasNext()) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) it.next();
                String fileName = entry.getFilename();
                SftpATTRS attrs = entry.getAttrs();
                // 递归下载文件
                if (!attrs.isDir()) {
                    this.downFile(remotePath, fileName, localPath);
                    return;
                } else {
                    remotePath = getAbsolutePath(remotePath) + fileName + "/";
                    localPath = getAbsolutePath(localPath) + fileName + "/";
                    batchDownFiles(remotePath, localPath);
                }
            }
        }

    }

    /**
     * 上传文件
     *
     * @param input
     * @param fileName
     * @param remotePath
     * @throws Exception
     */
    public void uploadFile(InputStream input, String fileName, String remotePath)
            throws Exception {
        logger.info("uploadFile|上传文件|fileName:"+fileName+ "|remotePath:"+
                remotePath);
        try {
            // 判断远程文件夹是否存在
            createRemoteDir(remotePath);
            this.sftp.put(input, fileName);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void uploadFile(String localPath, String fileName, String remotePath)
            throws Exception {
        logger.info("uploadFile|文件上传到远程服务器|localPath:"+ localPath,
                "|fileName:"+ fileName+ "|remotePath"+ remotePath);

        // 获取本地文件
        File file = new File(getAbsolutePath(localPath) + fileName);
        InputStream input = new FileInputStream(file);
        // 上传本地文件
        uploadFile(input, fileName, remotePath);
    }

    /**
     * 上传需要转base64的文件到服务器
     * @param localPath
     * @param fileName
     * @param remotePath
     * @param remoteFileName
     * @throws Exception
     */
    public void uploadNeedDesFile(String localPath,String fileName,String remotePath
            ,String remoteFileName)throws Exception{
        logger.info("uploadFile|文件上传到远程服务器|localPath:"+ localPath,
                "|fileName:"+ fileName+ "|remotePath"+ remotePath);
        // 获取本地文件
        File file = new File(getAbsolutePath(localPath) + fileName);
        InputStream input = new FileInputStream(file);
        byte[] inputBytes = FileDesUtil.encryptFile(input);
        if(null != inputBytes){
            InputStream ins = new ByteArrayInputStream(inputBytes);
            uploadFile(ins,remoteFileName,remotePath);
        }
    }

    /**
     * 上传需要转base64的文件到服务器
     * @param inputStream
     * @param remotePath
     * @param remoteFileName
     * @throws Exception
     */
    public void uploadNeedDesFile(InputStream inputStream,String remotePath
            ,String remoteFileName)throws Exception{
        logger.info("uploadFile|文件上传到远程服务器|remotePath"+ remotePath);

        byte[] inputBytes = FileDesUtil.encryptFile(inputStream);
        if(null != inputBytes){
            InputStream ins = new ByteArrayInputStream(inputBytes);
            uploadFile(ins,remoteFileName,remotePath);
        }
    }

    public void batchUploadFiles(String localPath, String remotePath)
            throws Exception {
        logger.info("uploadFile|批量文件上传到远程服务器|localPath:"+ localPath+
                "|remotePath"+ remotePath);

        // 判定本地文件
        localPath = getAbsolutePath(localPath);
        remotePath = getAbsolutePath(remotePath);
        File file = new File(localPath);
        if (!file.exists()) {
//			logger.info("uploadFile|批量文件上传到远程服务器|本地文件夹不存在");
            return;
        }

        File[] files = file.listFiles();
        InputStream input = null;
        for (File f : files) {
            if (f != null) {
                String fileName = f.getName();

                if (f.isFile()) {
                    input = new FileInputStream(f);
                    uploadFile(input, fileName, remotePath);
                    return;
                } else if (f.isDirectory()) {
                    localPath = localPath + fileName + "/";
                    remotePath = remotePath + fileName + "/";
                    this.batchUploadFiles(localPath, remotePath);
                }
            }
        }
    }

    /**
     * 目录下文件是否存在
     * @param remotePath
     * @param fileName
     * @return
     */
    public boolean existFile(String remotePath, String fileName) {
        try {
            remotePath = getAbsolutePath(remotePath);
            if (!existDir(remotePath)) {
                return false;
            }

            String fullName = remotePath + fileName;
            InputStream in = this.sftp.get(fullName);
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
     * 注意这里采用绝对路径
     *
     * @param remotePath
     * @throws SftpException
     * @throws JSchException
     */
    private void createRemoteDir(String remotePath) throws SftpException,
            JSchException {
        if (this.existDir(remotePath)) {
            this.sftp.cd(remotePath);
            return;
        }

        String[] str = remotePath.trim().split("/");
        StringBuffer filePath = new StringBuffer("/");
        for (String path : str) {
            if (!path.equals("")) {
                filePath.append(path + "/");
                if (this.existDir(filePath.toString())) {
                    this.sftp.cd(filePath.toString());
                } else {
                    this.sftp.mkdir(filePath.toString());
                    this.sftp.cd(filePath.toString());
                }
            }
        }
        this.sftp.cd(remotePath);
    }

    /**
     * 初始化连接
     *
     * @throws JSchException
     */
    private void init() throws JSchException {
        this.sftp = getConnectByPrivateKey();
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
            SftpATTRS attrs = this.sftp.lstat(remotePath);
            return attrs.isDir();
        } catch (Exception e) {
            logger.info("existDir|"+remotePath+"不存在");
            return false;
        }
    }

    /**
     * 根据私钥获取连接
     * @return
     * @throws JSchException
     */
    private ChannelSftp getConnectByPrivateKey() throws JSchException {

        JSch jsch = new JSch();
        jsch.addIdentity(privateKeyFile);
        this.sshSession = jsch.getSession(username, host, port);
        sshSession.setConfig("StrictHostKeyChecking", "no");
        sshSession.connect();
        Channel channel = sshSession.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;
        return sftp;

    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        if (this.sshSession != null && this.sshSession.isConnected()) {
            this.sshSession.disconnect();
        }

        if (this.sftp != null && this.sftp.isConnected()) {
            this.sftp.disconnect();
        }
    }

    /**
     * 删除文件
     * @param directory
     * @param deleteFile
     * @return
     * @throws SftpException
     */
    public boolean deleteSFTP(String directory, String deleteFile)
            throws SftpException {
        this.sftp.cd(directory);
        this.sftp.rm(deleteFile);
        return true;
    }

    public static void main1(String[] args) throws Exception{
        //用户名 由平台提供
        String authName = "crpl_bsb";
        //秘钥路径
        String authKey = "/opt/id_rsa_crpl_bsb";
        //服务器地址 由平台提供
        String conHost = "172.25.61.4";
        //服务器端口号
        Integer sftpPort = 20000;
        //编码
        String CODING = "UTF-8";

        SftpUtil util = null;
        InputStream input = null;
        try {
            util = new SftpUtil();
            //上传文件内容
            String str = "hello world";
            input = new ByteArrayInputStream(str.getBytes(CODING));
            util.uploadFile(input,"helloWorld","/upload/test/");
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(util != null){
                util.disconnect();
            }
            if(input != null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
    	File file = new File("D:\\crpl_wandaph\\20180808\\reconciliation\\2.txt");
    	String content = FileUtils.readFileToString(file, "utf-8");
    	InputStream ins = new ByteArrayInputStream(content.getBytes());
        if(null == ins){
            return;
        }
        byte[] desFile = FileDesUtil.decryptFile(ins);
        InputStream inputStream = new ByteArrayInputStream(desFile);
        String result = IOUtils.toString(inputStream, "utf-8");
        System.out.println(result);
	}
}
