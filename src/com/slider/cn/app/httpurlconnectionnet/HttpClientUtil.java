package com.slider.cn.app.httpurlconnectionnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import com.slider.cn.app.exception.NetException;
import android.content.Context;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;


/**
 * 这个类主要类实现patch的请求方法和https 的请求
 * @author slider
 *
 */
public class HttpClientUtil extends CookieImpl{
	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";
	public static final String HTTP_PATCH = "PATCH";
	
	private final String CONTENT_TYPE = "Content-type";
	public final static String CONTENT_URLENCODE = "application/x-www-form-urlencoded";
	public final static String CONTENT_JSON = "application/json";
	
	private static HttpClientUtil httpclient;
	private HttpClientUtil(){ }
	public synchronized static HttpClientUtil getInstance(){
		if(httpclient==null)
			httpclient = new HttpClientUtil();
		return httpclient;
	}
	
	public String connectGet(String url) throws ClientProtocolException, IOException{
		String result = "";
		//HttpClient client = new DefaultHttpClient();
		HttpClient client = getNewHttpsClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = client.execute(httpGet);
		result = read(response.getEntity().getContent());
		return result;
	}
	public String connectPatch(String url,Context context,String json) throws NetException{
		System.out.println("connectPatch json "+json);
		String result = "";
		PostMethod patch = new PostMethod(url){
			@Override
			public String getName() {
				return "PATCH";
			}
		};
		patch.setRequestHeader("Accept", "application/json");
		patch.setRequestHeader("Content-Type", "application/json");//if setRequestEntity里面StringRequestEntity 有设置"application/json" 这里就可以去掉
		patch.setRequestHeader("X-CSRFTOKEN", getValue(context, CSRFTOKEN));
		patch.setRequestHeader("Cookie", getValue(context, COOKIE));
		
		try {
			patch.setRequestEntity(new StringRequestEntity(json,"application/json", "UTF-8"));
			org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);//设置连接超时时间(单位毫秒) 
			client.getHttpConnectionManager().getParams().setSoTimeout(5000);//设置读数据超时时间(单位毫秒) 
			int code = client.executeMethod(patch);
			if(code>299){
				System.out.println("HttpClientUtil connectPatch code > 299");
				
			}else{
				
			}
			result = read(patch.getResponseBodyAsStream());
		} catch (UnsupportedEncodingException e) {
			throw new NetException("not support this format");
		}catch (HttpException e) {
			throw new NetException(NetException.ERROR_SERVER);
		} catch (IOException e) {
			throw new NetException(NetException.ERROR_SERVER);
		}	
			
		return result;
	}
	public HttpResult connectPost(String url,Context context,String contentType, String entityString) throws NetException {
		HttpResult result = null;

		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPostRequest = new HttpPost(url);

		httpPostRequest.setHeader(CONTENT_TYPE, contentType);
		httpPostRequest.setHeader("Cookie", getValue(context, COOKIE));
		httpPostRequest.setHeader("X-CSRFTOKEN", getValue(context, CSRFTOKEN));

		try {
			StringEntity se = new StringEntity(entityString);
			httpPostRequest.setEntity(se);
			HttpResponse response = (HttpResponse) httpclient
					.execute(httpPostRequest);
			// Get hold of the response entity (-> the data):
			result = doResponse(response);
		} catch (UnsupportedEncodingException e) {
			throw new NetException(NetException.ERROR_URL);
		} catch (IOException e) {
			throw new NetException(NetException.ERROR_SERVER);
		}

		return result;
	}
	private HttpResult doResponse(HttpResponse response) throws NetException{
		HttpResult result = new HttpResult();
		result.setReponseCode(response.getStatusLine().getStatusCode());
		try {
			result.setResult(read(response.getEntity().getContent()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new NetException(NetException.ERROR_SERVER);
		}
		return result;
	}

	private String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1024);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}
	
	
	public HttpClient getNewHttpsClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        //HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
}
class MySSLSocketFactory extends SSLSocketFactory {
    SSLContext sslContext = SSLContext.getInstance("TLS");

    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);

        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sslContext.init(null, new TrustManager[] { tm }, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}
