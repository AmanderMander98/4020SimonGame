package com.amandamcnair.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    enum MediaState {NOT_READY, PLAYING, PAUSED, STOPPED};
    private MediaState mediaState;

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        findViewById(R.id.original_simon_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSimonOriginalGame();
            }
        });

        findViewById(R.id.simon_trickster_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSimonTricksterGame();
            }
        });

        findViewById(R.id.simon_rewind_button_mainactivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSimonRewindGame();
            }
        });

        findViewById(R.id.about_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutGame();
            }
        });


        findViewById(R.id.play_button).setOnClickListener(new StartListener());
        findViewById(R.id.pause_button).setOnClickListener(new PauseListener());
        findViewById(R.id.stop_button).setOnClickListener(new StopListener());

    }




    @Override
    protected void onPause() {
        super.onPause();
        stopAudio();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
            mediaState = MediaState.NOT_READY;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playAudio();
    }

    private void aboutGame() {
        Intent intent = new Intent(getApplicationContext(), About.class);
        startActivity(intent);
    }

    private void playSimonOriginalGame() {
        Intent intent = new Intent(getApplicationContext(), SimonOriginalGame.class);
        startActivity(intent);
    }

    private void playSimonRewindGame() {
        Intent intent = new Intent(getApplicationContext(), SimonRewindGame.class);
        startActivity(intent);
    }

    private void playSimonTricksterGame()
    {
        Intent intent = new Intent(getApplicationContext(), SimonTricksterGame.class);
        startActivity(intent);
    }

    class StartListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            playAudio();
        }
    }

    class PauseListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(mediaPlayer != null)
            {
                mediaPlayer.pause();
                mediaState = MediaState.PAUSED;
            }

        }
    }

    class StopListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            stopAudio();
        }
    }

    private void playAudio()
    {
        if(mediaPlayer == null)
        {
            mediaState = MediaState.NOT_READY;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.intro_theme_0);
            mediaPlayer.setLooping(true);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Log.i("MEDIA", "------------ ready to play");
                    mediaState = MediaState.PLAYING;
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
        else if(mediaState == MediaState.PAUSED)
        {
            mediaPlayer.start();
            mediaState = MediaState.PLAYING;
        }
        else if(mediaState == MediaState.STOPPED)
        {
            mediaPlayer.prepareAsync();
            mediaState = MediaState.NOT_READY;
        }
    }

    private void stopAudio()
    {
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaState = MediaState.STOPPED;

        }
    }
}
