package com.spencer.therapynotestracker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Session.class}, version = 1, exportSchema = false)
public abstract class SessionRoomDatabase extends RoomDatabase {

    abstract SessionDao SessionDao();

    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile SessionRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static SessionRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SessionRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    SessionRoomDatabase.class, "Session_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}

