package com.example.alarmaapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alarmaapp.database.AppDatabase;
import com.example.alarmaapp.database.ContactoDao;
import com.example.alarmaapp.model.Contacto;

import java.util.List;

public class ContactoRepository {

    private ContactoDao contactoDao;

    public ContactoRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        contactoDao = db.contactoDao();
    }

    public void insertar(Contacto contacto) {
        new Thread(() -> contactoDao.insertar(contacto)).start();
    }

    public void actualizar(Contacto contacto) {
        new Thread(() -> contactoDao.actualizar(contacto)).start();
    }

    public void eliminar(Contacto contacto) {
        new Thread(() -> contactoDao.eliminar(contacto)).start();
    }

    public LiveData<List<Contacto>> getContactosPorAlarma(long alarmaId) {
        return contactoDao.getContactosPorAlarma(alarmaId);
    }
}