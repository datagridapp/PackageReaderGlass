package com.datagridapp.packagereader.params;

import android.content.Context;

import com.datagridapp.packagereader.R;
import com.datagridapp.packagereader.http.HttpMethod;
import com.datagridapp.packagereader.http.HttpParams;
import com.datagridapp.packagereader.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Tomas on 24.6.2014.
 */
public class OrderParams extends HttpParams {

    static final String LOG_NAME = OrderParams.class.getSimpleName();

    public OrderParams(Context context, String uuid, HttpResponse response) {
        super();

        setUrl(context, R.string.config_url, "public/form/order/".concat(uuid).concat(".json"));
        setMethod(HttpMethod.GET);
        setResponse(response);
    }

    public JSONArray getJsonArray() {
        return getResponse().getJsonArray("companySet");
    }

    public JSONObject getJsonObject(int position) {
        return getResponse().getJsonObject("companySet", position);
    }

    public String[] getStringArray() {
        return getResponse().getStringArray("companySet","title");
    }
}
