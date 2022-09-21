package com.baontq.pnlib.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_book", foreignKeys = @ForeignKey(entity = Genre.class, parentColumns = "genre_id", childColumns = "genre_id", onDelete = ForeignKey.CASCADE))
public class Book {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "book_id")
    private int id;
    @ColumnInfo(name = "book_name")
    private String name;
    @ColumnInfo(name = "book_price")
    private Double price;
    @ColumnInfo(name = "genre_id", index = true)
    private int genreId;

    public Book(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    @Ignore
    public Book() {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }
}
