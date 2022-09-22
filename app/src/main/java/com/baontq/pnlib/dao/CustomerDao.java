package com.baontq.pnlib.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.baontq.pnlib.model.Customer;

import java.util.List;

@Dao
public interface CustomerDao {
    @Query("SELECT * FROM tbl_customer")
    List<Customer> getAll();

    @Insert
    long insert(Customer customer);

    @Delete
    int delete(Customer customer);

    @Update
    int update(Customer customer);
}
