package com.example.alarmaapp.navegacion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmaapp.R;

public class RepeticionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeticion);

        // Recogemos los días que ya estaban seleccionados (si venimos de editar)
        boolean lunes     = getIntent().getBooleanExtra("lunes", false);
        boolean martes    = getIntent().getBooleanExtra("martes", false);
        boolean miercoles = getIntent().getBooleanExtra("miercoles", false);
        boolean jueves    = getIntent().getBooleanExtra("jueves", false);
        boolean viernes   = getIntent().getBooleanExtra("viernes", false);
        boolean sabado    = getIntent().getBooleanExtra("sabado", false);
        boolean domingo   = getIntent().getBooleanExtra("domingo", false);

        CheckBox cbLunes     = findViewById(R.id.cbLunes);
        CheckBox cbMartes    = findViewById(R.id.cbMartes);
        CheckBox cbMiercoles = findViewById(R.id.cbMiercoles);
        CheckBox cbJueves    = findViewById(R.id.cbJueves);
        CheckBox cbViernes   = findViewById(R.id.cbViernes);
        CheckBox cbSabado    = findViewById(R.id.cbSabado);
        CheckBox cbDomingo   = findViewById(R.id.cbDomingo);
        Button btnGuardar    = findViewById(R.id.btnGuardar);

        // Precargamos los días seleccionados
        cbLunes.setChecked(lunes);
        cbMartes.setChecked(martes);
        cbMiercoles.setChecked(miercoles);
        cbJueves.setChecked(jueves);
        cbViernes.setChecked(viernes);
        cbSabado.setChecked(sabado);
        cbDomingo.setChecked(domingo);

        btnGuardar.setOnClickListener(v -> {
            // Devolvemos los días seleccionados a NuevaAlarma/EditarAlarmaActivity
            Intent resultado = new Intent();
            resultado.putExtra("lunes",     cbLunes.isChecked());
            resultado.putExtra("martes",    cbMartes.isChecked());
            resultado.putExtra("miercoles", cbMiercoles.isChecked());
            resultado.putExtra("jueves",    cbJueves.isChecked());
            resultado.putExtra("viernes",   cbViernes.isChecked());
            resultado.putExtra("sabado",    cbSabado.isChecked());
            resultado.putExtra("domingo",   cbDomingo.isChecked());
            setResult(RESULT_OK, resultado);
            finish();
        });
    }
}