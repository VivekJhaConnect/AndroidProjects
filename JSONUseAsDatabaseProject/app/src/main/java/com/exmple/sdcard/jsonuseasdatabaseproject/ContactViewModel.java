package com.exmple.sdcard.jsonuseasdatabaseproject;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    public ContactRepository repository;
    public LiveData<List<Contact>> allContacts;
    private Application application;

    public ContactViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void loadData(String filename) {
        repository = new ContactRepository(application, filename);
        allContacts = repository.getAllContacts();
    }

    public void insert(Contact contact) {
        repository.insert(contact);
    }

    public void delete(Contact contact) {
        repository.delete(contact);
    }

    public LiveData<List<Contact>> getAllContacts() {
        return allContacts;
    }
}
