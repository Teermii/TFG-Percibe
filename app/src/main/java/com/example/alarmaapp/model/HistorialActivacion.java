package com.example.alarmaapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

@Entity(
        tableName = "historial",
        foreignKeys = @ForeignKey(
                entity = Alarma.class,
                parentColumns = "id",
                childColumns = "alarmaId",
                onDelete = ForeignKey.CASCADE // Si se borra la alarma, se borra su historial
        ),
        indices = {@Index("alarmaId")}
)
public class HistorialActivacion {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long alarmaId;
    @NotNull
    private String fecha;
    @NotNull
    private String hora;

    public HistorialActivacion(long alarmaId, String fecha, String hora) {
        this.alarmaId = alarmaId;
        this.fecha = fecha;
        this.hora = hora;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getAlarmaId() { return alarmaId; }
    public void setAlarmaId(long alarmaId) { this.alarmaId = alarmaId; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
}