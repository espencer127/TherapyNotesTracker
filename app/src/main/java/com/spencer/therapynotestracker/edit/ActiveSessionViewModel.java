package com.spencer.therapynotestracker.edit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.spencer.therapynotestracker.database.Session;
import com.spencer.therapynotestracker.database.SessionRepository;

public class ActiveSessionViewModel extends AndroidViewModel {

    private SessionRepository mRepository;

    private final MutableLiveData<Session> selectedItem = new MutableLiveData<Session>();

    public ActiveSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = new SessionRepository(application);
    }

    public void selectItem(Session item) {
        selectedItem.setValue(item);
    }

    public LiveData<Session> getSelectedItem() {
        return selectedItem;
    }

    public void deleteItem(String dateDate) {
        mRepository.delete(dateDate);
    }

    public void editItem(Session session) {
        mRepository.edit(session);
    }

    public void editItem(String date, String newAgenda) {
        mRepository.editItem(date, newAgenda);
    }
}
