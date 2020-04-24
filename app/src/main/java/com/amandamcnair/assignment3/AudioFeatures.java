//Alexandria Banta
//Amanda McNair
//CSCI 4020

package com.amandamcnair.assignment3;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioFeatures {
    public MediaPlayer mediaPlayer;
    public MainActivity.MediaState mediaState;
    //private Context context;
    public Context context;


    public AudioFeatures(MediaPlayer mediaPlayer, MainActivity.MediaState mediaState, Context context) {
        this.mediaPlayer = mediaPlayer;
        this.mediaState = mediaState;
        this.context = context;
    }

    public void playAudio() {

        if(mediaPlayer == null)
        {
            mediaState = MainActivity.MediaState.NOT_READY;
            mediaPlayer = MediaPlayer.create(context,R.raw.mushroom_theme_0);
            mediaPlayer.setLooping(true);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Log.i("MEDIA", "------------ ready to play");
                    mediaState = MainActivity.MediaState.PLAYING;
                    mediaPlayer.start();
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Log.i("MEDIA", "------------ problem playing sound");
                    return false;
                }
            });
        }
        else if(mediaState == MainActivity.MediaState.PAUSED)
        {
            mediaPlayer.start();
            mediaState = MainActivity.MediaState.PLAYING;
        }
        else if(mediaState == MainActivity.MediaState.STOPPED)
        {
            mediaPlayer.prepareAsync();
            mediaState = MainActivity.MediaState.NOT_READY;
        }
    }

    public void stopAudio() {
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaState = MainActivity.MediaState.STOPPED;

        }
    }

}
