package com.example.alarmaapp.crud;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.alarmaapp.MapaActivity;
import com.example.alarmaapp.R;
import com.example.alarmaapp.database.AppDatabase;
import com.example.alarmaapp.model.Alarma;
import com.example.alarmaapp.model.Categoria;
import com.example.alarmaapp.model.Repeticion;
import com.example.alarmaapp.navegacion.ContactosActivity;
import com.example.alarmaapp.navegacion.RepeticionActivity;
import com.example.alarmaapp.viewmodel.AlarmaViewModel;
import com.example.alarmaapp.viewmodel.CategoriaViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditarAlarmaActivity extends AppCompatActivity {

    private AlarmaViewModel viewModel;
    private EditText etNombre, etRadio;
    private Button btnAbrirMapa;
    private Button btnRepeticion;
    private LatLng ubicacionSeleccionada;
    private long alarmaId;
    private boolean alarmaActiva;
    private List<Categoria> listaCategorias = new ArrayList<>();
    private Long categoriaSeleccionadaId = null;
    private boolean repLunes, repMartes, repMiercoles, repJueves,
                    repViernes, repSabado, repDomingo;

    private final ActivityResultLauncher<Intent> mapaLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    double latitud  = result.getData().getDoubleExtra("latitud", 0);
                    double longitud = result.getData().getDoubleExtra("longitud", 0);
                    ubicacionSeleccionada = new LatLng(latitud, longitud);
                    new Thread(() -> {
                        try {
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            List<Address> resultados = geocoder.getFromLocation(latitud, longitud, 1);
                            if (resultados != null && !resultados.isEmpty()) {
                                String direccion = resultados.get(0).getAddressLine(0);
                                runOnUiThread(() -> btnAbrirMapa.setText(direccion));
                            }
                        } catch (IOException e) {
                            runOnUiThread(() ->
                                    Toast.makeText(this, "Ubicación seleccionada ✓", Toast.LENGTH_SHORT).show()
                            );
                        }
                    }).start();
                }
            });

    private final ActivityResultLauncher<Intent> repeticionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    repLunes     = result.getData().getBooleanExtra("lunes", false);
                    repMartes    = result.getData().getBooleanExtra("martes", false);
                    repMiercoles = result.getData().getBooleanExtra("miercoles", false);
                    repJueves    = result.getData().getBooleanExtra("jueves", false);
                    repViernes   = result.getData().getBooleanExtra("viernes", false);
                    repSabado    = result.getData().getBooleanExtra("sabado", false);
                    repDomingo   = result.getData().getBooleanExtra("domingo", false);
                    btnRepeticion.setText(obtenerTextoDias());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_alarma);

        viewModel = new ViewModelProvider(this).get(AlarmaViewModel.class);

        etNombre      = findViewById(R.id.etNombre);
        etRadio       = findViewById(R.id.etRadio);
        btnAbrirMapa  = findViewById(R.id.btnAbrirMapa);
        btnRepeticion = findViewById(R.id.btnRepeticion);
        Button btnGuardar  = findViewById(R.id.btnGuardar);
        Button btnCancelar = findViewById(R.id.btnCancelar);
        Button btnEliminar = findViewById(R.id.btnEliminar);
        Spinner spinnerCategoria = findViewById(R.id.spinnerCategoria);
        EditText etNota = findViewById(R.id.etNota);

        // ── Datos del Intent ──────────────────────────────────────────────────
        alarmaId = getIntent().getLongExtra("id", -1);
        String nombre   = getIntent().getStringExtra("nombre");
        double latitud  = getIntent().getDoubleExtra("latitud", 0);
        double longitud = getIntent().getDoubleExtra("longitud", 0);
        double radio    = getIntent().getDoubleExtra("radio", 100);
        // Conservamos el estado del switch para no reactivar alarmas que el usuario habia apagado
        alarmaActiva    = getIntent().getBooleanExtra("activa", true);

        categoriaSeleccionadaId = getIntent().getLongExtra("categoriaId", -1L);
        if (categoriaSeleccionadaId == -1L) categoriaSeleccionadaId = null;

        String nota = getIntent().getStringExtra("nota");
        etNota.setText(nota != null ? nota : "");

        // ── Precargar campos ──────────────────────────────────────────────────
        etNombre.setText(nombre);
        etRadio.setText(String.valueOf(radio));
        ubicacionSeleccionada = new LatLng(latitud, longitud);

        // Mostrar dirección actual en el botón del mapa
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> resultados = geocoder.getFromLocation(latitud, longitud, 1);
                if (resultados != null && !resultados.isEmpty()) {
                    String direccion = resultados.get(0).getAddressLine(0);
                    runOnUiThread(() -> btnAbrirMapa.setText(direccion));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // ── Precargar repetición ──────────────────────────────────────────────
        AppDatabase db = AppDatabase.getInstance(this);
        new Thread(() -> {
            Repeticion rep = db.repeticionDao().getRepeticionPorAlarmaDirecto(alarmaId);
            if (rep != null) {
                repLunes     = rep.isLunes();
                repMartes    = rep.isMartes();
                repMiercoles = rep.isMiercoles();
                repJueves    = rep.isJueves();
                repViernes   = rep.isViernes();
                repSabado    = rep.isSabado();
                repDomingo   = rep.isDomingo();
                runOnUiThread(() -> btnRepeticion.setText(obtenerTextoDias()));
            }
        }).start();

        // ── Categorías ────────────────────────────────────────────────────────
        CategoriaViewModel categoriaViewModel = new ViewModelProvider(this)
                .get(CategoriaViewModel.class);

        categoriaViewModel.getTodasLasCategorias().observe(this, categorias -> {
            listaCategorias = categorias;
            List<String> nombres = new ArrayList<>();
            nombres.add("Sin categoría");
            for (Categoria c : categorias) nombres.add(c.getNombre());
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, nombres);
            spinnerAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            spinnerCategoria.setAdapter(spinnerAdapter);

            if (categoriaSeleccionadaId != null) {
                for (int i = 0; i < categorias.size(); i++) {
                    if (categorias.get(i).getId() == categoriaSeleccionadaId) {
                        spinnerCategoria.setSelection(i + 1);
                        break;
                    }
                }
            }
        });

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    categoriaSeleccionadaId = null;
                } else if (position - 1 < listaCategorias.size()) {
                    categoriaSeleccionadaId = listaCategorias.get(position - 1).getId();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ── Botones ───────────────────────────────────────────────────────────
        btnAbrirMapa.setOnClickListener(v ->
                mapaLauncher.launch(new Intent(this, MapaActivity.class)));

        btnRepeticion.setOnClickListener(v -> {
            Intent intent = new Intent(this, RepeticionActivity.class);
            intent.putExtra("lunes",     repLunes);
            intent.putExtra("martes",    repMartes);
            intent.putExtra("miercoles", repMiercoles);
            intent.putExtra("jueves",    repJueves);
            intent.putExtra("viernes",   repViernes);
            intent.putExtra("sabado",    repSabado);
            intent.putExtra("domingo",   repDomingo);
            repeticionLauncher.launch(intent);
        });

        Button btnContactos = findViewById(R.id.btnContactos);
        btnContactos.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactosActivity.class);
            intent.putExtra("alarmaId", alarmaId);
            intent.putExtra("nombreAlarma", nombre);
            startActivity(intent);
        });

        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString().trim();
            String radioStr    = etRadio.getText().toString().trim();

            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "Escribe un nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            float nuevoRadio = radioStr.isEmpty() ? 100f : Float.parseFloat(radioStr);

            String nuevaNota = etNota.getText().toString().trim();

            Alarma actualizada = new Alarma(
                    nuevoNombre,
                    ubicacionSeleccionada.latitude,
                    ubicacionSeleccionada.longitude,
                    nuevoRadio,
                    true,
                    categoriaSeleccionadaId,
                    nuevaNota
            );
            actualizada.setId(alarmaId);
            actualizada.setId(alarmaId);
            viewModel.actualizar(actualizada);

            // Guardamos la repetición actualizada
            new Thread(() -> {
                Repeticion rep = new Repeticion(alarmaId, repLunes, repMartes, repMiercoles,
                        repJueves, repViernes, repSabado, repDomingo);
                db.repeticionDao().insertar(rep);
                runOnUiThread(this::finish);
            }).start();
        });

        btnCancelar.setOnClickListener(v -> finish());

        btnEliminar.setOnClickListener(v -> {
            Alarma alarma = new Alarma(nombre, latitud, longitud, radio, true, categoriaSeleccionadaId, nota);
            alarma.setId(alarmaId);
            viewModel.eliminar(alarma);
            finish();
        });
    }

    private String obtenerTextoDias() {
        StringBuilder sb = new StringBuilder();
        if (repLunes)     sb.append("L ");
        if (repMartes)    sb.append("M ");
        if (repMiercoles) sb.append("X ");
        if (repJueves)    sb.append("J ");
        if (repViernes)   sb.append("V ");
        if (repSabado)    sb.append("S ");
        if (repDomingo)   sb.append("D ");
        return sb.length() > 0 ? sb.toString().trim() : "Repetición";
    }
}