package io.github.jhcpokemon.downloaddemo.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.jhcpokemon.downloaddemo.model.ThreadInfo;

public class ThreadDAOImpl implements ThreadDAO {
    private DBHelper dbHelper;

    public ThreadDAOImpl(Context context) {
        dbHelper = new DBHelper(context);
    }

    @Override
    public void insertThread(ThreadInfo threadInfo) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("insert into thread_info (thread_id,url,start,end,finished) " +
                "values(?,?,?,?,?)", new Object[]{threadInfo.getId(), threadInfo.getUrl(),
                threadInfo.getStart(), threadInfo.getEnd(), threadInfo.getFinished()});
        database.close();
    }

    @Override
    public void deleteThread(String url, int thread_id) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("delete from thread_info where url = ? and thread_id = ?",
                new Object[]{url, thread_id});
        database.close();
    }

    @Override
    public void updateThread(String url, int thread_id, int finished) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("update thread_info set finished = ? where url = ? and thread_id = ?",
                new Object[]{finished, url, thread_id});
        database.close();
    }

    @Override
    public List<ThreadInfo> queryThread(String url) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        List<ThreadInfo> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from thread_info where url = ?", new String[]{url});
        while (cursor.moveToNext()) {
            ThreadInfo threadInfo = new ThreadInfo();
            threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            list.add(threadInfo);
        }
        cursor.close();
        database.close();
        return list;
    }

    @Override
    public boolean threadExists(String url, int thread_id) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from thread_info where url = ? and thread_id = ?", new String[]{url, thread_id + ""});
        boolean exist = cursor.moveToNext();
        cursor.close();
        database.close();
        return exist;
    }
}
