package com.zero.star.sample.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    public static final int ONOPEN = 1;
    public static final int ONCLOSE = 0;

    private Handler mHandler1;
    private Handler mHandler2;


    private WebSocketClient mClient;

    private int state = ONCLOSE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mHandler1 =  new Handler() {

        };
        connectWebSocket();

        // 送信ボタン
        Button button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = (EditText) findViewById(R.id.edit);
                try {
                    // 送信
                    if(state == ONOPEN) mClient.send(edit.getText().toString());
                } catch (NotYetConnectedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void connectWebSocket(){

        mHandler2 = new Handler();

        if ("sdk".equals(Build.PRODUCT)) {
            // エミュレータの場合はIPv6を無効
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }

        try {

            URI uri = new URI("ws://192.168.0.78:3333");

            mClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d(TAG, "onOpen");
                    state = ONOPEN;
                }

                @Override
                public void onMessage(final String message) {
                    Log.d(TAG, "onMessage");
                    Log.d(TAG, "Message:" + message);
                    mHandler2.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception ex) {
                    Log.d(TAG, "onError");
                    ex.printStackTrace();
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "onClose");
                    state = ONCLOSE;
                }
            };
            mClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}