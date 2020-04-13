package com.amandamcnair.assignment3;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class About extends AppCompatActivity {

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
}
