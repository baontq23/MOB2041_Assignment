package com.baontq.pnlib.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_genre")
public class Genre {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "genre_id")
    private int id;
    @ColumnInfo(name = "genre_name")
    private String name;
    @ColumnInfo(name = "genre_location")
    private String location;

    public Genre(String name, String location) {
        this.name = name;
        this.location = location;
    }

    @Ignore
    public Genre() {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
