package com.noorhan.flickrfetch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    Spinner noOfPostsSpinner;
    ArrayList<String> valuesArray;
    Button pinkbtn,bluebtn;
    boolean userIsInteracting=false;
    SharedPreferences mypref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //checks user prefered toolbar color
        SharedPreferences mypref= PreferenceManager.getDefaultSharedPreferences(this);
        String color=mypref.getString("color", "#1119DF");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(color));
        setSupportActionBar(toolbar);

        //initialize components
        pinkbtn=(Button) findViewById(R.id.settings_pink_button2);
        pinkbtn.setOnClickListener(this);

        bluebtn=(Button) findViewById(R.id.settings_blue_button);
        bluebtn.setOnClickListener(this);


        noOfPostsSpinner =(Spinner) findViewById(R.id.settings_options_spinner);

        valuesArray=new ArrayList<String>();
        valuesArray.add("20");
        valuesArray.add("50");
        valuesArray.add("100");
        valuesArray.add("150");
        valuesArray.add("200");


        ArrayAdapter postsAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,valuesArray);
        postsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        noOfPostsSpinner.setAdapter(postsAdapter);

        noOfPostsSpinner.setOnItemSelectedListener(this);

        //checks previous position for spinner
        int spinnerposition = mypref.getInt("position",0);
        if (spinnerposition!= 0) {
            //get your values to restore...
            noOfPostsSpinner.setSelection(spinnerposition);
        }


    }



    //saving selected number of posts
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mypref = PreferenceManager.getDefaultSharedPreferences(this);

        //only sets no of posts and saves position if user is reacting
        if (userIsInteracting) {
            mypref.edit().putString("NoOfPosts", ((TextView) view).getText().toString()).commit();
            mypref.edit().putInt("position",position).commit();
            Toast.makeText(this, String.valueOf("You will view " + ((TextView) view).getText().toString()) + " posts", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //saving theme color
    @Override
    public void onClick(View view) {
        if (view==bluebtn)
        {
            SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(this);
            mypref.edit().putString("color", "#1119DF").commit();
            Intent intent= new Intent(this,StartActivity.class);
            startActivity(intent);
            finish();
        }
        else if(view==pinkbtn)
        {
            SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(this);
            mypref.edit().putString("color", "#D30086").commit();
            Intent intent= new Intent(this,StartActivity.class);
            startActivity(intent);
            finish();
        }
    }

// checks if user pressed on spinner
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            Intent intent= new Intent(this,StartActivity.class);
            startActivity(intent);
            finish();

        }
        return true;
    }
}
