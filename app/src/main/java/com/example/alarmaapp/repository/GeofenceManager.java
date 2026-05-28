package com.example.alarmaapp.repository;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.alarmaapp.model.Alarma;
import com.example.alarmaapp.view.GeofenceBroadcastReceiver;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

// Clase importantisima, Geofencer crea circulos ficticios en el mapa y te avisa cuando alguien entra y sale de ellos
public class GeofenceManager {

    // Cliente oficial de Google para gestionar Geofencer
    private GeofencingClient geofencingClient;
    private Context context;

    public GeofenceManager(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
    }

    // "Sobre cerrado" que apunta al receiver. Android lo abrira cuando se cumpla la condición
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );
    }

    // Registramos la alarma en el Geofencer para que la este todo el rato monitoreando
    @SuppressLint("MissingPermission")
    public void registrarGeofence(Alarma alarma) {
        Geofence geofence = new Geofence.Builder()
                // Usamos el ID de ROOM para que tengan el mismo id
                .setRequestId(String.valueOf(alarma.getId()))
                // Definimos el circulo que queremos que monitoree
                .setCircularRegion(
                        alarma.getLatitud(),
                        alarma.getLongitud(),
                        (float) alarma.getRadio()
                )
                // Que nunca expire
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                /* Que avise al entrar y salir de la zona (en una última version no creo que sirva
                * lo tengo ahora para hacer pruebas de que funcione bien)
                */
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .build();

        // Creamos el registro
        GeofencingRequest request = new GeofencingRequest.Builder()
                /* Si al momento de crear la alarma, ya estas en la zona de activacion, se activara no se
                * tendra que esperar a cruzar el circulo para que se active
                */
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

        geofencingClient.addGeofences(request, getPendingIntent());
    }

    // Cuando borramos una alarma, tambien la borraremos del Geofencer
    public void eliminarGeofence(Alarma alarma) {
        List<String> ids = new ArrayList<>();
        ids.add(String.valueOf(alarma.getId()));
        geofencingClient.removeGeofences(ids);
    }
}
