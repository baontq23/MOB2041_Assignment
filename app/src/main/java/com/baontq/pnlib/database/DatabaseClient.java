package com.baontq.pnlib.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private Context mContext;
    private static DatabaseClient mInstance;
    private AppDatabase appDatabase;

    public DatabaseClient(Context mContext) {
        this.mContext = mContext;
        appDatabase = Room.databaseBuilder(mContext, AppDatabase.class, "mob2041_db").fallbackToDestructiveMigration().build();
    }

    public static synchronized DatabaseClient getInstance(Context mContext) {
        if (mInstance == null ) {
            mInstance = new DatabaseClient(mContext);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
