package com.example.alarmaapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
<<<<<<< HEAD
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;
=======
>>>>>>> b43534b4eaad35c67556c7bffae87d2c2bf6ec8f

@Entity(
        tableName = "historial",
        foreignKeys = @ForeignKey(
                entity = Alarma.class,
                parentColumns = "id",
                childColumns = "alarmaId",
                onDelete = ForeignKey.CASCADE // Si se borra la alarma, se borra su historial
<<<<<<< HEAD
        ),
        indices = {@Index("alarmaId")}
=======
        )
>>>>>>> b43534b4eaad35c67556c7bffae87d2c2bf6ec8f
)
public class HistorialActivacion {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long alarmaId;
<<<<<<< HEAD
    @NotNull
    private String fecha;
    @NotNull
    private String hora;

    public HistorialActivacion(long alarmaId, String fecha, String hora) {
        this.alarmaId = alarmaId;
=======
    private String nombreAlarma;
    private String fecha;
    private String hora;

    public HistorialActivacion(long alarmaId, String nombreAlarma,
                               String fecha, String hora) {
        this.alarmaId = alarmaId;
        this.nombreAlarma = nombreAlarma;
>>>>>>> b43534b4eaad35c67556c7bffae87d2c2bf6ec8f
        this.fecha = fecha;
        this.hora = hora;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getAlarmaId() { return alarmaId; }
    public void setAlarmaId(long alarmaId) { this.alarmaId = alarmaId; }

<<<<<<< HEAD
=======
    public String getNombreAlarma() { return nombreAlarma; }
    public void setNombreAlarma(String nombreAlarma) { this.nombreAlarma = nombreAlarma; }

>>>>>>> b43534b4eaad35c67556c7bffae87d2c2bf6ec8f
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
}