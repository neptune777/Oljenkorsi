package com.example.android.oljenkorsi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 98;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 99;
    public static final int REQUEST_SMS = 0;
    private TextView locationTextView;
    private TextView accuracyTextView;
    private TextView timeTextView;
    private TextView mInfoTextView;

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private Context context;
    private Button sendSMSButton;
    private Button settingsButton;


    private boolean onOff;

    private static final String LOCATION_EXTRA = "query";
    private static final String ONOFF_EXTRA = "onoff";
    private static final String LOCATIONUPDATES_EXTRA = "locUpdates";
    private ArrayList<String> mPhoneNumbers;
    private String mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;

        kysyLupaa(context);
        kysyLupaa2(context);





        locationTextView              = findViewById(R.id.textView);
        accuracyTextView              = findViewById(R.id.textView2);
        timeTextView                  = findViewById(R.id.textView3);
        mInfoTextView                 = findViewById(R.id.textView4);
        sendSMSButton                 = findViewById(R.id.button);
        settingsButton                = findViewById(R.id.button2);

        setupSharedPreferences();

        sendSMSButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            sendSMS("045 1562740","Test message");

            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent startSettingsActivity = new Intent(context, SettingsActivity.class);
                startActivity(startSettingsActivity);

            }
        });



        if (savedInstanceState != null && savedInstanceState.getParcelable(LOCATION_EXTRA)!=null) {

            setButtonsVisibility();
            mLocation = savedInstanceState.getParcelable(LOCATION_EXTRA);
            onOff = savedInstanceState.getBoolean(ONOFF_EXTRA);
            Log.d("JEE ","" + onOff);
            locationTextView.setText("Latitudi: " + mLocation.getLatitude() + ", Longitudi: " + mLocation.getLongitude());
            accuracyTextView.setText("Paikannuksen tarkkuus: " + mLocation.getAccuracy());
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date(mLocation.getTime());
            String formattedTime = format.format(date);
            timeTextView.setText("Paikannusaika: " + formattedTime);
            setInfo();
            //locationNotReadyTextView.setText("Paikannustyyppi: " + mLocation.getProvider());


         }else if(savedInstanceState != null){
            setButtonsVisibility();
            Log.d("JEE ","" + onOff);
            onOff = savedInstanceState.getBoolean(ONOFF_EXTRA);
            setInfo();

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
                    //locationTypeTextView.setText("Paikannustyyppi: " + mLocation.getProvider());
                    setButtonsVisibility();
                    setInfo();


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
/*
        if(onOff){
            sendSMSButton.setVisibility(View.INVISIBLE);
            settingsButton.setVisibility(View.VISIBLE);
            aloita();
        }else{

            sendSMSButton.setVisibility(View.VISIBLE);
            settingsButton.setVisibility(View.INVISIBLE);
            lopeta();

        }

*/

        setInfo();
        aloita();

    }

    private void setInfo(){

        if((mLocation==null || mLocation!=null) && mPhoneNumbers.size()==0){
            mInfoTextView.setText(getString(R.string.phoneNumbersMissing));
            mInfoTextView.setVisibility(View.VISIBLE);
        }

        if(mLocation==null && mPhoneNumbers.size()>0){
            mInfoTextView.setText(getString(R.string.locationNotReady));
            mInfoTextView.setVisibility(View.VISIBLE);
        }
        if(mLocation!=null && mPhoneNumbers.size()>0){
            mInfoTextView.setVisibility(View.INVISIBLE);

        }

    }

    private void setButtonsVisibility(){

        if((mPhoneNumbers.size()==0 || mPhoneNumbers.size()>0) && mLocation==null){
            sendSMSButton.setVisibility(View.INVISIBLE);
            settingsButton.setVisibility(View.VISIBLE);
        }if(mPhoneNumbers.size()==0 && mLocation!=null){
            sendSMSButton.setVisibility(View.INVISIBLE);
            settingsButton.setVisibility(View.VISIBLE);
        }if(mPhoneNumbers.size()>0 && mLocation!=null){
            sendSMSButton.setVisibility(View.VISIBLE);
            settingsButton.setVisibility(View.INVISIBLE);
        }

    }

    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
      //  SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPhoneNumbers = new ArrayList<>();

        //SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String num1= sharedPreferences.getString(getResources().getString(R.string.number1_pref),
        getResources().getString(R.string.number1_pref_default));



        Log.d("setupSharedPreferences_","setupSharedPreferences_ " + num1);


        String json1 = sharedPreferences.getString(getResources().getString(R.string.number1_pref),
                "0");
        String json2 = sharedPreferences.getString(getResources().getString(R.string.number2_pref),
                "0");
        String json3 = sharedPreferences.getString(getResources().getString(R.string.number3_pref),
                "0");
        String json4 = sharedPreferences.getString(getResources().getString(R.string.number4_pref),
                "0");
        String json5 = sharedPreferences.getString(getResources().getString(R.string.number5_pref),
                "0");
        //  gson.fromJson(json, Person.class).getClass();

        if(json1!="0" && !mPhoneNumbers.contains(getNumberFromObject(json1))){
            mPhoneNumbers.add(getNumberFromObject(json1));
        }if(json2!="0" && !mPhoneNumbers.contains(getNumberFromObject(json2))){
            mPhoneNumbers.add(getNumberFromObject(json2));
        }if(json3!="0" && !mPhoneNumbers.contains(getNumberFromObject(json3))){
            mPhoneNumbers.add(getNumberFromObject(json3));
        }if(json4!="0" && !mPhoneNumbers.contains(getNumberFromObject(json4))){
            mPhoneNumbers.add(getNumberFromObject(json4));
        }if(json5!="0" && !mPhoneNumbers.contains(getNumberFromObject(json5))){
            mPhoneNumbers.add(getNumberFromObject(json5));
        }

        for(int i=0;i<mPhoneNumbers.size();i++){

            Log.d("setupSharedPreferences","JSONS " + "i=" + i + " " + mPhoneNumbers.get(i) );
        }









