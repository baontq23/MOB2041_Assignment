package com.baontq.pnlib.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_customer")
public class Customer {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "customer_id")
    private int id;
    @ColumnInfo(name = "customer_name")
    private String name;

    public Customer(String name) {
        this.name = name;
    }

    @Ignore
    public Customer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
