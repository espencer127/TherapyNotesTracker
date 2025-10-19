package com.spencer.therapynotestracker.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SessionRepository {
    private final SessionDao mSessionDao;
    private LiveData<List<Session>> mAllSessions;

    // Note that in order to unit test the WordRepository, you have to remove the Application
// dependency. This adds complexity and much more code, and this sample is not about testing.
// See the BasicSample in the android-architecture-components repository at
// https://github.com/googlesamples
    public SessionRepository(Application application) {
        SessionRoomDatabase db = SessionRoomDatabase.getDatabase(application);
        mSessionDao = db.SessionDao();
        mAllSessions = mSessionDao.getAlphabetizedSessions();
    }

    // Room executes all queries on a separate thread.
// Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Session>> getAllSessions() {
        return mAllSessions;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
// that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Session session) {
        SessionRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSessionDao.insert(session);
        });
        mAllSessions = mSessionDao.getAlphabetizedSessions();
    }

    public void insertAll(List<Session> sessions) {
        SessionRoomDatabase.databaseWriteExecutor.execute(() -> {
            for (Session session : sessions) {
                mSessionDao.insert(session);
            }
        });
        mAllSessions = mSessionDao.getAlphabetizedSessions();
    }

    public void delete(String date) {
        SessionRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSessionDao.delete(date);
        });
        mAllSessions = mSessionDao.getAlphabetizedSessions();
    }

    public void deleteAll() {
        SessionRoomDatabase.databaseWriteExecutor.execute(mSessionDao::deleteAll);

        mAllSessions = mSessionDao.getAlphabetizedSessions();
    }

    public void edit(Session session) {
        SessionRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSessionDao.updateSession(session.getDate(), session.getAgenda(), session.getNotes(), session.getTherapist());
        });
        mAllSessions = mSessionDao.getAlphabetizedSessions();
    }

    public void editItem(String dateDate, String newAgenda) {
        SessionRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSessionDao.updateSession(dateDate, newAgenda);
        });
        mAllSessions = mSessionDao.getAlphabetizedSessions();

    }
}
