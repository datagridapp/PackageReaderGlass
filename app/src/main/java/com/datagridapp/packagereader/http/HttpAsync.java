package com.datagridapp.packagereader.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * use to make multiple async http calls in separate threat
 */
public class HttpAsync extends AsyncTask<HttpParams, Void, HttpParams[]> {

    private static final String LOG_NAME = HttpAsync.class.getSimpleName();

    private static final String LOG_DIVIDER = "==========================";

    @Override
    protected HttpParams[] doInBackground(final HttpParams... params) {
        for (final HttpParams param : params) {
            final HttpClient client = new HttpClient(param.getUrl());
            client.setJSONString(param.getInputJsonString());
            //client.addHeader("Content-Type", "appication/json"); // if required
            final HttpResponse rs = param.getResponse();
            try {
                rs.onStart();
                client.execute(param.getMethod());
                if (client.getResponseCode() != 200) {
                    rs.setErrorResponse(client.getErrorMessage());
                    rs.setReposeCode(client.getResponseCode());
                } else {
                    rs.setErrorResponse(null);
                    try {
                        rs.setJsonObject(new JSONObject(client.getResponse()));
                    } catch (JSONException e) {
                        // do nothing , no all responses can be translated into  json
                        //Log.e(LOG_NAME, "Cannot convert result to json object", e);
                    }
                    rs.setReposeCode(client.getResponseCode());
                }
            } catch (Exception e) {
                rs.setErrorResponse(e.getMessage());
            }
        }

        return params;
    }

    @Override
    protected void onPostExecute(final HttpParams[] params) {
        super.onPostExecute(params);

        for (final HttpParams param : params) {
            final HttpResponse rs = param.getResponse();
            if (rs.getErrorResponse() != null) {
                Log.i(LOG_NAME, LOG_DIVIDER);
                Log.i(LOG_NAME, "Received server error: ".concat(rs.getErrorResponse().toString()).concat(",\nWith params: ".concat(param.toString())));
                Log.i(LOG_NAME, LOG_DIVIDER);
                rs.onFail(rs.getReposeCode(), rs.getErrorResponse());
            } else {
                Log.i(LOG_NAME, LOG_DIVIDER);
                Log.i(LOG_NAME, "Received server response: ");
                if (rs.getJsonObject() != null) {
                    Log.i(LOG_NAME, rs.getJsonObject().toString());
                }
                Log.i(LOG_NAME, "With params: ".concat(param.toString()));
                Log.i(LOG_NAME, LOG_DIVIDER);
                rs.onSuccess(rs.getReposeCode(), rs.getJsonObject(), param);
            }
        }
    }

}
