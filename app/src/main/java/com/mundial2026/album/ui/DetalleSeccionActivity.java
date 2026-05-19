package com.mundial2026.album.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mundial2026.album.databinding.ActivityDetalleSeccionBinding;
import com.mundial2026.album.model.EstadoLamina;
import com.mundial2026.album.model.Lamina;
import java.util.List;

public class DetalleSeccionActivity extends AppCompatActivity {

    public static final String EXTRA_SECCION = "extra_seccion";

    private ActivityDetalleSeccionBinding binding;
    private AlbumViewModel viewModel;
    private LaminaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalleSeccionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String seccion = getIntent().getStringExtra(EXTRA_SECCION);
        if (seccion == null) { finish(); return; }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(seccion);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(AlbumViewModel.class);

        adapter = new LaminaAdapter(new LaminaAdapter.OnLaminaClickListener() {
            @Override
            public void onMasClick(Lamina lamina) {
                viewModel.marcarTengo(lamina.getNumero());
            }
            @Override
            public void onMenosClick(Lamina lamina) {
                viewModel.marcarFalta(lamina.getNumero());
            }
        });

        binding.recyclerDetalle.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerDetalle.setAdapter(adapter);

        viewModel.setSeccion(seccion);
        viewModel.laminasFiltradas.observe(this, laminas -> {
            adapter.submitList(laminas);
            int tengo  = 0;
            int faltan = 0;
            for (Lamina l : laminas) {
                if (l.getEstado() == EstadoLamina.FALTA) faltan++;
                else                                      tengo++;
            }
            binding.tvResumenSeccion.setText(
                    "Total: " + laminas.size() +
                    "  |  Tengo: " + tengo +
                    "  |  Faltan: " + faltan);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
