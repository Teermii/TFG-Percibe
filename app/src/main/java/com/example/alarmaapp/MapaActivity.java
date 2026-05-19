package com.example.alarmaapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng ubicacionSeleccionada;
    private Circle circuloActual;
    private EditText etBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        etBuscar = findViewById(R.id.etBuscar);
        Button btnBuscar = findViewById(R.id.btnBuscar);
        Button btnConfirmar = findViewById(R.id.btnConfirmar);

        // cuando el mapa este listo, llamra automaticamente a onMapReady()
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnBuscar.setOnClickListener(v -> buscarDireccion());

        // Devolver la ubicación seleccionada a NuevaAlarmaActivity
        btnConfirmar.setOnClickListener(v -> {
            if (ubicacionSeleccionada == null) {
                Toast.makeText(this, "Selecciona una ubicación en el mapa", Toast.LENGTH_SHORT).show();
                return;
            }
            // devolvemos las coordenadas seleccionadas al intent que abrio esta activity
            Intent resultado = new Intent();
            resultado.putExtra("latitud", ubicacionSeleccionada.latitude);
            resultado.putExtra("longitud", ubicacionSeleccionada.longitude);
            setResult(RESULT_OK, resultado);
            finish();
        });
    }

    // Metodo para buscar una direccion, el propio Geofence puede convertir texto a coordenadas
    private void buscarDireccion() {
        String direccion = etBuscar.getText().toString().trim();

        if (direccion.isEmpty()) {
            Toast.makeText(this, "Escribe una dirección", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // como el Geocoder hace una peticion de red, no puede ir en el hilo principal
        new Thread(() -> {
            try {
                List<Address> resultados = geocoder.getFromLocationName(direccion, 1);

                // convertimos la direccion escrita en coordenadas
                if (resultados != null && !resultados.isEmpty()) {
                    Address resultado = resultados.get(0);
                    LatLng coordenadas = new LatLng(resultado.getLatitude(), resultado.getLongitude());

                    /* volvemos al hilo principal (no se puede tocar la UI desde uno secundario) para enfocar
                    * la camara y dibujar un circulo en la direccion seleecionada
                     */
                    runOnUiThread(() -> {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15f));
                        dibujarCirculo(coordenadas);
                        ubicacionSeleccionada = coordenadas;
                        Toast.makeText(this, resultado.getAddressLine(0), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Dirección no encontrada", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al buscar", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    // metodo para dibujar los circulos
    private void dibujarCirculo(LatLng coordenadas) {
        // si ya hay un circulo simplemente se borra
        if (circuloActual != null) {
            circuloActual.remove();
        }
        // segun las coordenadas agregamos al mapa el nuevo circulo (lo dibujamos)
        circuloActual = mMap.addCircle(new CircleOptions()
                .center(coordenadas)
                .radius(100)
                .strokeColor(0xFF0000FF)
                .fillColor(0x220000FF));
    }

    // cuando el mapa esta listo, se llama a este metodo automaticamente, crearemos el mapa y dejaremos España enfocada
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng espania = new LatLng(40.416775, -3.703790);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(espania, 6f));

        mMap.setOnMapClickListener(latLng -> {
            ubicacionSeleccionada = latLng;
            dibujarCirculo(latLng);
        });
    }
}