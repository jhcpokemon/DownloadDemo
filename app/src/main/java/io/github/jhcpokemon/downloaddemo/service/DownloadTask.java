package io.github.jhcpokemon.downloaddemo.service;


import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.github.jhcpokemon.downloaddemo.database.ThreadDAO;
import io.github.jhcpokemon.downloaddemo.database.ThreadDAOImpl;
import io.github.jhcpokemon.downloaddemo.model.FileInfo;
import io.github.jhcpokemon.downloaddemo.model.ThreadInfo;

public class DownloadTask {
    private Context context;
    private FileInfo fileInfo;
    private ThreadDAO threadDAO;
    private boolean isPause = false;

    public DownloadTask(Context context, FileInfo fileInfo) {
        this.context = context;
        this.fileInfo = fileInfo;
        threadDAO = new ThreadDAOImpl(context);
    }

    public void download() {
        List<ThreadInfo> list = threadDAO.queryThread(fileInfo.getUrl());
        ThreadInfo threadInfo;
        if (list.isEmpty()) {
            threadInfo = new ThreadInfo(0, fileInfo.getUrl(), 0, fileInfo.getLength(), 0);
        } else {
            threadInfo = list.get(0);
        }
        new DownloadThread(threadInfo).start();
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    class DownloadThread extends Thread {
        ThreadInfo threadInfo;

        public DownloadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            /**
             * 插入进程信息
             */
            if (!threadDAO.threadExists(threadInfo.getUrl(), threadInfo.getId())) {
                threadDAO.insertThread(threadInfo);
            }

            HttpURLConnection connection = null;
            InputStream inputStream = null;
            RandomAccessFile randomAccessFile = null;
            int finished = threadInfo.getFinished();
            try {
                URL url = new URL(threadInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                int now = threadInfo.getStart() + threadInfo.getFinished();
                connection.setRequestProperty("Range", "bytes = " + now + "-" + threadInfo.getEnd()); // Range of the serve file

                File file = new File(MyDownloadService.PATH, fileInfo.getName());
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.seek(now);                                                          // Seek to range of local file
                Intent intent = new Intent(MyDownloadService.ACTION_UPDATE);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    byte[] buffer = new byte[1024 * 4];
                    inputStream = connection.getInputStream();
                    int len;
                    long time = System.currentTimeMillis();

                    while ((len = inputStream.read(buffer)) != -1) {
                        randomAccessFile.write(buffer, 0, len);
                        finished += len;
                        if (System.currentTimeMillis() - time > 500) {
                            intent.putExtra("finished", (int) Math.ceil(finished * 100 / fileInfo.getLength()));
                            context.sendBroadcast(intent);                                           //发送广播更新UI
                            time = System.currentTimeMillis();
                        }

                        /**
                         * 更新进度
                         */
                        if (isPause) {
                            threadDAO.updateThread(threadInfo.getUrl(), threadInfo.getId(), finished);
                            return;
                        }
                    }

                    if (!isPause) {
                        intent.setAction(MyDownloadService.ACTION_FINISH);
                        context.sendBroadcast(intent);
                    }

                    /**
                     * 下载完成，删除进程信息
                     */
                    threadDAO.deleteThread(threadInfo.getUrl(), threadInfo.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (inputStream != null) {
                        inputStream.close();
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
