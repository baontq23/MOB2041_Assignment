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

    @Query("SELECT * FROM tbl_book ORDER BY book_borrow_count DESC LIMIT 10")
    List<Book> getTop10();

    @Query("UPDATE tbl_book SET book_borrow_count = book_borrow_count + 1 WHERE book_id = :id")
    int increaseBorrowCount(int id);

    @Query("UPDATE tbl_book SET book_borrow_count = book_borrow_count - 1 WHERE book_id = :id")
    int decreaseBorrowCount(int id);
}
