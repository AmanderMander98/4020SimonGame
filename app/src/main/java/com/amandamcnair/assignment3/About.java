//Alexandria Banta
//Amanda McNair
//CSCI 4020
package com.amandamcnair.assignment3;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class About extends AppCompatActivity {

    enum MediaState {NOT_READY, PLAYING, PAUSED, STOPPED};
    private MainActivity.MediaState mediaState;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        findViewById(R.id.about_intro_theme_button).setOnClickListener(new AboutListenerIntroTheme());
        findViewById(R.id.about_mushroom_theme_0_button).setOnClickListener(new AboutListenerMushroomTheme());
        findViewById(R.id.about_developers_button).setOnClickListener(new AboutDevelopers());
        findViewById(R.id.about_happy_button).setOnClickListener(new AboutHappyTheme());
        findViewById(R.id.about_dungeon_button).setOnClickListener(new AboutDungeounTheme());
        findViewById(R.id.about_iceland_button).setOnClickListener(new AboutIcelandTheme());

        findViewById(R.id.play_button).setOnClickListener(new StartListener());
        findViewById(R.id.pause_button).setOnClickListener(new PauseListener());
        findViewById(R.id.stop_button).setOnClickListener(new StopListener());

    }

    class AboutListenerIntroTheme implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            String message = "<html>" +
                    "<h2>About Platformer Game Music Pack</h2>" +
                    "<p>Music</p>" +
                    "<p><b>Source:</b> Intro Theme Music<br>" +
                    "<b>Creator:</b> CodeManu<br>" +
                    /*"<b>Link: </b> <a href='https://opengameart.org/sites/default/files/Intro%20Theme_0.mp3'>Source website</a><br>" +*/
                    "<b>Link: </b> <a href='https://opengameart.org/content/platformer-game-music-pack'>Source website</a><br>" +
                    "<b>License: </b> CC-BY 3.0</br>" +
                    "<b></b>" +
                    "</p></html>" ;
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(Html.fromHtml(message));
            builder.setPositiveButton("Ok", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            // must be done after the call to show();
            // allows anchor tags to work
            TextView tv = (TextView) dialog.findViewById(android.R.id.message);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    class AboutListenerMushroomTheme implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            String message = "<html>" +
                    "<h2>About Platformer Game Music Pack</h2>" +
                    "<p>Music</p>" +
                    "<p><b>Source:</b> Mushroom Theme<br>" +
                    "<b>Creator:</b> CodeManu<br>" +
                    /*"<b>Link: </b> <a href='https://opengameart.org/sites/default/files/Intro%20Theme_0.mp3'>Source website</a><br>" +*/
                    "<b>Link: </b> <a href='https://opengameart.org/content/platformer-game-music-pack'>Source website</a><br>" +
                    "<b>License: </b> CC-BY 3.0</br>" +
                    "<b></b>" +
                    "</p></html>" ;
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(Html.fromHtml(message));
            builder.setPositiveButton("Ok", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            // must be done after the call to show();
            // allows anchor tags to work
            TextView tv = (TextView) dialog.findViewById(android.R.id.message);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    class AboutDevelopers implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            String message = "<html>" +
                    "<h2>About the Developers</h2>" +
                    "<p style='text-align: center;'><b>Amanda McNair and Alexandria Banta</b>" +
                    "<p>Links</p>" +
                    "<a href='https://github.com/AmanderMander98'>Amanda's Github</a><br>" +
                    "<a href='https://github.com/alexandriabanta'>Alexandria's Github</a><br>" +
                    "<b>License: We're professionals" +
                    "<b></b>" +
                    "</p></html>" ;
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(Html.fromHtml(message));
            builder.setPositiveButton("Ok", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            // must be done after the call to show();
            // allows anchor tags to work
            TextView tv = (TextView) dialog.findViewById(android.R.id.message);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    class AboutHappyTheme implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            String message = "<html>" +
                    "<h2>About Happy Arcade Tune</h2>" +
                    "<p>Music</p>" +
                    "<p><b>Source:</b> Happy<br>" +
                    "<b>Creator:</b> Rezoner<br>" +
                    "<b>Link: </b> <a href='https://opengameart.org/content/happy-arcade-tune'>Source website</a><br>" +
                    "<b>License: </b> CC-BY 3.0</br>" +
                    "<b></b>" +
                    "</p></html>" ;
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(Html.fromHtml(message));
            builder.setPositiveButton("Ok", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            // must be done after the call to show();
            // allows anchor tags to work
            TextView tv = (TextView) dialog.findViewById(android.R.id.message);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    class AboutDungeounTheme implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            String message = "<html>" +
                    "<h2>About Platformer Game Music Pack</h2>" +
                    "<p>Music</p>" +
                    "<p><b>Source:</b> Dungeoun Theme<br>" +
                    "<b>Creator:</b> CodeManu<br>" +
                    "<b>Link: </b> <a href='https://opengameart.org/sites/default/files/Dungeon%20Theme_0.mp3'>Source website</a><br>" +
                    "<b>License: </b> CC-BY 3.0</br>" +
                    "<b></b>" +
                    "</p></html>" ;
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(Html.fromHtml(message));
            builder.setPositiveButton("Ok", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            // must be done after the call to show();
            // allows anchor tags to work
            TextView tv = (TextView) dialog.findViewById(android.R.id.message);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    class AboutIcelandTheme implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            String message = "<html>" +
                    "<h2>About Platformer Game Music Pack</h2>" +
                    "<p>Music</p>" +
                    "<p><b>Source:</b> Iceland Theme<br>" +
                    "<b>Creator:</b> CodeManu<br>" +
                    "<b>Link: </b> <a href='https://opengameart.org/sites/default/files/Iceland%20Theme_0.mp3'>Source website</a><br>" +
                    "<b>License: </b> CC-BY 3.0</br>" +
                    "<b></b>" +
                    "</p></html>" ;
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(Html.fromHtml(message));
            builder.setPositiveButton("Ok", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            // must be done after the call to show();
            // allows anchor tags to work
            TextView tv = (TextView) dialog.findViewById(android.R.id.message);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
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
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.iceland_theme);
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
