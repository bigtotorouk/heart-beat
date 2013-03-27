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
import java.util.List;
import java.util.Map;

import com.slider.cn.app.exception.NetException;

import android.content.Context;
import android.widget.Toast;

public class HttpJsonUtil extends CookieImpl{
	private static final String BOUNDARY = "---------7d4a6d158c9";	//分割符号
	public static final String HTTP_POST = "POST";
	//public static final String HTTP_PATCH = "PATCH"; /* httpUrlConnection 没有patch这个方法。 */
		
	/* single pattern */
	private static HttpJsonUtil instance;
	private HttpJsonUtil(){}
	public synchronized static HttpJsonUtil getInstance(){
		if(instance==null){
			instance = new HttpJsonUtil();
		}
		return instance;
	}
	/**
	 * define http-get request
	 * @param url
	 * @param context
	 * @param requireLogin
	 * @return
	 * @throws NetException
	 */
	public HttpResult connectGet(String url,Context context,boolean requireLogin) throws NetException{
		System.out.println("HttpJsonUtil-->connectGet :url"+url);
		HttpResult response;
		URL urlGet;
		HttpURLConnection connection;
		
		try {
			urlGet = new URL(url);
			connection = (HttpURLConnection) urlGet.openConnection();
			connection.setConnectTimeout(5000);// （单位：毫秒）jdk
			connection.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
			if(requireLogin){
				/* set cookie ,set csrftoken */
				setCsrftoken(context, connection);
				setCookie(context, connection);
			}
				
			response = doResponse(connection);
		} catch (MalformedURLException e) {
			throw new NetException("url error");
		} catch (IOException e) {
			throw new NetException(NetException.ERROR_SERVER);
		}

		return response;

	}
	/**
	 * 
	 * @param url
	 * @param context
	 * @param json
	 * @param method :POST commit the object; PATCH modify the object
	 * @return
	 * @throws NetException 
	 */
	public HttpResult connectPost(String url,Context context,String json) throws NetException{
		System.out.println("HttpJsonUtil-->connectPost :url"+url+" json is"+json);
		HttpResult response = null;
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
			connection.setRequestProperty("Content-Type", "application/json"); //传递的json字符串
			
			/* set cookie ,set csrftoken */
			setCsrftoken(context, connection);
			setCookie(context, connection);
			//!!!!!!!!!!!!!!
			writeRequestHeaders(connection);
			System.out.println("json: "+ json);
			//!!!!!!!!!!!!!!
			connection.connect(); 
			
		    // 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，   
		    // 所以在开发中不调用上述的connect()也可以)。   

		    OutputStream os = connection.getOutputStream();  
			os.write(json.getBytes()); // 这里数据的组织形式在你
		    os.flush();
		    os.close();
		    //这里为止，所有的http请求设置，包括数据都应经完毕，下面一布进行从httpUrlConnection读数据
		    //HttpURLConnection的connect()函数，实际上只是建立了一个与服务器的tcp连接，并没有实际发送http请求。
		    //无论是post还是get，http请求实际上直到HttpURLConnection的getInputStream()这个函数里面才正式发送出去。
		    
		    response = doResponse(connection);
		    	
		}catch(IOException e){
			throw new NetException(NetException.ERROR_SERVER);
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
	 * @throws NetException 
	 */
	public HttpResult connectPost(String url,Context context,List<FileEntity> fileparams) throws NetException{
		HttpResult response = null;
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
			/* set cookie, set csrftoken */
			setCsrftoken(context, connection);
			setCookie(context, connection);
			//connection.setRequestProperty("Cookie", "sessionid=32bcad14897cf838cdd1dbc406bcfcea; expires=Mon, 20-Aug-2012 03:49:02 GMT; httponly; Max-Age=1209600; Path=/");//传递cookie值，不传递要错误
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
			response = doResponse(connection);
			
			
		}catch(IOException e){
			throw new NetException(NetException.ERROR_SERVER);
		}
		return response;
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
	 * @return HttpResult
	 * @throws IOException 
	 */
	private HttpResult doResponse(HttpURLConnection connection) throws IOException{
		HttpResult response = new HttpResult(); 
		int responseCode = connection.getResponseCode();
		response.setReponseCode(responseCode);
		System.out.println("response code is :"+responseCode);
		InputStream is = null;
		switch (responseCode) {
		case HttpURLConnection.HTTP_CREATED:
			/* Response Header 的 Location 字段为新增资源的 URI */
			Map<String, List<String>> headers = connection.getHeaderFields(); 
			List<String> urls = headers.get(HttpResult.LOCATION_HEADER);
			if(urls!=null&&urls.size()>0){
				response.setResult(urls.get(0));
				return response;
			}
		case HttpURLConnection.HTTP_OK:
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
		
		response.setResult(read(is));
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
	
	
	/*private void writeResponseHeaders(HttpURLConnection connection){
    	System.out.println(" ----------- write response header --------- ");
    	
    	Map<String, List<String>> headers = connection.getHeaderFields(); 
		for(Map.Entry<String, List<String>> entry : headers.entrySet()) { 
			String key = entry.getKey(); 
            for(String value : entry.getValue()){ 
            	System.out.println(key + ": " + value); 
            } 
		} 
	}*/
	
}
