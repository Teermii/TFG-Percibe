package com.example.alarmaapp.navegacion;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmaapp.R;
import com.example.alarmaapp.view.HistorialAdapter;
import com.example.alarmaapp.viewmodel.HistorialViewModel;

// Activaciones de cada alarma
public class DetalleEstadisticasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_estadisticas);

        long alarmaId   = getIntent().getLongExtra("alarmaId", -1);
        String nombre   = getIntent().getStringExtra("nombre");

        TextView tvNombre        = findViewById(R.id.tvNombre);
        TextView tvNumActivaciones = findViewById(R.id.tvNumActivaciones);
        TextView tvUltima        = findViewById(R.id.tvUltima);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        tvNombre.setText(nombre != null ? nombre : "");

        HistorialViewModel viewModel = new ViewModelProvider(this).get(HistorialViewModel.class);
        HistorialAdapter adapter = new HistorialAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Historial completo de esta alarma
        viewModel.getHistorialPorAlarma(alarmaId).observe(this, historial -> {
            adapter.setHistorial(historial);
        });

        // Numero de activaciones
        viewModel.getNumActivaciones(alarmaId).observe(this, num -> {
            tvNumActivaciones.setText(num + " activaciones");
        });

        // Ultima activacion
        viewModel.getUltimaActivacion(alarmaId).observe(this, ultima -> {
            if (ultima != null) {
                tvUltima.setText("Última: " + ultima.getFecha() + " a las " + ultima.getHora());
            } else {
                tvUltima.setText("Sin activaciones todavía");
            }
        });
    }
}