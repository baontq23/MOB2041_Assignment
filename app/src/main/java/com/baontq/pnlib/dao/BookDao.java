package com.baontq.pnlib.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.baontq.pnlib.model.Book;
import com.baontq.pnlib.model.Genre;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM tbl_book")
    List<Book> getAll();

    @Insert
    long insert(Book book);

    @Delete
    int delete(Book book);

    @Update
    int update(Book book);
}
