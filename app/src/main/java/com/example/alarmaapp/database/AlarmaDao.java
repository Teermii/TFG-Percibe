package com.example.alarmaapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmaapp.model.Alarma;
import java.util.List;

// Aquí declaramos que queremos hacer con la tabla, Room escribe el SQL real
@Dao
public interface AlarmaDao {

    @Insert                       // Room escribe el INSERT
    long insertar(Alarma alarma); // Devuelve el id generado

    @Update
    void actualizar (Alarma alarma);

    @Delete
    void eliminar (Alarma alarma);

    // LiveData = la pantalla se actualiza SOLA cuando cambian los datos
    @Query("SELECT * FROM alarmas ORDER BY id DESC")
    LiveData<List<Alarma>> getTodasLasAlarmas();

    @Query("SELECT * FROM alarmas WHERE id = :id")
    LiveData<Alarma> getAlarmaPorId(int id);

    // "Directo" = sin LiveData, para consultar una vez desde segundo plano (receivers)
    @Query("SELECT * FROM alarmas WHERE id = :id")
    Alarma getAlarmaPorIdDirecto(long id);

    // Para el BootReceiver: todas las alarmas que están encendidas
    @Query("SELECT * FROM alarmas WHERE activa = 1")
    List<Alarma> getAlarmasActivasDirecto();

    // Para filtrar la lista por categoria
    @Query("SELECT * FROM alarmas WHERE categoriaId = :categoriaId ORDER BY id DESC")
    LiveData<List<Alarma>> getAlarmasPorCategoria(long categoriaId);
}