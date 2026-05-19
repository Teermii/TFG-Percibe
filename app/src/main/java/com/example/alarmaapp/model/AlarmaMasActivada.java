package com.example.alarmaapp.model;

/**
 * No es una entidad de Room, es un POJO para devolver el resultado
 * de la query "alarma más activada" con su nombre y el número de veces.
 */
public class AlarmaMasActivada {
    public String nombreAlarma;
    public int veces;
}