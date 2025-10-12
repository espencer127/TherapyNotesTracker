package com.spencer.therapynotestracker;

import androidx.lifecycle.LiveData;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SessionDao {

    /**
     * The Room Magic is in this file, where you map a Java method call to an SQL query.
     *
     * When you are using complex data types, such as Date, you have to also supply type converters.
     * To keep this example basic, no types that require type converters are used.
     * See the documentation at
     * https://developer.android.com/topic/libraries/architecture/room.html#type-converters
     */


        // LiveData is a data holder class that can be observed within a given lifecycle.
        // Always holds/caches latest version of data. Notifies its active observers when the
        // data has changed. Since we are getting all the contents of the database,
        // we are notified whenever any of the database contents have changed.
        @Query("SELECT * FROM session_table ORDER BY id ASC")
        LiveData<List<Session>> getAlphabetizedSessions();

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        void insert(Session session);

        @Query("DELETE FROM session_table")
        void deleteAll();

        @Query("DELETE FROM session_table WHERE date = :date")
        void delete(String date);

        @Query("UPDATE session_table SET agenda = :newAgenda, notes = :newNotes, therapist = :newTherapist WHERE date = :date")
        void updateSession(String date, String newAgenda, String newNotes, String newTherapist);


}
