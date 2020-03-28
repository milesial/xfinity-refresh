package com.example.xfinityrefresh.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChangeMacViewModel extends ViewModel {
    private MutableLiveData<String> macAddr;

    public MutableLiveData<String> getMac() {
        if (macAddr == null) {
            macAddr = new MutableLiveData<String>();
        }
        return macAddr;
    }
}
