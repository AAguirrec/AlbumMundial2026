package com.mundial2026.album.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para hashear contraseñas con SHA-256.
 * SHA-256 es un algoritmo de hashing unidireccional:
 * nunca se puede recuperar la clave original desde el hash.
 */
public class ShaUtils {

    private ShaUtils() { /* no instanciar */ }

    /**
     * Convierte una cadena de texto a su hash SHA-256 en formato hexadecimal.
     * @param texto La contraseña en texto plano
     * @return Hash de 64 caracteres hexadecimales, o null si falla
     */
    public static String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(
                    texto.getBytes(StandardCharsets.UTF_8)
            );
            // Convertir bytes a hexadecimal
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica si una clave en texto plano coincide con su hash guardado.
     */
    public static boolean verificar(String textPlano, String hashGuardado) {
        String hashIngresado = sha256(textPlano);
        return hashIngresado != null && hashIngresado.equals(hashGuardado);
    }
}
