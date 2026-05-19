package com.example.alarmaapp.navegacion;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.alarmaapp.R;
import com.example.alarmaapp.model.Alarma;
import com.example.alarmaapp.viewmodel.AlarmaViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapaGeneralActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private AlarmaViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_general);

        viewModel = new ViewModelProvider(this).get(AlarmaViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Cuando el mapa esté listo observamos las alarmas y las pintamos
        viewModel.getTodasLasAlarmas().observe(this, alarmas -> {
            mMap.clear(); // Limpiamos el mapa antes de redibujar
            if (alarmas == null || alarmas.isEmpty()) return;
            pintarAlarmas(alarmas);
        });
    }

    private void pintarAlarmas(List<Alarma> alarmas) {

        // Builder para calcular los límites del mapa y que quepan todas las alarmas
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (Alarma alarma : alarmas) {
            LatLng posicion = new LatLng(alarma.getLatitud(), alarma.getLongitud());

            // Chincheta con el nombre de la alarma
            mMap.addMarker(new MarkerOptions()
                    .position(posicion)
                    .title(alarma.getNombre())
                    .snippet("Radio: " + alarma.getRadio() + "m · " +
                            (alarma.isActiva() ? "Activa" : "Inactiva")));

            // Círculo azul que representa la zona de la alarma
            mMap.addCircle(new CircleOptions()
                    .center(posicion)
                    .radius(alarma.getRadio())
                    .strokeColor(alarma.isActiva() ? 0xFF1A73E8 : 0xFF888888)
                    .fillColor(alarma.isActiva() ? 0x221A73E8 : 0x22888888));

            boundsBuilder.include(posicion);
        }

        // Si solo hay una alarma centramos en ella con zoom fijo
        if (alarmas.size() == 1) {
            LatLng unica = new LatLng(alarmas.get(0).getLatitud(), alarmas.get(0).getLongitud());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(unica, 14f));
        } else {
            // Si hay varias ajustamos la cámara para que quepan todas
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(), 150));
        }

        // Listener para el popup al tocar una chincheta
        mMap.setOnInfoWindowClickListener(marker -> {
            // Aquí podrías abrir el detalle de la alarma en el futuro
        });
    }
}