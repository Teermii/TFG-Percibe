package com.example.alarmaapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.alarmaapp.model.Configuracion;
import com.example.alarmaapp.repository.ConfiguracionRepository;

public class ConfiguracionViewModel extends AndroidViewModel {

    private ConfiguracionRepository repository;

    public ConfiguracionViewModel(Application application) {
        super(application);
        repository = new ConfiguracionRepository(application);
    }

    public void actualizar(Configuracion configuracion) {
        repository.actualizar(configuracion);
    }

    public LiveData<Configuracion> getConfiguracion() {
        return repository.getConfiguracion();
    }
}