package com.example.alarmaapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alarmaapp.database.AlarmaDao;
import com.example.alarmaapp.database.AppDatabase;
import com.example.alarmaapp.model.Alarma;

import java.util.List;

import java.util.function.Consumer;

public class AlarmaRepository {

    // Referencia al Dao para ejecutar las operaciones de la bd
    private AlarmaDao alarmaDao;
    // Lista con todas las alarmas, se ejecutará una sola vez y ROOM la irá actualizando
    private LiveData<List<Alarma>> todasLasAlarmas;


    public AlarmaRepository (Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        alarmaDao = db.alarmaDao();
        todasLasAlarmas = alarmaDao.getTodasLasAlarmas();
    }

    public LiveData<List<Alarma>> getTodasLasAlarmas(){
        return todasLasAlarmas;
    }

    /* Para insertar una alarma, tendriamos que crear un hilo secundario (ROOM no permite realizar
    * operaciones en el hilo principal), depués hacemos un callback para recoger el ID generado
    * por ROOM, necesario para registrar luego la alarma con el mismo ID en el Geofence
    */
    public void insertar(Alarma alarma, Consumer<Long> callback) {
        new Thread(() -> {
            // Guardamos la alarma y le pasamos el ID al Viewmodel
            long id = alarmaDao.insertar(alarma);
            callback.accept(id);
        }).start();
    }

    public void actualizar(Alarma alarma) {
        new Thread(() -> alarmaDao.actualizar(alarma)).start();
    }

    public void eliminar(Alarma alarma) {
        new Thread(() -> alarmaDao.eliminar(alarma)).start();
    }

    public LiveData<List<Alarma>> getAlarmasPorCategoria(long categoriaId) {
        return alarmaDao.getAlarmasPorCategoria(categoriaId);
    }
}
