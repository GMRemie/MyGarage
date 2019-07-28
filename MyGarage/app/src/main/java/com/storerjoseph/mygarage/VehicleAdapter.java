package com.storerjoseph.mygarage;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class VehicleAdapter extends BaseAdapter {
    private static final String TAG = "VehicleAdapter";
    private static final int ID_CONSTANT = 0x010101010;
    private final ArrayList<Vehicle> vehicles;
    private final Context mContext;

    public VehicleAdapter(ArrayList<Vehicle> _vehicles, Context mContext) {
        this.vehicles = _vehicles;
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        if (vehicles != null){
            return vehicles.size();
        }else{
            Log.i(TAG, "getCount: EMPTY");
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(vehicles != null && position >= 0 && position <= vehicles.size()){
            return vehicles.get(position);
        }else {
            Log.i(TAG, "getItem: Null or out of bounds!");
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (vehicles != null && position >= 0 && position <= vehicles.size()){
            return ID_CONSTANT + position;
        }else {
            Log.i(TAG, "getItemId: NULL collection or out of bounds");
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.vehicle_adapter,parent,false);
        }

        Vehicle va = (Vehicle)getItem(position);

        TextView vehNick = convertView.findViewById(R.id.adapterNick);
        TextView vehicleDetailOne = convertView.findViewById(R.id.adapterDetailOne);
        TextView vehicleDetailTwo = convertView.findViewById(R.id.adapterDetailTwo);

        String vehicleDetailTextOne = va.year + ", " + va.make + " " + va.model;
        String vehicleDetailTextTwo = "VIN: " + va.vinNumber;

        vehNick.setText(va.nickName);
        vehicleDetailOne.setText(vehicleDetailTextOne);
        vehicleDetailTwo.setText(vehicleDetailTextTwo);
        return convertView;
    }
}