/*
        try {
        Person person =
        int number1 = Integer.parseInt(sharedPreferences.getString(getString(R.string.number1_pref),
            "0"));
        mPhoneNumbers.add(number1);
        }catch(NumberFormatException e){
        Log.d("","" + e.getLocalizedMessage());
        }

        try {

        int number2 = Integer.parseInt(sharedPreferences.getString(getString(R.string.number2_pref),
            "0"));
        mPhoneNumbers.add(number2);
        }catch(NumberFormatException e){
        Log.d("","" + e.getLocalizedMessage());
        }

        try {

        int number3 = Integer.parseInt(sharedPreferences.getString(getString(R.string.number3_pref),
            "0"));
        mPhoneNumbers.add(number3);
        }catch(NumberFormatException e){
        Log.d("","" + e.getLocalizedMessage());
        }

        try {

        int number4 = Integer.parseInt(sharedPreferences.getString(getString(R.string.number4_pref),
           "0"));
            mPhoneNumbers.add(number4);
        }catch(NumberFormatException e){
        Log.d("","" + e.getLocalizedMessage());
        }

        try {

        int number5 = Integer.parseInt(sharedPreferences.getString(getString(R.string.number5_pref),
           "0"));
            mPhoneNumbers.add(number5);
        }catch(NumberFormatException e){
            Log.d("","" + e.getLocalizedMessage());
        }


*/








    for(int i=0;i<mPhoneNumbers.size();i++){

        Log.d("number_ ", + i+1 + " " + mPhoneNumbers.get(i) );
    }


    /*
  int number1 = Integer.parseInt(sharedPreferences.getString(getString(R.string.number1_pref),"0"));
    Log.d("number1"," " + number1);

    mPhoneNumbers.add(number1);

    mPhoneNumbers.add(Integer.parseInt(sharedPreferences.getString(getString(R.string.number2_pref),"0")));

    mPhoneNumbers.add(Integer.parseInt(sharedPreferences.getString(getString(R.string.number3_pref),"0")));

    mPhoneNumbers.add(Integer.parseInt(sharedPreferences.getString(getString(R.string.number4_pref), "0")));

    mPhoneNumbers.add(Integer.parseInt(sharedPreferences.getString(getString(R.string.number5_pref), "0")));
*/

    mMessage = sharedPreferences.getString(getString(R.string.message_pref),
            getResources().getString(R.string.message_pref_default));


        Log.d("onCreate_","onCreate_ " + mMessage);





        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    protected String getNumberFromObject(String json){


        try {

            Gson gson = new Gson();
            Person person;
            person = gson.fromJson(json, Person.class);

            //  Log.d("value_","class" + gson.fromJson(json, Person.class).getClass());
            try {
                String value = person.getPhoneNumber();
                return value;


            } catch (NullPointerException e) {

                String mes = e.getLocalizedMessage();
                Log.d("", "" + mes);
                return null;
            }
        } catch (IllegalStateException | JsonSyntaxException e) {

            String mes = e.getLocalizedMessage();
            Log.d("", "" + mes);

            return null;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("onResume","onResume_");
        setupSharedPreferences();
        setButtonsVisibility();
        setInfo();
    }


    public void sendSMS(String phoneNo, String msg) {


        try {
            kysyLupaa2(context);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }



        Log.d("sendSMS"," method called");
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

    public boolean kysyLupaa2(final Context context){
        Log.d("lokasofta", "kysyLupaa2()");
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("lokasofta", " Permission is not granted");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.d("lokasofta", "Kerran kysytty, mutta ei lupaa... Nyt ei kysytä uudestaan");

            } else {
                Log.d("lokasofta", " Request the permission");
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);

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
            //locationTypeTextView.setText("Paikannustyyppi: " + mLocation.getProvider());

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
            }case REQUEST_SMS:
                if (grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access sms", Toast.LENGTH_SHORT).show();
                    sendSMS("045 1562740","Test message");

                }else {
                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and sms", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(SEND_SMS)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{SEND_SMS},
                                                        REQUEST_SMS);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;

        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
        setupSharedPreferences();
    }





}
