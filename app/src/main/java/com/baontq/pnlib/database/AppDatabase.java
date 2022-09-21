package com.baontq.pnlib.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.baontq.pnlib.dao.GenreDao;
import com.baontq.pnlib.dao.LibrarianDao;
import com.baontq.pnlib.model.Book;
import com.baontq.pnlib.model.Genre;
import com.baontq.pnlib.model.Librarian;

@Database(entities = {Librarian.class, Genre.class, Book.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LibrarianDao librarianDao();
    public abstract GenreDao genreDao();
}
