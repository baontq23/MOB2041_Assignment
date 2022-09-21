package com.baontq.pnlib.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.baontq.pnlib.model.Genre;

import java.util.List;

@Dao
public interface GenreDao {
    @Query("SELECT * FROM tbl_genre")
    List<Genre> getAll();

    @Insert
    long insert(Genre genre);

    @Delete
    int delete(Genre genre);

    @Update
    int update(Genre genre);
}
