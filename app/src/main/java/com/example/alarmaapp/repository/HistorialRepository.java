package com.example.alarmaapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alarmaapp.database.AppDatabase;
import com.example.alarmaapp.database.HistorialDao;
import com.example.alarmaapp.model.AlarmaMasActivada;
import com.example.alarmaapp.model.HistorialActivacion;

import java.util.List;

public class HistorialRepository {

    private HistorialDao historialDao;

    public HistorialRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        historialDao = db.historialDao();
    }

    public void insertar(HistorialActivacion activacion) {
        new Thread(() -> historialDao.insertar(activacion)).start();
    }

    public LiveData<List<HistorialActivacion>> getTodoElHistorial() {
        return historialDao.getTodoElHistorial();
    }

    public LiveData<List<HistorialActivacion>> getHistorialPorAlarma(long alarmaId) {
        return historialDao.getHistorialPorAlarma(alarmaId);
    }

    public LiveData<Integer> getNumActivaciones(long alarmaId) {
        return historialDao.getNumActivaciones(alarmaId);
    }

    public LiveData<HistorialActivacion> getUltimaActivacion(long alarmaId) {
        return historialDao.getUltimaActivacion(alarmaId);
    }

    public LiveData<Integer> getTotalActivaciones() {
        return historialDao.getTotalActivaciones();
    }

    public LiveData<AlarmaMasActivada> getAlarmaMasActivada() {
        return historialDao.getAlarmaMasActivada();
    }

    public LiveData<Integer> getActivacionesHoy(String hoy) {
        return historialDao.getActivacionesHoy(hoy);
    }
}