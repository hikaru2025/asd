package com.wandaph.filetarnsfer.utils;

import com.hsjry.lang.log.Log;
import com.hsjry.lang.log.TenantLog;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.security.SecureRandom;

public class FileDesUtil {
    private static final Log logger = TenantLog.get(FileDesUtil.class);
    private static String DES = "DES";
    private static String key;

    static {
        key = ResourceUtil.getValue("jd.sftp.des.key");
    }

    public static void downLoadFile(InputStream in,String targetFilePath) {
        //读取图片字节数组
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(in);
            byte[] decrypt = decrypt(bytes, format(key));
            in.close();
            for(int i=0;i<bytes.length;++i)
            {
                if(bytes[i]<0)
                {//调整异常数据
                    bytes[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = targetFilePath;//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(decrypt);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }

    public static void uploadFile(InputStream inputStream,String remotePath) throws Exception{
        String encrypt = null;
        FileOutputStream fop = null;
        File file;
        try {
            byte[] getData = readInputStream(inputStream);
            encrypt = encrypt(getData, key);
            file = new File(remotePath);
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = encrypt.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] encryptFile(InputStream inputStream)throws Exception{
        String encrypt = null;
        try {
            byte[] getData = readInputStream(inputStream);
            encrypt = encrypt(getData, key);
            // get the content in bytes
            byte[] contentInBytes = encrypt.getBytes();
            return contentInBytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptFile(InputStream inputStream)throws Exception{
        //读取图片字节数组
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(inputStream);
            byte[] decrypt = decrypt(bytes, format(key));
            inputStream.close();
            for(int i=0;i<bytes.length;++i)
            {
                if(bytes[i]<0)
                {//调整异常数据
                    bytes[i]+=256;
                }
            }
           return decrypt;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }

        bos.close();
        return bos.toByteArray();
    }


    /**
     * Description 根据键值进行加密
     *
     * @return
     */
    public static String encrypt(String data, String key) throws Exception {
        byte[] bt = encrypt(data.getBytes("UTF-8"), format(key));
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }

    /**
     * Description 根据键值进行加密
     *
     * @return
     */
    public static String encrypt(byte[] bytes, String key) throws Exception {
        logger.info("待加密的key为-->{}",key);
        byte[] bt = encrypt(bytes, format(key));
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }

    /**
     * Description 根据键值进行解密
     *
     * @return
     */
    public static String decrypt(String data, String key) throws Exception {
        if (data == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf = decoder.decodeBuffer(data);
        byte[] bt = decrypt(buf, format(key));
        return new String(bt, "UTF-8");
    }

    /**
     * Description 根据键值进行加密
     *
     * @return
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }

    /**
     * Description 根据键值进行解密
     *
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }

    /**
     * 将字符串格式化为>=8字节长度的字节数组，不足补0
     *
     * @return
     */
    public static byte[] format(String str) throws Exception {
        byte[] bt = str.getBytes("UTF-8");
        if (bt.length < 8) {
            byte[] btnew = new byte[8];
            for (int i = 0; i < bt.length; i++) {
                btnew[i] = bt[i];
            }
            for (int i = bt.length; i < 8; i++) {
                btnew[i] = 0;
            }
            return btnew;
        }
        return bt;
    }
}

