package com.example.alarmaapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alarmaapp.database.AppDatabase;
import com.example.alarmaapp.database.CategoriaDao;
import com.example.alarmaapp.model.Categoria;

import java.util.List;

public class CategoriaRepository {

    private CategoriaDao categoriaDao;

    public CategoriaRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoriaDao = db.categoriaDao();
    }

    public void insertar(Categoria categoria) {
        new Thread(() -> categoriaDao.insertar(categoria)).start();
    }

    public void actualizar(Categoria categoria) {
        new Thread(() -> categoriaDao.actualizar(categoria)).start();
    }

    public void eliminar(Categoria categoria) {
        new Thread(() -> categoriaDao.eliminar(categoria)).start();
    }

    public LiveData<List<Categoria>> getTodasLasCategorias() {
        return categoriaDao.getTodasLasCategorias();
    }
}