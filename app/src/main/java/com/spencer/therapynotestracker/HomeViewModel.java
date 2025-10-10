package com.spencer.therapynotestracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<String>> binsLiveData = new MutableLiveData<>();

    public void setBins(List<String> bins) {
        binsLiveData.setValue(bins);
    }

    public LiveData<List<String>> getBins() {
        return binsLiveData;
    }

    public void addItem(String item) {
        List<String> temp = binsLiveData.getValue();
        temp.add(item);
        binsLiveData.setValue(temp);
    }

    public void addItems(List<String> items) {
        List<String> temp = binsLiveData.getValue();

        for (int i=0; i<items.size();i++) {
            temp.add(items.get(i));
        }
        binsLiveData.setValue(temp);
    }

}