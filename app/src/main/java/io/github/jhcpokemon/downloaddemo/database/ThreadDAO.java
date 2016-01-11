package io.github.jhcpokemon.downloaddemo.database;

import java.util.List;

import io.github.jhcpokemon.downloaddemo.model.ThreadInfo;

public interface ThreadDAO {
    void insertThread(ThreadInfo threadInfo);
    void deleteThread(String url,int thread_id);
    void updateThread(String url,int thread_id,int finished);
    List<ThreadInfo> queryThread(String url);
    boolean threadExists(String url,int thread_id);
}
