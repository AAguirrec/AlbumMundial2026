package com.mundial2026.album.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entidad Room que representa un usuario registrado.
 * La clave se guarda como hash SHA-256, nunca en texto plano.
 */
@Entity(tableName = "usuarios")
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String nombre;       // Nombre para mostrar

    @NonNull
    private String correo;       // Correo único (sirve como username)

    @NonNull
    private String claveHash;    // SHA-256 de la clave

    // ── Constructor ──────────────────────────────────────────────────────────
    public Usuario(@NonNull String nombre, @NonNull String correo, @NonNull String claveHash) {
        this.nombre    = nombre;
        this.correo    = correo;
        this.claveHash = claveHash;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int    getId()        { return id; }
    public String getNombre()    { return nombre; }
    public String getCorreo()    { return correo; }
    public String getClaveHash() { return claveHash; }

    // ── Setter solo para el id (Room lo necesita) ─────────────────────────────
    public void setId(int id) { this.id = id; }
}
