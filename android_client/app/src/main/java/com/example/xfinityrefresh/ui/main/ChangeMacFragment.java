package com.example.xfinityrefresh.ui.main;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xfinityrefresh.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;


public class ChangeMacFragment extends Fragment implements View.OnClickListener {
    private ChangeMacViewModel vModel;
    private Button button;

    public ChangeMacFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vModel = ViewModelProviders.of(getActivity()).get(ChangeMacViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_change_mac, container, false);

        button = root.findViewById(R.id.changeMacButton);
        button.setOnClickListener(this);

        vModel.getMac().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (!s.isEmpty()) {
                    button.setEnabled(true);
                    button.setText(String.format(getString(R.string.change_mac), s));
                } else {
                    button.setEnabled(false);
                    button.setText(getString(R.string.change_mac_empty));
                }
            }
        });
        return root;

    }

    @Override
    public void onClick(View v) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Process p;
                final StringBuffer output = new StringBuffer();

                try {
                    p = Runtime.getRuntime().exec("su");
                    DataOutputStream dos = new DataOutputStream(p.getOutputStream());
                    final String mac = vModel.getMac().getValue();
                    if (mac == null || mac.isEmpty())
                        return;

                    dos.writeBytes("busybox ifconfig wlan0 hw ether " + mac + "\n");
                    dos.writeBytes("busybox ip link show wlan0\n");

                    dos.writeBytes("exit\n");
                    dos.flush();
                    dos.close();
                    p.waitFor();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    output.append("New state for wlan0:\n");
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        output.append(line + "\n");
                    }

                    final Pattern regex = Pattern.compile(mac.toLowerCase());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (regex.matcher(output.toString()).find()) {
                                Toast.makeText(getContext(), "Successfully activated pass! You can now connect to the hotspot", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "Error activating pass " + mac, Toast.LENGTH_LONG).show();
                            }
                            ((TextView) getView().findViewById(R.id.changeMacText)).setText(output.toString());
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(r);
        r.run();

    }
}
