package com.example.windowchatlesson4;
import javafx.beans.property.SimpleStringProperty;

public class RowText {

    private SimpleStringProperty contact;

    public RowText(String contact) {

        this.contact = new SimpleStringProperty(contact);
    }

    public SimpleStringProperty contactProperty() {

        return contact;
    }

    public String getContact() {

        return contact.get();
    }

    public void setContact(String contact) {

        this.contact.set(contact);
    }
}
