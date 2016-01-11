package io.github.jhcpokemon.downloaddemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.jhcpokemon.downloaddemo.R;
import io.github.jhcpokemon.downloaddemo.model.FileInfo;
import io.github.jhcpokemon.downloaddemo.service.MyDownloadService;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.progress_text)
    TextView textView;
    private FileInfo fileInfo;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyDownloadService.ACTION_UPDATE)) {
                int progress = intent.getIntExtra("finished", 0);
                progressBar.setProgress(progress);
            } else if (intent.getAction().equals(MyDownloadService.ACTION_FINISH)) {
                progressBar.setProgress(100);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fileInfo = new FileInfo(0, "manman.apk", "http://dd.myapp.com/16891/D4787B8D20CA85BE7556D60286B9A36C.apk?fsname=com.itcode.reader_2.3.2_32.apk", 0, 0);
        textView.setText(fileInfo.getName());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyDownloadService.ACTION_UPDATE);
        intentFilter.addAction(MyDownloadService.ACTION_FINISH);
        registerReceiver(receiver, intentFilter);
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                Intent intent = new Intent(this, MyDownloadService.class);
                intent.putExtra("fileInfo", fileInfo);
                intent.setAction(MyDownloadService.ACTION_START);
                startService(intent);
                break;
            case R.id.pause:
                intent = new Intent(this, MyDownloadService.class);
                intent.putExtra("fileInfo", fileInfo);
                intent.setAction(MyDownloadService.ACTION_PAUSE);
                startService(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        unregisterReceiver(receiver);
        stopService(new Intent(this, MyDownloadService.class));
    }
}
