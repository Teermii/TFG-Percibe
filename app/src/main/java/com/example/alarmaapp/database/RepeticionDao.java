package com.example.alarmaapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.alarmaapp.model.Repeticion;

@Dao
public interface RepeticionDao {

    // OnConflictStrategy.REPLACE: si ya existe una repetición para esta alarma, la reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertar(Repeticion repeticion);

    @Query("SELECT * FROM repeticion WHERE alarmaId = :alarmaId")
    LiveData<Repeticion> getRepeticionPorAlarma(long alarmaId);

    // Sin LiveData para consultar desde el BroadcastReceiver en hilo secundario
    @Query("SELECT * FROM repeticion WHERE alarmaId = :alarmaId")
    Repeticion getRepeticionPorAlarmaDirecto(long alarmaId);

    @Query("DELETE FROM repeticion WHERE alarmaId = :alarmaId")
    void eliminarPorAlarma(long alarmaId);
}