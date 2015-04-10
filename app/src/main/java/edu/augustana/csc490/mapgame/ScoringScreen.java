package edu.augustana.csc490.mapgame;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
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


public class ScoringScreen extends Activity implements PopupMenu.OnMenuItemClickListener {


    String newScore;
    float score;
    int round;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        newScore = intent.getStringExtra("newScore");
        score = intent.getFloatExtra("score", -1);
        round = intent.getIntExtra("round",-1);

        setContentView(R.layout.scoringscreen);
        ImageButton nextLocationButton = (ImageButton) findViewById(R.id.newLoc);
        nextLocationButton.setOnClickListener(nextLocButtonListener);


        TextView scoreView = (TextView) findViewById(R.id.scoreTextView);
        TextView totalScore = (TextView) findViewById(R.id.totalScore);

        TextView roundNumView = (TextView) findViewById(R.id.roundNumView);
        roundNumView.setText("Round "+ Integer.toString(round));


        Log.w("newScore","" + newScore);
        scoreView.setText("Last Round: " + newScore + " km");
        totalScore.setText("Total Score: " + String.format("%.0f", score) + " km");


        if(round >= 5){
            nextLocationButton.setVisibility(View.GONE);

            /////////////// http://stackoverflow.com/questions/7965494/how-to-put-some-delay-in-calling-an-activity-from-another-activity

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent2 = new Intent(ScoringScreen.this,GameOver.class);
                    intent2.putExtra("score",score);
                    startActivity(intent2);

                }
            }, 3000);

            /////////////// END http://stackoverflow.com/questions/7965494/how-to-put-some-delay-in-calling-an-activity-from-another-activity
        }

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
                Intent i = new Intent(ScoringScreen.this, MainActivity.class);
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

    public View.OnClickListener nextLocButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(ScoringScreen.this, StreetMode.class);
            intent.putExtra("score", score);
            intent.putExtra("round", round);
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



}
