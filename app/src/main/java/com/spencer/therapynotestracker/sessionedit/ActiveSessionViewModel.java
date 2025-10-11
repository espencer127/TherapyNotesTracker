package com.spencer.therapynotestracker.sessionedit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.spencer.therapynotestracker.Session;

public class ActiveSessionViewModel extends ViewModel {

    private final MutableLiveData<Session> selectedItem = new MutableLiveData<Session>();

    public ActiveSessionViewModel() {

    }

    public ActiveSessionViewModel(Session session) {
        selectedItem.setValue(session);
    }

    public void selectItem(Session item) {
        selectedItem.setValue(item);
    }

    public LiveData<Session> getSelectedItem() {
        return selectedItem;
    }

}
