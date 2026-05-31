package com.example.alarmaapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.alarmaapp.navegacion.ConfiguracionActivity;
import com.example.alarmaapp.navegacion.ListaAlarmasActivity;
import com.example.alarmaapp.navegacion.MapaGeneralActivity;
import com.example.alarmaapp.navegacion.EstadisticasActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pedirPermisosUbicacion();

        CardView cardAlarmas      = findViewById(R.id.cardAlarmas);
        CardView cardMapa         = findViewById(R.id.cardMapa);
        CardView cardEstadisticas = findViewById(R.id.cardEstadisticas);

        cardAlarmas.setOnClickListener(v ->
                startActivity(new Intent(this, ListaAlarmasActivity.class)));

        cardMapa.setOnClickListener(v ->
                startActivity(new Intent(this, MapaGeneralActivity.class)));

        cardEstadisticas.setOnClickListener(v ->
                startActivity(new Intent(this, EstadisticasActivity.class)));

        ImageButton btnConfiguracion = findViewById(R.id.btnConfiguracion);
        btnConfiguracion.setOnClickListener(v ->
                startActivity(new Intent(this, ConfiguracionActivity.class)));
    }

    // Pedimos los permisos en cascada (solo se pueden pedir de 1 en 1)
    private void pedirPermisosUbicacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 99);
                return;
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 3);
        }
    }

    // Si se acepta un permiso se invoca al metodo de pedir otra vez para pedir el siguiente
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pedirPermisosUbicacion();
        }
    }
}