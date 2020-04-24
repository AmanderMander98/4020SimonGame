//Alexandria Banta
//Amanda McNair
//CSCI 4020

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
import android.os.Looper;
import android.util.Log;
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

 Note: Logic is same as Simon Original. Just same color buttons and same sounds.

 */

public class SimonTricksterGame extends AppCompatActivity {
    public MainActivity.MediaState mediaState;
    public MediaPlayer mediaPlayer;

    //use helper class objects
    private GameValues TGV = new GameValues();
    private SimonAlertDialogHelper adHelper = new SimonAlertDialogHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simontrickstergame);

        //set listeners and action bar setup
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        findViewById(R.id.play_button).setOnClickListener(new StartListener());
        findViewById(R.id.pause_button).setOnClickListener(new PauseListener());
        findViewById(R.id.stop_button).setOnClickListener(new StopListener());

        //use sharedpreferences to save the high score
        SharedPreferences simonRewindPrefs = this.getSharedPreferences("HIGHSCORESimonTrickster", getApplicationContext().MODE_PRIVATE);
        TGV.highestScore = simonRewindPrefs.getInt("HIGHSCORESimonTrickster", 0);
        runOnUiThread(new Runnable() {
            public void run() {
                TextView tv = findViewById(R.id.highestscore_textview);
                tv.setText("High score: " + TGV.highestScore);
                Log.i("HIGH SCORE", "High score: " + TGV.highestScore);
            }
        });


        TGV.soundsLoaded = new HashSet<>();

        AudioAttributes.Builder attributeBuilder = new AudioAttributes.Builder();
        attributeBuilder.setUsage(AudioAttributes.USAGE_GAME);

        SoundPool.Builder spBuilder = new SoundPool.Builder();
        spBuilder.setAudioAttributes(attributeBuilder.build());

        spBuilder.setMaxStreams(1);

        TGV.soundPool = spBuilder.build();
        TGV.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleID, int status) {
                if (status == 0) { // success
                    TGV.soundsLoaded.add(sampleID);
                    Log.i("SOUND", "Sound loaded " + sampleID);
                } else {
                    Log.i("SOUND", "Error cannot load sound status = " + status);
                }
            }
        });

        TGV.scoreText = findViewById(R.id.score_textview);
        TGV.highestScoreText = findViewById(R.id.highestscore_textview);

        loadSoundPoolSounds();

        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAnimation();

                findViewById(R.id.start_button).setEnabled(false);
                Toast.makeText(getApplicationContext(), "Game has begun!", Toast.LENGTH_SHORT).show();

                setOnClickListeners();
            }
        });

        adHelper.showRulesAlertDialog();
    }

    //onclicklistener for all four buttons
    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("OnClickListener", "animationIsRunning: " + TGV.animationIsRunning + " v.id = " + view.getId());

            //if not in the middle of animation,
            if (!TGV.animationIsRunning) {

                //set up a new thread with buttonclicktask class
                TGV.currentButton = view;
                Log.i("OnClickListener", "currentButton: " + TGV.currentButton);
                TGV.buttonClickedForThisRound = true;

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        TGV.buttonPressHandler = new Handler();
                        TGV.buttonPressHandler.post(new ButtonClickTask());
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //check for new high score. if so,
                // save to sharedPreferences and update the textview.
                if (TGV.score >= TGV.highestScore) {
                    TGV.highestScore = TGV.score;
                    adHelper.highScoreToast();

                    SharedPreferences highScoresSimonOriginal = getSharedPreferences("HIGHSCORESimonOriginal", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorSimonOriginal = highScoresSimonOriginal.edit();

                    editorSimonOriginal.putInt("HIGHSCORESimonOriginal", TGV.highestScore);
                    editorSimonOriginal.commit();

                    TextView tv = findViewById(R.id.highestscore_textview);
                    tv.setText("High score: " + TGV.highestScore);
                    Log.i("HIGH SCORE", "High score: " + TGV.highestScore);
                }
            }
        });

        initializePlays();
        TGV.animationIsRunning = true;

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(750);  // sleep for a moment before beginning
                    for (int j = 0; j < TGV.numOfBlocksToClick; j++) {
                        // this is the computer creating the button animations
                        Log.i("Loop start", "j = " + j + " TGV.numOfBlocksToClick = " + TGV.numOfBlocksToClick);

                        //play appropriate animation and sound for each button in sequence
                        if (TGV.plays[j] == 1) {
                            runAnimationAndPlaySound(findViewById(R.id.red_button), TGV.bell);
                        } else if (TGV.plays[j] == 2) {
                            runAnimationAndPlaySound(findViewById(R.id.green_button), TGV.ding);
                        } else if (TGV.plays[j] == 3) {
                            runAnimationAndPlaySound(findViewById(R.id.blue_button), TGV.dong);
                        } else if (TGV.plays[j] == 4) {
                            runAnimationAndPlaySound(findViewById(R.id.yellow_button), TGV.high_ding);
                        }

                        Log.i("Loop end", "TGV.plays[j] = " + TGV.plays[j]);

                        //sleep for 1 second between button animations
                        Thread.sleep(1000);

                    }
                    //signify animation is done, and enable buttons
                    TGV.animationIsRunning = false;
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
            if (TGV.plays[i] == 0 || TGV.plays == null) {
                TGV.plays[i] = TGV.random.nextInt(4) + 1;  // 1 -4
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
            switch (TGV.currentButton.getId()) {
                case R.id.red_button:
                    TGV.buttonClick = 1;
                    break;
                case R.id.green_button:
                    TGV.buttonClick = 2;
                    break;
                case R.id.blue_button:
                    TGV.buttonClick = 3;
                    break;
                case R.id.yellow_button:
                    TGV.buttonClick = 4;
                    break;
            }

            //if the clicked button does NOT match the button
            //at this place in the sequence
            if (TGV.plays[TGV.numOfClicks] != TGV.buttonClick) {
                Log.i("Player lost.", "");
                rightButtonClicked = false;

                //end turn, disable buttons, show alertdialog
                cancelTurn();
                playSound(TGV.lose);
                disableButtons();
                adHelper.gameOverAlertDialog();

            } else { //otherwise, they clicked the right button
                rightButtonClicked = true;

                //animate appropriate button and sound for the clicked button
                if (TGV.currentButton.getId() == R.id.red_button) {
                    buttonToAnimate = R.id.red_button;
                    soundToPlay = TGV.bell;
                } else if (TGV.currentButton.getId() == R.id.green_button) {
                    buttonToAnimate = R.id.green_button;
                    soundToPlay = TGV.ding;
                } else if (TGV.currentButton.getId() == R.id.blue_button) {
                    buttonToAnimate = R.id.blue_button;
                    soundToPlay = TGV.dong;
                } else if (TGV.currentButton.getId() == R.id.yellow_button) {
                    buttonToAnimate = R.id.yellow_button;
                    soundToPlay = TGV.high_ding;
                }

                //add a click for this round and activate animation
                TGV.numOfClicks++;
                runAnimationAndPlaySound(findViewById(buttonToAnimate), soundToPlay);

                //if this is the end of this round
                if (TGV.numOfBlocksToClick == TGV.numOfClicks) {
                    finishedTheRound = true;

                    //increment score and reset clicks
                    TGV.score++;
                    TGV.numOfClicks = 0;

                    //if they beat their score, new high score.
                    //show in textview, and save in sharedpreferences.
                    if (TGV.numOfBlocksToClick > TGV.highestScore) {
                        newHighScore = true;
                        TGV.highestScore = TGV.numOfBlocksToClick;

                        // save high score in unique sharedpreferences object
                        SharedPreferences highScoresSimonRewind = getSharedPreferences("HIGHSCORESimonTrickster", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorSimonRewind = highScoresSimonRewind.edit();
                        editorSimonRewind.putInt("HIGHSCORESimonTrickster", TGV.highestScore);
                        editorSimonRewind.commit();
                    }
                }
            }

            //if the round is done,
            if (finishedTheRound) {
                //check if they won. if so, end game.
                //otherwise, go on to next turn.
                if (TGV.score == TGV.winningScore) {
                    adHelper.gameWonAlertDialog();
                } else {
                    TGV.numOfBlocksToClick++;
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
                        playSound(TGV.lose);
                        findViewById(R.id.start_button).setEnabled(true);

                    } else if (rightButtonClicked) { // right button was clicked
                        TGV.scoreText.setText("Score: " + TGV.score);

                        if (TGV.score > TGV.highestScore) {
                            TextView tv = findViewById(R.id.highestscore_textview);
                            tv.setText("High score: " + TGV.highestScore);
                            Log.i("HIGH SCORE", "High score: " + TGV.highestScore);
                        }
                    }
                }
            });

            // when done, set all booleans back to false
            // to prep for next turn.
            TGV.buttonClickedForThisRound = false;
            setAllBoolsToFalse();

        }

        private void setAllBoolsToFalse() {
            rightButtonClicked = true;
            finishedTheRound = false;
            newHighScore = false;
        }
    }


    private void playSound(int soundId) {
        if (TGV.soundsLoaded.contains(soundId)) {
            TGV.soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    private void setOnClickListeners() {
        findViewById(R.id.red_button).setOnClickListener(onClick);
        findViewById(R.id.green_button).setOnClickListener(onClick);
        findViewById(R.id.blue_button).setOnClickListener(onClick);
        findViewById(R.id.yellow_button).setOnClickListener(onClick);


        findViewById(R.id.red_button).setEnabled(true);
        findViewById(R.id.green_button).setEnabled(true);
        findViewById(R.id.blue_button).setEnabled(true);
        findViewById(R.id.yellow_button).setEnabled(true);
    }

    public void runAnimationAndPlaySound(View view, int sound) {
        playSound(sound);

        // when button auto clicks, it will animate
        TGV.animation.setDuration(300);
        TGV.animation.setInterpolator(new LinearInterpolator());
        view.startAnimation(TGV.animation);
    }

    private void enableButtons() {
        Log.i("Buttons: ", "enabled");
        setOnClickListeners();
    }

    public void disableButtons() {
        //set all to null on touch listeners
        Log.i("Buttons: ", "disabled");
        findViewById(R.id.red_button).setEnabled(false);
        findViewById(R.id.green_button).setEnabled(false);
        findViewById(R.id.blue_button).setEnabled(false);
        findViewById(R.id.yellow_button).setEnabled(false);
    }

    private void loadSoundPoolSounds() {
        TGV.bell = TGV.soundPool.load(this, R.raw.bell, 1);
        TGV.ding = TGV.soundPool.load(this, R.raw.bell, 1);
        TGV.dong = TGV.soundPool.load(this, R.raw.bell, 1);
        TGV.high_ding = TGV.soundPool.load(this, R.raw.bell, 1);
        TGV.lose = TGV.soundPool.load(this, R.raw.lose, 1);
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
        if (TGV.buttonPressHandler != null) {
            TGV.buttonPressHandler = null;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(SimonTricksterGame.this); // need a new one because of running activity
            builder.setTitle("Simon Trickster");
            //builder.setMessage("You lost :( \n Click 'Play again!' or 'home' to go back to home.");
            builder.setMessage("Welcome to Simon Trickster!\n\n " +
                    "Your goal is to repeat the sequence of buttons in the order in which they appear." +
                    " But watch out-- the button colors and sounds are the same!\n\n" +
                    "Good luck!\n\n");

            builder.setNegativeButton("LET'S PLAY!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int choice) {
                }
            });

            AlertDialog dialog = builder.create();

            //user can't click out of alertdialog
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            dialog.getWindow().setLayout(1100, 800);
        }

        private void gameWonAlertDialog() {
            //cancelTurn();
            //Toast.makeText(getApplicationContext(), "GAME OVER!", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(SimonTricksterGame.this); // need a new one because of running activity
            builder.setTitle("GAME OVER!");
            //builder.setMessage("You lost :( \n Click 'Play again!' or 'home' to go back to home.");
            builder.setMessage("You WON!!! \n Your score was " + TGV.score + ".\nClick 'home' to go back to home.");

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
            //cancelTurn();
            Toast.makeText(getApplicationContext(), "GAME OVER!", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(SimonTricksterGame.this); // need a new one because of running activity
            builder.setTitle("GAME OVER!");
            //builder.setMessage("You lost :( \n Click 'Play again!' or 'home' to go back to home.");
            builder.setMessage("You lost :( \n Your score was " + TGV.score + ".\nClick 'home' to go back to home.");

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
