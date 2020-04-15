
package com.huawei.codelabpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "PushDemoLog";
    private Button btnToken;
    private String pushtoken;
    StringBuffer b = new StringBuffer();
    protected String tag ="PUSH" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnToken = findViewById(R.id.btn_get_token);
        btnToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToken();
            }
        });

        //register boardcast receiver
        MyReceiver receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TAG);
        this.registerReceiver(receiver, filter);
    }

    /**
     * get token
     */
    private void getToken() {
        Log.i(TAG, "get token: begin");

        // get token
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    pushtoken = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, "HCM");
                    if(!TextUtils.isEmpty(pushtoken)) {
                        Log.i(TAG, "get token:" + pushtoken);
                        showLog("Token is " + pushtoken);
                    }
                } catch (Exception e) {
                    Log.i(TAG,"getToken failed, " + e);
                    showLog("getToken failed, " + e);

                }
            }
        }.start();
    }

    /**
     * Custom Broadcast Class
     * Used to retrieve messages from the MyPushService
     */
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Log.i(TAG, bundle.getString("msg"));
            if (bundle != null && bundle.getString("msg") != null) {
                //If the method name is onNewToken, set the value of token to the value of `msg'.
                Log.i(TAG, bundle.getString("method"));
                showLog(bundle.getString("method") + ":" + bundle.getString("msg"));
            }
        }
    }

    public void showLog(String addLog) {
        b.append(tag).append('\n').append(addLog).append('\n');
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View tvView = findViewById(R.id.tv_log);
                View svView = findViewById(R.id.sv_log);
                if (tvView instanceof TextView) {
                    ((TextView) tvView).setText(b.toString());
                    Log.d("msg", b.toString());
                }
                if (svView instanceof ScrollView) {
                    ((ScrollView) svView).fullScroll(View.FOCUS_DOWN);
                }

            }
        });
    }
}
