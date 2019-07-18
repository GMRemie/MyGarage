package com.storerjoseph.mygarage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkClass {

    public NetworkClass() {
    }
    public static final Boolean hasConnection(Context context){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null){

            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info != null){
                boolean isConnected = info.isConnected();
                return isConnected;
            }
        }else{
            return false;
        }
        return false;
    }
}
