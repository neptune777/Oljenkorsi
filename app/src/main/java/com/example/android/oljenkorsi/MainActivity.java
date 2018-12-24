package com.example.android.oljenkorsi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 98;
    private TextView locationTextView;
    private TextView accuracyTextView;
    private TextView timeTextView;
    private TextView locationTypeTextView;

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private Context context;
    private Button haePaikkaButton;
    private Button lopetaButton;


    private boolean onOff;

    private static final String LOCATION_EXTRA = "query";
    private static final String ONOFF_EXTRA = "onoff";
    private static final String LOCATIONUPDATES_EXTRA = "locUpdates";
    private ArrayList<Integer> mPhoneNumbers;
    private String mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;




        locationTextView        = findViewById(R.id.textView);
        accuracyTextView        = findViewById(R.id.textView2);
        timeTextView            = findViewById(R.id.textView3);
        locationTypeTextView    = findViewById(R.id.textView4);
        haePaikkaButton         = findViewById(R.id.button);
        lopetaButton            = findViewById(R.id.button2);



        haePaikkaButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onOff=true;
                aloita();
                haePaikkaButton.setVisibility(View.INVISIBLE);
                lopetaButton.setVisibility(View.VISIBLE);
            }
        });

        lopetaButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onOff=false;
                lopeta();
                haePaikkaButton.setVisibility(View.VISIBLE);
                lopetaButton.setVisibility(View.INVISIBLE);
            }
        });
/*
        showOnMapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
              //  naytaKartalla();
            }
        });

        */

        if (savedInstanceState != null && savedInstanceState.getParcelable(LOCATION_EXTRA)!=null) {
            mLocation = savedInstanceState.getParcelable(LOCATION_EXTRA);
            onOff = savedInstanceState.getBoolean(ONOFF_EXTRA);
            Log.d("JEE ","" + onOff);
            locationTextView.setText("Latitudi: " + mLocation.getLatitude() + ", Longitudi: " + mLocation.getLongitude());
            accuracyTextView.setText("Paikannuksen tarkkuus: " + mLocation.getAccuracy());
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date(mLocation.getTime());
            String formattedTime = format.format(date);
            timeTextView.setText("Paikannusaika: " + formattedTime);
            locationTypeTextView.setText("Paikannustyyppi: " + mLocation.getProvider());
           // showOnMapButton.setVisibility(View.VISIBLE);

         }else if(savedInstanceState != null){
            Log.d("JEE ","" + onOff);
            onOff = savedInstanceState.getBoolean(ONOFF_EXTRA);
           // showOnMapButton.setVisibility(View.INVISIBLE);
        }

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){

            //mLocation =  intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);

            mLocation =  intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
            Log.d("MainActivity","Prööt! " + mLocation.getLongitude());
           // onOff = savedInstanceState.getBoolean(ONOFF_EXTRA);
            Log.d("JEE ","" + onOff);
            locationTextView.setText("Latitudi: " + mLocation.getLatitude() + ", Longitudi: " + mLocation.getLongitude());
            accuracyTextView.setText("Paikannuksen tarkkuus: " + mLocation.getAccuracy());
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date(mLocation.getTime());
            String formattedTime = format.format(date);
            timeTextView.setText("Paikannusaika: " + formattedTime);
            locationTypeTextView.setText("Paikannustyyppi: " + mLocation.getProvider());
           // showOnMapButton.setVisibility(View.VISIBLE);

        }

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("lokapaikka", ("paikka on muuttunut "+location.getLatitude()+", "+location.getLongitude()));


                if(isBetterLocation(location, mLocation)){

                    mLocation = location;         // tallennetaan myöhempää käyttöä varten
                    locationTextView.setText("Latitudi: " + mLocation.getLatitude() + ", Longitudi: " + mLocation.getLongitude());
                    accuracyTextView.setText("Paikannuksen tarkkuus: " + mLocation.getAccuracy());
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date(mLocation.getTime());
                    String formattedTime = format.format(date);
                    timeTextView.setText("Paikannusaika: " + formattedTime);
                    locationTypeTextView.setText("Paikannustyyppi: " + mLocation.getProvider());
                    //showOnMapButton.setVisibility(View.VISIBLE);


                }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(onOff){
            haePaikkaButton.setVisibility(View.INVISIBLE);
            lopetaButton.setVisibility(View.VISIBLE);
            aloita();
        }else{

            haePaikkaButton.setVisibility(View.VISIBLE);
            lopetaButton.setVisibility(View.INVISIBLE);
            lopeta();

        }


        setupSharedPreferences();

    }


    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);



