package com.mundial2026.album.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.mundial2026.album.model.EstadoLamina;
import com.mundial2026.album.model.Lamina;
import java.util.List;

@Dao
public interface LaminaDao {

    // ── Consultas generales ──────────────────────────────────────────────────

    @Query("SELECT * FROM laminas ORDER BY numero ASC")
    LiveData<List<Lamina>> getAllLaminas();

    @Query("SELECT * FROM laminas WHERE seccion = :seccion ORDER BY numero ASC")
    LiveData<List<Lamina>> getLaminasPorSeccion(String seccion);

    @Query("SELECT DISTINCT seccion FROM laminas ORDER BY seccion ASC")
    LiveData<List<String>> getSecciones();

    @Query("SELECT * FROM laminas WHERE numero = :numero LIMIT 1")
    Lamina getLaminaByNumero(int numero);   // llamar en hilo de fondo

    // ── Estadísticas ─────────────────────────────────────────────────────────

    @Query("SELECT COUNT(*) FROM laminas")
    LiveData<Integer> getTotalLaminas();

    @Query("SELECT COUNT(*) FROM laminas WHERE estado = 'TIENE' OR estado = 'REPETIDA'")
    LiveData<Integer> getTotalTengo();

    @Query("SELECT COUNT(*) FROM laminas WHERE estado = 'FALTA'")
    LiveData<Integer> getTotalFaltan();

    @Query("SELECT COUNT(*) FROM laminas WHERE estado = 'REPETIDA'")
    LiveData<Integer> getTotalRepetidas();

    @Query("SELECT COALESCE(SUM(cantidad) - COUNT(*), 0) FROM laminas WHERE estado = 'REPETIDA'")
    LiveData<Integer> getTotalSobrantes();

    // ── Operaciones ──────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLamina(Lamina lamina);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllLaminas(List<Lamina> laminas);

    @Update
    void updateLamina(Lamina lamina);

    @Query("UPDATE laminas SET estado = :estado, cantidad = :cantidad WHERE numero = :numero")
    void updateEstado(int numero, EstadoLamina estado, int cantidad);

    @Delete
    void deleteLamina(Lamina lamina);
}
