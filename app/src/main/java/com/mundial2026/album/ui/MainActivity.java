package com.mundial2026.album.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mundial2026.album.R;
import com.mundial2026.album.databinding.ActivityMainBinding;
import com.mundial2026.album.model.Lamina;
import com.mundial2026.album.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AlbumViewModel      viewModel;
    private LaminaAdapter       adapter;
    private SessionManager      session;

    private String filtroEstado  = "TODOS";
    private String busquedaTexto = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        session   = new SessionManager(this);
        viewModel = new ViewModelProvider(this).get(AlbumViewModel.class);

        // Mostrar nombre del usuario en el título
        String nombre = session.getNombre();
        if (!nombre.isEmpty() && getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle("Hola, " + nombre);
        }

        setupRecyclerView();
        setupSpinnerSecciones();
        setupBotonesEstado();
        observarViewModel();
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        adapter = new LaminaAdapter(new LaminaAdapter.OnLaminaClickListener() {
            @Override public void onMasClick(Lamina lamina)   { viewModel.marcarTengo(lamina.getNumero()); }
            @Override public void onMenosClick(Lamina lamina) { viewModel.marcarFalta(lamina.getNumero()); }
        });
        binding.recyclerLaminas.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerLaminas.setAdapter(adapter);
        binding.recyclerLaminas.setHasFixedSize(true);
    }

    // ── Spinner ───────────────────────────────────────────────────────────────

    private void setupSpinnerSecciones() {
        viewModel.secciones.observe(this, secciones -> {
            List<String> opciones = new ArrayList<>();
            opciones.add("Todas las secciones");
            opciones.addAll(secciones);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, opciones);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerSecciones.setAdapter(spinnerAdapter);
            binding.spinnerSecciones.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(AdapterView<?> p, android.view.View v, int pos, long id) {
                    viewModel.setSeccion(pos == 0 ? null : secciones.get(pos - 1));
                }
                @Override public void onNothingSelected(AdapterView<?> p) {}
            });
        });
    }

    // ── Filtros ───────────────────────────────────────────────────────────────

    private void setupBotonesEstado() {
        binding.btnTodos.setOnClickListener(v     -> { filtroEstado = "TODOS";    aplicarFiltro(null); });
        binding.btnFaltan.setOnClickListener(v    -> { filtroEstado = "FALTA";    aplicarFiltro(null); });
        binding.btnTengo.setOnClickListener(v     -> { filtroEstado = "TIENE";    aplicarFiltro(null); });
        binding.btnRepetidas.setOnClickListener(v -> { filtroEstado = "REPETIDA"; aplicarFiltro(null); });
    }

    private void aplicarFiltro(List<Lamina> laminasIn) {
        List<Lamina> lista = laminasIn != null
                ? laminasIn
                : (viewModel.laminasFiltradas.getValue() != null
                   ? viewModel.laminasFiltradas.getValue() : new ArrayList<>());

        List<Lamina> filtrada = new ArrayList<>();
        for (Lamina lam : lista) {
            if (filtroEstado.equals("TODOS") || lam.getEstado().name().equals(filtroEstado))
                filtrada.add(lam);
        }
        if (!busquedaTexto.isEmpty()) {
            List<Lamina> buscada = new ArrayList<>();
            for (Lamina lam : filtrada) {
                if (String.valueOf(lam.getNumero()).contains(busquedaTexto) ||
                    lam.getDescripcion().toLowerCase().contains(busquedaTexto.toLowerCase()))
                    buscada.add(lam);
            }
            filtrada = buscada;
        }
        adapter.submitList(filtrada);
        binding.tvContadorFiltro.setText(filtrada.size() + " láminas");
    }

    // ── Observadores ──────────────────────────────────────────────────────────

    private void observarViewModel() {
        viewModel.laminasFiltradas.observe(this, laminas -> aplicarFiltro(laminas));
        viewModel.totalLaminas.observe(this,  n -> binding.tvTotal.setText("Total: " + n));
        viewModel.totalTengo.observe(this,    n -> binding.tvTengo.setText("Tengo: " + n));
        viewModel.totalFaltan.observe(this,   n -> binding.tvFaltan.setText("Faltan: " + n));
        viewModel.sobrantes.observe(this,     n -> binding.tvSobrantes.setText("Para cambio: " + (n != null ? n : 0)));
    }

    // ── Menú ──────────────────────────────────────────────────────────────────

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Buscar por # o nombre…");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { return false; }
            @Override public boolean onQueryTextChange(String t) {
                busquedaTexto = t != null ? t : "";
                aplicarFiltro(null);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas cerrar la sesión de " + session.getNombre() + "?")
                .setPositiveButton("Sí", (d, w) -> {
                    session.cerrarSesion();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
            return true;

        } else if (id == R.id.action_usuario) {
            Toast.makeText(this,
                    "Usuario: " + session.getNombre() + "\n" + session.getCorreo(),
                    Toast.LENGTH_LONG).show();
            return true;

        } else if (id == R.id.action_info) {
            Toast.makeText(this, "Álbum Mundial 2026 – Panini\n© Coca-Cola / FIFA",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
