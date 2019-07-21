package com.storerjoseph.mygarage.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.storerjoseph.mygarage.R;
import com.storerjoseph.mygarage.Vehicle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "AddFragment";
    private static final String Garage_Account = "MyGarage.Storer.Account";
    private GoogleSignInAccount account;
    private FirebaseDatabase database;
    private DatabaseReference dataRef;
    
    public static AddFragment newInstance(GoogleSignInAccount account) {
        
        Bundle args = new Bundle();
        args.putParcelable(Garage_Account,account);
        AddFragment fragment = new AddFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.garage_add,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        account = arguments.getParcelable(Garage_Account);

        // firebase setup
        database = FirebaseDatabase.getInstance();
        dataRef = database.getReference(account.getId());

        getActivity().setTitle("Create Vehicle");
        getActivity().findViewById(R.id.saveVehicle).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        //TODO:: NETWORK CHECK
        if (v == getActivity().findViewById(R.id.saveVehicle)){
            // Week 3 need to check to make sure the fields are filled accordingly
            EditText vehicleNick = getActivity().findViewById(R.id.vehicleNick);
            EditText vehicleVin = getActivity().findViewById(R.id.vehicleVIN);
            Vehicle vehicle = new Vehicle(vehicleNick.getText().toString(),vehicleVin.getText().toString());
            dataRef.child("vehicles").push().setValue(vehicle);
        }else{
        }
    }
}
