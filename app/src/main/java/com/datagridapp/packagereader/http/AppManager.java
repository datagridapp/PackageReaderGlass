package com.datagridapp.packagereader.http;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

public class AppManager {
	
	private static AppManager   _instance;

    private AppManager(){}

    public static AppManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new AppManager();
        }
        return _instance;
    }
    
    public CookieStore getCookieStore() {
    	if (mCookieStore == null) {
    		 mCookieStore = new BasicCookieStore();
		}
		return mCookieStore;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.mCookieStore = cookieStore;
	}

	private CookieStore mCookieStore;
}
