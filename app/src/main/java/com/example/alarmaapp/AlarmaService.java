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

public class AlarmaService extends Service {

    public static final String CHANNEL_ID = "alarma_channel";
    public static final String ACTION_STOP = "com.example.alarmaapp.STOP_ALARM";
    private static final int NOTIF_ID = 1001;

    private Ringtone ringtone;
    private Vibrator vibrator;

    // Para guardar el volumen original del usuario y restaurarlo al parar
    private AudioManager audioManager;
    private int volumenOriginal = -1;

    // Handler para la parada automática
    private Handler handler;
    private Runnable runnableParada;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        String nombre = intent != null ? intent.getStringExtra("nombre") : null;
        String nota   = intent != null ? intent.getStringExtra("nota")   : null;

        crearCanalNotificacion();
        startForeground(NOTIF_ID, construirNotificacion(nombre, nota));

        // ── Leer configuración en hilo secundario ────────────────────────────
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

    private void aplicarConfiguracion(Configuracion config) {
        // ── Sonido ────────────────────────────────────────────────────────────
        if (config.isSonido()) {
            // Ajustamos el volumen del stream de alarma según la configuración
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (audioManager != null) {
                int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                volumenOriginal = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                int volumenNuevo = (int) (maxVol * (config.getVolumen() / 100f));
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volumenNuevo, 0);
            }

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(this, uri);
            if (ringtone != null) ringtone.play();
        }

        // ── Vibración ─────────────────────────────────────────────────────────
        if (config.isVibracion()) {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                long[] patron = {0, 1000, 500};
                vibrator.vibrate(VibrationEffect.createWaveform(patron, 0));
            }
        }

        // ── Parada automática ─────────────────────────────────────────────────
        // tiempoParadaSeg = 0 significa que NO se para automáticamente
        if (config.getTiempoParadaSeg() > 0) {
            handler = new Handler(Looper.getMainLooper());
            runnableParada = this::stopSelf;
            handler.postDelayed(runnableParada, config.getTiempoParadaSeg() * 1000L);
        }
    }

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
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPI, true)
                .setOngoing(true)
                .setAutoCancel(false)
                .build();
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarmas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones de alarma geolocalizada");
            channel.setSound(null, null);
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Paramos sonido y vibración
        if (ringtone != null && ringtone.isPlaying()) ringtone.stop();
        if (vibrator != null) vibrator.cancel();

        // Restauramos el volumen original del usuario
        if (audioManager != null && volumenOriginal >= 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volumenOriginal, 0);
        }

        // Cancelamos la parada automática si estaba programada
        if (handler != null && runnableParada != null) {
            handler.removeCallbacks(runnableParada);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}