package com.example.alarmaapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmaapp.model.Contacto;

import java.util.List;

@Dao
public interface ContactoDao {

    @Insert
    long insertar(Contacto contacto);

    @Update
    void actualizar(Contacto contacto);

    @Delete
    void eliminar(Contacto contacto);

    // Contactos de una alarma concreta (con LiveData para la UI)
    @Query("SELECT * FROM contactos WHERE alarmaId = :alarmaId ORDER BY nombre ASC")
    LiveData<List<Contacto>> getContactosPorAlarma(long alarmaId);

    // Contactos de una alarma concreta (sin LiveData, para el BroadcastReceiver)
    @Query("SELECT * FROM contactos WHERE alarmaId = :alarmaId")
    List<Contacto> getContactosPorAlarmaDirecto(long alarmaId);
}