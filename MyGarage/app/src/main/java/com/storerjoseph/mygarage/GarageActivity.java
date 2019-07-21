package com.storerjoseph.mygarage;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.storerjoseph.mygarage.Fragments.AddFragment;

import androidx.appcompat.app.AppCompatActivity;

public class GarageActivity extends AppCompatActivity {

    private GoogleSignInAccount account;
    public static final String TAG = "GarageActivty";
    private NetworkClass networkClass;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.fab);
        Intent launchedIntent = getIntent();

        if (launchedIntent.hasExtra(MainActivity.Garage_EXTRA)){
            account = (GoogleSignInAccount) launchedIntent.getParcelableExtra(MainActivity.Garage_EXTRA);
        }

        // check for network before calling the vehicle lists
        networkClass = new NetworkClass();
        if (networkClass.hasConnection(this)){
            // since we have internet let's only set our onclicklistener for our floating action button if we do have internet
            fab.setOnClickListener(fabListener);
            // Load vehicles here

        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setTitle("Network Error").setMessage("Error connecting to network. Please try again later")
                    .setPositiveButton("OK",null);
            alert.show();
        }

    }




    FloatingActionButton.OnClickListener fabListener = new FloatingActionButton.OnClickListener(){

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: Fab clicked");
            getSupportFragmentManager().beginTransaction().add(R.id.fragView,AddFragment.newInstance()).addToBackStack("backstack").commit();
            v.setVisibility(View.INVISIBLE);

        }
    };
}
