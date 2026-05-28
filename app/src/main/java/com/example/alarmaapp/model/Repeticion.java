package com.example.alarmaapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "repeticion",
    foreignKeys = @ForeignKey(
        entity = Alarma.class,
        parentColumns = "id",
        childColumns = "alarmaId",
        onDelete = ForeignKey.CASCADE
    )
)
public class Repeticion {

    @PrimaryKey
    private long alarmaId;

    private boolean lunes;
    private boolean martes;
    private boolean miercoles;
    private boolean jueves;
    private boolean viernes;
    private boolean sabado;
    private boolean domingo;

    public Repeticion(long alarmaId, boolean lunes, boolean martes, boolean miercoles,
                      boolean jueves, boolean viernes, boolean sabado, boolean domingo) {
        this.alarmaId  = alarmaId;
        this.lunes     = lunes;
        this.martes    = martes;
        this.miercoles = miercoles;
        this.jueves    = jueves;
        this.viernes   = viernes;
        this.sabado    = sabado;
        this.domingo   = domingo;
    }

    public long getAlarmaId() { return alarmaId; }
    public void setAlarmaId(long alarmaId) { this.alarmaId = alarmaId; }

    public boolean isLunes() { return lunes; }
    public void setLunes(boolean lunes) { this.lunes = lunes; }

    public boolean isMartes() { return martes; }
    public void setMartes(boolean martes) { this.martes = martes; }

    public boolean isMiercoles() { return miercoles; }
    public void setMiercoles(boolean miercoles) { this.miercoles = miercoles; }

    public boolean isJueves() { return jueves; }
    public void setJueves(boolean jueves) { this.jueves = jueves; }

    public boolean isViernes() { return viernes; }
    public void setViernes(boolean viernes) { this.viernes = viernes; }

    public boolean isSabado() { return sabado; }
    public void setSabado(boolean sabado) { this.sabado = sabado; }

    public boolean isDomingo() { return domingo; }
    public void setDomingo(boolean domingo) { this.domingo = domingo; }
}