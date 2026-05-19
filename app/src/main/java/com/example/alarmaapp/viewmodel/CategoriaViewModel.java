package com.example.alarmaapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.alarmaapp.model.Categoria;
import com.example.alarmaapp.repository.CategoriaRepository;

import java.util.List;

public class CategoriaViewModel extends AndroidViewModel {

    private CategoriaRepository repository;
    private LiveData<List<Categoria>> todasLasCategorias;

    public CategoriaViewModel(Application application) {
        super(application);
        repository = new CategoriaRepository(application);
        todasLasCategorias = repository.getTodasLasCategorias();
    }

    public void insertar(Categoria categoria) {
        repository.insertar(categoria);
    }

    public void actualizar(Categoria categoria) {
        repository.actualizar(categoria);
    }

    public void eliminar(Categoria categoria) {
        repository.eliminar(categoria);
    }

    public LiveData<List<Categoria>> getTodasLasCategorias() {
        return todasLasCategorias;
    }
}