package org.hades.musicservice.service;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.hades.musicservice.base.Config;

/**
 * Created by Hades on 16/11/22.
 */
public class MusicService extends IntentService{

    private int nowId = -1;
    private MyMusicBinder mBinder = new MyMusicBinder();
    private MediaPlayer player = null;

    public class MyMusicBinder extends Binder {

        public boolean setMusic(int musicId) {
            Log.i("MyMusicBinder", "setMusic");
            //如果之前有先暂停
            stopMusic();

            try {
                player = MediaPlayer.create(getApplicationContext(), Config.musicIds[musicId]);
                nowId = musicId;
                player.start();
                Log.i("MyMusicBinder", "Music Start");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        public String getNowMusicName() {
            if (nowId >= 0) {
                return Config.musicNames[nowId];
            } else {
                return "当前没有播放歌曲";
            }
        }

        public void stopMusic() {
            if (nowId >= 0) {
                if (player != null) {
                    player.stop();
                    Log.i("setMusic()", "停止当前播放音乐");
                }
                player = null;
            }
        }
    }


    public MusicService() {
        super("MyMusicService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("MyMusicService", "onBind");
        nowId = intent.getIntExtra("MusicId", -1);
        if (nowId != -1) {
            mBinder.setMusic(nowId);

        }
        return mBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.i("MyMusicBinder", "onHandleIntent");

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
        }
        player = null;
    }
}
