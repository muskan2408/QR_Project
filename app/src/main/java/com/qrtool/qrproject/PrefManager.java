package com.qrtool.qrproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Lincoln on 05/05/16.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "welcome";
    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";


    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    public static final String KEY_MOBILE = "contact";

    // Email address (make variable public to access from outside)
    public static final String KEY_PIN = "pin";
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void createUserLoginSession(String contact,int pin){
// Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_MOBILE, "contact" );

        // Storing email in pref
        editor.putString(KEY_PIN,"pin");

        // commit changes
        editor.commit();

    }

    public boolean checkLogin(){
        // Check login status
        if(!this.isUserLoggedIn()){

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, SignIn.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            return true;
        }
        return false;
    }
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();

        // user name
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));

        // user email id
        user.put(KEY_PIN, pref.getString(KEY_PIN, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, SignIn.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

}
