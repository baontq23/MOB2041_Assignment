package com.baontq.pnlib.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_librarian")
public class Librarian {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "librarian_id")
    private int id;
    @ColumnInfo(name = "librarian_full_name")
    private String fullName;
    @ColumnInfo(name = "librarian_username")
    private String username;
    @ColumnInfo(name = "librarian_password")
    private String password;
    @ColumnInfo(name = "librarian_role")
    private String role;

    public Librarian(String fullName, String username, String password, String role) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Ignore
    public Librarian() {
        this.role = "User";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean isValid() {
        if (this.username.equalsIgnoreCase("")) return false;
        if (this.fullName.equalsIgnoreCase("")) return false;
        return true;
    }
}
