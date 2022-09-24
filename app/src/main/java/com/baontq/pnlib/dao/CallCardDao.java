package com.baontq.pnlib.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
}