try {
    mPhoneNumbers.add(Integer.parseInt(sharedPreferences.getString(getString(R.string.number1_pref),
            getResources().getString(R.string.number1_pref_default))));

    mPhoneNumbers.add(Integer.parseInt(sharedPreferences.getString(getString(R.string.number2_pref),
            getResources().getString(R.string.number2_pref_default))));

    mPhoneNumbers.add(Integer.parseInt(sharedPreferences.getString(getString(R.string.number3_pref),
            getResources().getString(R.string.number3_pref_default))));

    mPhoneNumbers.add(Integer.parseInt(sharedPreferences.getString(getString(R.string.number4_pref),
            getResources().getString(R.string.number4_pref_default))));

    mPhoneNumbers.add(Integer.parseInt(sharedPreferences.getString(getString(R.string.number5_pref),
            getResources().getString(R.string.number5_pref_default))));


    mMessage = sharedPreferences.getString(getString(R.string.message_pref),
            getResources().getString(R.string.message_pref_default));

}catch (NumberFormatException e){
    Log.d("setUpSharedPreferences"," " + e.getLocalizedMessage());
}






        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }


    public void lopeta(){
        // Remove the listener you previously added
        mLocationManager.removeUpdates(mLocationListener);
    }


    public void aloita(){

               try {
                   //tarkistetaan lupa
                   kysyLupaa(context);
                   //Huonossa paikassa hidas haku
                   mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                   //Ottaa verkon paikan, joten yleensä nopea tapa hakea joku sijainti
                   mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);





                   if(mLocation!=null ) {

                       locationTextView.setText("Latitudi: " + mLocation.getLatitude() + ", Longitudi: " + mLocation.getLongitude());
                       accuracyTextView.setText("Paikannuksen tarkkuus: " + mLocation.getAccuracy());
                     //  showOnMapButton.setVisibility(View.VISIBLE);

                   }else{
                       locationTextView.setText("Paikannus aloitettu, mutta paikkatiedot eivät vielä valmiita... odota, ole hyvä");
                     //  showOnMapButton.setVisibility(View.INVISIBLE);
                   }
               }catch (SecurityException e){
                   Log.d("lokasofta", "Virhe: Sovelluksella ei ollut oikeuksia lokaatioon");
               }





    }

    public boolean kysyLupaa(final Context context){
        Log.d("lokasofta", "kysyLupaa()");
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("lokasofta", " Permission is not granted");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.d("lokasofta", "Kerran kysytty, mutta ei lupaa... Nyt ei kysytä uudestaan");

            } else {
                Log.d("lokasofta", " Request the permission");
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }
            return false;
        } else {

            Log.d("lokasofta", "Permission has already been granted");
            return true;
        }

    }


    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getParcelable(LOCATION_EXTRA)!=null) {
            mLocation = savedInstanceState.getParcelable(LOCATION_EXTRA);
            onOff = savedInstanceState.getBoolean(ONOFF_EXTRA);
            Log.d("JEE ","" + onOff);
            locationTextView.setText("Latitudi: " + mLocation.getLatitude() + ", Longitudi: " + mLocation.getLongitude());
            accuracyTextView.setText("Paikannuksen tarkkuus: " + mLocation.getAccuracy());
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date(mLocation.getTime());
            String formattedTime = format.format(date);
            timeTextView.setText("Paikannusaika: " + formattedTime);
            locationTypeTextView.setText("Paikannustyyppi: " + mLocation.getProvider());

        }else if(savedInstanceState != null){
            Log.d("JEE ","" + onOff);
            onOff = savedInstanceState.getBoolean(ONOFF_EXTRA);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("lokasofta ", "onRequestPermissionsResult()");

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("lokasofta", "lupa tuli!");
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    aloita();
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.d("lokasofta", "Haetaan paikkaa tietyin väliajoin");
                        //Request location updates:
                        //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,mLocationListener);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("lokasofta", "Ei tullu lupaa!");
                }
                return;
            }

        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);


    }

/*
    public void naytaKartalla(){

        Context context = MainActivity.this;

        /* This is the class that we want to start (and open) when the button is clicked.
        Class destinationActivity = MapsActivity.class;

        /*
         * Here, we create the Intent that will start the Activity we specified above in
         * the destinationActivity variable. The constructor for an Intent also requires a
         * context, which we stored in the variable named "context".

        Intent startChildActivityIntent = new Intent(context, destinationActivity);



        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT,mLocation);
        startChildActivityIntent.putExtra(Intent.EXTRA_COMPONENT_NAME,onOff);
        /*
         * Once the Intent has been created, we can use Activity's method, "startActivity"
         * to start the ChildActivity.

        startActivity(startChildActivityIntent);

    }
*/




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mLocation!=null) {
          outState.putParcelable(LOCATION_EXTRA, mLocation);
          outState.putBoolean(ONOFF_EXTRA,onOff);
        }else {
            outState.putBoolean(ONOFF_EXTRA, onOff);
        }

    }


    /**
     * Methods for setting up the menu
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our visualizer_menu layout to this menu */
        inflater.inflate(R.menu.sms_menu, menu);
        /* Return true so that the visualizer_menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
