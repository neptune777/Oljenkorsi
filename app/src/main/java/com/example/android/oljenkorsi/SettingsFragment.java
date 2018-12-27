package com.example.android.oljenkorsi;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


// TODO (1) Implement OnPreferenceChangeListener
public class SettingsFragment extends PreferenceFragmentCompat implements
        OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    private final int PICK_CONTACT = 1;
    private Preference num1;
    private String mActivePreference_key;
    Person mPerson;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // Add visualizer preferences, defined in the XML file in res->xml->pref_visualizer
        addPreferencesFromResource(R.xml.pref_sms);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
       // prefScreen.getPreference(R.string.number1_pref).setLayoutResource((android.support.constraint.ConstraintLayout)View.inflate(getContext(),R.layout.custom_layout, null));
        //rLay=(android.support.constraint.ConstraintLayout)View.inflate(getContext(),R.layout.custom_layout, null);
        // Go through all of the preferences, and set up their preference summary.
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            // You don't need to set up preference summaries for checkbox preferences because
            // they are already set up in xml using summaryOff and summary On



                Gson gson = new Gson();
                String json = sharedPreferences.getString(p.getKey(), "");
             //  gson.fromJson(json, Person.class).getClass();
                Person person;

                if(i==0) {
                    setPreferenceSummary(p, getResources().getString(R.string.number_field_default));
                }else if(i==1) {
                    setPreferenceSummary(p, getResources().getString(R.string.number_field_default));
                }else if(i==2) {
                    setPreferenceSummary(p, getResources().getString(R.string.number_field_default));
                }else if(i==3) {
                    setPreferenceSummary(p, getResources().getString(R.string.number_field_default));
                }else if(i==4) {
                    setPreferenceSummary(p, getResources().getString(R.string.number_field_default));
                }else if(i==7) {
                    //setPreferenceSummary(p, getResources().getString(R.string.message_pref_default));
                    setPreferenceSummary(p, json);

                }

                try{
                    person = gson.fromJson(json, Person.class);
                  //  Log.d("value_","class" + gson.fromJson(json, Person.class).getClass());

                    try{
                        String value = person.getPhoneNumber();
                        setPreferenceSummary(p, value);

                        if(i==0) {
                            p.setTitle(person.getName());
                        }else if(i==1) {
                            p.setTitle(person.getName());
                        }else if(i==2) {
                            p.setTitle(person.getName());
                        }else if(i==3) {
                            p.setTitle(person.getName());
                        }else if(i==4) {
                            p.setTitle(person.getName());
                        }

                    }catch (NullPointerException e){

                        String mes = e.getLocalizedMessage();
                        Log.d("","" + mes);

                    }

                }catch (IllegalStateException | JsonSyntaxException e){

                    String mes = e.getLocalizedMessage();
                    Log.d("","" + mes);


                }


            }




        // TODO (3) Add the OnPreferenceChangeListener specifically to the EditTextPreference
       // String preferenceName = getResources().getString(R.string.number1_pref);
        Preference preference1 = findPreference(getResources().getString(R.string.number1_pref));
        preference1.setOnPreferenceChangeListener(this);
        Preference preference2 = findPreference(getResources().getString(R.string.number2_pref));
        preference2.setOnPreferenceChangeListener(this);
        Preference preference3 = findPreference(getResources().getString(R.string.number3_pref));
        preference3.setOnPreferenceChangeListener(this);
        Preference preference4 = findPreference(getResources().getString(R.string.number4_pref));
        preference4.setOnPreferenceChangeListener(this);
        Preference preference5 = findPreference(getResources().getString(R.string.number5_pref));
        preference5.setOnPreferenceChangeListener(this);
        Preference preference6 = findPreference(getResources().getString(R.string.message_pref));
        preference6.setOnPreferenceChangeListener(this);





    }







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.colorPreferenceScreenBackground));


       // container.getChildAt(0);
     //   Log.d("PRÄÄT","PRÄÄT" + view.getRootView().set);

        return view;
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {



        // Figure out which preference was changed
        Preference preference = findPreference(key);
       // preference.getSummary();
        if (null != preference) {
            // Updates the summary for the preference
            if (!(preference instanceof EditTextPreference)) {

                Gson gson = new Gson();
                String json = sharedPreferences.getString(preference.getKey(), "");
               final Person obj = gson.fromJson(json, Person.class);
                String value = obj.getPhoneNumber();
                String title = obj.getName();
                preference.setTitle(title);

                setPreferenceSummary(preference, value);
                Log.d("onSharedPreferenceChang","EEE_ Preference "+value);

            }else if(preference instanceof EditTextPreference){

                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
                Log.d("onSharedPreferenceChang","EEE_ EditTextPreference "+value);

            }
        }
    }

    /**
     * Updates the summary for the preference
     *
     * @param preference The preference to be updated
     * @param value      The value that the preference was updated to
     */
    private void setPreferenceSummary(Preference preference, String value) {

        if (!(preference instanceof EditTextPreference)) {
            Log.d("setPreferenceSummary","setPreferenceSummary 1" + preference.getKey());
            preference.setSummary(value);



        } else  {
            Log.d("setPreferenceSummary","setPreferenceSummary 2" + value);

            preference.setSummary(value);

        }
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast error = Toast.makeText(getContext(), "Please select a number between 0.1 and 3", Toast.LENGTH_SHORT);



        Log.d("onPreferenceChange","onPreferenceChange " +newValue);



            try {
                int size = ((String) newValue).length();
                if (size <= 0) {

                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                error.show();
                return false;
            }


        if(preference instanceof EditTextPreference){
            Log.d("PREET","PREET " + newValue);

            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(getResources().getString(R.string.message_pref), (String) newValue);

            editor.commit();

            return true;

        }


        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //CharSequence charSequence = "" + R.string.number1_pref;

        Preference preference1 =  findPreference(getResources().getString(R.string.number1_pref));

        preference1.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick( Preference pref )
            {
                // Run your custom method
                Log.d("PRÖ ", "");
                callContacts(pref);

                return true;
            }
        });

        Preference preference2 = findPreference(getResources().getString(R.string.number2_pref));

        preference2.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick( Preference pref )
            {
                // Run your custom method
                Log.d("PRÖ ", "");
                callContacts(pref);

                return true;
            }
        });

        Preference preference3 =  findPreference(getResources().getString(R.string.number3_pref));

        preference3.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick( Preference pref )
            {
                // Run your custom method
                Log.d("PRÖ ", "");
                callContacts(pref);

                return true;
            }
        });

        Preference preference4 =  findPreference(getResources().getString(R.string.number4_pref));

        preference4.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick( Preference pref )
            {
                // Run your custom method
                Log.d("PRÖ ", "");
                callContacts(pref);

                return true;
            }
        });

        Preference preference5 =  findPreference(getResources().getString(R.string.number5_pref));

        preference5.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick( Preference pref )
            {
                // Run your custom method
                Log.d("PRÖ ", "");
                callContacts(pref);

                return true;
            }
        }); Preference preference6 =  findPreference(getResources().getString(R.string.preference_key));

        preference6.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
        {
            @SuppressLint("RestrictedApi")
            public boolean onPreferenceClick(Preference pref )
            {
                // Run your custom method

                Preference preference =  findPreference(getResources().getString(R.string.message_pref));
                preference.performClick();


                return true;
            }
        });


        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    private void callContacts(Preference preference){

        Intent intent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent,PICK_CONTACT);
        mActivePreference_key = preference.getKey();

    }





    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        super.onActivityResult(reqCode, resultCode, data);


        if(reqCode == PICK_CONTACT){
            if(resultCode == AppCompatActivity.RESULT_OK){
                Uri contactData = data.getData();
                Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                Log.d("Claaaas", "CLAAAAS " +getActivity());


                if(c.moveToFirst()){
                    String name   = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String number_ = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.PHONETIC_NAME));
                  int numberColumnIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                  String number = c.getString(numberColumnIndex);
                  Preference preference = findPreference(mActivePreference_key);
                  //  EditTextPreference editTextPref = (EditTextPreference) findPreference("number1_pref");
                   // getPreferenceManager().getOnDisplayPreferenceDialogListener().onDisplayPreferenceDialog(editTextPref);

                 // preference.setSummary(number);
                    CharSequence charSequence = "You've picked:" +number;
                    //EditTextPreference editTextPref = (EditTextPreference) findPreference(mActivePreference_key);
                    //editTextPref.setDefaultValue(number);

                    //preference.setSummary(number);
                    //preference.setDefaultValue(number);
                    if(!(preference instanceof EditTextPreference)) {
                        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        //editor.putString(mActivePreference_key, number);
                        Gson gson = new Gson();
                       final Person person = new Person(name, number);
                        mPerson = person;
                        String json = gson.toJson(person);
                        editor.putString(mActivePreference_key, json);
                       // prefsEditor.putString("MyObject", json);
                        editor.commit();

                        String s = (String) preference.getSummary();
                        Log.d("SUMMARY","SUMMARY " +s);

                    }



                   // getPreferenceManager().showDialog(editTextPref);



                    //preference.setDefaultValue(number);

                   // editTextPref.setDialogTitle(number);
                  //  editTextPref.getEditText();



                    Log.d("onActivityResult","editTextPref.getDialogMessage() name is " +preference.getTitle());
                    Toast.makeText(getContext(),charSequence,Toast.LENGTH_LONG);

                }
            }
        }

    }
}

