package com.song.gofaraway;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.song.gofaraway.comment.Config;
import com.song.gofaraway.utils.PatternUtils;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements Callback, Handler.Callback {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 10;
    private String TAG = "MainActivity";
    private ProgressBar pb;
    String filepath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public void click(View view) {
        String fileName = "1.apk";
        String downUrl = Config.Url.BASE + fileName;
        downloadFile(downUrl);
    }

    private void downloadFile(String downUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(downUrl)
                .build();
        client.newCall(request).enqueue(this);
    }


    public void getPermiss(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            Toast.makeText(this, "权限已经申请", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.e(TAG, "onFailure: ", e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        InputStream is = response.body().byteStream();
        File f = new File(filepath, "1.apk");
        FileOutputStream fos = new FileOutputStream(f);
        long totalSize = response.body().contentLength();
        long downloaded = 0;
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = is.read(bytes)) != -1) {
            fos.write(bytes);
            downloaded += len;
            int progress = (int) ((downloaded * 1.0f / totalSize) * 100);
            Message msg = new Message();
            msg.what = 1;
            msg.arg1 = progress;
            mhandler.sendMessage(msg);
        }
        response.body().close();
        is.close();
        fos.close();
    }

    Handler mhandler = new Handler(this);

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            pb.setProgress(msg.arg1);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限已经申请", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "已经拒绝申请", Toast.LENGTH_LONG).show();
            }
        }
    }
}
