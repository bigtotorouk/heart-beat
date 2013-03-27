package com.slider.cn.app.httpurlconnectionnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slider.cn.app.exception.NetException;

import android.content.Context;
import android.util.Log;
/**
 * 这里类主要是用来登录注册用的。其它的网络请求都放在HttpJsonUtil里面了.这个类有个重要的功能就是
 * 获取cookie并保存，获取crsf-token并保存，为以后的网络请求做好准备
 * 这里的网络请求数据都是 application/x-www-form-urlencoded  . 有两个网络请求方法：get，post
 * @author slider
 */
public class HttpUtil extends CookieImpl {
	private static final String BOUNDARY = "---------7d4a6d158c9";	//分割符号
	private static String session = "gqnA5Fx2UrtbT6iDKX4M0sJPzcWp8qiF";
	private static String cookie="";
	private String status = "null";
	private static HttpUtil instance = null;
	
	/* single pattern */
	private HttpUtil(){	}
	public synchronized static HttpUtil getInstance(){
		if(instance==null){
			instance = new HttpUtil();
		}
		return instance;
	}
	/**
	 * 返回session存在“csrftoken“字段中,下次调用带上“X-CSRFTOKEN“，并赋上该值
	 * @param url
	 * @param params
	 * @return
	 * @throws NetException 
	 */
	public String newSession(String url,Context context,HashMap<String, String> params) throws NetException {
		URL urlGet;
		HttpURLConnection connection;
		
		if(params!=null)
			url = url +"?"+Util.encodeParams(params);
		try {
			urlGet = new URL(url);
		
		connection = (HttpURLConnection) urlGet.openConnection();
		connection.setConnectTimeout(5000);// （单位：毫秒）jdk
		connection.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
		if(String.valueOf(connection.getResponseCode()).startsWith("2")){
			cookie = connection.getHeaderField("Set-Cookie"); 
			System.out.println("Cookie : "+cookie);
			session = cookie.substring(0, cookie.indexOf(";"));
	        session = session.substring(session.indexOf("=")+1);	
	        updateCsrftoken(context, session);
		}
		} catch (MalformedURLException e) {
			throw new NetException(NetException.ERROR_URL);
		} catch (IOException e) {
			throw new NetException(NetException.ERROR_SERVER);
		}
		return session;
	}
	
