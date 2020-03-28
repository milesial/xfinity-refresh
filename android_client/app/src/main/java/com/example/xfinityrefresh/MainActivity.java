package com.example.xfinityrefresh;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.xfinityrefresh.ui.main.ChangeMacFragment;
import com.example.xfinityrefresh.ui.main.CodeScannerFragment;
import com.example.xfinityrefresh.ui.main.WifiConnectFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        WifiConnectFragment wifiFragment = new WifiConnectFragment();
        fragmentTransaction.add(R.id.linearLayout, wifiFragment);
        CodeScannerFragment camFragment = new CodeScannerFragment();
        fragmentTransaction.add(R.id.linearLayout, camFragment);
        ChangeMacFragment f = new ChangeMacFragment();
        fragmentTransaction.add(R.id.linearLayout, f);
        fragmentTransaction.commit();


    }


}
