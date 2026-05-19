package com.mundial2026.album.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.mundial2026.album.data.LaminaRepository;
import com.mundial2026.album.model.Lamina;
import java.util.List;

public class AlbumViewModel extends AndroidViewModel {

    private final LaminaRepository repository;

    public final LiveData<List<Lamina>> todasLasLaminas;
    public final LiveData<List<String>> secciones;
    public final LiveData<Integer>      totalLaminas;
    public final LiveData<Integer>      totalTengo;
    public final LiveData<Integer>      totalFaltan;
    public final LiveData<Integer>      totalRepetidas;
    public final LiveData<Integer>      sobrantes;

    // Se inicializan en el constructor, DESPUES del repositorio
    private final MutableLiveData<String> seccionActiva;
    public final LiveData<List<Lamina>>   laminasFiltradas;

    public AlbumViewModel(@NonNull Application application) {
        super(application);

        // 1. Repositorio primero
        repository      = new LaminaRepository(application);

        // 2. LiveData del repositorio
        todasLasLaminas = repository.todasLasLaminas;
        secciones       = repository.secciones;
        totalLaminas    = repository.totalLaminas;
        totalTengo      = repository.totalTengo;
        totalFaltan     = repository.totalFaltan;
        totalRepetidas  = repository.totalRepetidas;
        sobrantes       = repository.sobrantes;

        // 3. switchMap despues de que todasLasLaminas ya esta asignado
        seccionActiva    = new MutableLiveData<>(null);
        laminasFiltradas = Transformations.switchMap(seccionActiva, seccion -> {
            if (seccion == null) return todasLasLaminas;
            return repository.getLaminasPorSeccion(seccion);
        });
    }

    public void setSeccion(String seccion) {
        seccionActiva.setValue(seccion);
    }

    public void marcarTengo(int numero) {
        repository.marcarTengo(numero);
    }

    public void marcarFalta(int numero) {
        repository.marcarFalta(numero);
    }

    public void updateLamina(Lamina lamina) {
        repository.updateLamina(lamina);
    }
}
