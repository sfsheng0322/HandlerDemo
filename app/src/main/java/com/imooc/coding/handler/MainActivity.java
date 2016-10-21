package com.imooc.coding.handler;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Android 基础核心之 Handler
 *
 * apk url: https://github.com/sfsheng0322/HandlerDemo/blob/master/StickyHeaderListView.apk?raw=true
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvTip;
    private ProgressBar progressBar;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String tip = (String) msg.obj;
            tvTip.setText(tip);
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

    public void ClickA(View v) {
        new ThreadA().start();
    }

    public void ClickB(View v) {
        new ThreadB().start();
    }

    public void ClickC(View v) {
        new ThreadC().start();
    }

    public void ClickD(View v) {
        downloadApkFile();
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
                sendMsg(count, "SEND方式模拟下载进度: " + count + "%");
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
                        tvTip.setText("POST方式模拟下载进度: " + count + "%");
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

            sendMsg(0, "我来自ThreadC");
            Looper.loop();
        }
    }

    private void downloadApkFile() {
        String url = "https://github.com/sfsheng0322/HandlerDemo/blob/master/StickyHeaderListView.apk?raw=true";
        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(new Request.Builder().url(url).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    saveApkFile(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveApkFile(Response response) throws Exception {
        byte[] buf = new byte[2048];
        long sum = 0;
        int len = 0;
        long total = response.body().contentLength();
        InputStream is = response.body().byteStream();
        File apkFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Demo.apk");
        FileOutputStream fos = new FileOutputStream(apkFile);
        while ((len = is.read(buf)) != -1) {
            fos.write(buf, 0, len);
            sum += len;
            int progress = (int) (sum * 1.0f / total * 100);
            sendMsg(progress, "真实下载操作: " + progress + "%");
        }
        fos.flush();
        is.close();
        fos.close();
        ApkUtil.install(getApplicationContext(), apkFile.getPath());
    }

    private void sendMsg(int size, String tip) {
        Message msg = mHandler.obtainMessage();
        msg.arg1 = size;
        msg.obj = tip;
        mHandler.sendMessage(msg);
    }
}
