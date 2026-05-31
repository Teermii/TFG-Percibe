package com.example.alarmaapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.alarmaapp.model.Contacto;
import com.example.alarmaapp.repository.ContactoRepository;

import java.util.List;

public class ContactoViewModel extends AndroidViewModel {

    private ContactoRepository repository;

    public ContactoViewModel(Application application) {
        super(application);
        repository = new ContactoRepository(application);
    }

    public void insertar(Contacto contacto) {
        repository.insertar(contacto);
    }

    public void actualizar(Contacto contacto) {
        repository.actualizar(contacto);
    }

    public void eliminar(Contacto contacto) {
        repository.eliminar(contacto);
    }

    public LiveData<List<Contacto>> getContactosPorAlarma(long alarmaId) {
        return repository.getContactosPorAlarma(alarmaId);
    }
}