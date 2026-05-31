package com.example.alarmaapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmaapp.model.Categoria;
import java.util.List;

@Dao
public interface CategoriaDao {

    @Insert
    long insertar(Categoria categoria);

    @Update
    void actualizar(Categoria categoria);

    @Delete
    void eliminar(Categoria categoria);

    // Categorias ordenadas alfabeticamente
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    LiveData<List<Categoria>> getTodasLasCategorias();

    // Directo, sin LiveData (por si se necesita en segundo plano)
    @Query("SELECT * FROM categorias WHERE id = :id")
    Categoria getCategoriaPorIdDirecto(long id);
}