	public HttpResult connectGet(String url,Context context,HashMap<String, String> params) throws NetException{
		HttpResult result;
		URL urlGet;
		HttpURLConnection connection;

		if (params != null)
			url = url + "?" + Util.encodeParams(params);
		try {
			urlGet = new URL(url);
		
			connection = (HttpURLConnection) urlGet.openConnection();
			connection.setConnectTimeout(5000);// （单位：毫秒）jdk
			connection.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			// setCookie(context, connection);
			connection.setRequestProperty(
							"Cookie",
							"sessionid=c413a34a20c255813f553dd56de9a6d3; expires=Thu, 09-Aug-2012 02:24:19 GMT; httponly; Max-Age=1209600; Path=/");// 传递cookie值，不传递要错误
	
			
			result = doResponse(connection);
		} catch (MalformedURLException e) {
			throw new NetException(NetException.ERROR_URL);
		} catch (IOException e) {
			throw new NetException(NetException.ERROR_SERVER);
		}
		return result;

	}
	/**
	 * 这里好像只有登录(login)或注册时候调用
	 * @param url
	 * @param params
	 * @return
	 * @throws NetException 
	 * @throws Exception 
	 */
	public HttpResult connectPost(String url,Context context,HashMap<String, String> params,boolean requireLogin) throws NetException{
		System.out.println("HttpUtil-->connecPost url "+url+" body"+params);
		HttpResult response = new HttpResult();
		URL urlPost;
		HttpURLConnection connection;

		try {
			urlPost = new URL(url);
		
			connection = (HttpURLConnection) urlPost.openConnection();
	
			// 设定请求的方法为"POST"，默认是GET
			connection.setRequestMethod("POST");
			connection.setDoOutput(true); // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,
											// 默认情况下是false;
			// connection.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true;
			// ---- 這個实际上沒有必要设置
			connection.setUseCaches(false); // Post 请求不能使用缓存，因为要保证post数据安全
			connection.setConnectTimeout(5000);// （单位：毫秒）jdk
			connection.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
	
			
			if(!requireLogin){
				connection.addRequestProperty("X-CSRFTOKEN", session);// session是post向服务器请求回来的csrftoken的值
				connection.setRequestProperty("Cookie", cookie);// 传递cookie值，不传递要错误
			}else{
				setCsrftoken(context, connection);
				setCookie(context, connection);
			}
			
	
			// !!!!!!!!!!!!!!
			//writeRequestHeaders(connection);
			System.out.println("params: " + Util.encodeParams(params));
			// !!!!!!!!!!!!!!
			connection.connect();
	
			// 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，
			// 所以在开发中不调用上述的connect()也可以)。
	
			OutputStream os = connection.getOutputStream();
			os.write(Util.encodeParams(params).getBytes()); // 这里数据的组织形式在你
			os.flush();
			os.close();
	
			// 这里为止，所有的http请求设置，包括数据都应经完毕，下面一布进行从httpUrlConnection读数据
			// HttpURLConnection的connect()函数，实际上只是建立了一个与服务器的tcp连接，并没有实际发送http请求。
			// 无论是post还是get，http请求实际上直到HttpURLConnection的getInputStream()这个函数里面才正式发送出去。
			System.out.println("HttpUtil-->connectPost responseCode "+connection.getResponseCode());
			int responseCode = connection.getResponseCode();
			if (String.valueOf(responseCode).startsWith("2")) {
				response.setReponseCode(responseCode);
				if(!requireLogin){
					updateCookie(context, connection.getHeaderField("Set-Cookie"));
					Log.d("HttpUtil ", "update cookie : "+connection.getHeaderField("Set-Cookie"));
				}
				response.setResult(read(connection.getInputStream()));
			} else {
				response.setReponseCode(responseCode);
				response.setResult(read(connection.getInputStream()));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new NetException(NetException.ERROR_URL);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetException(NetException.ERROR_SERVER);
		}

		//writeResponseHeaders(connection);

		return response;
	}
	
	
	/**
	 * 这个接口暂时关闭，该功能转移到HttpJsonUtil类中了
	 * post 上传文件(包括多个文件)
	 * @param url
	 * @param params 传递的是参数
	 * @param fileparams 文件数据
	 * @param contentType 文件类型
	 * @return
	 */
	public HttpResult connectPost(String url,HashMap<String, String> params,List<FileEntity> fileparams){
		HttpResult result = null;
		URL urlPost;
		HttpURLConnection connection;
		try{
			urlPost = new URL(url);
			connection = (HttpURLConnection) urlPost.openConnection();
			
			// 设定请求的方法为"POST"，默认是GET   
			connection.setRequestMethod("POST");  
			connection.setDoOutput(true);  //设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;   
			connection.setDoInput(true);  // 设置是否从httpUrlConnection读入，默认情况下是true;   ---- 這個实际上沒有必要设置
			connection.setUseCaches(false); //Post 请求不能使用缓存，因为要保证post数据安全
			connection.setConnectTimeout(5000);// （单位：毫秒）jdk
			connection.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			connection.setRequestProperty("connection", "keep-alive");//发送大文件是需要保持链接
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			connection.setRequestProperty("Cookie", "sessionid=c413a34a20c255813f553dd56de9a6d3; expires=Thu, 09-Aug-2012 02:24:19 GMT; httponly; Max-Age=1209600; Path=/");//传递cookie值，不传递要错误
			connection.connect(); 
			
		    // 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，   
		    // 所以在开发中不调用上述的connect()也可以)。 
		    OutputStream os = connection.getOutputStream();  
		    writeFilesParams(os, fileparams);
		    os.flush();
		    os.close();
		    
		    //这里为止，所有的http请求设置，包括数据都应经完毕，下面一布进行从httpUrlConnection读数据
		    //HttpURLConnection的connect()函数，实际上只是建立了一个与服务器的tcp连接，并没有实际发送http请求。
		    //无论是post还是get，http请求实际上直到HttpURLConnection的getInputStream()这个函数里面才正式发送出去。	
		    result = doResponse(connection);
			
			
		}catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 读取从服务器返回的数据
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private  String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1024);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }
	
	
	/**
	 * 把文件写书outputStream流里面
	 * @param os
	 * @param fileparams 文件数据
	 * @param contentType 文件类型
	 */
	private void writeFilesParams(OutputStream os,List<FileEntity> fileparams){
		for (FileEntity fileEntity:fileparams) {  
            String name = fileEntity.getName();  
            File value = fileEntity.getValue(); 
            StringBuilder sb = new StringBuilder();
            String dataStart = sb.append("--" + BOUNDARY + "\r\n")
            		.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + value.getName() + "\"\r\n")
            		.append("Content-Type: " + parseContentType(value) + "\r\n\r\n").toString();
            String dataEnd = "\r\n--" + BOUNDARY + "--\r\n";

			try {
				os.write(dataStart.getBytes());
				FileInputStream fis;
				fis = new FileInputStream(fileEntity.getValue());
				int rn2;  
	            byte[] buf = new byte[1024];  
	            while((rn2=fis.read(buf, 0, 1024))>0){     
	                os.write(buf,0,rn2);   
	            }
	            os.write(dataEnd.getBytes());
	            fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}    
        }  
	}
	
	private String parseContentType(File file) {
		String contentType = "image/jpg";
		String fileName = file.getName();
		fileName = fileName.toLowerCase();
		if (fileName.endsWith(".jpg"))
			contentType = "image/jpg";
		else if (fileName.endsWith(".png"))
			contentType = "image/png";
		else if (fileName.endsWith(".jpeg"))
			contentType = "image/jpeg";
		else if (fileName.endsWith(".gif"))
			contentType = "image/gif";
		else if (fileName.endsWith(".bmp"))
			contentType = "image/bmp";
		else
			throw new RuntimeException("Unsupported file types'" + fileName + "'(With or without file extension)");
		return contentType;
	}
	/**
	 * deal with http-status-code :
	 * 2** read the connection.getInputStream() or read the connection.getErrorStream()
	 * @param connection
	 * @return body or error info
	 * @throws IOException 
	 */
	private HttpResult doResponse(HttpURLConnection connection) throws IOException{
		HttpResult result = new HttpResult();
		int responseCode = connection.getResponseCode();
		System.out.println("response code is :"+responseCode);
		InputStream is = null;
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
		case HttpURLConnection.HTTP_CREATED:
		case HttpURLConnection.HTTP_ACCEPTED:
		case HttpURLConnection.HTTP_NOT_AUTHORITATIVE:
		case HttpURLConnection.HTTP_NO_CONTENT:
		case HttpURLConnection.HTTP_RESET:
		case HttpURLConnection.HTTP_PARTIAL:
			is = connection.getInputStream();
			break;
		default:
			is = connection.getErrorStream();
			break;
		}
		result.setReponseCode(responseCode);
		result.setResult(read(is));
		
		return result;
		
	}
	/**
	 * it must be called before connecting, or throw IllegalStateException
	 * @param connection
	 */
	private void writeRequestHeaders(HttpURLConnection connection){
		System.out.println(" ----------- write request header --------- ");
		Map<String, List<String>> data = connection.getRequestProperties();
		for(Map.Entry<String, List<String>> entry : data.entrySet()) { 
			String key = entry.getKey(); 
            for(String value : entry.getValue()){ 
            	System.out.println(key + ": " + value); 
            } 
		} 
	}
	/**
	 * print response-header data-entry
	 * @param connection
	 */
	private void writeResponseHeaders(HttpURLConnection connection){
    	System.out.println(" ----------- write response header --------- ");
    	
    	Map<String, List<String>> headers = connection.getHeaderFields(); 
		for(Map.Entry<String, List<String>> entry : headers.entrySet()) { 
			String key = entry.getKey(); 
            for(String value : entry.getValue()){ 
            	System.out.println(key + ": " + value); 
            } 
		} 
	}
	
	public static void main(String[] args) {
		/*String server = "http://192.168.1.66/api/v1";
		String url = "http://www.baidu.com";
		//System.out.println(HttpUtil.getInstance().connectGet(url));
		String beatsUrl = server + "/beat/1?format=json";
		new AsyncRunner().run(beatsUrl, null, "GET", null,null);
		System.out.println();*/
		//getBeat();
	}
	public String getStatus(){
		return this.status;
	}
}
