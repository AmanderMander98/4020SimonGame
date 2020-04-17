package com.amandamcnair.assignment3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
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

public class SimonRewindGame extends AppCompatActivity {

    //RGV = rewind game values
    GameValues RGV = new GameValues(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simonrewindgame);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        findViewById(R.id.play_button_sr).setOnClickListener(new StartListener());
        findViewById(R.id.pause_button_sr).setOnClickListener(new PauseListener());
        findViewById(R.id.stop_button_sr).setOnClickListener(new StopListener());

        // green stuff cannot be the same in another class                 // right here below
        SharedPreferences simonOriginalprefs = this.getSharedPreferences("HIGHSCOREsimonOriginal", getApplicationContext().MODE_PRIVATE);
        // right here below
        RGV.highestScore = simonOriginalprefs.getInt("HIGHSCOREsimonRewind", 0);
        runOnUiThread(new Runnable() {
            public void run() {
                TextView tv = findViewById(R.id.highestscore_textview_sr);
                tv.setText("High score: " + RGV.highestScore);
                Log.i("HIGH SCORE", "High score: " + RGV.highestScore);
            }
        });


        RGV.soundsLoaded = new HashSet<Integer>();

        AudioAttributes.Builder attributeBuilder = new AudioAttributes.Builder();
        attributeBuilder.setUsage(AudioAttributes.USAGE_GAME);

        SoundPool.Builder spBuilder = new SoundPool.Builder();
        spBuilder.setAudioAttributes(attributeBuilder.build());

        spBuilder.setMaxStreams(1);

        RGV.soundPool = spBuilder.build();

