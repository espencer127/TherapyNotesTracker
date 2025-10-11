package com.spencer.therapynotestracker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private SessionRepository mRepository;

    private LiveData<List<Session>> sessionsListLiveData;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mRepository = new SessionRepository(application);
        sessionsListLiveData = mRepository.getAllSessions();
    }

    public LiveData<List<Session>> getSessions() {
        return sessionsListLiveData;
    }

    public void addItems(List<Session> items) {
        for (int i=0; i<items.size();i++) {
            insert(items.get(i));
        }
    }

    void insert(Session session) {
        mRepository.insert(session);
    }

}