package com.example.alarmaapp.navegacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmaapp.crud.EditarAlarmaActivity;
import com.example.alarmaapp.crud.NuevaAlarmaActivity;
import com.example.alarmaapp.R;
import com.example.alarmaapp.model.Alarma;
import com.example.alarmaapp.model.Categoria;
import com.example.alarmaapp.view.AlarmaAdapter;
import com.example.alarmaapp.viewmodel.AlarmaViewModel;
import com.example.alarmaapp.viewmodel.CategoriaViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListaAlarmasActivity extends AppCompatActivity {

    private AlarmaViewModel alarmaViewModel;
    private CategoriaViewModel categoriaViewModel;
    private AlarmaAdapter adapter;
    private TextView tvVacia;
    private RecyclerView recyclerView;

    // Lista de categorias para el spinner
    private List<Categoria> listaCategorias = new ArrayList<>();
    private Long categoriaFiltradaId = null; // null = mostrar todas
    private LiveData<List<Alarma>> alarmasObservadas = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alarmas);

        recyclerView = findViewById(R.id.recyclerView);
        tvVacia      = findViewById(R.id.tvVacia); // mensaje de "no hay alarmas"
        Spinner spinnerCategorias    = findViewById(R.id.spinnerCategorias);
        ImageButton btnCategorias    = findViewById(R.id.btnCategorias);
        FloatingActionButton fabAnadir = findViewById(R.id.fabAnadir);

        alarmaViewModel     = new ViewModelProvider(this).get(AlarmaViewModel.class);
        categoriaViewModel  = new ViewModelProvider(this).get(CategoriaViewModel.class);


        adapter = new AlarmaAdapter(alarmaViewModel, alarma -> {
            Intent intent = new Intent(this, EditarAlarmaActivity.class);
            intent.putExtra("id", alarma.getId());
            intent.putExtra("nombre", alarma.getNombre());
            intent.putExtra("latitud", alarma.getLatitud());
            intent.putExtra("longitud", alarma.getLongitud());
            intent.putExtra("radio", alarma.getRadio());
            intent.putExtra("activa", alarma.isActiva());
            intent.putExtra("categoriaId", alarma.getCategoriaId() != null ?
                    alarma.getCategoriaId() : -1L);
            intent.putExtra("nota", alarma.getNota());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Desplegable de categorías
        categoriaViewModel.getTodasLasCategorias().observe(this, categorias -> {
            listaCategorias = categorias;
            adapter.setCategorias(categorias);

            // Primera opcion siempre es "Todas"
            List<String> nombres = new ArrayList<>();
            nombres.add("Todas");
            for (Categoria c : categorias) nombres.add(c.getNombre());

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, nombres);
            spinnerAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            spinnerCategorias.setAdapter(spinnerAdapter);
        });

        // Al elegir categoria en el desplegable, filtraremos
        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // sin filtro = todas
                    categoriaFiltradaId = null;
                    observarTodasLasAlarmas();
                } else if (position - 1 < listaCategorias.size()) {
                    // Filtrar por la categoría seleccionada
                    categoriaFiltradaId = listaCategorias.get(position - 1).getId();
                    observarAlarmasPorCategoria(categoriaFiltradaId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnCategorias.setOnClickListener(v ->
                startActivity(new Intent(this, CategoriasActivity.class)));

        fabAnadir.setOnClickListener(v ->
                startActivity(new Intent(this, NuevaAlarmaActivity.class)));
    }

    // Cambia la fuente de datos activa y elimina el observer anterior para evitar acumulación
    private void setAlarmasObserver(LiveData<List<Alarma>> liveData) {
        if (alarmasObservadas != null) alarmasObservadas.removeObservers(this);
        alarmasObservadas = liveData;
        liveData.observe(this, alarmas -> {
            adapter.setAlarmas(alarmas);
            tvVacia.setVisibility(alarmas.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(alarmas.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private void observarTodasLasAlarmas() {
        setAlarmasObserver(alarmaViewModel.getTodasLasAlarmas());
    }

    private void observarAlarmasPorCategoria(long categoriaId) {
        setAlarmasObserver(alarmaViewModel.getAlarmasPorCategoria(categoriaId));
    }
}