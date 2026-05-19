package com.mundial2026.album.model;

/**
 * Estados posibles de una lámina del álbum.
 */
public enum EstadoLamina {
    FALTA,      // No la tiene
    TIENE,      // Tiene exactamente 1
    REPETIDA    // Tiene más de 1 (las extras son para intercambio)
}
