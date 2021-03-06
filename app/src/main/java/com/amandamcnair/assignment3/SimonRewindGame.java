//Alexandria Banta
//Amanda McNair
//CSCI 4020

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

/*

 Overview: when start button is clicked, play the animation of buttons user should click.
 If correct button is clicked, add 1 to button sequence and move on to next turn until win.
 Else, cancel the turn and end game.

 Note: to see if correct button is clicked, we check if plays[score - numOfClicks] != buttonClick
 rather than plays[numofclicks] as in Simon Original. This checks the array in reverse order.

 */

public class SimonRewindGame extends AppCompatActivity {

    public MainActivity.MediaState mediaState;
    public MediaPlayer mediaPlayer;

    //use helper class objects
    private GameValues RGV = new GameValues();
    private SimonAlertDialogHelper adHelper = new SimonAlertDialogHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simonrewindgame);

        //set listeners and action bar setup
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        findViewById(R.id.play_button_sr).setOnClickListener(new StartListener());
        findViewById(R.id.pause_button_sr).setOnClickListener(new PauseListener());
        findViewById(R.id.stop_button_sr).setOnClickListener(new StopListener());

        //use sharedpreferences to save the high score
        SharedPreferences simonRewindPrefs = this.getSharedPreferences("HIGHSCORESimonRewind", getApplicationContext().MODE_PRIVATE);
        // right here below
        RGV.highestScore = simonRewindPrefs.getInt("HIGHSCORESimonRewind", 0);
        runOnUiThread(new Runnable() {
            public void run() {
                //RGV.highestScore = 0;
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
                playAnimation();

                findViewById(R.id.start_button_sr).setEnabled(false);
                Toast.makeText(getApplicationContext(), "Game has begun!", Toast.LENGTH_SHORT).show();

                setOnClickListeners();
            }
        });

        adHelper.showRulesAlertDialog();
    }

    View.OnTouchListener nullTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };

    //onclicklistener for all four buttons
    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("OnClickListener", "animationIsRunning: " + RGV.animationIsRunning + " v.id = " + view.getId());

            //if not in the middle of animation,
            if (!RGV.animationIsRunning) {

                //set up a new thread with buttonclicktask class
                RGV.currentButton = view;
                Log.i("OnClickListener", "currentButton: " + RGV.currentButton);
                RGV.buttonClickedForThisRound = true;

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        RGV.buttonPressHandler = new Handler();
                        RGV.buttonPressHandler.post(new ButtonClickTask());
                        Looper.loop();
                    }
                };
                //start the new thread
                t.start();
            } else {
                //otherwise, just display an error toast
                adHelper.animationRunningToast();
            }
        }
    };

    private void playAnimation() {
        //Runnable runnable;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //check for new high score. if so,
                // save to sharedPreferences and update the textview.
                if (RGV.score >= RGV.highestScore) {
                    RGV.highestScore = RGV.score;
                    adHelper.highScoreToast();

                    SharedPreferences highScoresSimonOriginal = getSharedPreferences("HIGHSCORESimonRewind", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorSimonOriginal = highScoresSimonOriginal.edit();
                    // right here below
                    editorSimonOriginal.putInt("HIGHSCORESimonRewind", RGV.highestScore);
                    editorSimonOriginal.commit();

                    TextView tv = findViewById(R.id.highestscore_textview_sr);
                    tv.setText("High score: " + RGV.highestScore);
                    Log.i("HIGH SCORE", "High score: " + RGV.highestScore);
                }
            }
        });

        initializePlays();
        RGV.animationIsRunning = true;

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(750);  // sleep for a moment before beginning
                    for (int j = 0; j < RGV.numOfBlocksToClick; j++) {
                        // this is the computer creating the button animations
                        Log.i("Loop start", "j = " + j + " RGV.numOfBlocksToClick = " + RGV.numOfBlocksToClick);

                        //play appropriate animation and sound for each button in sequence
                        if (RGV.plays[j] == 1) {
                            runAnimationAndPlaySound(findViewById(R.id.red_button_sr), RGV.bell);
                        } else if (RGV.plays[j] == 2) {
                            runAnimationAndPlaySound(findViewById(R.id.green_button_sr), RGV.ding);
                        } else if (RGV.plays[j] == 3) {
                            runAnimationAndPlaySound(findViewById(R.id.blue_button_sr), RGV.dong);
                        } else if (RGV.plays[j] == 4) {
                            runAnimationAndPlaySound(findViewById(R.id.yellow_button_sr), RGV.high_ding);
                        }

                        Log.i("Loop end", "RGV.plays[j] = " + RGV.plays[j]);

                        //sleep for 1 second between button animations
                        Thread.sleep(1000);

                    }
                    //signify animation is done, and enable buttons
                    RGV.animationIsRunning = false;
                    enableButtons();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //start the new thread
        new Thread(runnable).start();
    }

    private void initializePlays() {
        //set up the array for the button sequence
        for (int i = 0; i < 20; i++) {
            if (RGV.plays[i] == 0 || RGV.plays == null) {
                RGV.plays[i] = RGV.random.nextInt(4) + 1;  // 1 -4
            }
        }
    }

    // activated upon clicking one of the buttons
    class ButtonClickTask implements Runnable {
        private boolean rightButtonClicked = true;
        private boolean finishedTheRound = false;
        private boolean newHighScore = false;

        private int buttonToAnimate;
        private int soundToPlay;

        @Override
        public void run() {
            //in background thread

            //determine which button was clicked
            switch (RGV.currentButton.getId()) {
                case R.id.red_button_sr:
                    RGV.buttonClick = 1;
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

            //if the clicked button does NOT match the button
            //at this place in the sequence
            if (RGV.plays[(RGV.score - RGV.numOfClicks)] != RGV.buttonClick) {
                Log.i("Player lost.", "");
                rightButtonClicked = false;

                //end turn, disable buttons, show alertdialog
                cancelTurn();
                playSound(RGV.lose);
                disableButtons();
                adHelper.gameOverAlertDialog();
                //break;

            } else { //otherwise, they clicked the right button
                rightButtonClicked = true;

                //animate appropriate button and sound for the clicked button
                if (RGV.currentButton.getId() == R.id.red_button_sr) {
                    buttonToAnimate = R.id.red_button_sr;
                    soundToPlay = RGV.bell;
                } else if (RGV.currentButton.getId() == R.id.green_button_sr) {
                    buttonToAnimate = R.id.green_button_sr;
                    soundToPlay = RGV.ding;
                } else if (RGV.currentButton.getId() == R.id.blue_button_sr) {
                    buttonToAnimate = R.id.blue_button_sr;
                    soundToPlay = RGV.dong;
                } else if (RGV.currentButton.getId() == R.id.yellow_button_sr) {
                    buttonToAnimate = R.id.yellow_button_sr;
                    soundToPlay = RGV.high_ding;
                }

                //add a click for this round and activate animation
                RGV.numOfClicks++;
                runAnimationAndPlaySound(findViewById(buttonToAnimate), soundToPlay);

                //if this is the end of this round
                if (RGV.numOfBlocksToClick == RGV.numOfClicks) {
                    finishedTheRound = true;

                    //increment score and reset clicks
                    RGV.score++;
                    RGV.numOfClicks = 0;

                    //if they beat their score, new high score.
                    //show in textview, and save in sharedpreferences.
                    if (RGV.numOfBlocksToClick > RGV.highestScore) {
                        newHighScore = true;
                        RGV.highestScore = RGV.numOfBlocksToClick;

                        // save high score in unique sharedpreferences object
                        SharedPreferences highScoresSimonRewind = getSharedPreferences("HIGHSCORESimonRewind", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorSimonRewind = highScoresSimonRewind.edit();
                        editorSimonRewind.putInt("HIGHSCORESimonRewind", RGV.highestScore);
                        editorSimonRewind.commit();
                    }
                }
            }

            //if the round is done,
            if (finishedTheRound) {
                //check if they won. if so, end game.
                //otherwise, go on to next turn.
                if (RGV.score == RGV.winningScore) {
                    adHelper.gameWonAlertDialog();
                } else {
                    RGV.numOfBlocksToClick++;
                    final Runnable runnable = new Runnable() {
                        public void run() {
                            playAnimation();
                        }
                    };

                    new Thread(runnable).start();
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //update UI as needed in main thread
                    if (!rightButtonClicked) {
                        playSound(RGV.lose);
                        //disableButtons();
                        findViewById(R.id.start_button_sr).setEnabled(true);

                    } else if (rightButtonClicked) { // right button was clicked
                        RGV.scoreText.setText("Score: " + RGV.score);

                        if (RGV.score > RGV.highestScore) {
                            TextView tv = findViewById(R.id.highestscore_textview_sr);
                            tv.setText("High score: " + RGV.highestScore);
                            Log.i("HIGH SCORE", "High score: " + RGV.highestScore);
                        }
                    }
                }
            });

            // when done, set all booleans back to false
            // to prep for next turn.
            RGV.buttonClickedForThisRound = false;
            setAllBoolsToFalse();
        }

        private void setAllBoolsToFalse() {
            rightButtonClicked = true;
            finishedTheRound = false;
            newHighScore = false;
        }
    }


    private void playSound(int soundId) {
        if (RGV.soundsLoaded.contains(soundId)) {
            RGV.soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    private void setOnClickListeners() {
        findViewById(R.id.red_button_sr).setOnClickListener(onClick);
        findViewById(R.id.green_button_sr).setOnClickListener(onClick);
        findViewById(R.id.blue_button_sr).setOnClickListener(onClick);
        findViewById(R.id.yellow_button_sr).setOnClickListener(onClick);


        findViewById(R.id.red_button_sr).setEnabled(true);
        findViewById(R.id.green_button_sr).setEnabled(true);
        findViewById(R.id.blue_button_sr).setEnabled(true);
        findViewById(R.id.yellow_button_sr).setEnabled(true);
    }

    public void runAnimationAndPlaySound(View view, int sound) {
        playSound(sound);

        // when button auto clicks, it will animate
        RGV.animation.setDuration(300);
        RGV.animation.setInterpolator(new LinearInterpolator());
        view.startAnimation(RGV.animation);
    }

    private void enableButtons() {
        Log.i("Buttons: ", "enabled");
        setOnClickListeners();
    }

    public void disableButtons() {
        //set all to null on touch listeners
        Log.i("Buttons: ", "disabled");

        findViewById(R.id.red_button_sr).setEnabled(false);
        findViewById(R.id.green_button_sr).setEnabled(false);
        findViewById(R.id.blue_button_sr).setEnabled(false);
        findViewById(R.id.yellow_button_sr).setEnabled(false);
    }

    private void loadSoundPoolSounds() {
        RGV.bell = RGV.soundPool.load(this, R.raw.bell, 1);
        RGV.ding = RGV.soundPool.load(this, R.raw.ding, 1);
        RGV.dong = RGV.soundPool.load(this, R.raw.dong, 1);
        RGV.high_ding = RGV.soundPool.load(this, R.raw.high_ding, 1);
        RGV.lose = RGV.soundPool.load(this, R.raw.lose, 1);
    }


    public void playAudio() {
        if (mediaPlayer == null) {
            mediaState = MainActivity.MediaState.NOT_READY;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mushroom_theme_0);
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

        } else if (mediaState == MainActivity.MediaState.PAUSED) {
            mediaPlayer.start();
            mediaState = MainActivity.MediaState.PLAYING;
        } else if (mediaState == MainActivity.MediaState.STOPPED) {
            mediaPlayer.prepareAsync();
            mediaState = MainActivity.MediaState.NOT_READY;
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaState = MainActivity.MediaState.STOPPED;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelTurn();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            mediaState = MainActivity.MediaState.NOT_READY;
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
            RGV.buttonPressHandler = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playAudio();
    }

    class StartListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            playAudio();
        }
    }

    class PauseListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                mediaState = MainActivity.MediaState.PAUSED;
            }
        }
    }

    class StopListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            stopAudio();
        }
    }

    public class SimonAlertDialogHelper {
        private void showRulesAlertDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(SimonRewindGame.this); // need a new one because of running activity
            builder.setTitle("Simon Rewind");
            //builder.setMessage("You lost :( \n Click 'Play again!' or 'home' to go back to home.");
            builder.setMessage("Welcome to Simon Rewind!\n\n " +
                    "Your goal is to repeat the sequence of buttons in REVERSE order.\n\n" +
                    "Good luck!\n\n");

            builder.setNegativeButton("LET'S PLAY!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int choice) {
                }
            });

            AlertDialog dialog = builder.create();

            //user can't click out of alertdialog
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            dialog.getWindow().setLayout(1100, 700);
        }

        private void gameWonAlertDialog() {
            cancelTurn();
            //Toast.makeText(getApplicationContext(), "GAME OVER!", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(SimonRewindGame.this); // need a new one because of running activity
            builder.setTitle("GAME OVER!");
            //builder.setMessage("You lost :( \n Click 'Play again!' or 'home' to go back to home.");
            builder.setMessage("You WON!!! \n Your score was " + RGV.score + ".\nClick 'home' to go back to home.");

            builder.setNegativeButton("HOME", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int choice) {
                    // Dismiss Dialog
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    getApplicationContext().startActivity(i);
                }
            });

            AlertDialog dialog = builder.create();
            //user can't click out of alertdialog
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
            dialog.getWindow().setLayout(1100, 600);
        }

        private void gameOverAlertDialog() {
            cancelTurn();
            Toast.makeText(getApplicationContext(), "GAME OVER!", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(SimonRewindGame.this); // need a new one because of running activity
            builder.setTitle("GAME OVER!");
            //builder.setMessage("You lost :( \n Click 'Play again!' or 'home' to go back to home.");
            builder.setMessage("You lost :( \n Your score was " + RGV.score + ".\nClick 'home' to go back to home.");

            builder.setNegativeButton("HOME", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int choice) {
                    // Dismiss Dialog
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    getApplicationContext().startActivity(i);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            dialog.getWindow().setLayout(1100, 600);
        }

        private void highScoreToast() {
            Toast.makeText(getApplicationContext(), "HIGH SCORE!", Toast.LENGTH_SHORT).show();
        }

        private void animationRunningToast() {
            Toast.makeText(getApplicationContext(), "WAIT! Pay attention to the sequence!", Toast.LENGTH_SHORT).show();
        }
    }
}
