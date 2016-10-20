package com.imooc.coding.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvTip;
    private ProgressBar progressBar;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvTip.setText("SEND方式更新UI: " + msg.arg1 + "%");
            progressBar.setProgress(msg.arg1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTip = (TextView) findViewById(R.id.tv_tip);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        Log.d("--->", "onCreate() getMainLooper() Thread Name: " + Looper.getMainLooper().getThread().getName());
        Log.d("--->", "onCreate() myLooper() Thread Name: " + Looper.myLooper().getThread().getName());
        Log.d("--->", "onCreate() mHandler Thread Name: " + mHandler.getLooper().getThread().getName());
    }

    public void Click1(View v) {
        new ThreadA().start();
    }

    public void Click2(View v) {
        new ThreadC().start();
    }

    class ThreadA extends Thread {

        private int count = 0;

        @Override
        public void run() {
            super.run();
            count = 0;
            while (count < 100) {
                try {
                    Thread.sleep(80);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                msg.arg1 = count;
                mHandler.sendMessage(msg);
            }
        }
    }

    class ThreadB extends Thread {

        private int count = 0;

        @Override
        public void run() {
            super.run();
            count = 0;
            while (count < 100) {
                try {
                    Thread.sleep(80);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvTip.setText("POST方式更新UI: " + count + "%");
                        progressBar.setProgress(count);
                    }
                });
            }
        }
    }

    class ThreadC extends Thread {

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            Looper.myLooper();

            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Log.d("--->", "ThreadC run() handleMessage(): " + mHandler.getLooper().getThread().getName());
                    Log.d("--->", "ThreadC run() handleMessage(): " + msg.obj);

                    tvTip.setText("ThreadC run() handleMessage(): " + msg.obj);
                }
            };

            Log.d("--->", "ThreadC run() getMainLooper() Thread Name: " + Looper.getMainLooper().getThread().getName());
            Log.d("--->", "ThreadC run() myLooper() Thread Name: " + Looper.myLooper().getThread().getName());
            Log.d("--->", "ThreadC run() mHandler Thread Name: " + mHandler.getLooper().getThread().getName());

            Message msg = Message.obtain();
            msg.obj = "我来自ThreadC";
            mHandler.sendMessage(msg);

            Looper.loop();
        }
    }
}
