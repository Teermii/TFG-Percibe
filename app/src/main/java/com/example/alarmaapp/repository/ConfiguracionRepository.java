package com.example.alarmaapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alarmaapp.database.AppDatabase;
import com.example.alarmaapp.database.ConfiguracionDao;
import com.example.alarmaapp.model.Configuracion;

public class ConfiguracionRepository {

    private ConfiguracionDao configuracionDao;

    public ConfiguracionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        configuracionDao = db.configuracionDao();
    }

    public void actualizar(Configuracion configuracion) {
        new Thread(() -> configuracionDao.insertar(configuracion)).start();
    }

    public LiveData<Configuracion> getConfiguracion() {
        return configuracionDao.getConfiguracion();
    }
}