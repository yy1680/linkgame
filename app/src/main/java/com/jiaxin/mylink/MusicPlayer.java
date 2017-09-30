package com.jiaxin.mylink;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by jiaxin on 2017/1/3.
 */

public class MusicPlayer {
    private Context context;
    private MediaPlayer player;

    public MusicPlayer(Context context) {
        this.context = context;
    }

    public void playBackGroundMusic() {
        if (player == null) {
            player = MediaPlayer.create(context, R.raw.bgm);
        }
        player.setLooping(true);
        if (!player.isPlaying()) {
            player.start();
        }
    }

    public void stopBackGroundMusic() {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
    }

    public void playSoundEffect() {
        int maxStream = 2;
        int streamType = AudioManager.STREAM_MUSIC;
        int srcQulity = 0;
        SoundPool soundPool = new SoundPool(maxStream,streamType,srcQulity);
        int sound = soundPool.load(context,R.raw.dismiss,1);
        int leftVolume = 1;
        int rightVolume = 1;
        int priority = 1;
        int loop = 0;
        float rate = 1.0f;
        soundPool.play(sound,leftVolume,rightVolume,priority,loop,rate);
    }
}
