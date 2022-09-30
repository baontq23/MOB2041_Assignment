package com.baontq.pnlib.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.baontq.pnlib.model.BookAnalytics;
import com.baontq.pnlib.model.CallCard;

import java.util.List;

@Dao
public interface CallCardDao {
    @Query("SELECT * FROM tbl_call_card")
    List<CallCard> listAll();

    @Insert
    long store(CallCard callCard);

    @Delete
    int delete(CallCard callCard);

    @Update
    int update(CallCard callCard);

    @Query("select sum(book_price) as totalPrice, count(tbl_book.book_id) as borrowCount from  tbl_call_card inner join tbl_book ON tbl_call_card.book_id =  tbl_book.book_id where call_card_borrow_time BETWEEN :startDate AND :endDate")
    BookAnalytics analyticsByDate(String startDate, String endDate);
}
