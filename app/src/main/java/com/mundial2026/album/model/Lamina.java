package com.mundial2026.album.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entidad Room que representa una lámina del Álbum Panini FIFA World Cup 2026.
 */
@Entity(tableName = "laminas")
public class Lamina {

    @PrimaryKey
    private int numero;          // Número único de la lámina

    @NonNull
    private String seccion;      // Ej: "Colombia", "Argentina", "FIFA"

    @NonNull
    private String descripcion;  // Nombre jugador / escudo / estadio

    @NonNull
    private EstadoLamina estado; // FALTA | TIENE | REPETIDA

    private int cantidad;        // Cuántas unidades tiene (0 = falta)

    // ── Constructor ──────────────────────────────────────────────────────────
    public Lamina(int numero, @NonNull String seccion,
                  @NonNull String descripcion,
                  @NonNull EstadoLamina estado, int cantidad) {
        this.numero      = numero;
        this.seccion     = seccion;
        this.descripcion = descripcion;
        this.estado      = estado;
        this.cantidad    = cantidad;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int getNumero()              { return numero; }
    public String getSeccion()          { return seccion; }
    public String getDescripcion()      { return descripcion; }
    public EstadoLamina getEstado()     { return estado; }
    public int getCantidad()            { return cantidad; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setNumero(int numero)              { this.numero = numero; }
    public void setSeccion(String seccion)         { this.seccion = seccion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setEstado(EstadoLamina estado)     { this.estado = estado; }
    public void setCantidad(int cantidad)          { this.cantidad = cantidad; }
}
