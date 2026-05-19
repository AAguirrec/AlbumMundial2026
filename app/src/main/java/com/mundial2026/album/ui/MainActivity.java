package com.mundial2026.album.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mundial2026.album.R;
import com.mundial2026.album.databinding.ActivityMainBinding;
import com.mundial2026.album.model.Lamina;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AlbumViewModel viewModel;
    private LaminaAdapter adapter;

    private String filtroEstado = "TODOS";   // TODOS | FALTA | TIENE | REPETIDA
    private String busquedaTexto = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(AlbumViewModel.class);

        setupRecyclerView();
        setupSpinnerSecciones();
        setupBotonesEstado();
        observarViewModel();
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────

    private void setupRecyclerView() {
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

        binding.recyclerLaminas.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerLaminas.setAdapter(adapter);
        binding.recyclerLaminas.setHasFixedSize(true);
    }

    // ── Spinner secciones ─────────────────────────────────────────────────────

    private void setupSpinnerSecciones() {
        viewModel.secciones.observe(this, secciones -> {
            List<String> opciones = new ArrayList<>();
            opciones.add("Todas las secciones");
            opciones.addAll(secciones);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, opciones);
            spinnerAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerSecciones.setAdapter(spinnerAdapter);

            binding.spinnerSecciones.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, android.view.View view,
                                           int position, long id) {
                    String seccion = position == 0 ? null : secciones.get(position - 1);
                    viewModel.setSeccion(seccion);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        });
    }

    // ── Botones de filtro por estado ──────────────────────────────────────────

    private void setupBotonesEstado() {
        binding.btnTodos.setOnClickListener(v    -> { filtroEstado = "TODOS";    aplicarFiltro(null); });
        binding.btnFaltan.setOnClickListener(v   -> { filtroEstado = "FALTA";    aplicarFiltro(null); });
        binding.btnTengo.setOnClickListener(v    -> { filtroEstado = "TIENE";    aplicarFiltro(null); });
        binding.btnRepetidas.setOnClickListener(v-> { filtroEstado = "REPETIDA"; aplicarFiltro(null); });
    }

    private void aplicarFiltro(List<Lamina> laminasIn) {
        List<Lamina> lista = laminasIn != null
                ? laminasIn
                : (viewModel.laminasFiltradas.getValue() != null
                   ? viewModel.laminasFiltradas.getValue()
                   : new ArrayList<>());

        // Filtro por estado
        List<Lamina> filtrada = new ArrayList<>();
        for (Lamina lam : lista) {
            if (filtroEstado.equals("TODOS") || lam.getEstado().name().equals(filtroEstado)) {
                filtrada.add(lam);
            }
        }

        // Filtro por búsqueda de texto
        if (!busquedaTexto.isEmpty()) {
            List<Lamina> buscada = new ArrayList<>();
            for (Lamina lam : filtrada) {
                boolean matchNumero = String.valueOf(lam.getNumero()).contains(busquedaTexto);
                boolean matchDesc   = lam.getDescripcion()
                        .toLowerCase().contains(busquedaTexto.toLowerCase());
                if (matchNumero || matchDesc) buscada.add(lam);
            }
            filtrada = buscada;
        }

        adapter.submitList(filtrada);
        binding.tvContadorFiltro.setText(filtrada.size() + " láminas");
    }

    // ── Observadores ──────────────────────────────────────────────────────────

    private void observarViewModel() {
        viewModel.laminasFiltradas.observe(this, laminas -> aplicarFiltro(laminas));

        viewModel.totalLaminas.observe(this,
                n -> binding.tvTotal.setText("Total: " + n));
        viewModel.totalTengo.observe(this,
                n -> binding.tvTengo.setText("Tengo: " + n));
        viewModel.totalFaltan.observe(this,
                n -> binding.tvFaltan.setText("Faltan: " + n));
        viewModel.sobrantes.observe(this,
                n -> binding.tvSobrantes.setText("Para cambio: " + (n != null ? n : 0)));
    }

    // ── Menú (SearchView) ──────────────────────────────────────────────────────

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Buscar por # o nombre…");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                busquedaTexto = newText != null ? newText : "";
                aplicarFiltro(null);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_info) {
            Toast.makeText(this,
                    "Álbum Mundial 2026 – Panini\n© Coca-Cola / FIFA",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
