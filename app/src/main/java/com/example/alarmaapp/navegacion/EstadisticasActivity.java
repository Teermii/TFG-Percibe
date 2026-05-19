package com.example.alarmaapp.navegacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmaapp.R;
import com.example.alarmaapp.view.AlarmaAdapter;
import com.example.alarmaapp.viewmodel.AlarmaViewModel;
import com.example.alarmaapp.viewmodel.HistorialViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EstadisticasActivity extends AppCompatActivity {

    private AlarmaViewModel alarmaViewModel;
    private HistorialViewModel historialViewModel;
    private AlarmaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        TextView tvTotal             = findViewById(R.id.tvTotal);
        TextView tvHoy               = findViewById(R.id.tvHoy);
        TextView tvMasActivada       = findViewById(R.id.tvMasActivada);
        TextView tvMasActivadaVeces  = findViewById(R.id.tvMasActivadaVeces);
        RecyclerView recyclerView    = findViewById(R.id.recyclerView);
        TextView tvVacia             = findViewById(R.id.tvVacia);

        alarmaViewModel    = new ViewModelProvider(this).get(AlarmaViewModel.class);
        historialViewModel = new ViewModelProvider(this).get(HistorialViewModel.class);

        // ── Lista de alarmas (al tocar una vas a su detalle) ─────────────────
        adapter = new AlarmaAdapter(alarmaViewModel, alarma -> {
            Intent intent = new Intent(this, DetalleEstadisticasActivity.class);
            intent.putExtra("alarmaId", alarma.getId());
            intent.putExtra("nombre", alarma.getNombre());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        alarmaViewModel.getTodasLasAlarmas().observe(this, alarmas -> {
            adapter.setAlarmas(alarmas);
            tvVacia.setVisibility(alarmas.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(alarmas.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // ── Estadísticas globales ────────────────────────────────────────────
        historialViewModel.getTotalActivaciones().observe(this, total ->
                tvTotal.setText(String.valueOf(total != null ? total : 0)));

        // Fecha de hoy en el mismo formato que se guarda en historial
        String hoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        historialViewModel.getActivacionesHoy(hoy).observe(this, hoyTotal ->
                tvHoy.setText(String.valueOf(hoyTotal != null ? hoyTotal : 0)));

        historialViewModel.getAlarmaMasActivada().observe(this, masActivada -> {
            if (masActivada != null) {
                tvMasActivada.setText(masActivada.nombreAlarma);
                tvMasActivadaVeces.setText(masActivada.veces + " activaciones");
            } else {
                tvMasActivada.setText("Sin datos");
                tvMasActivadaVeces.setText("Aún no hay activaciones");
            }
        });
    }
}