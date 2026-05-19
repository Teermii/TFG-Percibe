package com.example.alarmaapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Tabla con UNA sola fila que guarda la configuración global de la app.
 * El id es siempre 1 — solo hay una configuración en toda la app.
 */
@Entity(tableName = "configuracion")
public class Configuracion {

    @PrimaryKey
    private int id = 1; // Siempre 1, solo existe una fila

    private boolean sonido;       // ¿Reproduce sonido al activarse?
    private boolean vibracion;    // ¿Vibra al activarse?
    private int volumen;          // 0-100 (porcentaje del volumen del sistema)
    private int tiempoParadaSeg;  // Segundos hasta parar automáticamente (0 = nunca)

    public Configuracion() {
        // Valores por defecto
        this.sonido = true;
        this.vibracion = true;
        this.volumen = 80;
        this.tiempoParadaSeg = 60; // 1 minuto por defecto
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isSonido() { return sonido; }
    public void setSonido(boolean sonido) { this.sonido = sonido; }

    public boolean isVibracion() { return vibracion; }
    public void setVibracion(boolean vibracion) { this.vibracion = vibracion; }

    public int getVolumen() { return volumen; }
    public void setVolumen(int volumen) { this.volumen = volumen; }

    public int getTiempoParadaSeg() { return tiempoParadaSeg; }
    public void setTiempoParadaSeg(int tiempoParadaSeg) { this.tiempoParadaSeg = tiempoParadaSeg; }
}