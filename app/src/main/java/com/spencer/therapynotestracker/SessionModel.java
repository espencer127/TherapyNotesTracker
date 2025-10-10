package com.spencer.therapynotestracker;

public class SessionModel {
    private String date;
    private String agenda;
    private String notes;

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
