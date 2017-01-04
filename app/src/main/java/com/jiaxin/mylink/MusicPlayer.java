package com.jiaxin.mylink;

import android.content.Context;
import android.media.MediaPlayer;

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
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.dismiss);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mediaPlayer.start();
    }
}
