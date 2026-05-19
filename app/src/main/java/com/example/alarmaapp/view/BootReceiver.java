package com.example.alarmaapp.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.alarmaapp.database.AppDatabase;
import com.example.alarmaapp.model.Alarma;
import com.example.alarmaapp.repository.GeofenceManager;

import java.util.List;

/* Cuando Android reinicia el dispositivo borra TODAS las geocercas registradas.
 * Si no hicieramos nada, las alarmas seguirian guardadas en la base de datos pero
 * el sistema ya no estaria vigilando las zonas, asi que nunca se dispararian.
 *
 * Este BroadcastReceiver se ejecuta justo despues de que el dispositivo termina
 * de arrancar (action BOOT_COMPLETED) y vuelve a registrar todas las alarmas
 * que estuvieran activas.
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        // Aceptamos BOOT_COMPLETED y tambien LOCKED_BOOT_COMPLETED por si la app
        // tiene que arrancar antes de desbloquear
        String action = intent.getAction();
        boolean esBoot = Intent.ACTION_BOOT_COMPLETED.equals(action)
                || "android.intent.action.LOCKED_BOOT_COMPLETED".equals(action)
                || "android.intent.action.QUICKBOOT_POWERON".equals(action);
        if (!esBoot) return;

        // Sin permiso de ubicacion no podemos registrar nada, salimos sin hacer ruido
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Sin permiso de ubicacion, no se re-registran las geocercas");
            return;
        }

        // Como onReceive corre en el hilo principal y solo nos dan unos segundos,
        // usamos goAsync() para tener mas tiempo y trabajamos en un hilo aparte.
        final PendingResult pendingResult = goAsync();

        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);
                // No podemos usar LiveData fuera de UI, hacemos una query sincrona
                List<Alarma> activas = db.alarmaDao().getAlarmasActivasDirecto();
                if (activas == null || activas.isEmpty()) {
                    Log.i(TAG, "No hay alarmas activas que re-registrar");
                    return;
                }

                GeofenceManager manager = new GeofenceManager(context.getApplicationContext());
                for (Alarma a : activas) {
                    manager.registrarGeofence(a);
                }
                Log.i(TAG, "Re-registradas " + activas.size() + " geocercas tras el reinicio");
            } catch (Exception e) {
                Log.e(TAG, "Error re-registrando geocercas", e);
            } finally {
                pendingResult.finish();
            }
        }).start();
    }
}
