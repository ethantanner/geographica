package edu.augustana.csc490.mapgame;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.location.Address;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;


public class GameOver extends Activity implements PopupMenu.OnMenuItemClickListener {

    float score;
    SharedPreferences highScorePref;
    SharedPreferences.Editor highScoreEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        score = intent.getFloatExtra("score", -1);

        highScorePref = this.getSharedPreferences("scores", Context.MODE_PRIVATE);
        highScoreEditor = highScorePref.edit();

        int highScore = highScorePref.getInt("highscore", 1000000000);

        int scoreInt = Integer.parseInt(String.format("%.0f", score));
        Log.w("scoreInt", Integer.toString(scoreInt));

        if(scoreInt < highScore) {
            highScoreEditor.putInt("highscore", scoreInt);
            highScoreEditor.commit();
        }

        setContentView(R.layout.gameover);
        ImageButton playAgainButton = (ImageButton) findViewById(R.id.playAgain);
        playAgainButton.setOnClickListener(startButtonListener);


        TextView gameOverScore = (TextView) findViewById(R.id.gameOverScore);
        TextView bestScore = (TextView) findViewById(R.id.bestScore);

        gameOverScore.setText("Total Score: " + String.format("%.0f", score) + " km");
        bestScore.setText("Best Score: "+ Integer.toString(highScorePref.getInt("highscore", -1000)) + " km");


        SharedPreferences locations = this.getSharedPreferences("locations", Context.MODE_PRIVATE);
        Log.w("Locations SharedPreferences", locations.getAll().toString());

    }


    public View.OnClickListener startButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(GameOver.this, StreetMode.class);
            intent.putExtra("score", (float) 0);
            intent.putExtra("round", 0);
            startActivity(intent);

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void popUpOptions(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.optionsMainMenu:
                Intent i = new Intent(GameOver.this, MainActivity.class);
                startActivity(i);
                return true;
            case R.id.optionsResetLocations:
                SharedPreferences locations = this.getSharedPreferences("locations", Context.MODE_PRIVATE);
                SharedPreferences.Editor locationsEdit = locations.edit();
                locationsEdit.clear();
                locationsEdit.commit();
                return true;
            case R.id.optionsResetBestScore:
                SharedPreferences bestScore = this.getSharedPreferences("scores", Context.MODE_PRIVATE);
                SharedPreferences.Editor bestScoreEdit = bestScore.edit();
                bestScoreEdit.clear();
                bestScoreEdit.commit();
                return true;
            default:
                return false;
        }
    }

}
