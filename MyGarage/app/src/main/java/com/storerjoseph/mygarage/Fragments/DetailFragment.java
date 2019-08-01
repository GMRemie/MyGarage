package com.storerjoseph.mygarage.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.storerjoseph.mygarage.NetworkClass;
import com.storerjoseph.mygarage.R;
import com.storerjoseph.mygarage.Vehicle;

import org.apache.commons.io.IOUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {

    private static final String ARG_VEHICLE = "mygarage.passed.vehicle";
    private static final String Garage_Account = "MyGarage.Storer.Account";
    private DatabaseReference dataRef;
    private List<Vehicle> vehicles;

    private Vehicle vehicle;

    public static DetailFragment newInstance(Vehicle vehicle, GoogleSignInAccount account) {
        
        Bundle args = new Bundle();
        
        DetailFragment fragment = new DetailFragment();
        args.putSerializable(ARG_VEHICLE,vehicle);
        args.putParcelable(Garage_Account,account);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vehicle_details,container,false);
    }

    private final Button.OnClickListener blistener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragView,WebView.newInstance(vehicle)).addToBackStack("back").commit();
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // only one menu item
        dataRef.child("vehicles").addListenerForSingleValueEvent(vehListener);

        return super.onOptionsItemSelected(item);
    }

    private final ValueEventListener vehListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Vehicle svc = snapshot.getValue(Vehicle.class);
                if (svc.vinNumber == vehicle.vinNumber){
                    dataRef.child("vehicles").child(snapshot.getKey()).removeValue();
                    // pop back
                    getFragmentManager().popBackStack();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        vehicle = (Vehicle) arguments.getSerializable(ARG_VEHICLE);

        GoogleSignInAccount account = arguments.getParcelable(Garage_Account);

        // firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dataRef = database.getReference(account.getId());



        getActivity().setTitle(vehicle.nickName);
        Button shopforparts = getActivity().findViewById(R.id.shopForParts);
        shopforparts.setOnClickListener(blistener);
        // update detail view

        setHasOptionsMenu(true);


        // image
        NetworkClass networkClass = new NetworkClass();
        if (networkClass.hasConnection(getContext())){
            // CarMD API requirements
            String carMD_Endpoint = "https://api.carmd.com/v3.0/image?vin=";
            String urlAndVIN = carMD_Endpoint + vehicle.vinNumber;
            DataTask task = new DataTask();
            task.execute(urlAndVIN);
        }

        // form
        TextView carName = getActivity().findViewById(R.id.detailName);
        TextView carYear = getActivity().findViewById(R.id.detailYear);
        TextView carMake = getActivity().findViewById(R.id.detailMake);
        TextView carModel = getActivity().findViewById(R.id.detailModel);
        TextView carEngine = getActivity().findViewById(R.id.detailEngine);
        TextView carTrim = getActivity().findViewById(R.id.detailTrim);
        TextView carTrans = getActivity().findViewById(R.id.detailTransmission);

        carName.setText(vehicle.nickName);
        carYear.setText(vehicle.year.toString());
        carMake.setText(vehicle.make);
        carModel.setText(vehicle.model);
        if (vehicle.engine != "" || vehicle.engine != null){
            carEngine.setText(vehicle.engine);
        }
        if (vehicle.trim != "" || vehicle.trim != null) {
            carTrim.setText(vehicle.trim);

        }
        if (vehicle.transmission != "" || vehicle.transmission != null) {
            carTrans.setText(vehicle.transmission);
        }

    }


    private String getCarImage(String stringUrl){
        try {
            URL url = new URL(stringUrl);
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("content-type","application/json");
            String carMD_AuthKey = "Basic NjdhMWI4NDEtNTliYi00YTZjLWFhNTctYzBiNTFkZTYyNzAz";
            connection.setRequestProperty("authorization", carMD_AuthKey);
            String carMD_Token = "523b9cded5df4cd8bb564364e3fe8971";
            connection.setRequestProperty("partner-token", carMD_Token);
            connection.connect();

            InputStream is = connection.getInputStream();
            String data = IOUtil.toString(is,"UTF-8");
            is.close();
            connection.disconnect();

            return data;

        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    private class DataTask extends AsyncTask<String,Integer, String>{

        @Override
        protected String doInBackground(String... strings) {

            String data = getCarImage(strings[0]);

            try {
                JSONObject outer = new JSONObject(data);
                JSONObject vehdata = outer.getJSONObject("data");
                String image_path = vehdata.getString("image");
                Log.i("TAG", "doInBackground: AAA" + data);
                return image_path;

            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String imagePath) {
            Log.i("TAG", "onPostExecute: Loaded into post executed " + imagePath);
            ImageView imgView = getActivity().findViewById(R.id.detailImage);
            if ( imagePath.equals("") || imagePath == null){
                // empty
            }else {
                Picasso.get().load(imagePath).into(imgView);
            }

        }
    }



}




