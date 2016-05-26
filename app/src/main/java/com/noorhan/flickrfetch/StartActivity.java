package com.noorhan.flickrfetch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    Button fetchbtn, tutorialbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences mypref= PreferenceManager.getDefaultSharedPreferences(this);
        String color=mypref.getString("color", "#1119DF");
        Log.i("color",color);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(color));
        setSupportActionBar(toolbar);

        fetchbtn=(Button)findViewById(R.id.start_fetch_button);
        fetchbtn.setOnClickListener(this);
        fetchbtn.setBackgroundColor(Color.parseColor(color));

        tutorialbtn=(Button)findViewById(R.id.start_tutorial_button2);
        tutorialbtn.setOnClickListener(this);
        tutorialbtn.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_exit_action_menu) {
            finish();
            System.exit(0);
        }

        else if (id==R.id.main_settings_action_menu)
        {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {

        if (view==fetchbtn)
        {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        else if (view==tutorialbtn)
        {
            Intent intent = new Intent(this,TutorialActivity.class);
            startActivity(intent);
        }
    }
}
