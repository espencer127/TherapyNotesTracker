package com.spencer.therapynotestracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<SessionModel>> binsLiveData = new MutableLiveData<>();

    public void setBins(List<SessionModel> bins) {
        binsLiveData.setValue(bins);
    }

    public LiveData<List<SessionModel>> getBins() {
        return binsLiveData;
    }

    public void addItem(SessionModel item) {
        List<SessionModel> temp = binsLiveData.getValue();
        temp.add(item);
        binsLiveData.setValue(temp);
    }

    public void addItems(List<SessionModel> items) {
        List<SessionModel> temp = binsLiveData.getValue();

        for (int i=0; i<items.size();i++) {
            temp.add(items.get(i));
        }
        binsLiveData.setValue(temp);
    }

}