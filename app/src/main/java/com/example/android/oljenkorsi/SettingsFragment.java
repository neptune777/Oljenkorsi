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

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



// TODO (1) Implement OnPreferenceChangeListener
public class SettingsFragment extends PreferenceFragmentCompat implements
        OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    private final int PICK_CONTACT = 1;
    private Preference num1;
    private String mActivePreference_key;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // Add visualizer preferences, defined in the XML file in res->xml->pref_visualizer
        addPreferencesFromResource(R.xml.pref_sms);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        // Go through all of the preferences, and set up their preference summary.
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            // You don't need to set up preference summaries for checkbox preferences because
            // they are already set up in xml using summaryOff and summary On

            if(!(p instanceof EditTextPreference) && p.getSummary()==null && i==0){

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(p.getKey(), getResources().getString(R.string.number1_pref_default));
                editor.commit();
            }else if(!(p instanceof EditTextPreference) && p.getSummary()==null && i==1){

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(p.getKey(), getResources().getString(R.string.number2_pref_default));
                editor.commit();
            }else if(!(p instanceof EditTextPreference) && p.getSummary()==null && i==2){

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(p.getKey(), getResources().getString(R.string.number3_pref_default));
                editor.commit();
            }
            else if(!(p instanceof EditTextPreference) && p.getSummary()==null && i==3){

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(p.getKey(), getResources().getString(R.string.number4_pref_default));
                editor.commit();
            }

            if (!(p instanceof CheckBoxPreference)) {

                String value = sharedPreferences.getString(p.getKey(), "");

                Log.d("value_","value_" + value);
                setPreferenceSummary(p, value);
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
        Preference preference5 = findPreference(getResources().getString(R.string.message_pref));
        preference5.setOnPreferenceChangeListener(this);
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {



        // Figure out which preference was changed
        Preference preference = findPreference(key);
        preference.getSummary();
        if (null != preference) {
            // Updates the summary for the preference
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                Log.d("onSharedPreferenceChang","preference value:: "+value);
                setPreferenceSummary(preference, value);
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
        if (preference instanceof ListPreference) {
            // For list preferences, figure out the label of the selected value
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                // Set the summary to that label
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (preference instanceof Preference) {
            // For EditTextPreferences, set the summary to the value's simple string representation.
            preference.setSummary(value);
        }
    }

    // TODO (2) Override onPreferenceChange. This method should try to convert the new preference value
    // to a float; if it cannot, show a helpful error message and return false. If it can be converted
    // to a float check that that float is between 0 (exclusive) and 3 (inclusive). If it isn't, show
    // an error message and return false. If it is a valid number, return true.
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast error = Toast.makeText(getContext(), "Please select a number between 0.1 and 3", Toast.LENGTH_SHORT);

        String number = (String) newValue;
        //int val = Integer.parseInt(number.trim());

        Log.d("onPreferenceChange","onPreferenceChange ");


            String stringSize = (String) newValue;
            try {
                float size = Float.parseFloat(stringSize);
                if (size <= 0) {

                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                error.show();
                return false;
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
getActivity().getIntent();

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
                        editor.putString(mActivePreference_key, number);
                        editor.commit();

                        String s = (String) preference.getSummary();
                        Log.d("SUMMARY","SUMMARY " +s);

                    }else {

                    EditTextPreference editTextPref = (EditTextPreference) findPreference(mActivePreference_key);
                    editTextPref.setText(number);
                    /*
                    editTextPref.setDefaultValue(editTextPref.getText());
                    editTextPref.setDialogTitle(editTextPref.getText());
                    editTextPref.setDialogMessage(editTextPref.getText());
                    */
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

