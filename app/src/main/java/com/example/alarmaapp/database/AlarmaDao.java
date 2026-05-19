package com.example.alarmaapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmaapp.model.Alarma;

import java.util.List;

/* Esta interfaz DAO definirá las cosas que podremos hacer con la tabla "alarmas",
 * ROOM recogerá todo esto y hara el codigo SQLite pertinente
 */
@Dao // Avisamos a ROOM que en esta interfaz hay operaciones para la base de datos
public interface AlarmaDao {

    // Equivalente a: INSERT INTO ALARMAS Values...
    @Insert
    long insertar(Alarma alarma);

    // Equivalente a: UPDATE ALARMAS...
    @Update
    void actualizar (Alarma alarma);

    // Equivalente a: DELETE FROM ALARMAS...
    @Delete
    void eliminar (Alarma alarma);

    // Usamos LiveData, ya que, así ROOM avisará autómaticamente cuando cambien los datos de la lista
    @Query("SELECT * FROM alarmas ORDER BY id DESC")
    LiveData<List<Alarma>> getTodasLasAlarmas();

    @Query("SELECT * FROM alarmas WHERE id = :id")
    LiveData<Alarma> getAlarmaPorId(int id);

    @Query("SELECT * FROM alarmas WHERE id = :id")
    Alarma getAlarmaPorIdDirecto(long id);

    // Sin LiveData, para usar desde el BootReceiver en hilo secundario
    @Query("SELECT * FROM alarmas WHERE activa = 1")
    List<Alarma> getAlarmasActivasDirecto();

    @Query("SELECT * FROM alarmas WHERE categoriaId = :categoriaId ORDER BY id DESC")
    LiveData<List<Alarma>> getAlarmasPorCategoria(long categoriaId);
}
