package com.mundial2026.album.utils;

import com.mundial2026.album.model.EstadoLamina;
import com.mundial2026.album.model.Lamina;
import java.util.ArrayList;
import java.util.List;

/**
 * Genera la lista completa de láminas del Álbum Panini FIFA World Cup 2026.
 *
 * Ajusta TOTAL_LAMINAS y los rangos cuando Panini publique el listado oficial.
 */
public class DatosIniciales {

    private static final int TOTAL_LAMINAS = 670;

    // ── Secciones con sus rangos de números ──────────────────────────────────
    private static final String[][] SECCIONES = {
        // { "Nombre sección", "inicio", "fin" }
        { "FIFA / Intro",           "1",   "20" },
        { "Estadios USA",          "21",   "50" },
        { "Estadios MEX/CAN",      "51",   "60" },
        // Grupo A
        { "México",                "61",   "73" },
        { "Estados Unidos",        "74",   "86" },
        { "Canadá",                "87",   "99" },
        // Grupo B
        { "Argentina",            "100",  "112" },
        { "Ecuador",              "113",  "125" },
        { "Chile",                "126",  "138" },
        { "Perú",                 "139",  "151" },
        // Grupo C
        { "Brasil",               "152",  "164" },
        { "Colombia",             "165",  "177" },
        { "Uruguay",              "178",  "190" },
        { "Venezuela",            "191",  "203" },
        // Grupo D
        { "Francia",              "204",  "216" },
        { "España",               "217",  "229" },
        { "Alemania",             "230",  "242" },
        { "Portugal",             "243",  "255" },
        // Grupo E
        { "Inglaterra",           "256",  "268" },
        { "Italia",               "269",  "281" },
        { "Países Bajos",         "282",  "294" },
        { "Bélgica",              "295",  "307" },
        // Grupo F
        { "Marruecos",            "308",  "320" },
        { "Senegal",              "321",  "333" },
        { "Nigeria",              "334",  "346" },
        { "Costa de Marfil",      "347",  "359" },
        // Grupo G
        { "Japón",                "360",  "372" },
        { "Corea del Sur",        "373",  "385" },
        { "Australia",            "386",  "398" },
        { "Arabia Saudita",       "399",  "411" },
        // Grupo H
        { "Irán",                 "412",  "424" },
        { "Serbia",               "425",  "437" },
        { "Polonia",              "438",  "450" },
        { "Croacia",              "451",  "463" },
        // Resto hasta 670
        { "Otras selecciones",    "464",  String.valueOf(TOTAL_LAMINAS) }
    };

    public static List<Lamina> generarLaminas() {
        List<Lamina> lista = new ArrayList<>();

        for (String[] sec : SECCIONES) {
            String nombre = sec[0];
            int inicio    = Integer.parseInt(sec[1]);
            int fin       = Integer.parseInt(sec[2]);

            for (int num = inicio; num <= fin; num++) {
                lista.add(new Lamina(
                    num,
                    nombre,
                    "Lámina #" + num + " - " + nombre,
                    EstadoLamina.FALTA,
                    0
                ));
            }
        }

        return lista;
    }
}
