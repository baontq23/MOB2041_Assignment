package com.baontq.pnlib.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.baontq.pnlib.model.Librarian;

import java.util.List;

@Dao
public interface LibrarianDao {
    @Query("SELECT * FROM tbl_librarian")
    List<Librarian> getAll();

    @Query("SELECT * FROM tbl_librarian WHERE librarian_username = :username AND " +
            "librarian_password = :password LIMIT 1")
    Librarian auth(String username, String password);
    @Insert
    long store(Librarian librarian);

    @Delete
    int delete(Librarian librarian);
}
