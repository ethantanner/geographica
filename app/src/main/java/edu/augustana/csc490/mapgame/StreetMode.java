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
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;


public class StreetMode extends Activity implements OnStreetViewPanoramaReadyCallback, PopupMenu.OnMenuItemClickListener {

    StreetViewPanorama mainPanorama;
    String actualPosition;
    float score;
    int round;
    SharedPreferences visitedLocations;
    SharedPreferences.Editor visitedLocationsEditor;

    Boolean repeatLocations = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        actualPosition = intent.getStringExtra("actualPosition");
        score = intent.getFloatExtra("score", -1);
        round = intent.getIntExtra("round",-1);

        setContentView(R.layout.streetview);
        ImageButton switchToMap = (ImageButton) findViewById(R.id.button1);
        switchToMap.setOnClickListener(switchToMapListener);


        StreetViewPanoramaFragment panoramaFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetviewpanorama);
        panoramaFragment.getStreetViewPanoramaAsync(this);

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama){
        mainPanorama = panorama;
        mainPanorama.setOnStreetViewPanoramaChangeListener(panoramaChangeListener);

        visitedLocations = this.getSharedPreferences("locations", Context.MODE_PRIVATE);
        visitedLocationsEditor = visitedLocations.edit();

        if(actualPosition != null){
            mainPanorama.setPosition(playRound(),10);
            TextView roundNumView = (TextView) findViewById(R.id.roundNumView);
            roundNumView.setText("Round "+ Integer.toString(round));
        }else {
            mainPanorama.setPosition(playRound(), 20000000);
            round +=1;
            TextView roundNumView = (TextView) findViewById(R.id.roundNumView);
            roundNumView.setText("Round "+ Integer.toString(round));
        }
        mainPanorama.setStreetNamesEnabled(false);
    }

        //PROUD OF THIS!
    public StreetViewPanorama.OnStreetViewPanoramaChangeListener panoramaChangeListener = new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
        @Override
        public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
            Log.w("panoid", streetViewPanoramaLocation.panoId);

            if (!repeatLocations) {
                if(visitedLocations.contains(streetViewPanoramaLocation.panoId)) {
                    mainPanorama.setPosition(playRound(), 20000000);
                }
                visitedLocationsEditor.putBoolean(streetViewPanoramaLocation.panoId, true);
                visitedLocationsEditor.commit();
            }
        }
    };

    public View.OnClickListener switchToMapListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            actualPosition = mainPanorama.getLocation().position.toString();
            Log.w("PanoID", mainPanorama.getLocation().panoId);
            Intent intent = new Intent(StreetMode.this, MapMode.class);
            intent.putExtra("actualPosition", actualPosition);
            intent.putExtra("score", score);
            intent.putExtra("round", round);
            startActivity(intent);

        }
    };

    public LatLng playRound(){


        // if returning from map view
        if(actualPosition != null){
            String[] latlong =  actualPosition.split(",");
            String latString = latlong[0].substring(latlong[0].indexOf("(")+1);
            String longString = latlong[1].substring(0,latlong[1].length()-1);
            double latitude;
            double longitude;
            if(latString.substring(0,1).equals("-")){
                Log.w("negative Latitude","");
                latString = latString.substring(1);
                latitude = Double.parseDouble(latString) * -1;
            }else {
                latitude = Double.parseDouble(latString);
            }
            if(longString.substring(0,1).equals("-")){
                Log.w("negative Longitude","");
                longString = longString.substring(1);
                longitude = Double.parseDouble(longString) * -1;
            }else {
                longitude = Double.parseDouble(longString);
            }
            LatLng newLatLng = new LatLng(latitude,longitude);
            Log.w("new LatLng", newLatLng.toString());
            return newLatLng;
        }



        return randomLatLng();

    }

    public LatLng randomLatLng(){
        Random r = new Random();

        double latitude = (double) r.nextInt(90);

        if(r.nextInt(100) <= 50){
            latitude *= -1;
        }

        //Eliminate antarctica

        while(latitude <-60){
            latitude = (double) r.nextInt(90);
            if(r.nextInt(100) <= 50){
                latitude *=-1;
            }
        }

        latitude += ((double) r.nextInt(9)) * .1;
        latitude += ((double) r.nextInt(9)) * .01;
        latitude += ((double) r.nextInt(9)) * .001;
        latitude += ((double) r.nextInt(9)) * .0001;
        latitude += ((double) r.nextInt(9)) * .00001;
        latitude += ((double) r.nextInt(9)) * .000001;

        double longitude = (double) r.nextInt(180);

        if(r.nextInt(100) <= 50){
            longitude *= -1;
        }

        longitude += ((double) r.nextInt(9)) * .1;
        longitude += ((double) r.nextInt(9)) * .01;
        longitude += ((double) r.nextInt(9)) * .001;
        longitude += ((double) r.nextInt(9)) * .0001;
        longitude += ((double) r.nextInt(9)) * .00001;
        longitude += ((double) r.nextInt(9)) * .000001;


        //49.036473,9.055766

        Log.w("random Latitude:", Double.toString(latitude));
        Log.w("random Longitude:", Double.toString(longitude));


        return new LatLng(latitude,longitude);

    }


    public LatLng playRoundZip(){

        Random r = new Random();

        if(actualPosition != null){
            String[] latlong =  actualPosition.split(",");
            String latString = latlong[0].substring(latlong[0].indexOf("(")+1);
            String longString = latlong[1].substring(0,latlong[1].length()-1);
            double latitude;
            double longitude;
            if(latString.substring(0,1).equals("-")){
                Log.w("negative Latitude","");
                latString = latString.substring(1);
                latitude = Double.parseDouble(latString) * -1;
            }else {
                latitude = Double.parseDouble(latString);
            }
            if(longString.substring(0,1).equals("-")){
                Log.w("negative Longitude","");
                longString = longString.substring(1);
                longitude = Double.parseDouble(longString) * -1;
            }else {
                longitude = Double.parseDouble(longString);
            }
            LatLng newLatLng = new LatLng(latitude,longitude);
            Log.w("new LatLng", newLatLng.toString());
            return newLatLng;
        }

        int zipcode = 0;

        while(zipcode<1 || zipcode>96162){
            zipcode = r.nextInt(99999);
            Log.w("playRound","loop");
        }

        String randomZip = String.valueOf(zipcode);

        LatLng location = getLocationFromZipCode(randomZip);

        while(location == null){
            zipcode = 0;

            while(zipcode<1 || zipcode>96162){
                zipcode = r.nextInt(99999);
                Log.w("playRound","loop");
            }
            location = getLocationFromZipCode(randomZip);
        }
        return location;

    }

    // code from stackoverflow.com/questions/3641304/get-latitude-and-longitude-using-zipcode//////////////////////////////////////////////////////////////////
    public LatLng getLocationFromZipCode(String zipCode){

        final Geocoder geocoder = new Geocoder(this);

        Log.w("geocoder zipcode input", zipCode);

        LatLng location = new LatLng(41.507844, -90.514576);

        try{
            List<Address> addresses = geocoder.getFromLocationName(zipCode,1);
            if(addresses != null && !addresses.isEmpty()){
                Address address = addresses.get(0);

                location = new LatLng(address.getLatitude(),address.getLongitude());
            }else{
                Toast.makeText(this, "Geocode from zip error", Toast.LENGTH_LONG).show();
                return null;
            }
        }catch(IOException e){

        }

        return location;
    }
    // END code from stackoverflow.com/questions/3641304/get-latitude-and-longitude-using-zipcode//////////////////////////////////////////////////////////////


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
                Intent i = new Intent(StreetMode.this, MainActivity.class);
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
