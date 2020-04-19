package com.amandamcnair.assignment3;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.Random;
import java.util.Set;

public class GameValues {

    GameValues() { }
    GameValues(Context context) { this.context = context; }

    private Context context;

    public TextView scoreText;
    public TextView highestScoreText;
    public int buttonClick;
    public int choice;
    public int numOfBlocksToClick = 0;
    public int numOfClicks = 0;

    public int plays[] = new int[20];

    public int score = 0;
    public int highestScore = 0;
    public Random random = new Random();
    public final Handler handler = new Handler();

    public SoundPool soundPool;
    public Set<Integer> soundsLoaded;
    public int bell;
    public int ding;
    public int dong;
    public int high_ding;
    public int lose;
    public Animation animation = new AlphaAnimation(1, 0);

    public MainActivity.MediaState mediaState = MainActivity.MediaState.NOT_READY;
    public MediaPlayer mediaPlayer = new MediaPlayer();

    public AudioFeatures audioFeatures = new AudioFeatures(mediaPlayer, mediaState, context);

}
