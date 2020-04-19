package com.amandamcnair.assignment3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SimonOriginalGame extends AppCompatActivity {

    private TextView scoreText;
    private TextView highestScoreText;
    private int buttonClick;
    private int choice;
    private int numOfBlocksToClick = 0;
    private int numOfClicks = 0;

    private int plays[] = new int[20];

    private int score = 0;
    private int highestScore = 0;
    Random random = new Random();
    final Handler handler = new Handler();
    //private SoundPool soundPool;
    //private int lose = soundPool.load(this, R.raw.lose, 1);
    private SoundPool soundPool;
    private Set<Integer> soundsLoaded;
    private int bell;
    private int ding;
    private int dong;
    private int high_ding;
    private int lose;
    private Animation animation = new AlphaAnimation(1, 0);

    enum MediaState {NOT_READY, PLAYING, PAUSED, STOPPED};
    private MainActivity.MediaState mediaState;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simonoriginalgame);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        findViewById(R.id.play_button).setOnClickListener(new StartListener());
        findViewById(R.id.pause_button).setOnClickListener(new PauseListener());
        findViewById(R.id.stop_button).setOnClickListener(new StopListener());

        // green stuff cannot be the same in another class                 // right here below
        SharedPreferences simonOriginalprefs = this.getSharedPreferences("HIGHSCOREsimonOriginal", getApplicationContext().MODE_PRIVATE);
                                                   // right here below
        highestScore = simonOriginalprefs.getInt("HIGHSCOREsimonOriginal", 0);
        runOnUiThread(new Runnable() {
            public void run() {
                TextView tv = findViewById(R.id.highestscore_textview);
                tv.setText("High score: " + highestScore);
                Log.i("HIGH SCORE", "High score: " + highestScore);
            }
        });


        soundsLoaded = new HashSet<Integer>();

        AudioAttributes.Builder attributeBuilder = new AudioAttributes.Builder();
        attributeBuilder.setUsage(AudioAttributes.USAGE_GAME);

        SoundPool.Builder spBuilder = new SoundPool.Builder();
        spBuilder.setAudioAttributes(attributeBuilder.build());

        spBuilder.setMaxStreams(1);

        soundPool = spBuilder.build();

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleID, int status) {
                if (status == 0) // success
                {
                    soundsLoaded.add(sampleID);
                    Log.i("SOUND", "Sound loaded " + sampleID);
                } else {
                    Log.i("SOUND", "Error cannot load sound status = " + status);
                }
            }
        });

        scoreText = findViewById(R.id.score_textview);
        highestScoreText = findViewById(R.id.highestscore_textview);

        bell = soundPool.load(this, R.raw.bell, 1);
        ding = soundPool.load(this, R.raw.ding, 1);
        dong = soundPool.load(this, R.raw.dong, 1);
        high_ding = soundPool.load(this, R.raw.high_ding, 1);
        lose = soundPool.load(this, R.raw.lose, 1);

        findViewById(R.id.red_button).setOnTouchListener(touched);
        findViewById(R.id.green_button).setOnTouchListener(touched);
        findViewById(R.id.blue_button).setOnTouchListener(touched);
        findViewById(R.id.yellow_button).setOnTouchListener(touched);

        findViewById(R.id.red_button).setEnabled(false);
        findViewById(R.id.green_button).setEnabled(false);
        findViewById(R.id.blue_button).setEnabled(false);
        findViewById(R.id.yellow_button).setEnabled(false);

        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playGame();
                findViewById(R.id.start_button).setEnabled(false);
                Toast.makeText(getApplicationContext(), "Game has begun!", Toast.LENGTH_SHORT).show();

            }
        });

    }

        View.OnTouchListener touched = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    // player clicking buttons
                    switch (v.getId()) {
                        case R.id.red_button:
                            buttonClick = 1;
                            playSound(bell);
                            break;
                        case R.id.green_button:
                            buttonClick = 2;
                            break;
                        case R.id.blue_button:
                            buttonClick = 3;
                            break;
                        case R.id.yellow_button:
                            buttonClick = 4;
                            break;
                    }

                    // if player clicks wrong button, he/she loses
                    if (plays[numOfClicks] != buttonClick)
                    {
                        playSound(lose);
                        findViewById(R.id.red_button).setEnabled(false);
                        findViewById(R.id.green_button).setEnabled(false);
                        findViewById(R.id.blue_button).setEnabled(false);
                        findViewById(R.id.yellow_button).setEnabled(false);

                        findViewById(R.id.start_button).setEnabled(true);

                        Toast.makeText(getApplicationContext(), "GAME OVER!", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(SimonOriginalGame.this); // need a new one because of running activity
                        builder.setTitle("GAME OVER!");
                        //builder.setMessage("You lost :( \n Click 'Play again!' or 'home' to go back to home.");
                        builder.setMessage("You lost :( \n Your score was " + score + "\nClick 'home' to go back to home.");

                        builder.setNegativeButton("HOME", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int choice) {
                                // Dismiss Dialog
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                getApplicationContext().startActivity(i);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getWindow().setLayout(1100, 600);

                        return true;
                    }
                    //if the user gets its right
                    if (v.getId() == R.id.red_button)
                    {
                        playSound(bell);
                        // when I click, it will animate
                        animation = new AlphaAnimation(1, 0);
                        animation.setDuration(300);
                        animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.red_button).startAnimation(animation);
                        //Log.i("PRESSED RED", "Red");
                    }
                    else if (v.getId() == R.id.green_button)
                    {
                        playSound(ding);
                        // when I click, it will animate
                        animation = new AlphaAnimation(1, 0);
                        animation.setDuration(300);
                        animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.green_button).startAnimation(animation);
                        //Log.i("PRESSED GREEN", "Green");
                    }
                    else if (v.getId() == R.id.blue_button)
                    {
                        playSound(dong);
                        // when I click, it will animate
                        animation = new AlphaAnimation(1, 0);
                        animation.setDuration(300);
                        animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.blue_button).startAnimation(animation);
                        //Log.i("PRESSED BLUE", "Blue");
                    }
                    else if (v.getId() == R.id.yellow_button)
                    {
                        playSound(high_ding);
                        // when I click, it will animate
                        animation = new AlphaAnimation(1, 0);
                        animation.setDuration(300);
                        animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.yellow_button).startAnimation(animation);
                        //Log.i("PRESSED YELLOW", "Yellow");
                    }
                    numOfClicks++;


                    if (numOfBlocksToClick == numOfClicks) {

                        score++;
                        scoreText.setText("Score: " + score);

                        numOfClicks = 0;
                        if (numOfBlocksToClick > highestScore) {
                            highestScore = numOfBlocksToClick;
                            // green stuff cannot be the same in another class                 // right here below
                            SharedPreferences highScoresSimonOriginal = getSharedPreferences("HIGHSCOREsimonOriginal", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editorSimonOriginal = highScoresSimonOriginal.edit();
                                                         // right here below
                            editorSimonOriginal.putInt("HIGHSCOREsimonOriginal", highestScore);
                            editorSimonOriginal.commit();

                            highestScoreText.setText("High score: " + highestScore);

                        }

                        final Runnable runnable = new Runnable() {
                            public void run() {
                                playGame();
                            }
                        };
                        handler.postDelayed(runnable, 1000); // without, you can click the same button over and over again and not record your score!
                    }
                }
                return true;
            }
        };

    private void playSound(int soundId) {
        if (soundsLoaded.contains(soundId)) {
            soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }


private int j;
    private void playGame() {

        findViewById(R.id.red_button).setEnabled(true);
        findViewById(R.id.green_button).setEnabled(true);
        findViewById(R.id.blue_button).setEnabled(true);
        findViewById(R.id.yellow_button).setEnabled(true);

        for (int i = 0; i < 20; i++) {
            if (plays[i] == 0) // assign button a number between 1-4
            {
                //plays[i] = random.nextInt(4);  // 0 - 3
                plays[i] = random.nextInt(4) + 1;  // 1 -4
                Log.i("RANDOM", "" + plays[i] + "\n");
                break; // need this so it goes back to the first button in order
            }
        }
        numOfBlocksToClick++;

        // this is the computer creating the buttons
        for (j = 0; j < numOfBlocksToClick; j++) {
            final int newJ = j;
            Log.i("J", "" + j);

            //Log.i("newJ", "" + newJ);
            Log.i("Num of Blocks to Click", "" + numOfBlocksToClick);
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    if (plays[newJ] == 1)
                    {
                        playSound(bell);
                        //findViewById(R.id.red_button).performClick();
                        // when button auto clicks, it will animate
                        animation.setDuration(300);
                        animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.red_button).startAnimation(animation);
                        //Toast.makeText(getApplicationContext(), "Red!", Toast.LENGTH_SHORT).show();

                    }
                    else if (plays[newJ] == 2)
                    {
                        playSound(ding);
                        //findViewById(R.id.green_button).performClick();
                        // when button auto clicks, it will animate
                         animation.setDuration(300);
                        animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.green_button).startAnimation(animation);
                        //Toast.makeText(getApplicationContext(), "Green!", Toast.LENGTH_SHORT).show();

                    }
                    else if (plays[newJ] == 3)
                    {
                        playSound(dong);

                        findViewById(R.id.blue_button).performClick();
                        // when button auto clicks, it will animate
                        animation.setDuration(300);
                        animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.blue_button).startAnimation(animation);

                        //Toast.makeText(getApplicationContext(), "Blue!", Toast.LENGTH_SHORT).show();

                    }
                    else if (plays[newJ] == 4)
                    {
                        playSound(high_ding);

                        //findViewById(R.id.yellow_button).performClick();

                        // when button auto clicks, it will animate
                        Animation animation = new AlphaAnimation(1, 0);
                        animation.setDuration(300);
                        animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.yellow_button).startAnimation(animation);
                        //Toast.makeText(getApplicationContext(), "Yellow!", Toast.LENGTH_SHORT).show();
                    }

                }

            };
            handler.postDelayed(runnable, (1000) * j);
        }
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
            mediaState = MainActivity.MediaState.NOT_READY;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playAudio();
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
                mediaState = MainActivity.MediaState.PAUSED;
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
            mediaState = MainActivity.MediaState.NOT_READY;
            mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.mushroom_theme_0);
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

    private void stopAudio()
    {
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaState = MainActivity.MediaState.STOPPED;

        }
    }
}