        RGV.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleID, int status) {
                if (status == 0) // success
                {
                    RGV.soundsLoaded.add(sampleID);
                    Log.i("SOUND", "Sound loaded " + sampleID);
                } else {
                    Log.i("SOUND", "Error cannot load sound status = " + status);
                }
            }
        });

        RGV.scoreText = findViewById(R.id.score_textview_sr);
        RGV.highestScoreText = findViewById(R.id.highestscore_textview_sr);

        RGV.bell = RGV.soundPool.load(this, R.raw.bell, 1);
        RGV.ding = RGV.soundPool.load(this, R.raw.ding, 1);
        RGV.dong = RGV.soundPool.load(this, R.raw.dong, 1);
        RGV.high_ding = RGV.soundPool.load(this, R.raw.high_ding, 1);
        RGV.lose = RGV.soundPool.load(this, R.raw.lose, 1);

        findViewById(R.id.red_button_sr).setOnTouchListener(touched);
        findViewById(R.id.green_button_sr).setOnTouchListener(touched);
        findViewById(R.id.blue_button_sr).setOnTouchListener(touched);
        findViewById(R.id.yellow_button_sr).setOnTouchListener(touched);

        findViewById(R.id.red_button_sr).setEnabled(false);
        findViewById(R.id.green_button_sr).setEnabled(false);
        findViewById(R.id.blue_button_sr).setEnabled(false);
        findViewById(R.id.yellow_button_sr).setEnabled(false);

        findViewById(R.id.start_button_sr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playGame();
                findViewById(R.id.start_button_sr).setEnabled(false);
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
                    case R.id.red_button_sr:
                        RGV.buttonClick = 1;
                        playSound(RGV.bell);
                        break;
                    case R.id.green_button_sr:
                        RGV.buttonClick = 2;
                        break;
                    case R.id.blue_button_sr:
                        RGV.buttonClick = 3;
                        break;
                    case R.id.yellow_button_sr:
                        RGV.buttonClick = 4;
                        break;
                }

                // if player clicks wrong button, he/she loses
                if (RGV.plays[RGV.numOfClicks] != RGV.buttonClick)
                {
                    playSound(RGV.lose);
                    findViewById(R.id.red_button_sr).setEnabled(false);
                    findViewById(R.id.green_button_sr).setEnabled(false);
                    findViewById(R.id.blue_button_sr).setEnabled(false);
                    findViewById(R.id.yellow_button_sr).setEnabled(false);

                    findViewById(R.id.start_button).setEnabled(true);

                    Toast.makeText(getApplicationContext(), "GAME OVER!", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SimonRewindGame.this); // need a new one because of running activity
                    builder.setTitle("GAME OVER!");
                    //builder.setMessage("You lost :( \n Click 'Play again!' or 'home' to go back to home.");
                    builder.setMessage("You lost :( \n Your score was " + RGV.score + "\nClick 'home' to go back to home.");

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
                if (v.getId() == R.id.red_button_sr)
                {
                    playSound(RGV.bell);
                    // when I click, it will animate
                    RGV.animation = new AlphaAnimation(1, 0);
                    RGV.animation.setDuration(300);
                    RGV.animation.setInterpolator(new LinearInterpolator());
                    findViewById(R.id.red_button_sr).startAnimation(RGV.animation);
                    //Log.i("PRESSED RED", "Red");
                }
                else if (v.getId() == R.id.green_button_sr)
                {
                    playSound(RGV.ding);
                    // when I click, it will animate
                    RGV.animation = new AlphaAnimation(1, 0);
                    RGV.animation.setDuration(300);
                    RGV.animation.setInterpolator(new LinearInterpolator());
                    findViewById(R.id.green_button_sr).startAnimation(RGV.animation);
                    //Log.i("PRESSED GREEN", "Green");
                }
                else if (v.getId() == R.id.blue_button_sr)
                {
                    playSound(RGV.dong);
                    // when I click, it will animate
                    RGV.animation = new AlphaAnimation(1, 0);
                    RGV.animation.setDuration(300);
                    RGV.animation.setInterpolator(new LinearInterpolator());
                    findViewById(R.id.blue_button_sr).startAnimation(RGV.animation);
                    //Log.i("PRESSED BLUE", "Blue");
                }
                else if (v.getId() == R.id.yellow_button_sr)
                {
                    playSound(RGV.high_ding);
                    // when I click, it will animate
                    RGV.animation = new AlphaAnimation(1, 0);
                    RGV.animation.setDuration(300);
                    RGV.animation.setInterpolator(new LinearInterpolator());
                    findViewById(R.id.yellow_button_sr).startAnimation(RGV.animation);
                    //Log.i("PRESSED YELLOW", "Yellow");
                }
                RGV.numOfClicks++;


                if (RGV.numOfBlocksToClick == RGV.numOfClicks) {

                    RGV.score++;
                    RGV.scoreText.setText("Score: " + RGV.score);

                    RGV.numOfClicks = 0;
                    if (RGV.numOfBlocksToClick > RGV.highestScore) {
                        RGV.highestScore = RGV.numOfBlocksToClick;
                        // green stuff cannot be the same in another class                 // right here below
                        SharedPreferences highScoresSimonRewind = getSharedPreferences("HIGHSCOREsimonOriginal", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorSimonRewind = highScoresSimonRewind.edit();
                        // right here below
                        editorSimonRewind.putInt("HIGHSCOREsimonOriginal", RGV.highestScore);
                        editorSimonRewind.commit();

                        RGV.highestScoreText.setText("High score: " + RGV.highestScore);

                    }
                    final Runnable runnable = new Runnable() {
                        public void run() {
                            playGame();
                        }
                    };
                    RGV.handler.postDelayed(runnable, 1000); // without, you can click the same button over and over again and not record your score!
                }
            }
            return true;
        }
    };

    private void playSound(int soundId) {
        if (RGV.soundsLoaded.contains(soundId)) {
            RGV.soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }


    private int j;
    private void playGame() {

        findViewById(R.id.red_button_sr).setEnabled(true);
        findViewById(R.id.green_button_sr).setEnabled(true);
        findViewById(R.id.blue_button_sr).setEnabled(true);
        findViewById(R.id.yellow_button_sr).setEnabled(true);

        for (int i = 0; i < 20; i++) {
            if (RGV.plays[i] == 0) // assign button a number between 1-4
            {
                //plays[i] = random.nextInt(4);  // 0 - 3
                RGV.plays[i] = RGV.random.nextInt(4) + 1;  // 1 -4
                Log.i("RANDOM", "" + RGV.plays[i] + "\n");
                break; // need this so it goes back to the first button in order
            }
        }
        RGV.numOfBlocksToClick++;

        // this is the computer creating the buttons
        for (j = 0; j < RGV.numOfBlocksToClick; j++) {
            final int newJ = j;
            Log.i("J", "" + j);

            //Log.i("newJ", "" + newJ);
            Log.i("Num of Blocks to Click", "" + RGV.numOfBlocksToClick);
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    if (RGV.plays[newJ] == 1)
                    {
                        playSound(RGV.bell);
                        //findViewById(R.id.red_button).performClick();
                        // when button auto clicks, it will animate
                        RGV.animation.setDuration(300);
                        RGV.animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.red_button_sr).startAnimation(RGV.animation);
                        //Toast.makeText(getApplicationContext(), "Red!", Toast.LENGTH_SHORT).show();

                    }
                    else if (RGV.plays[newJ] == 2)
                    {
                        playSound(RGV.ding);
                        //findViewById(R.id.green_button).performClick();
                        // when button auto clicks, it will animate
                        RGV.animation.setDuration(300);
                        RGV.animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.green_button_sr).startAnimation(RGV.animation);
                        //Toast.makeText(getApplicationContext(), "Green!", Toast.LENGTH_SHORT).show();

                    }
                    else if (RGV.plays[newJ] == 3)
                    {
                        playSound(RGV.dong);

                        findViewById(R.id.blue_button).performClick();
                        // when button auto clicks, it will animate
                        RGV.animation.setDuration(300);
                        RGV.animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.blue_button_sr).startAnimation(RGV.animation);

                        //Toast.makeText(getApplicationContext(), "Blue!", Toast.LENGTH_SHORT).show();

                    }
                    else if (RGV.plays[newJ] == 4)
                    {
                        playSound(RGV.high_ding);

                        //findViewById(R.id.yellow_button).performClick();

                        // when button auto clicks, it will animate
                        Animation animation = new AlphaAnimation(1, 0);
                        animation.setDuration(300);
                        animation.setInterpolator(new LinearInterpolator());
                        findViewById(R.id.yellow_button_sr).startAnimation(animation);
                        //Toast.makeText(getApplicationContext(), "Yellow!", Toast.LENGTH_SHORT).show();
                    }

                }

            };
            RGV.handler.postDelayed(runnable, (1000) * j);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RGV.audioFeatures.stopAudio();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(RGV.audioFeatures.mediaPlayer != null)
        {
            RGV.audioFeatures.mediaPlayer.release();
            RGV.audioFeatures.mediaPlayer = null;
            RGV.audioFeatures.mediaState = MainActivity.MediaState.NOT_READY;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RGV.audioFeatures.playAudio();
    }

    class StartListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            RGV.audioFeatures.playAudio();
        }
    }

    class PauseListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(RGV.audioFeatures.mediaPlayer != null)
            {
                RGV.audioFeatures.mediaPlayer.pause();
                RGV.audioFeatures.mediaState = MainActivity.MediaState.PAUSED;
            }

        }
    }

    class StopListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            RGV.audioFeatures.stopAudio();
        }
    }
}
