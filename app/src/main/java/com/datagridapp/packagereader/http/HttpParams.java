package com.datagridapp.packagereader.http;

import android.content.Context;

/**
 * use to create param object for JsonHttp calls (execute(param), get(param), post(param))
 */
public class HttpParams {
	
	static final String LOG_NAME = HttpParams.class.getSimpleName();
	
	private static final StringBuffer STR_BUFF = new StringBuffer(60);
	private String url;
	
	public String getUrl() {
		return url;
	}
	
	/**
	 * provide http url, or use getString(R.string.base_url)
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * use to create url from multiple resources
	 * 
	 * @param context
	 * @param res
	 * @return
	 */
	public void setUrl(Context context, int ...res) {
		STR_BUFF.setLength(0);
		if (context != null) {
			for (final int rs : res) {
				STR_BUFF.append(context.getString(rs));
			}
		}
		url = STR_BUFF.toString();
	}
	
	/**
	 * use to create url from resource and route
	 * 
	 * @param context
	 * @param res
	 * @param route
	 * @return
	 */
	public void setUrl(Context context, int res, String route) {
		STR_BUFF.setLength(0);
		if (context != null) {
			STR_BUFF.append(context.getString(res));
			STR_BUFF.append(route);
		}
		url = STR_BUFF.toString();
	}
	
	// ---
	
	private HttpMethod method;
	
	public HttpMethod getMethod() {
		return method;
	}
	
	/**
	 * use GET or POST
	 * @param type
	 */
	public void setMethod(final HttpMethod method) {
		this.method = method;
	}
	
	// ---
	
	public String getInputJsonString() {
		return inputJsonString;
	}

	public void setInputJsonString(String inputJsonString) {
		this.inputJsonString = inputJsonString;
	}

	private String inputJsonString;
	
	// ---
	
	private transient HttpResponse reponse;
	
	public HttpResponse getResponse() {
		return reponse;
	}
	
	/**
	 * set response if you call async json
	 * 
	 * @param response
	 */
	public void setResponse(HttpResponse response ) {
		reponse = response;
	}
	
	// ---
	
	/**
	 * Constructor
	 */
	public HttpParams() {}
	
	@Override
	public String toString() {
		STR_BUFF.setLength(0);
		STR_BUFF.append("Url: ").append(url)
		.append(",\nMethod: ").append(HttpMethod.POST.equals(method) ? "POST" : "GET");
		return STR_BUFF.toString();
	}

}
