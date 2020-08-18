package com.example.pricecheckerapp.Classes;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context _context;



    // shared pref mode
    int PRIVATE_MODE = 0;



    // Shared preferences file name
    private static final String PREF_NAME = "branch";



//    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String Branch = "Branch";



    public PrefManager(Context context) {

        this._context = context;

        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

        editor = pref.edit();

    }



    public void set_branch(String branch) {

        editor.putString(Branch, branch);

        editor.commit();

    }



    public String get_branch() {

        return pref.getString(Branch, "10.0.0.0");

    }



}


