package com.wandaph.filetarnsfer.utils;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


/**
 * create at 2018/5/8 by yexz
 * 用于实现http协议方式请求远程服务
 * */
@SuppressWarnings({ "deprecation", "unused" })
public class HttpCilentUtil {
	//日志
	private static Logger logger = LoggerFactory.getLogger(HttpCilentUtil.class);
	//链接超时 10000毫秒
	public static final int CONNTIMEOUT = 10000;
	//读取超时 10000毫秒
	public static final int READTIMEOUT = 10000;


	/**
	 * create at 2018/5/8 by yexz
	 * 用于处理get方式的请求。
	 * @param url 带参数的url链接
	 * @return String 如果请求失败则返回请求状态，如果请求成功则返回响应的内容,状态码20180508表示没有进行过请求
	 * */
	public static String doGet(String url) { 
		//请求实例
		CloseableHttpClient client = null;
		//响应实例
		CloseableHttpResponse response=null;
		//请求结果码，如果最后返回20180508则表示没有执行
		int status = 20180508;
		//返回值
		String result;
		try {  
			//请求实例
			client = HttpClients.createDefault(); 
			//发送get请求  
			HttpGet request = new HttpGet(url);  
			response = client.execute(request); 
			//获取响应状态
			status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				//请求成功  
				InputStream inputStream = response.getEntity().getContent();
				String strResult =inputStream2String(inputStream,"UTF-8");
				//返回赋值
				result=strResult;
			}else {
				//请求失败
				result="get请求返回:"+status+"("+url+")";
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			//结束后关闭response
			if (response != null) {  
				try {  
					response.close();  
				} catch (IOException e) {  
					e.printStackTrace();  
				}  
			}  
			//关闭httpclient
			try {  
				client.close();  
			} catch (IOException e) {  
				e.printStackTrace();  
			}  
		} 

		return null;
	}
	/**
	 * create at 2018/5/8 by yexz
	 * 用于处理post请求方式的请求
	 * 适用于参数为json格式字符串的场景
	 * @param  url 请求url链接
	 * @param  param 请求参数
	 * @param  charset 字符集
	 * @return String 如果请求失败则返回请求状态，如果请求成功则返回响应的内容，状态码20180508表示没有进行过请求
	 * */
	public static String doPost(String url,String param,String charset) {
		//打印入参
		logger.info("发送Post请求:"+"("+url+")"+"{"+param+"}");
		//创建httpPost实例
		CloseableHttpClient httpclient = HttpClients.createDefault();  
		HttpPost httpPost = new HttpPost(url); 
		//设置头信息，后续可以作为入参传进来
		//设置指定的字符集为utf-8 
		StringEntity entity = new StringEntity(param, charset);  
		//设入参数
		entity.setContentEncoding(charset);
		entity.setContentType("application/json");
		httpPost.setEntity(entity);   
		//定义响应实例
		CloseableHttpResponse response = null;  
		//局部的state状态，最后返回，如果返回20180508则表示没有执行http请求
		int state=20180508;
		//返回值
		String result="";
		try {
			//发送请求，得到回参
			response = httpclient.execute(httpPost);  
			//获取响应状态
			state = response.getStatusLine().getStatusCode();
			//根据返回状态进行相关处理
			if (state == HttpStatus.SC_OK) {
				//成功：返回json字符串
				InputStream inputStream = response.getEntity().getContent();
				String strResult =inputStream2String(inputStream,charset);
				logger.info("Post请求结束:"+strResult);
				//return jsonString;  
				//返回值赋值

				result = "post请求返回:"+state+"("+url+")"+"{"+strResult+"}";

			}  
			else{  
				//失败：打印错误状态码
				logger.error("post请求返回:"+state+"("+url+")");  
				//返回值赋值
				result="post请求返回:"+state+"("+url+")";
			}  
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return "调用接口出错："+e.getCause().getLocalizedMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return "调用接口出错："+e.getCause().getLocalizedMessage();
		}finally {
			//结束后关闭response
			if (response != null) {  
				try {  
					response.close();  
				} catch (IOException e) {  
					e.printStackTrace();  
				}  
			}  
			//关闭httpclient
			try {  
				httpclient.close();  
			} catch (IOException e) {  
				e.printStackTrace();  
			}  
		}
		logger.info(result);
		return result;
	}
	/**
	 * 字节流转为String
	 * */
	public static   String   inputStream2String   (InputStream   inputStream,String charset)   throws   IOException   { 
		final int bufferSize = 1024;
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(inputStream, charset);
		for (; ; ) {
		    int rsz = in.read(buffer, 0, buffer.length);
		    if (rsz < 0)
		        break;
		    out.append(buffer, 0, rsz);
		}
		return out.toString();
	} 
}
