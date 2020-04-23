package com.amandamcnair.assignment3;

import android.media.SoundPool;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.Random;
import java.util.Set;

public class GameValues {

    public final int winningScore = 20;
    public int plays[] = new int[winningScore];

    public int buttonClick;
    public int numOfBlocksToClick = 1;
    public int numOfClicks = 0;

    public int score = 0;
    public int highestScore = 0;

    //the sound IDs
    public int bell;
    public int ding;
    public int dong;
    public int high_ding;
    public int lose;

    public boolean animationIsRunning = false;
    public boolean buttonClickedForThisRound = false;

    public TextView scoreText;
    public TextView highestScoreText;
    public View currentButton;

    public Animation animation = new AlphaAnimation(1, 0);
    public Random random = new Random();

    public Handler buttonPressHandler;

    public SoundPool soundPool;
    public Set<Integer> soundsLoaded;

    GameValues() {}
}
