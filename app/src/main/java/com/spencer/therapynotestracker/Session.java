package com.spencer.therapynotestracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "session_table")
public class Session {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "date")
    private String date;

    @NonNull
    @ColumnInfo(name = "agenda")
    private String agenda;

    @NonNull
    @ColumnInfo(name = "notes")
    private String notes;

    public Session(String date, String agenda, String notes) {
        this.date = date;
        this.agenda = agenda;
        this.notes = notes;
    }

    public void setDate(String string) {
        this.date = string;
    }

    public void setAgenda(String string) {
        this.agenda = string;
    }

    public void setNotes(String string) {
        this.notes = string;
    }

    public String getDate() {
        return this.date;
    }

    public String getAgenda() {
        return this.agenda;
    }

    public String getNotes() {
        return this.notes;
    }


}
