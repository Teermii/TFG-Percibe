package com.example.alarmaapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.alarmaapp.model.Alarma;
import com.example.alarmaapp.model.Categoria;
import com.example.alarmaapp.model.HistorialActivacion;
import com.example.alarmaapp.model.Repeticion;
import com.example.alarmaapp.model.Contacto;
import com.example.alarmaapp.model.Configuracion;

// Declaramos todas las tablas y la versión del esquema
@Database(
        entities = {Alarma.class, HistorialActivacion.class, Categoria.class,
                Repeticion.class, Contacto.class, Configuracion.class },
        version = 10,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // Instancia estatica para el singletone
    private static AppDatabase instancia;

    public abstract AlarmaDao alarmaDao();
    public abstract HistorialDao historialDao();
    public abstract CategoriaDao categoriaDao();
    public abstract RepeticionDao repeticionDao();
    public abstract ContactoDao contactoDao();
    public abstract ConfiguracionDao configuracionDao();

    // Patron singletone sincronizado para asegurarnos de que solo haya 1 instancia de la BD
    public static synchronized AppDatabase getInstance(Context context) {
        if (instancia == null) {
            instancia = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "alarma_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instancia;
    }
}
