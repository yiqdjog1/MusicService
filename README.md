# MusicService
Android端的后台音乐播放程序通用框架

# 说明：
	通过Activity 调用 Service 进行音乐播放，
	使用 ServiceConnection 类进行Service 绑定，自定义MyMusicBinder 类继承Binder
	在Service绑定成功后 将Service Binder保存在Activity中，
	从而使Service与Activity进行通讯。

# 核心类 

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
	
# 核心方法：
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
