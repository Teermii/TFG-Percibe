package com.example.alarmaapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.example.alarmaapp.database.AppDatabase;
import com.example.alarmaapp.model.Configuracion;
// Servicio que hacer sonar y vibrar la alarma, funciona en segundo plano
public class AlarmaService extends Service {

    public static final String CHANNEL_ID = "alarma_channel";
    public static final String ACTION_STOP = "com.example.alarmaapp.STOP_ALARM";
    private static final int NOTIF_ID = 1001;

    private Ringtone ringtone;
    private Vibrator vibrator;

    // Para guardar el volumen original del usuario y restaurarlo al parar
    private AudioManager audioManager;
    private int volumenOriginal = -1;

    // Handler para la parada automatica
    private Handler handler;
    private Runnable runnableParada;

    // Se ejecuta cuando alguien arranca el servicio
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        // Guardamos el nombre y la nota de la alarma
        String nombre = intent != null ? intent.getStringExtra("nombre") : null;
        String nota   = intent != null ? intent.getStringExtra("nota")   : null;

        crearCanalNotificacion();
        startForeground(NOTIF_ID, construirNotificacion(nombre, nota));

        // Leemos la configuracion en un hilo secundario, Room no se puede leer en el principal
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            Configuracion config = db.configuracionDao().getConfiguracionDirecto();

            // Si nunca se ha guardado configuración, usamos los valores por defecto
            if (config == null) config = new Configuracion();

            final Configuracion configFinal = config;

            // Volvemos al hilo principal para usar las APIs de sonido/vibración
            new Handler(Looper.getMainLooper()).post(() -> aplicarConfiguracion(configFinal));
        }).start();

        return START_NOT_STICKY;
    }

    // Aplica sonido, vibración y parada automática según la configuracion
    private void aplicarConfiguracion(Configuracion config) {
        // Sonido
        if (config.isSonido()) {
            // Ajustamos el volumen del stream de alarma segun la configuración
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (audioManager != null) {
                int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                volumenOriginal = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                int volumenNuevo = Math.round(maxVol * (config.getVolumen() / 100f));
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volumenNuevo, 0);
            }

            // Cogemos el sonido de alarma del sistema y lo reproducimos
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(this, uri);
            if (ringtone != null) ringtone.play();
        }

        // Vibracion
        if (config.isVibracion()) {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                // Espera 0ms, vibra 1000ms, espera 500ms. El 0 final = repetir
                long[] patron = {0, 1000, 500};
                vibrator.vibrate(VibrationEffect.createWaveform(patron, 0));
            }
        }

        // Parada automatica
        // tiempoParadaSeg = 0 significa que no se para automaticamente
        if (config.getTiempoParadaSeg() > 0) {
            handler = new Handler(Looper.getMainLooper());
            runnableParada = this::stopSelf;
            handler.postDelayed(runnableParada, config.getTiempoParadaSeg() * 1000L);
        }
    }

    // Construye la notificación que abre la pantalla de alarma
    private Notification construirNotificacion(String nombre, String nota) {
        Intent activityIntent = new Intent(this, AlarmaActivaActivity.class);
        activityIntent.putExtra("nombre", nombre);
        activityIntent.putExtra("nota", nota);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent fullScreenPI = PendingIntent.getActivity(
                this, 0, activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icono_alarma)
                .setContentTitle(nombre != null ? nombre : "Alarma")
                .setContentText(nota != null && !nota.isEmpty() ? nota : "Toca para ver la alarma")
                .setPriority(NotificationCompat.PRIORITY_MAX) // Mxima prioridad
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                // Enciende la pantalla y muestra la activity aun con el movil bloqueado
                .setFullScreenIntent(fullScreenPI, true)
                .setOngoing(true)
                .setAutoCancel(false)
                .build();
    }

    // Crea el canal de notificaciones (obligatorio desde Android 8)
    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarmas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones de alarma geolocalizada");
            channel.setSound(null, null); // el sonido lo gestionamos nosotros
            channel.setBypassDnd(true); // ignora el modo "No molestar"
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    // Se ejecuta cuando el servicio se destruye (al parar la alarma)
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Paramos sonido y vibración
        if (ringtone != null && ringtone.isPlaying()) ringtone.stop();
        if (vibrator != null) vibrator.cancel();

        // Devolvemos el volumen al usuario tal como estaba
        if (audioManager != null && volumenOriginal >= 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volumenOriginal, 0);
        }

        // Cancelamos la parada automatica si estaba programada
        if (handler != null && runnableParada != null) {
            handler.removeCallbacks(runnableParada);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}