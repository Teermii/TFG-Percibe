package com.example.alarmaapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

// Hacemos que esta clase sea una tabla de la base de datos

@Entity(
        tableName = "alarmas",
        foreignKeys = @ForeignKey(
                entity = Categoria.class,
                parentColumns = "id",
                childColumns = "categoriaId",
                onDelete = ForeignKey.SET_NULL // Si se borra la categoría, la alarma queda sin categoría
        )
) // "alarmas" será el nombre de la tabla
public class Alarma {

    // Cada atributo será una columna de la tabla
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String nombre;
    private double latitud;
    private double longitud;
    private double radio;
    private boolean activa;
    private Long categoriaId; // Long con mayúscula para permitir null (sin categoría)
    private String nota;

    // Constructor
    public Alarma(String nombre, double latitud, double longitud, double radio, boolean activa, Long categoriaId, String nota) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.radio = radio;
        this.activa = activa;
        this.categoriaId = categoriaId;
        this.nota = nota;
    }

    // Getters y Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public double getRadio() { return radio; }
    public void setRadio(double radio) { this.radio = radio; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
}