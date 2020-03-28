package com.example.xfinityrefresh.ui.main;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xfinityrefresh.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;


public class CodeScannerFragment extends Fragment {

    private SurfaceView cameraView;
    private CameraSource cameraSource;
    private TextView textView;

    private ChangeMacViewModel vModel;


    public CodeScannerFragment() {
    }

    public static CodeScannerFragment newInstance() {
        CodeScannerFragment fragment = new CodeScannerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vModel = ViewModelProviders.of(getActivity()).get(ChangeMacViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_code_scanner, container, false);


        cameraView = (SurfaceView) root.findViewById(R.id.cameraView);
        textView = (TextView) root.findViewById(R.id.scannerText);

        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(getContext())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        cameraSource = new CameraSource
                .Builder(getContext(), barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1024, 1024)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d("EEE", "" + width + "" + height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        final Pattern macRegex = Pattern.compile("^[a-fA-F0-9:]{17}$");
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) { 
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                getActivity().runOnUiThread(new Runnable() {    // Use the post method of the TextView
                    public void run() {
                        if (barcodes.size() != 0) {
                            String value = barcodes.valueAt(0).displayValue;
                            if(macRegex.matcher(value).matches()) {
                                textView.setText(String.format(getString(R.string.pass_detected), value));
                                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.customGreen));
                                vModel.getMac().setValue(value);
                                Log.d("set0", "set");
                            } else {
                                textView.setText(R.string.wrong_qr);
                                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.customRed));

                            }
                        } else {
                            textView.setText("");
                        }
                    }
                });
            }
        });

        return root;
    }
}
