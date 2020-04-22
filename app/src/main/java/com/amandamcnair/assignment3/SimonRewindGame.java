package com.amandamcnair.assignment3;

import android.app.AlertDialog;
import android.os.Handler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;

public class SimonRewindGame extends AppCompatActivity {

    public MainActivity.MediaState mediaState;
    public MediaPlayer mediaPlayer;

    //still not sure im going to end up using this boolean
    private boolean animationIsRunning = false;

    private GameValues RGV = new GameValues();

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
        SharedPreferences simonRewindPrefs = this.getSharedPreferences("HIGHSCORESimonRewind", getApplicationContext().MODE_PRIVATE);
        // right here below
        RGV.highestScore = simonRewindPrefs.getInt("HIGHSCORESimonRewind", 0);
        runOnUiThread(new Runnable() {
            public void run() {
                TextView tv = findViewById(R.id.highestscore_textview_sr);
                tv.setText("High score: " + RGV.highestScore);
                Log.i("HIGH SCORE", "High score: " + RGV.highestScore);
            }
        });


        RGV.soundsLoaded = new HashSet<>();

        AudioAttributes.Builder attributeBuilder = new AudioAttributes.Builder();
        attributeBuilder.setUsage(AudioAttributes.USAGE_GAME);

        SoundPool.Builder spBuilder = new SoundPool.Builder();
        spBuilder.setAudioAttributes(attributeBuilder.build());

        spBuilder.setMaxStreams(1);

