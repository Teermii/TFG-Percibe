package com.example.alarmaapp.navegacion;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmaapp.R;
import com.example.alarmaapp.model.Categoria;
import com.example.alarmaapp.view.CategoriaAdapter;
import com.example.alarmaapp.viewmodel.CategoriaViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CategoriasActivity extends AppCompatActivity {

    private CategoriaViewModel viewModel;
    private CategoriaAdapter adapter;

    // Colores disponibles para las categorías
    private final String[] COLORES = {
            "#1A73E8", // Azul
            "#E53935", // Rojo
            "#43A047", // Verde
            "#FB8C00", // Naranja
            "#8E24AA", // Morado
    };

    private String colorSeleccionado = "#1A73E8"; // Azul por defecto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        RecyclerView recyclerView   = findViewById(R.id.recyclerView);
        TextView tvVacia            = findViewById(R.id.tvVacia);
        FloatingActionButton fabAdd = findViewById(R.id.fabAnadir);

        viewModel = new ViewModelProvider(this).get(CategoriaViewModel.class);

        adapter = new CategoriaAdapter(
                categoria -> mostrarDialogoEditar(categoria),
                categoria -> viewModel.eliminar(categoria)
        );

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cuando se cambian las categorias actualizamos la lista
        viewModel.getTodasLasCategorias().observe(this, categorias -> {
            adapter.setCategorias(categorias);
            tvVacia.setVisibility(categorias.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(categorias.isEmpty() ? View.GONE : View.VISIBLE);
        });

        fabAdd.setOnClickListener(v -> mostrarDialogoCrear());
    }

    // Dialoo para crear una categoria
    private void mostrarDialogoCrear() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_categoria, null);

        EditText etNombre    = dialogView.findViewById(R.id.etNombre);
        RadioGroup rgColores = dialogView.findViewById(R.id.rgColores);

        colorSeleccionado = COLORES[0]; // Azul por defecto

        // Guardamos que color hemos elegido
        rgColores.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAzul)    colorSeleccionado = COLORES[0];
            if (checkedId == R.id.rbRojo)    colorSeleccionado = COLORES[1];
            if (checkedId == R.id.rbVerde)   colorSeleccionado = COLORES[2];
            if (checkedId == R.id.rbNaranja) colorSeleccionado = COLORES[3];
            if (checkedId == R.id.rbMorado)  colorSeleccionado = COLORES[4];
        });

        new AlertDialog.Builder(this)
                .setTitle("Nueva categoría")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    if (!nombre.isEmpty()) {
                        viewModel.insertar(new Categoria(nombre, colorSeleccionado));
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Dialogo para editar
    private void mostrarDialogoEditar(Categoria categoria) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_categoria, null);

        EditText etNombre    = dialogView.findViewById(R.id.etNombre);
        RadioGroup rgColores = dialogView.findViewById(R.id.rgColores);

        etNombre.setText(categoria.getNombre());
        colorSeleccionado = categoria.getColor();

        // Marcamos el color actual
        if (colorSeleccionado.equals(COLORES[0])) rgColores.check(R.id.rbAzul);
        if (colorSeleccionado.equals(COLORES[1])) rgColores.check(R.id.rbRojo);
        if (colorSeleccionado.equals(COLORES[2])) rgColores.check(R.id.rbVerde);
        if (colorSeleccionado.equals(COLORES[3])) rgColores.check(R.id.rbNaranja);
        if (colorSeleccionado.equals(COLORES[4])) rgColores.check(R.id.rbMorado);

        rgColores.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAzul)    colorSeleccionado = COLORES[0];
            if (checkedId == R.id.rbRojo)    colorSeleccionado = COLORES[1];
            if (checkedId == R.id.rbVerde)   colorSeleccionado = COLORES[2];
            if (checkedId == R.id.rbNaranja) colorSeleccionado = COLORES[3];
            if (checkedId == R.id.rbMorado)  colorSeleccionado = COLORES[4];
        });

        new AlertDialog.Builder(this)
                .setTitle("Editar categoría")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    if (!nombre.isEmpty()) {
                        categoria.setNombre(nombre);
                        categoria.setColor(colorSeleccionado);
                        viewModel.actualizar(categoria);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}