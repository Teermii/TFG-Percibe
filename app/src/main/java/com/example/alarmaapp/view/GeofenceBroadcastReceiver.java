package com.example.alarmaapp.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.alarmaapp.AlarmaService;
import com.example.alarmaapp.database.AppDatabase;
import com.example.alarmaapp.model.Alarma;
import com.example.alarmaapp.model.Contacto;
import com.example.alarmaapp.model.HistorialActivacion;
import com.example.alarmaapp.model.Repeticion;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// La clase a la que s avisa cuando se entra en una zona vigilada
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Cogemos la info
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent == null || geofencingEvent.hasError()) {
            return;
        }

        int transicion = geofencingEvent.getGeofenceTransition();

        // Verificamos que se haya entrado en la zona y no salido
        if (transicion == Geofence.GEOFENCE_TRANSITION_ENTER) {

            List<Geofence> disparadas = geofencingEvent.getTriggeringGeofences();
            if (disparadas == null || disparadas.isEmpty()) return;

            // El id del geofence es el id de la alarma
            String idGeofence = disparadas.get(0).getRequestId();

            long alarmaIdParsed;
            try {
                alarmaIdParsed = Long.parseLong(idGeofence);
            } catch (NumberFormatException e) {
                Log.e(TAG, "ID de geofence inválido: " + idGeofence, e);
                return;
            }

            // goAsync() mantiene el proceso vivo hasta que el hilo llame a pendingResult.finish()
            final PendingResult pendingResult = goAsync();

            new Thread(() -> {
                try {
                AppDatabase db = AppDatabase.getInstance(context);
                Alarma alarma = db.alarmaDao().getAlarmaPorIdDirecto(alarmaIdParsed);

                if (alarma == null) return;

                // Comprobar repetición
                Repeticion rep = db.repeticionDao().getRepeticionPorAlarmaDirecto(alarma.getId());
                if (rep != null) {
                    int diaSemana = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    boolean hoyActivo = false;

                    // Comprobamos si el dia de hoy esta marcado
                    switch (diaSemana) {
                        case Calendar.MONDAY:    hoyActivo = rep.isLunes(); break;
                        case Calendar.TUESDAY:   hoyActivo = rep.isMartes(); break;
                        case Calendar.WEDNESDAY: hoyActivo = rep.isMiercoles(); break;
                        case Calendar.THURSDAY:  hoyActivo = rep.isJueves(); break;
                        case Calendar.FRIDAY:    hoyActivo = rep.isViernes(); break;
                        case Calendar.SATURDAY:  hoyActivo = rep.isSabado(); break;
                        case Calendar.SUNDAY:    hoyActivo = rep.isDomingo(); break;
                    }

                    // Si hay algun dia marcado pero hoy no es, no suena, sino hay ninguno marcado suena siempre
                    boolean algunDiaSeleccionado = rep.isLunes() || rep.isMartes()
                            || rep.isMiercoles() || rep.isJueves() || rep.isViernes()
                            || rep.isSabado() || rep.isDomingo();

                    if (algunDiaSeleccionado && !hoyActivo) return;
                }

                // Guardar en historial
                String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(new Date());
                String hora  = new SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(new Date());

                db.historialDao().insertar(new HistorialActivacion(
                        alarma.getId(), alarma.getNombre(), fecha, hora));

                // Enviar SMS a los contactos
                enviarSmsContactos(context, db, alarma.getId());

                // Lanzar servicio de alarma (muestra sobre pantalla de bloqueo)
                Intent alarmaIntent = new Intent(context, AlarmaService.class);
                alarmaIntent.putExtra("nombre", alarma.getNombre());
                alarmaIntent.putExtra("nota", alarma.getNota());
                androidx.core.content.ContextCompat.startForegroundService(context, alarmaIntent);
                } finally {
                    pendingResult.finish();
                }
            }).start();
        }
    }

    /* Envía un SMS a cada contacto registrado en la alarma con su mensaje personalizado
     * Si no hay permiso SEND_SMS no hace nada (sin crashear).
     */
    private void enviarSmsContactos(Context context, AppDatabase db, long alarmaId) {

        // Comprobamos el permiso antes de mandar nada
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Sin permiso SEND_SMS, no se envían SMS");
            return;
        }

        List<Contacto> contactos = db.contactoDao().getContactosPorAlarmaDirecto(alarmaId);
        if (contactos == null || contactos.isEmpty()) return;

        SmsManager smsManager = SmsManager.getDefault();

        for (Contacto contacto : contactos) {
            try {
                // divideMessage parte el mensaje en trozos si es muy largo (más de 160 caracteres)
                List<String> partes = smsManager.divideMessage(contacto.getMensaje());

                if (partes.size() == 1) {
                    smsManager.sendTextMessage(contacto.getTelefono(), null,
                            contacto.getMensaje(), null, null);
                } else {
                    // Si el mensaje es largo lo manda por partes (multipart SMS)
                    smsManager.sendMultipartTextMessage(contacto.getTelefono(), null,
                            new java.util.ArrayList<>(partes), null, null);
                }
                Log.i(TAG, "SMS enviado a " + contacto.getNombre());
            } catch (Exception e) {
                Log.e(TAG, "Error enviando SMS a " + contacto.getNombre(), e);
            }
        }
    }
}