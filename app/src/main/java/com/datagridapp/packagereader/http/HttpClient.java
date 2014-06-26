package com.datagridapp.packagereader.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.os.Environment;

public class HttpClient {
	
	public static String FILE_PATH = Environment.getExternalStorageDirectory() + "/download/";
	
	public static String FILE_APK = "BPApk.apk";

	private ArrayList<NameValuePair> mHeaders;

	private String mJsonBody;
	private String mMessage;

	private ArrayList<NameValuePair> mParams;
	private String mResponse;
	private int mResponseCode;

	private String mUrl;

	public HttpClient(String url) {
		this.mUrl = url;
		mParams = new ArrayList<NameValuePair>();
		mHeaders = new ArrayList<NameValuePair>();
	}

	public void addHeader(String name, String value) {
		mHeaders.add(new BasicNameValuePair(name, value));
	}

	public void addParam(String name, String value) {
		mParams.add(new BasicNameValuePair(name, value));
	}

	public void execute(HttpMethod method) throws Exception {
		switch (method) {
			case GET: {
				HttpGet request = new HttpGet(mUrl + addGetParams());
				request = (HttpGet) addHeaderParams(request);
				executeRequest(request, mUrl);
				break;
			}
			case POST: {
				HttpPost request = new HttpPost(mUrl);
				request = (HttpPost) addHeaderParams(request);
				request = (HttpPost) addBodyParams(request);
				executeRequest(request, mUrl);
				break;
			}
			case PUT: {
				HttpPut request = new HttpPut(mUrl);
				request = (HttpPut) addHeaderParams(request);
				request = (HttpPut) addBodyParams(request);
				executeRequest(request, mUrl);
				break;
			}
			case DELETE: {
				HttpDelete request = new HttpDelete(mUrl);
				request = (HttpDelete) addHeaderParams(request);
				executeRequest(request, mUrl);
				break;
			}
			case GET_FILE: {
				HttpGet request = new HttpGet(mUrl + addGetParams());
				request = (HttpGet) addHeaderParams(request);
				executeFileRequest(request, mUrl);
				
			}
		}
	}

	private HttpUriRequest addHeaderParams(HttpUriRequest request) throws Exception {
		for (NameValuePair h : mHeaders) {
			request.addHeader(h.getName(), h.getValue());
		}
		return request;
	}

	private HttpUriRequest addBodyParams(HttpUriRequest request) throws Exception {
		if (mJsonBody != null) {
			request.addHeader("Content-Type", "application/json");
			if (request instanceof HttpPost) {
				((HttpPost) request).setEntity(new StringEntity(mJsonBody, "UTF-8"));
			}
			else if (request instanceof HttpPut) {
				((HttpPut) request).setEntity(new StringEntity(mJsonBody, "UTF-8"));
			}
		} else if (!mParams.isEmpty()) {
			if (request instanceof HttpPost) {
				((HttpPost) request).setEntity(new UrlEncodedFormEntity(mParams, HTTP.UTF_8));
			}
			else if (request instanceof HttpPut) {
				((HttpPut) request).setEntity(new UrlEncodedFormEntity(mParams, HTTP.UTF_8));
			}
		}
		return request;
	}

	private String addGetParams() throws Exception {
		StringBuffer combinedParams = new StringBuffer();
		if (!mParams.isEmpty()) {
			combinedParams.append("?");
			for (NameValuePair p : mParams) {
				combinedParams.append((combinedParams.length() > 1 ? "&" : "") + p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8"));
			}
		}
		return combinedParams.toString();
	}

	public String getErrorMessage() {
		return mMessage;
	}

	public String getResponse() {
		return mResponse;
	}

	public int getResponseCode() {
		return mResponseCode;
	}

	public void setJSONString(String data) {
		mJsonBody = data;
	}

	private void executeRequest(HttpUriRequest request, String url) {

		DefaultHttpClient client = sslClient(new DefaultHttpClient());
		client.setCookieStore(AppManager.getInstance().getCookieStore());
		HttpParams params = client.getParams();

		// timeout 40 sec
		HttpConnectionParams.setConnectionTimeout(params, 40 * 1000);
		HttpConnectionParams.setSoTimeout(params, 40 * 1000);

		HttpResponse httpResponse;

		try {
			httpResponse = client.execute(request);
			mResponseCode = httpResponse.getStatusLine().getStatusCode();
			mMessage = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {

				InputStream instream = entity.getContent();
				mResponse = convertStreamToString(instream);
				
				instream.close();
			}

		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		}
	}
	
	private void executeFileRequest(HttpUriRequest request, String url) {
		
		DefaultHttpClient client = sslClient(new DefaultHttpClient());
		client.setCookieStore(AppManager.getInstance().getCookieStore());
		HttpParams params = client.getParams();

		// timeout 40 sec
		HttpConnectionParams.setConnectionTimeout(params, 40 * 1000);
		HttpConnectionParams.setSoTimeout(params, 40 * 1000);

		HttpResponse httpResponse;

		try {
			httpResponse = client.execute(request);
			mResponseCode = httpResponse.getStatusLine().getStatusCode();
			mMessage = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {

				InputStream instream = entity.getContent();
				
				File folder = new File(FILE_PATH);
				folder.mkdirs();
				
				File file = new File(folder, FILE_APK);
				FileOutputStream fileOutStrem = new FileOutputStream(file);
				
				byte[] buffer = new byte[1024];
				int len1 = 0;
				while ((len1 = instream.read(buffer)) != -1) {
					fileOutStrem.write(buffer, 0, len1);
				}
				
				fileOutStrem.close();
				instream.close();
			}

		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		}
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * solving problems with ssl
	 * 
	 * @param client
	 * @return
	 */
	private DefaultHttpClient sslClient(org.apache.http.client.HttpClient client) {
	    try {
	        X509TrustManager tm = new X509TrustManager() { 
	            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}
	            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}
	            public X509Certificate[] getAcceptedIssuers() {return null;}
	        };
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(null, new TrustManager[]{tm}, null);
	        SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
	        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        ClientConnectionManager ccm = client.getConnectionManager();
	        SchemeRegistry sr = ccm.getSchemeRegistry();
	        sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        sr.register(new Scheme("https", ssf, 443));
	        return new DefaultHttpClient(ccm, client.getParams());
	    } catch (Exception ex) {
	        return null;
	    }
	}
	
	private class CustomSSLSocketFactory extends SSLSocketFactory {
	     SSLContext sslContext = SSLContext.getInstance("TLS");

	     public CustomSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	         super(truststore);

	         TrustManager tm = new X509TrustManager() {
	             public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
	             public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
	             public X509Certificate[] getAcceptedIssuers() {return null;}
	         };

	         sslContext.init(null, new TrustManager[] { tm }, null);
	     }

	     public CustomSSLSocketFactory(SSLContext context) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
	        super(null);
	        sslContext = context;
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
	
}