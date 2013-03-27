package com.heart.beat.cn.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
	private static final String BOUNDARY = "---------7d4a6d158c9";	//分割符号
	private static String session = "gqnA5Fx2UrtbT6iDKX4M0sJPzcWp8qiF";
	private static String cookie="";
	private String status = "null";
	private static HttpUtil instance = null;
	
	
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
	 */
	public String getSession(String url,HashMap<String, String> params){
		URL urlGet;
		HttpURLConnection connection;
		try{
			if(params!=null)
				url = url +"?"+Util.encodeParams(params);
			urlGet = new URL(url);
			connection = (HttpURLConnection) urlGet.openConnection();
			cookie = connection.getHeaderField("Set-Cookie");  
            session = cookie.substring(0, cookie.indexOf(";"));
            session = session.substring(session.indexOf("=")+1);
			System.out.println("Cookie : "+cookie);
		}catch(IOException e){
			e.printStackTrace();
		}
		return session;
	}
	
	public String connectGet(String url,HashMap<String, String> params){
		String response = "";
		URL urlGet;
		HttpURLConnection connection;
		try{
			if(params!=null)
				url = url +"?"+Util.encodeParams(params);
			urlGet = new URL(url);
			connection = (HttpURLConnection) urlGet.openConnection();
			
			InputStream is = null;
			int responseCode = connection.getResponseCode();
			if (responseCode == 200){
                is = connection.getInputStream();
            }else{
            	is = connection.getErrorStream();
            }
			response = read(is);
			
		}catch(IOException e){
			e.printStackTrace();
		}
		return response;

	}
	public String connectPost(String url,HashMap<String, String> params,boolean header){
		String response = "";
		URL urlPost;
		HttpURLConnection connection;
		try{
			urlPost = new URL(url);
			connection = (HttpURLConnection) urlPost.openConnection();
			
			// 设定请求的方法为"POST"，默认是GET   
			connection.setRequestMethod("POST");  
			connection.setDoOutput(true);  //设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;   
			//connection.setDoInput(true);  // 设置是否从httpUrlConnection读入，默认情况下是true;   ---- 這個实际上沒有必要设置
			connection.setUseCaches(false); //Post 请求不能使用缓存，因为要保证post数据安全
			connection.setConnectTimeout(5000);// （单位：毫秒）jdk
			connection.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			connection.setRequestProperty("Cache-Control", "no-cache");
			connection.setRequestProperty("Pragma", "no-cache");
			connection.addRequestProperty("X-CSRFTOKEN", session);//session是post向服务器请求回来的csrftoken的值
			connection.setRequestProperty("Cookie", cookie);//传递cookie值，不传递要错误
			
			//!!!!!!!!!!!!!!
			writeRequestHeaders(connection);
			System.out.println("params: "+ Util.encodeParams(params));
			//!!!!!!!!!!!!!!
			connection.connect(); 

		    // 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，   
		    // 所以在开发中不调用上述的connect()也可以)。   

		    OutputStream os = connection.getOutputStream();  
			os.write(Util.encodeParams(params).getBytes()); // 这里数据的组织形式在你
		    os.flush();
		    os.close();
		    
		   
		    //这里为止，所有的http请求设置，包括数据都应经完毕，下面一布进行从httpUrlConnection读数据
		    //HttpURLConnection的connect()函数，实际上只是建立了一个与服务器的tcp连接，并没有实际发送http请求。
		    //无论是post还是get，http请求实际上直到HttpURLConnection的getInputStream()这个函数里面才正式发送出去。
		    
		    response = doResponse(connection);
		    	
		}catch(IOException e){
			e.printStackTrace();
		}
		return response;
	}
	
	public String openUrl(String url,HashMap<String, String> params,String method,List<FileEntity> file,boolean header){
		String response = "";
		if(method.equals("GET")){
			response = connectGet(url,params);
		}else if(method.equals("POST")){
			if(file==null){
				response = connectPost(url, params,header);
			}else{
				response = connectPost(url, params, file);
			}
		}
		return response;
	}
	
	/**
	 * post 上传文件(包括多个文件)
	 * @param url
	 * @param params 传递的是参数
	 * @param fileparams 文件数据
	 * @param contentType 文件类型
	 * @return
	 */
	public String connectPost(String url,HashMap<String, String> params,List<FileEntity> fileparams){
		String response = "";
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
			connection.setRequestProperty("connection", "keep-alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
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
		    InputStream is = connection.getInputStream();		
			response = read(is);
			
		}catch(IOException e){
			e.printStackTrace();
		}
		return response;
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
			throw new RuntimeException("不支持的文件类型'" + fileName + "'(或没有文件扩展名)");
		return contentType;
	}
	/**
	 * deal with http-status-code :
	 * 2** read the connection.getInputStream() or read the connection.getErrorStream()
	 * @param connection
	 * @return body or error info
	 * @throws IOException 
	 */
	private String doResponse(HttpURLConnection connection) throws IOException{
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
			status = "200";
			break;
		default:
			is = connection.getErrorStream();
			status = responseCode+"";
			break;
		}
		
		return read(is);
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
