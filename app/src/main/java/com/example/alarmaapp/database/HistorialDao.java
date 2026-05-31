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

    // Todo el historial ordenado de mas nuevo a mas antiguo
    @Query("SELECT * FROM historial ORDER BY id DESC")
    LiveData<List<HistorialActivacion>> getTodoElHistorial();

    // Historial de una alarma concreta
    @Query("SELECT * FROM historial WHERE alarmaId = :alarmaId ORDER BY id DESC")
    LiveData<List<HistorialActivacion>> getHistorialPorAlarma(long alarmaId);

    // Numero de veces que se ha activado una alarma concreta
    @Query("SELECT COUNT(*) FROM historial WHERE alarmaId = :alarmaId")
    LiveData<Integer> getNumActivaciones(long alarmaId);

    // Ultima activación de una alarma concreta
    @Query("SELECT * FROM historial WHERE alarmaId = :alarmaId ORDER BY id DESC LIMIT 1")
    LiveData<HistorialActivacion> getUltimaActivacion(long alarmaId);

    // Total de activaciones de todas las alarmas
    @Query("SELECT COUNT(*) FROM historial")
    LiveData<Integer> getTotalActivaciones();

    // Alarma que mas se ha activado (nombre + cuantas veces)
    @Query("SELECT a.nombre AS nombreAlarma, COUNT(*) AS veces " +
            "FROM historial h JOIN alarmas a ON a.id = h.alarmaId " +
            "GROUP BY h.alarmaId ORDER BY veces DESC LIMIT 1")
    LiveData<AlarmaMasActivada> getAlarmaMasActivada();

    // Activaciones de hoy
    @Query("SELECT COUNT(*) FROM historial WHERE fecha = :hoy")
    LiveData<Integer> getActivacionesHoy(String hoy);

    // Activaciones de esta semana (pasar los 7 días en formato dd/MM/yyyy)
    @Query("SELECT COUNT(*) FROM historial WHERE fecha IN (:fechas)")
    LiveData<Integer> getActivacionesUltimaSemana(List<String> fechas);
}