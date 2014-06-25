package datagridapp.packagereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // delayed camera activity
        // see: https://code.google.com/p/google-glass-api/issues/detail?id=259
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                processVoiceAction();
            }
        }, 100);

        /*Intent result = new Intent(this,ResultActivity.class);
        result.putExtra("contents", "55032;prevod;RZ456;");
        startActivity(result);*/
    }

    private void processVoiceAction() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivity(intent);
//        startActivityForResult(intent, 0);
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == 0) {
//            if (resultCode == RESULT_OK) {
//                String contents = intent.getStringExtra("SCAN_RESULT");
//                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
//                // Handle successful scan
//                Intent result = new Intent(this,ResultActivity.class);
//                result.putExtra("contents", contents);
//                startActivity(result);
//            } else if (resultCode == RESULT_CANCELED) {
//                // Handle cancel
//            }
//        }
//    }
}
