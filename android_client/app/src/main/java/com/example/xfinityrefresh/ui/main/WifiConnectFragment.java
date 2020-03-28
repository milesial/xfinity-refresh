package com.example.xfinityrefresh.ui.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.xfinityrefresh.R;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class WifiConnectFragment extends Fragment implements View.OnClickListener {

    private static final int FINE_LOCATION_PERMISSION = 42;
    private static final int[] WIFI_ICONS = {
            R.drawable.stat_notify_wifi_in_range,
            R.drawable.ic_wifi_signal_0,
            R.drawable.ic_wifi_signal_1,
            R.drawable.ic_wifi_signal_2,
            R.drawable.ic_wifi_signal_3,
            R.drawable.ic_wifi_signal_4
    };
    private Snackbar snackbar;
    private ScanResult xfinitywifi;
    private BroadcastReceiver wifiStateReceiver, wifiScanReceiver, supplicantStateReceiver;
    private ImageView imgView;
    private TextView mainText, subText;
    private View topCard;
    private ProgressBar progressBar;

    private enum State {
        WIFI_DISABLED,
        SCANNING,
        FOUND
    }

    private State currentState = State.WIFI_DISABLED;


    public static WifiConnectFragment newInstance() {
        WifiConnectFragment fragment = new WifiConnectFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wifi_connect, container, false);

        imgView = root.findViewById(R.id.imageView);
        mainText = root.findViewById(R.id.coStatusMain);
        subText = root.findViewById(R.id.coStatusSub);
        topCard = root.findViewById(R.id.topCard);
        progressBar = root.findViewById(R.id.progressBar);

        topCard.setOnClickListener(this);
        setState(State.WIFI_DISABLED);
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(wifiStateReceiver);
        getContext().unregisterReceiver(wifiScanReceiver);
        getContext().unregisterReceiver(supplicantStateReceiver);

    }

    @Override
    public void onResume() {
        super.onResume();


        String[] perms = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CAMERA};
        if (!hasPermissions(perms)) {
            ActivityCompat.requestPermissions(getActivity(), perms, FINE_LOCATION_PERMISSION);
        }


        wifiScanReceiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent intent) {
                checkScan();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(wifiScanReceiver, intentFilter);

        wifiStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                Context appContext = getActivity().getApplicationContext();
                WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);

                if (wifiManager.isWifiEnabled()) {
                    WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                    if (connectionInfo != null && connectionInfo.getSSID().equals("\"xfinitiwifi\"")) {
                        Log.d("WIFI", "connected");
                    } else {
                        wifiManager.startScan();
                        setState(State.SCANNING);
                    }
                } else {
                    setState(State.WIFI_DISABLED);
                }
            }
        };

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        getContext().registerReceiver(wifiStateReceiver, intentFilter2);


        supplicantStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                Context appContext = getActivity().getApplicationContext();
                WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
                WifiInfo connectionInfo = wifiManager.getConnectionInfo();

            }
        };
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        getContext().registerReceiver(supplicantStateReceiver, intentFilter3);
    }


    public void setState(State state) {
        currentState = state;
        Drawable icon;
        switch (state) {
            case WIFI_DISABLED:
                progressBar.setVisibility(View.INVISIBLE);
                topCard.setClickable(true);
                int c = ContextCompat.getColor(getContext(), R.color.customGreyLight);
                topCard.setBackgroundColor(c);
                icon = getResources().getDrawable(R.drawable.wifi_off);
                icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.customGrey), PorterDuff.Mode.SRC_ATOP);
                imgView.setImageDrawable(icon);
                mainText.setText(R.string.wifi_down_main);
                mainText.setTextColor(ContextCompat.getColor(getContext(), R.color.customRed));
                subText.setText(R.string.wifi_down_sub);
                break;
            case SCANNING:
                progressBar.setVisibility(View.VISIBLE);
                topCard.setClickable(false);
                icon = getResources().getDrawable(R.drawable.wifi_0);
                icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.customGrey), PorterDuff.Mode.SRC_ATOP);
                imgView.setImageDrawable(icon);

                mainText.setText(R.string.wait_hotspot_main);
                mainText.setTextColor(ContextCompat.getColor(getContext(), R.color.customYellow));
                subText.setText(R.string.wait_hotspot_sub);
                break;
            case FOUND:
                progressBar.setVisibility(View.INVISIBLE);
                int signal5 = WifiManager.calculateSignalLevel(xfinitywifi.level, 5);
                int signal100 = WifiManager.calculateSignalLevel(xfinitywifi.level, 100);
                icon = getResources().getDrawable(WIFI_ICONS[signal5 + 1]);
                icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.customGrey), PorterDuff.Mode.SRC_ATOP);
                imgView.setImageDrawable(icon);
                mainText.setText(R.string.found_hotspot_main);
                mainText.setTextColor(ContextCompat.getColor(getContext(), R.color.customGreen));
                subText.setText(String.format(getResources().getString(R.string.found_hotspot_sub), signal100));
                //connectToNetwork();
        }
    }

    public void refreshState() {
        setState(currentState);
    }

    private boolean checkScan() {
        Context appContext = getActivity().getApplicationContext();
        WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> results = wifiManager.getScanResults();
        int best_level = Integer.MIN_VALUE;
        ScanResult best_match = null;
        for (ScanResult res : results) {
            if (res.SSID.equals("xfinitywifi") && res.level > best_level) {
                best_match = res;
                best_level = res.level;
            }
        }

        xfinitywifi = best_match;
        if (best_match == null) {
            return false;
        } else {
            setState(State.FOUND);
            //connectToNetwork();
            return true;
        }
    }

    @Override
    public void onClick(View v) {

        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        new WifiOnTask(getView()).execute(wifiManager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == FINE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(getView(), "Please accept, so the app can scan Wi-Fi", Snackbar.LENGTH_LONG);
            }
        }
    }

    private boolean hasPermissions(String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private void connectToNetwork() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo.getSSID().equals("xfinitywifi")) {
            return;
        }

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"xfinitywifi\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }


    static public class WifiOnTask extends AsyncTask<WifiManager, Void, Boolean> {
        private WeakReference<View> view;

        WifiOnTask(View view) {
            this.view = new WeakReference<>(view);
        }

        protected void onPreExecute() {
            if (view != null)
                ((ProgressBar) view.get().findViewById(R.id.progressBar)).setIndeterminate(true);
        }

        protected Boolean doInBackground(WifiManager... params) {
            long time = System.currentTimeMillis();

            WifiManager wm = params[0];
            if (!wm.isWifiEnabled()) {
                boolean res = wm.setWifiEnabled(true);

                if (!res) {
                    return false;
                }
            }
            wm.startScan();
            return true;
        }

        protected void onPostExecute(Boolean result) {
            if (view != null) {
                ((ProgressBar) view.get().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);

                if (!result) {
                    Snackbar.make(view.get(), "Please enable Wi-Fi", Snackbar.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.get().getContext(), "Wi-Fi enabled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}