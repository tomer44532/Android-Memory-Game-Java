package com.example.memorygame.musicService;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.memorygame.R;

public class MusicService extends Service implements MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer;
    private int length = 0;
    private int world;// 0 = forest, 1 = candyland, 2 = city, 3 = menu



    public MusicService() {
    }

    public class ServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //mPlayer = MediaPlayer.create(this, R.raw.background);
        pickMusic();
        mPlayer.setOnErrorListener(this);

        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(50, 50);
        }


        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int
                    extra) {

                onError(mPlayer, what, extra);
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        world = intent.getIntExtra("worldMusic",-1);
        pickMusic();
/*        Toast.makeText(this, world+"", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "music playing is " +world, Toast.LENGTH_SHORT).show();*/
        if (mPlayer != null) {
            //Toast.makeText(this, "playing music here", Toast.LENGTH_SHORT).show();
            mPlayer.start();
        }
        return START_NOT_STICKY;
    }

    public void pauseMusic() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                length = mPlayer.getCurrentPosition();
            }
        }
    }

    public void resumeMusic() {
        pickMusic();

        if (mPlayer != null) {
            if (!mPlayer.isPlaying()) {

                mPlayer.seekTo(length);
                mPlayer.start();
            }
        }
    }

    public void startMusic() {
        //mPlayer = MediaPlayer.create(this, R.raw.background);
        pickMusic();
        mPlayer.setOnErrorListener(this);

        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(50, 50);
            mPlayer.start();
        }

    }

    public void stopMusic() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "Music player failed", Toast.LENGTH_SHORT).show();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
        return false;
    }
    private void pickMusic()
    {
        if(world==0)
        {
            mPlayer = MediaPlayer.create(this, R.raw.forest_music);
        }
        else if(world ==1)
        {
            mPlayer = MediaPlayer.create(this, R.raw.candyland_music);
        }
        else if(world ==2)
        {
            mPlayer = MediaPlayer.create(this, R.raw.city_music2);
        }
        else if(world == 3)
        {
            mPlayer = MediaPlayer.create(this, R.raw.background);
        }
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }
}