        RGV.soundPool = spBuilder.build();
        RGV.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleID, int status) {
                if (status == 0) { // success
                    RGV.soundsLoaded.add(sampleID);
                    Log.i("SOUND", "Sound loaded " + sampleID);
                } else {
                    Log.i("SOUND", "Error cannot load sound status = " + status);
                }
            }
        });

        RGV.scoreText = findViewById(R.id.score_textview_sr);
        RGV.highestScoreText = findViewById(R.id.highestscore_textview_sr);

        loadSoundPoolSounds();

        //disableButtons();

        findViewById(R.id.start_button_sr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playTurn();
                findViewById(R.id.start_button_sr).setEnabled(false);
                Toast.makeText(getApplicationContext(), "Game has begun!", Toast.LENGTH_SHORT).show();

                setOnTouchListeners();
            }
        });

        showRulesAlertDialog();
    }


    View.OnTouchListener nullTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event){
            return true;
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                //call for the button click task
                RGV.currentButton = v;

                // DONT WANT TIMER-- timer does the same thing on a schedule.
                // we only want this to repeat when it's called again.

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        RGV.buttonPressHandler = new Handler();
                        RGV.buttonPressHandler.post(new ButtonClickTask());
                        Looper.loop();
                    }
                };

                t.start();

                while (animationIsRunning) {
                    disableButtons();
                    try {
                        t.sleep(1000 * (RGV.numOfBlocksToClick + 1));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //after it's done running, enable buttons again
                enableButtons();
            }
            return true;
        }
    };


    private void playTurn() {
        initializePlays();
        disableButtons();
        animationIsRunning = true;

        for (int j = 0; j < RGV.numOfBlocksToClick; j++) {
            final int newJ = j;
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // this is the computer creating the button animations

                        Log.i("Num of Blocks to Click", "" + RGV.numOfBlocksToClick);

                        if (RGV.plays[newJ] == 1) {
                            runAnimationAndPlaySound(findViewById(R.id.red_button_sr), RGV.bell);
                        } else if (RGV.plays[newJ] == 2) {
                            runAnimationAndPlaySound(findViewById(R.id.green_button_sr),RGV.ding);
                        } else if (RGV.plays[newJ] == 3) {
                            runAnimationAndPlaySound(findViewById(R.id.blue_button_sr),RGV.dong);
                        } else if (RGV.plays[newJ] == 4) {
                            runAnimationAndPlaySound(findViewById(R.id.yellow_button_sr),RGV.high_ding);
                        }

                        Log.i("RGV.plays[i] = " , "" + RGV.plays[newJ]);
                }
            };

            RGV.animationHandler.postDelayed(runnable, (1000) * j);
        }

        animationIsRunning = false;
        enableButtons();
    }

    private void initializePlays() {
        for (int i = 0; i < 20; i++) {
            if (RGV.plays[i] == 0 || RGV.plays == null)  {
                RGV.plays[i] = RGV.random.nextInt(4) + 1;  // 1 -4
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTurn();
        stopAudio();
    }

    private void cancelTurn() {
        if (RGV.buttonPressHandler != null) {
            //buttonPressHandler.;
            RGV.buttonPressHandler = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelTurn();
        if(mediaPlayer != null) {
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
            if(mediaPlayer != null)  {
                mediaPlayer.pause();
                mediaState = MainActivity.MediaState.PAUSED;
            }

        }
    }

    class StopListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {  stopAudio(); }
    }



    class ButtonClickTask implements Runnable {
        private boolean rightButtonClicked = false;
        private boolean finishedTheRound = false;
        private boolean newHighScore = false;

        private int buttonToAnimate;
        private int soundToPlay;

        @Override
        public void run() {

            //do the ontouchlistener stuff
            switch (RGV.currentButton.getId()) {
                case R.id.red_button_sr:
                    RGV.buttonClick = 1;
                    //playSound(RGV.bell);
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

            if (RGV.plays[(RGV.score - RGV.numOfClicks)] != RGV.buttonClick) {
                Log.i("Player lost.", "");
                rightButtonClicked = false;

                cancelTurn();
                playSound(RGV.lose);
                disableButtons();
                //break;

            } else {
                rightButtonClicked = true;

                if (RGV.currentButton.getId() == R.id.red_button_sr) {
                    buttonToAnimate =  R.id.red_button_sr;
                    soundToPlay = RGV.bell;
                } else if (RGV.currentButton.getId() == R.id.green_button_sr) {
                    //runAnimationAndPlaySound(findViewById(R.id.green_button_sr), RGV.ding);
                    buttonToAnimate =  R.id.green_button_sr;
                    soundToPlay = RGV.ding;
                } else if (RGV.currentButton.getId() == R.id.blue_button_sr) {
                    //runAnimationAndPlaySound(findViewById(R.id.blue_button_sr), RGV.dong);
                    buttonToAnimate =  R.id.blue_button_sr;
                    soundToPlay = RGV.dong;
                } else if (RGV.currentButton.getId() == R.id.yellow_button_sr) {
                    //runAnimationAndPlaySound(findViewById(R.id.yellow_button_sr), RGV.high_ding);
                    buttonToAnimate =  R.id.yellow_button_sr;
                    soundToPlay = RGV.high_ding;
                }

                RGV.numOfClicks++;
                runAnimationAndPlaySound(findViewById(buttonToAnimate),soundToPlay);

                if (RGV.numOfBlocksToClick == RGV.numOfClicks) {
                    finishedTheRound = true;

                    RGV.score++;
                    RGV.numOfClicks = 0;
                    disableButtons();

                    if (RGV.numOfBlocksToClick > RGV.highestScore) {
                        newHighScore = true;
                        RGV.highestScore = RGV.numOfBlocksToClick;

                        // green stuff cannot be the same in another class                 // right here below
                        SharedPreferences highScoresSimonRewind = getSharedPreferences("HIGHSCORESimonRewind", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorSimonRewind = highScoresSimonRewind.edit();
                        // right here below
                        editorSimonRewind.putInt("HIGHSCORESimonRewind", RGV.highestScore);
                        editorSimonRewind.commit();
                    }
                }
            }


            Log.i("Button clicked: ",  "" + RGV.buttonClick);
            Log.i("Right button clicked: ",  "" + rightButtonClicked);
            Log.i("Finished the round: ",  "" + finishedTheRound);
            Log.i("New high score: ",  "" + newHighScore);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!rightButtonClicked) {
                        playSound(RGV.lose);
                        //disableButtons();
                        findViewById(R.id.start_button_sr).setEnabled(true);
                        gameOverAlertDialog();

                    } else if (rightButtonClicked) { // right button was clicked
                        //if the user gets its right
                        //runAnimationAndPlaySound(findViewById(buttonToAnimate),soundToPlay);

                        if (finishedTheRound) {
                            RGV.scoreText.setText("Score: " + RGV.score);
                            if (newHighScore) {
                                RGV.highestScoreText.setText("High score: " + RGV.highestScore);
                            }
                        }
                    }
                }
            });

            if (finishedTheRound) {
                RGV.numOfBlocksToClick++;
                final Runnable runnable = new Runnable() {
                    public void run() {
                        playTurn();
                    }
                };
                // without, you can click the same button over and over again and not record your score!
                RGV.animationHandler.postDelayed(runnable, 1000);
            }

            try {
                Thread.sleep(1000 * RGV.numOfBlocksToClick);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setAllBoolsToFalse();
            //when done, set all booleans back to false
        }

        private void setAllBoolsToFalse() {
            rightButtonClicked = false;
            finishedTheRound = false;
            newHighScore = false;
        }
    }


    private void playSound(int soundId) {
        if (RGV.soundsLoaded.contains(soundId)) {
            RGV.soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    private void setOnTouchListeners() {
        findViewById(R.id.red_button_sr).setEnabled(true);
        findViewById(R.id.green_button_sr).setEnabled(true);
        findViewById(R.id.blue_button_sr).setEnabled(true);
        findViewById(R.id.yellow_button_sr).setEnabled(true);

        findViewById(R.id.red_button_sr).setOnTouchListener(onTouchListener);
        findViewById(R.id.green_button_sr).setOnTouchListener(onTouchListener);
        findViewById(R.id.blue_button_sr).setOnTouchListener(onTouchListener);
        findViewById(R.id.yellow_button_sr).setOnTouchListener(onTouchListener);
    }

    public void runAnimationAndPlaySound(View view, int sound) {
        playSound(sound);

        // when button auto clicks, it will animate
        RGV.animation.setDuration(300);
        RGV.animation.setInterpolator(new LinearInterpolator());
        view.startAnimation(RGV.animation);
    }

    private void showRulesAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SimonRewindGame.this); // need a new one because of running activity
        builder.setTitle("Simon Rewind");
        //builder.setMessage("You lost :( \n Click 'Play again!' or 'home' to go back to home.");
        builder.setMessage("Welcome to Simon Rewind!\n\n " +
                "Your goal is to repeat the sequence of buttons in REVERSE order.\n\n" +
                "Good luck!\n\n");

        builder.setNegativeButton("LET'S PLAY!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int choice) { }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(1100, 700);
    }

    private void gameOverAlertDialog() {
        cancelTurn();
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
    }

    private void enableButtons() {
        setOnTouchListeners();
    }

    public void disableButtons() {
        //set all to null on touch listeners
        findViewById(R.id.red_button_sr).setEnabled(false);
        findViewById(R.id.green_button_sr).setEnabled(false);
        findViewById(R.id.blue_button_sr).setEnabled(false);
        findViewById(R.id.yellow_button_sr).setEnabled(false);

        /*
        findViewById(R.id.red_button_sr).setOnTouchListener(nullTouchListener);
        findViewById(R.id.green_button_sr).setOnTouchListener(nullTouchListener);
        findViewById(R.id.blue_button_sr).setOnTouchListener(nullTouchListener);
        findViewById(R.id.yellow_button_sr).setOnTouchListener(nullTouchListener);

         */
    }

    private void loadSoundPoolSounds() {
        RGV.bell = RGV.soundPool.load(this, R.raw.bell, 1);
        RGV.ding = RGV.soundPool.load(this, R.raw.ding, 1);
        RGV.dong = RGV.soundPool.load(this, R.raw.dong, 1);
        RGV.high_ding = RGV.soundPool.load(this, R.raw.high_ding, 1);
        RGV.lose = RGV.soundPool.load(this, R.raw.lose, 1);
    }


    public void playAudio() {
        if(mediaPlayer == null) {
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

        } else if(mediaState == MainActivity.MediaState.PAUSED) {
            mediaPlayer.start();
            mediaState = MainActivity.MediaState.PLAYING;
        } else if(mediaState == MainActivity.MediaState.STOPPED) {
            mediaPlayer.prepareAsync();
            mediaState = MainActivity.MediaState.NOT_READY;
        }
    }

    public void stopAudio() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaState = MainActivity.MediaState.STOPPED;
        }
    }
}
