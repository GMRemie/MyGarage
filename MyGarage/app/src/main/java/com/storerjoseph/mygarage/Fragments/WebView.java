package com.storerjoseph.mygarage.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.storerjoseph.mygarage.R;
import com.storerjoseph.mygarage.Vehicle;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



public class WebView extends Fragment {

    private static final String ARG_VEHICLE = "mygarage.passed.vehicle";

    public static WebView newInstance(Vehicle vehicle) {

        Bundle args = new Bundle();

        WebView fragment = new WebView();
        args.putSerializable(ARG_VEHICLE,vehicle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.web_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        Bundle args = getArguments();
        Vehicle vehicle = (Vehicle) Objects.requireNonNull(args).getSerializable(ARG_VEHICLE);

        setupWebView(Objects.requireNonNull(vehicle));
    }

    private void setupWebView(Vehicle vehicle){
        android.webkit.WebView webView = Objects.requireNonNull(getActivity()).findViewById(R.id.webViews);
        String carIDUrl = "https://www.carid.com/";
        String actualUrl = carIDUrl + "/" + vehicle.year.toString() + "-" + vehicle.make + "-" + vehicle.model + "-accessories/";

        // ENABLE JS
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);

        // Client
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl(actualUrl);

    }
}
