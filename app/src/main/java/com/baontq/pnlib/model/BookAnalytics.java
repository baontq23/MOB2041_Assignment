package com.baontq.pnlib.model;

public class BookAnalytics {
    private int borrowCount;
    private double totalPrice;

    public BookAnalytics() {
        borrowCount = 0;
        totalPrice = 0;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
