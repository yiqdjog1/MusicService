package org.hades.musicservice.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.hades.musicservice.R;
import org.hades.musicservice.base.Config;
import org.hades.musicservice.service.MusicService;

public class MainActivity extends Activity {

    private boolean isPlay = false;
    private ListView music_lv;
    private MusicService.MyMusicBinder binder = null;
    private ServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        music_lv = (ListView) findViewById(R.id.musiclist);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                Config.musicNames);
        music_lv.setAdapter(adapter);
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i("Main", "onServiceConnected");
                binder = (MusicService.MyMusicBinder) iBinder;
                getInfo();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i("Main", "onServiceDisconnected");
                binder = null;
            }
        };
        music_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Main", "onItemClick " + l);
                startMusic((int)l);
            }
        });


    }

    private void startMusic(int l) {
        Log.i("Main", "startMusic");
        if (binder == null) {
            //首次点击
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("MusicId", l);
            bindService(intent, conn, Service.BIND_AUTO_CREATE);
            Log.i("Main", "Service Bind");
            isPlay = true;
        } else if (binder != null){
            //之后点击
            isPlay = binder.setMusic(l);
        }
        getInfo();
    }

    private void getInfo() {
        Log.i("Main", "getInfo()");
        if (binder != null) {
            Toast.makeText(MainActivity.this, binder.getNowMusicName(), Toast.LENGTH_SHORT).show();
        }
    }

    public void mainOnClick(View view) {
        switch (view.getId()) {
            case R.id.stop_bt:
                Log.i("Main", "isPlay? " + isPlay);
                if (isPlay) {
                    stopMusic();
                } else {
                    Toast.makeText(MainActivity.this, "当前没有播放音乐", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void stopMusic() {
        Log.i("Main", "stopMusic() 方法入口");
        binder.stopMusic();
        Toast.makeText(MainActivity.this, "音乐停止", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
}
