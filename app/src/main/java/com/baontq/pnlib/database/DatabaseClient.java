package com.baontq.pnlib.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.baontq.pnlib.model.Book;
import com.baontq.pnlib.model.CallCard;
import com.baontq.pnlib.model.Customer;
import com.baontq.pnlib.model.Genre;
import com.baontq.pnlib.model.Librarian;

import java.util.concurrent.Executors;

public class DatabaseClient {

    private static DatabaseClient mInstance;
    private AppDatabase appDatabase;
    RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    getAppDatabase().librarianDao().store(new Librarian("Bao Nguyen", "baontq", "03072003", "Admin"));
                    getAppDatabase().genreDao().insert(new Genre("Lập trình", "Kệ 1"));
                    getAppDatabase().genreDao().insert(new Genre("Kỹ thuật", "Kệ 4"));
                    getAppDatabase().genreDao().insert(new Genre("Khoa học", "Kệ 2"));
                    getAppDatabase().genreDao().insert(new Genre("Giải thuật", "Kệ 3"));
                    getAppDatabase().bookDao().insert(new Book("C++", 15000.0, 1, 3));
                    getAppDatabase().bookDao().insert(new Book("Lắp robot", 32000.0, 2, 1));
                    getAppDatabase().bookDao().insert(new Book("10 vạn câu hỏi", 12000.0, 3,1));
                    getAppDatabase().bookDao().insert(new Book("javascript algorithms", 24000.0, 4,0));
                    getAppDatabase().customerDao().insert(new Customer("Quang Hưng"));
                    getAppDatabase().customerDao().insert(new Customer("Huy Hoàng"));
                    getAppDatabase().customerDao().insert(new Customer("Dương Nam"));
                    getAppDatabase().callCardDao().store(new CallCard(1, 1, 1, "2022-9-11", "2022-9-13", true));
                    getAppDatabase().callCardDao().store(new CallCard(2, 1, 1, "2022-8-11", "", false));
                    getAppDatabase().callCardDao().store(new CallCard(3, 1, 1, "2022-9-2", "", false));
                    getAppDatabase().callCardDao().store(new CallCard(2, 1, 2, "2022-7-11", "2022-9-13", true));
                    getAppDatabase().callCardDao().store(new CallCard(1, 1, 3, "2022-9-27", "", false));
                }
            });
        }
    };

    public DatabaseClient(Context mContext) {
        appDatabase = Room.databaseBuilder(mContext, AppDatabase.class, "mob2041_db").addCallback(callback).fallbackToDestructiveMigration().build();
    }


    public static synchronized DatabaseClient getInstance(Context mContext) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mContext);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
