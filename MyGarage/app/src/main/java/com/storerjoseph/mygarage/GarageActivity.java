package com.storerjoseph.mygarage;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.storerjoseph.mygarage.Fragments.AddFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class GarageActivity extends AppCompatActivity {

    private GoogleSignInAccount account;
    public static final String TAG = "GarageActivty";
    private NetworkClass networkClass;
    private FloatingActionButton fab;
    private FirebaseDatabase database;
    private DatabaseReference dataRef;
    private ArrayList<Vehicle> vehicles;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        Intent launchedIntent = getIntent();
        vehicles = new ArrayList<>();
        listView = findViewById(R.id.listView);

        if (launchedIntent.hasExtra(MainActivity.Garage_EXTRA)){
            account = (GoogleSignInAccount) launchedIntent.getParcelableExtra(MainActivity.Garage_EXTRA);
        }

        // check for network before calling the vehicle lists
        networkClass = new NetworkClass();
        if (networkClass.hasConnection(this)){
            // since we have internet let's only set our onclicklistener for our floating action button if we do have internet
            fab.setOnClickListener(fabListener);
            // Load vehicles here
            database = FirebaseDatabase.getInstance();
            dataRef = database.getReference(account.getId()).child("vehicles");
            dataRef.addValueEventListener(vehListener);

        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setTitle("Network Error").setMessage("Error connecting to network. Please try again later")
                    .setPositiveButton("OK",null);
            alert.show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setTitle("My Garage");
        fab.show();

    }


    FloatingActionButton.OnClickListener fabListener = new FloatingActionButton.OnClickListener(){

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: Fab clicked + " + account.getEmail());
            getSupportFragmentManager().beginTransaction().add(R.id.fragView,AddFragment.newInstance(account)).addToBackStack("backstack").commit();
            fab.hide();

        }
    };

    // event listener
    ValueEventListener vehListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            vehicles.removeAll(vehicles);
            Log.i(TAG, "onDataChange: On Data changed!");
            // Get Post object and use the values to update the UI
            Log.i(TAG, "onDataChange: Data changed " + dataSnapshot.getChildrenCount() );

            for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                Vehicle vehicle = snapshot.getValue(Vehicle.class);
                vehicles.add(vehicle);
                reloadData();
            }
            // ...
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };

    private void reloadData(){
        VehicleAdapter vehicleAdapter = new VehicleAdapter(vehicles,this);
        listView.setAdapter(vehicleAdapter);
        listView.setOnItemClickListener(listclickListener);
    }




    ListView.OnItemClickListener listclickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Vehicle selected = vehicles.get(position);

        }
    };
}
