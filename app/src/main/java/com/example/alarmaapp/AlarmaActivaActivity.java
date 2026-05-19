package com.example.alarmaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.app.KeyguardManager;
import android.os.Build;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmaActivaActivity extends AppCompatActivity {

    private boolean alarmaParada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mostrar sobre la pantalla de bloqueo y encender la pantalla
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            );
        }

        setContentView(R.layout.activity_alarma_activa);

        String nombreAlarma = getIntent().getStringExtra("nombre");
        String notaAlarma   = getIntent().getStringExtra("nota");

        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvNota   = findViewById(R.id.tvNota);
        Button   btnParar = findViewById(R.id.btnParar);

        tvNombre.setText(nombreAlarma != null ? nombreAlarma : "Alarma");

        if (notaAlarma != null && !notaAlarma.isEmpty()) {
            tvNota.setText(notaAlarma);
            tvNota.setVisibility(View.VISIBLE);
        } else {
            tvNota.setVisibility(View.GONE);
        }

        btnParar.setOnClickListener(v -> pararAlarma());
    }

    private void pararAlarma() {
        if (alarmaParada) return;
        alarmaParada = true;

        // Parar el servicio (que gestiona sonido y vibración)
        Intent stop = new Intent(this, AlarmaService.class);
        stop.setAction(AlarmaService.ACTION_STOP);
        startService(stop);

        finish();
    }

    @Override
    public void onBackPressed() {
        pararAlarma();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Si la activity se destruye sin que el usuario pulsara "Parar" (ej. sistema la mata),
        // también paramos el servicio para no dejar la alarma sonando indefinidamente.
        if (!alarmaParada) {
            Intent stop = new Intent(this, AlarmaService.class);
            stop.setAction(AlarmaService.ACTION_STOP);
            startService(stop);
        }
    }
}
