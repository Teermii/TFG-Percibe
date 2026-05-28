package com.example.alarmaapp.navegacion;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.alarmaapp.R;
import com.example.alarmaapp.model.Configuracion;
import com.example.alarmaapp.viewmodel.ConfiguracionViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

// Pntalla de ajustes del volumen, vibracion y tiempo hasta parar
public class ConfiguracionActivity extends AppCompatActivity {

    private ConfiguracionViewModel viewModel;
    private Configuracion configActual;

    private SwitchMaterial switchSonido;
    private SwitchMaterial switchVibracion;
    private SeekBar seekVolumen;
    private TextView tvVolumenValor;
    private SeekBar seekTiempo;
    private TextView tvTiempoValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        switchSonido    = findViewById(R.id.switchSonido);
        switchVibracion = findViewById(R.id.switchVibracion);
        seekVolumen     = findViewById(R.id.seekVolumen);
        tvVolumenValor  = findViewById(R.id.tvVolumenValor);
        seekTiempo      = findViewById(R.id.seekTiempo);
        tvTiempoValor   = findViewById(R.id.tvTiempoValor);

        viewModel = new ViewModelProvider(this).get(ConfiguracionViewModel.class);

        viewModel.getConfiguracion().observe(this, config -> {
            // Si es la primera vez que se abre y no hay configuración, creamos los valores por defecto
            if (config == null) {
                configActual = new Configuracion();
                viewModel.actualizar(configActual);
            } else {
                configActual = config;
            }

            // Cargamos los valores en la UI
            switchSonido.setChecked(configActual.isSonido());
            switchVibracion.setChecked(configActual.isVibracion());
            seekVolumen.setProgress(configActual.getVolumen());
            tvVolumenValor.setText(configActual.getVolumen() + "%");
            seekTiempo.setProgress(configActual.getTiempoParadaSeg());
            tvTiempoValor.setText(formatearTiempo(configActual.getTiempoParadaSeg()));
        });

        // Volcamos los valores guardados en los controles
        switchSonido.setOnCheckedChangeListener((btn, isChecked) -> {
            if (configActual != null) {
                configActual.setSonido(isChecked);
                viewModel.actualizar(configActual);
            }
        });

        // Vibracion
        switchVibracion.setOnCheckedChangeListener((btn, isChecked) -> {
            if (configActual != null) {
                configActual.setVibracion(isChecked);
                viewModel.actualizar(configActual);
            }
        });

        // Volumen
        seekVolumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvVolumenValor.setText(progress + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (configActual != null) {
                    configActual.setVolumen(seekBar.getProgress());
                    viewModel.actualizar(configActual);
                }
            }
        });

        // Tiempo hasta la parada automatica
        seekTiempo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTiempoValor.setText(formatearTiempo(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (configActual != null) {
                    configActual.setTiempoParadaSeg(seekBar.getProgress());
                    viewModel.actualizar(configActual);
                }
            }
        });
    }

    // Formatea los segundos a "30 seg" "1 min" "2 min 30 seg"
    private String formatearTiempo(int segundos) {
        if (segundos == 0) return "Nunca (manual)";
        if (segundos < 60) return segundos + " seg";
        int min = segundos / 60;
        int seg = segundos % 60;
        if (seg == 0) return min + " min";
        return min + " min " + seg + " seg";
    }
}