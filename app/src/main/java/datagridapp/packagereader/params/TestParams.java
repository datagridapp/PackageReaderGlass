package datagridapp.packagereader.params;

import android.content.Context;


import datagridapp.packagereader.R;
import datagridapp.packagereader.http.HttpMethod;
import datagridapp.packagereader.http.HttpParams;
import datagridapp.packagereader.http.HttpResponse;

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
