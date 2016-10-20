package com.imooc.coding.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvTip;
    private ProgressBar progressBar;

    private MyHandler mHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTip = (TextView) findViewById(R.id.tv_tip);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    public void sendMsg(View v) {
        new MyThread1().start();
    }

    public void postMsg(View v) {
        new MyThread2().start();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvTip.setText("SEND 方式更新 UI： " + msg.arg1 + "%");
            progressBar.setProgress(msg.arg1);
        }
    }

    class MyThread1 extends Thread {

        private int count = 0;

        @Override
        public void run() {
            super.run();
            count = 0;
            while (count < 100) {
                try {
                    Thread.sleep(100);
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

    class MyThread2 extends Thread {

        private int count = 0;

        @Override
        public void run() {
            super.run();
            count = 0;
            while (count < 100) {
                try {
                    Thread.sleep(100);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvTip.setText("POST 方式更新 UI： " + count + "%");
                        progressBar.setProgress(count);
                    }
                });
            }
        }
    }
}
