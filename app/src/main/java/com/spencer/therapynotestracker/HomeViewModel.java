package com.spencer.therapynotestracker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.spencer.therapynotestracker.database.Session;
import com.spencer.therapynotestracker.database.SessionRepository;

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

    public void insert(Session session) {
        mRepository.insert(session);
    }

}