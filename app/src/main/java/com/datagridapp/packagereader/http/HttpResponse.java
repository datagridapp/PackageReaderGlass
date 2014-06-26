package com.datagridapp.packagereader.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import java.util.ArrayList;

public abstract class HttpResponse {

    static final String LOG_NAME = HttpResponse.class.getSimpleName();

    private int reposeCode = 0;

    public int getReposeCode() {
        return reposeCode;
    }

    public void setReposeCode(int reposeCode) {
        this.reposeCode = reposeCode;
    }

    // ---

    private JSONObject resultObject;

    public JSONObject getJsonObject() {
        return resultObject;
    }

    public void setJsonObject(JSONObject resultObject) {
        this.resultObject = resultObject;
    }

    // --

    private String error;

    public String getErrorResponse() {
        return error;
    }

    public void setErrorResponse(String error) {
        this.error = error;
    }

    // ---

    public ArrayList<JSONObject> getJsonList(String name) {
        final ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        if (getJsonObject() != null) {
            final JSONArray arr = getJsonArray(name);
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        list.add(arr.getJSONObject(i));
                    } catch (JSONException e) {
                        Log.e(LOG_NAME, "Invalid result", e);
                    }
                }
            }
        }
        return list;
    }

    public JSONArray getJsonArray(String name) {
        if (getJsonObject() != null) {
            try {
                if (name.contains("::")) {
                    final JSONArray newArr = new JSONArray();
                    final JSONArray arr = getJsonObject().getJSONArray(name.split("::")[0]);
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject obj = arr.getJSONObject(i).getJSONObject(name.split("::")[1]);
                        newArr.put(obj);
                    }
                    return newArr;
                } else {
                    return getJsonObject().getJSONArray(name);
                }
            } catch (JSONException e) {
                Log.e(LOG_NAME, "Invalid result", e);
                return null;
            }
        } else {
            return null;
        }
    }

    public JSONObject getJsonObject(String name, int position) {
        try {
            return getJsonArray(name).getJSONObject(position);
        } catch (JSONException e) {
            Log.e(LOG_NAME, "Invalid result", e);
            return null;
        }
    }

    public String[] getStringArray(String name, String childName) {
        try {
            final JSONArray arr = getJsonArray(name);
            if (arr == null) {
                return null;
            }
            final String[] arrStr = new String[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                final JSONObject item = arr.getJSONObject(i);

                if (childName.contains(" ")) {
                    String[] childNames = childName.split(" ");
                    for (String string : childNames) {
                        if (arrStr[i] == null) {
                            arrStr[i] = "";
                        }
                        arrStr[i] += item.getString(string);
                        arrStr[i] += " ";
                    }
                } else {
                    arrStr[i] = item.getString(childName);
                }
            }
            return arrStr;
        } catch (JSONException e) {
            Log.e(LOG_NAME, "Invalid result", e);
            return null;
        }
    }

    // --------------------------------------------------------------
    // methods needed to override
    // --------------------------------------------------------------

    public void onStart() {
    }

    public void onSuccess(final int responseCode, final JSONObject result, HttpParams params) {
    }

    public void onFail(final int responseCode, String error) {
    }


}
