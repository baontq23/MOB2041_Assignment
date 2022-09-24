package com.baontq.pnlib.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_call_card", foreignKeys = {
        @ForeignKey(entity = Customer.class, parentColumns = "customer_id", childColumns = "customer_id", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
        @ForeignKey(entity = Librarian.class, parentColumns = "librarian_id", childColumns = "librarian_id", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
        @ForeignKey(entity = Book.class, parentColumns = "book_id", childColumns = "book_id", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
})
public class CallCard {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "call_card_id")
    private int id;

    @ColumnInfo(name = "customer_id", index = true)
    private int customerId;

    @ColumnInfo(name = "librarian_id", index = true)
    private int librarianId;

    @ColumnInfo(name = "book_id", index = true)
    private int bookId;

    @ColumnInfo(name = "call_card_borrow_time")
    private String borrowTime;

    @ColumnInfo(name = "call_card_return_time")
    private String returnTime;

    @ColumnInfo(name = "call_card_is_returned")
    private boolean isReturned;

    public CallCard(int customerId, int librarianId, int bookId, String borrowTime, String returnTime, boolean isReturned) {
        this.customerId = customerId;
        this.librarianId = librarianId;
        this.bookId = bookId;
        this.borrowTime = borrowTime;
        this.returnTime = returnTime;
        this.isReturned = isReturned;
    }

    @Ignore
    public CallCard() {
        this.isReturned = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(int librarianId) {
        this.librarianId = librarianId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(String borrowTime) {
        this.borrowTime = borrowTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }
}
