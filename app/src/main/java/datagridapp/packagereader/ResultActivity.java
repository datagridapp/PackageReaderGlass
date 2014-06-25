package datagridapp.packagereader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONObject;
import datagridapp.packagereader.http.HttpAsync;
import datagridapp.packagereader.http.HttpParams;
import datagridapp.packagereader.http.HttpResponse;
import datagridapp.packagereader.params.OrderParams;

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle extras = getIntent().getExtras();
        String contents;

        if (extras != null) {
            contents = extras.getString("contents");
            if (contents != null) {
                TextView view = (TextView) findViewById(R.id.contents);
                view.setText(contents);

                String[] contentArr = contents.split(";");
                if (contentArr[0] != null) {
                    // uuid is always stored first
                    String uuid = contentArr[0];
                    //getOrders(uuid);
                }
            }
        }
    }

    OrderParams mOrdersParams;

    void getOrders(String uuid) {
        mOrdersParams = new OrderParams(this,uuid,new HttpResponse() {
            @Override
            public void onSuccess(int responseCode, JSONObject result, HttpParams params) {

            }
            @Override
            public void onFail(int responseCode, String error) {
            }
        });
        new HttpAsync().execute(mOrdersParams);
    }
}
