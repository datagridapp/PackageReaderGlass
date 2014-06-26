package com.datagridapp.packagereader.params;

import android.content.Context;


import com.datagridapp.packagereader.R;
import com.datagridapp.packagereader.http.HttpMethod;
import com.datagridapp.packagereader.http.HttpParams;
import com.datagridapp.packagereader.http.HttpResponse;

/**
 * Created by tegi.sk
 * User: Tomas
 * Date: 14.7.2013
 * Time: 20:40
 */
public class TestParams extends HttpParams {

    public TestParams(Context context, HttpResponse response) {
        super();

        setUrl(context, R.string.config_url, "login/test.json");
        setMethod(HttpMethod.GET);
        setResponse(response);
    }
}
