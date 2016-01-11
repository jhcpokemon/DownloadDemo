package io.github.jhcpokemon.downloaddemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import io.github.jhcpokemon.downloaddemo.model.FileInfo;

public class MyDownloadService extends Service {
    public static final String ACTION_START = "action_start";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_UPDATE = "action_update";
    public static final String ACTION_FINISH = "action_finish";
    //public static final String TAG = "Download_service";
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/";
    public static final int MSG_INIT = 0;
    private DownloadTask downloadTask;

    public MyDownloadService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            //Log.i(TAG, fileInfo.toString());
            new NetworkThread(fileInfo).start();
        } else if (ACTION_PAUSE.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            if (downloadTask != null){
                downloadTask.setPause(true);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    //Log.i(TAG, fileInfo.toString());
                    downloadTask = new DownloadTask(MyDownloadService.this,fileInfo);
                    downloadTask.download();
                    break;
            }
        }
    };

    class NetworkThread extends Thread {
        private FileInfo fileInfo;

        public NetworkThread(FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        /**
         * 生成空白文件
         */
        @Override
        public void run() {
            HttpURLConnection connection = null;
            RandomAccessFile randomAccessFile = null;
            int length = -1;

            try {
                URL url = new URL(fileInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    length = connection.getContentLength();
                }

                if (length < 0) {
                    return;
                }

                File dir = new File(PATH);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                File file = new File(dir, fileInfo.getName());
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.setLength(length);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

                try {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            fileInfo.setLength(length);
            handler.obtainMessage(MSG_INIT, fileInfo).sendToTarget();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
