package com.example.alarmaapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmaapp.model.Configuracion;

@Dao
public interface ConfiguracionDao {

    // REPLACE: si ya existe la fila con id=1, la reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertar(Configuracion configuracion);

    @Update
    void actualizar(Configuracion configuracion);

    // Para la UI (reactivo con LiveData)
    @Query("SELECT * FROM configuracion WHERE id = 1")
    LiveData<Configuracion> getConfiguracion();

    // Para el AlarmaService (consulta directa, sin LiveData)
    @Query("SELECT * FROM configuracion WHERE id = 1")
    Configuracion getConfiguracionDirecto();
}