package com.storerjoseph.mygarage.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
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

import static android.widget.Toast.LENGTH_SHORT;

public class AddFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AddFragment";
    private static final String Garage_Account = "MyGarage.Storer.Account";
    private DatabaseReference dataRef;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    public static AddFragment newInstance(GoogleSignInAccount account) {
        
        Bundle args = new Bundle();
        args.putParcelable(Garage_Account,account);
        AddFragment fragment = new AddFragment();
        fragment.setArguments(args);
        return fragment;
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
        GoogleSignInAccount account = arguments.getParcelable(Garage_Account);

        // firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dataRef = database.getReference(account.getId());

        getActivity().setTitle("Create Vehicle");
        getActivity().findViewById(R.id.saveVehicle).setOnClickListener(this);
        getActivity().findViewById(R.id.vehicleScan).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        //TODO:: NETWORK CHECK
        if (v == getActivity().findViewById(R.id.saveVehicle)){
            // Week 3 need to check to make sure the fields are filled accordingly
            EditText vehicleNick = getActivity().findViewById(R.id.vehicleNick);
            EditText vehicleVin = getActivity().findViewById(R.id.vehicleVIN);
            saveVehicle(vehicleVin.getText().toString());
        }else{
            // get an image
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
            else
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();
            // disable buttons
            detector.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText texts) {
                            processTextRecognitionResult(texts);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "onFailure: Text recognition failed!");
                            Toast.makeText(getContext(),"Text recognition failed!",LENGTH_SHORT).show();

                            e.printStackTrace();
                        }
                    });

        }
    }

    private void processTextRecognitionResult(FirebaseVisionText texts){
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if(blocks.size() == 0){
            Log.i(TAG, "processTextRecognitionResult: No Text has been found!");
            Toast.makeText(getContext(),"No VIN detected!",LENGTH_SHORT).show();

            return;
        }
        for (int i = 0; i < blocks.size(); i++){
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++){
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++){
                    Log.i(TAG, "processTextRecognitionResult: Text found " + elements.get(k).getText());
                    if (elements.get(k).getText().length() == 17){
                        EditText vintext = getActivity().findViewById(R.id.vehicleVIN);
                        vintext.setText(elements.get(k).getText());
                    }else{
                        Toast.makeText(getContext(),"No VIN detected!",LENGTH_SHORT).show();
                    }
                }
            }
        }

    }


    // SAVE

    private void saveVehicle(String VIN){
        //dataRef.child("vehicles").push().setValue(vehicle);

        // has connection?
        NetworkClass networkClass = new NetworkClass();

        if (networkClass.hasConnection(getContext())){

            // CarMD API requirements
            String carMD_Endpoint = "https://api.carmd.com/v3.0/decode?vin=";
            String urlAndVIN = carMD_Endpoint + VIN;
            DataTask task = new DataTask();
            task.execute(urlAndVIN);

        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext())
                    .setTitle("Network Error").setMessage("Error connecting to network. Please try again later")
                    .setPositiveButton("OK",null);
            alert.show();
        }
    }

    private String getCarMDData(String stringUrl){
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

    private class DataTask extends AsyncTask<String,Integer, Vehicle>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "onPreExecute: Launched DataTask");
        }

        @Override
        protected Vehicle doInBackground(String... strings) {
            Log.i(TAG, "doInBackground: URL path" + strings[0]);
            String data = getCarMDData(strings[0]);
            try {
                JSONObject outer = new JSONObject(data);
                JSONObject vehdata = outer.getJSONObject("data");
                Integer year = vehdata.getInt("year");
                String make = vehdata.getString("make");
                String model = vehdata.getString("model");
                String engine = vehdata.getString("engine");
                String trim = vehdata.getString("trim");
                String transmission = vehdata.getString("transmission");
                return new Vehicle(year,make,model,trim,transmission,engine);

            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Vehicle vehicle) {
            super.onPostExecute(vehicle);
            if (vehicle != null){
                // we have a vehicle
                EditText nick = getActivity().findViewById(R.id.vehicleNick);
                EditText vin = getActivity().findViewById(R.id.vehicleVIN);
                vehicle.nickName = nick.getText().toString();
                vehicle.vinNumber = vin.getText().toString();

                Toast.makeText(getContext(),"Vehicle has been saved!",LENGTH_SHORT).show();
                dataRef.child("vehicles").push().setValue(vehicle);


            }
            else{
                Toast.makeText(getContext(),"Error! Something went wrong!",Toast.LENGTH_LONG).show();
            }
        }
    }


}
