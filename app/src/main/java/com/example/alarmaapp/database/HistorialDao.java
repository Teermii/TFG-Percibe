package com.example.alarmaapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.alarmaapp.model.AlarmaMasActivada;
import com.example.alarmaapp.model.HistorialActivacion;

import java.util.List;

@Dao
public interface HistorialDao {

    @Insert
    void insertar(HistorialActivacion activacion);

    // Todo el historial ordenado de más nuevo a más antiguo
    @Query("SELECT * FROM historial ORDER BY id DESC")
    LiveData<List<HistorialActivacion>> getTodoElHistorial();

    // Historial de una alarma concreta
    @Query("SELECT * FROM historial WHERE alarmaId = :alarmaId ORDER BY id DESC")
    LiveData<List<HistorialActivacion>> getHistorialPorAlarma(long alarmaId);

    // Número de veces que se ha activado una alarma concreta
    @Query("SELECT COUNT(*) FROM historial WHERE alarmaId = :alarmaId")
    LiveData<Integer> getNumActivaciones(long alarmaId);

    // Última activación de una alarma concreta
    @Query("SELECT * FROM historial WHERE alarmaId = :alarmaId ORDER BY id DESC LIMIT 1")
    LiveData<HistorialActivacion> getUltimaActivacion(long alarmaId);

    // Total de activaciones de TODAS las alarmas
    @Query("SELECT COUNT(*) FROM historial")
    LiveData<Integer> getTotalActivaciones();

    // Alarma que más se ha activado (nombre + cuántas veces)
    @Query("SELECT nombreAlarma, COUNT(*) as veces FROM historial " +
            "GROUP BY alarmaId ORDER BY veces DESC LIMIT 1")
    LiveData<AlarmaMasActivada> getAlarmaMasActivada();

    // Activaciones de hoy
    @Query("SELECT COUNT(*) FROM historial WHERE fecha = :hoy")
    LiveData<Integer> getActivacionesHoy(String hoy);

    // Activaciones de esta semana (últimos 7 días)
    @Query("SELECT COUNT(*) FROM historial")
    LiveData<Integer> getActivacionesUltimaSemana();
}