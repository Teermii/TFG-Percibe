package com.example.alarmaapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.alarmaapp.model.AlarmaMasActivada;
import com.example.alarmaapp.model.HistorialActivacion;
import com.example.alarmaapp.repository.HistorialRepository;

import java.util.List;

public class HistorialViewModel extends AndroidViewModel {

    private HistorialRepository repository;

    public HistorialViewModel(Application application) {
        super(application);
        repository = new HistorialRepository(application);
    }

    public void insertar(HistorialActivacion activacion) {
        repository.insertar(activacion);
    }

    public LiveData<List<HistorialActivacion>> getTodoElHistorial() {
        return repository.getTodoElHistorial();
    }

    public LiveData<List<HistorialActivacion>> getHistorialPorAlarma(long alarmaId) {
        return repository.getHistorialPorAlarma(alarmaId);
    }

    public LiveData<Integer> getNumActivaciones(long alarmaId) {
        return repository.getNumActivaciones(alarmaId);
    }

    public LiveData<HistorialActivacion> getUltimaActivacion(long alarmaId) {
        return repository.getUltimaActivacion(alarmaId);
    }

    public LiveData<Integer> getTotalActivaciones() {
        return repository.getTotalActivaciones();
    }

    public LiveData<AlarmaMasActivada> getAlarmaMasActivada() {
        return repository.getAlarmaMasActivada();
    }

    public LiveData<Integer> getActivacionesHoy(String hoy) {
        return repository.getActivacionesHoy(hoy);
    }
}