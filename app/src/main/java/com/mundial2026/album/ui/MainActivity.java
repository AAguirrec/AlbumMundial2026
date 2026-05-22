package com.mundial2026.album.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

        // Nombre del usuario en el subtítulo
        String nombre = session.getNombre();
        if (!nombre.isEmpty() && getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle("Hola, " + nombre);
        }

        setupRecyclerView();
        setupSpinnerSecciones();
        setupBotonesEstado();
        observarViewModel();
        verificarYCargarLaminas();
    }

    // ── Verificar si hay láminas, si no cargarlas ─────────────────────────────

    private void verificarYCargarLaminas() {
        // Observa el total: si llega 0 después de un momento, muestra botón de carga
        viewModel.totalLaminas.observe(this, total -> {
            if (total != null && total == 0) {
                binding.btnCargarLaminas.setVisibility(View.VISIBLE);
            } else {
                binding.btnCargarLaminas.setVisibility(View.GONE);
            }
        });

        binding.btnCargarLaminas.setOnClickListener(v -> {
            binding.btnCargarLaminas.setEnabled(false);
            binding.btnCargarLaminas.setText("Cargando láminas…");
            viewModel.recargarSiVacia(() ->
                runOnUiThread(() -> {
                    binding.btnCargarLaminas.setVisibility(View.GONE);
                    Toast.makeText(this, "✅ Láminas cargadas correctamente",
                            Toast.LENGTH_SHORT).show();
                })
            );
        });
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        adapter = new LaminaAdapter(new LaminaAdapter.OnLaminaClickListener() {
            @Override public void onMasClick(Lamina l)   { viewModel.marcarTengo(l.getNumero()); }
            @Override public void onMenosClick(Lamina l) { viewModel.marcarFalta(l.getNumero()); }
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
            spinnerAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerSecciones.setAdapter(spinnerAdapter);
            binding.spinnerSecciones.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                    viewModel.setSeccion(pos == 0 ? null : secciones.get(pos - 1));
                }
                @Override public void onNothingSelected(AdapterView<?> p) {}
            });
        });
    }

    // ── Botones filtro ────────────────────────────────────────────────────────

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
        viewModel.totalLaminas.observe(this, n -> binding.tvTotal.setText("Total: " + n));
        viewModel.totalTengo.observe(this,   n -> binding.tvTengo.setText("Tengo: " + n));
        viewModel.totalFaltan.observe(this,  n -> binding.tvFaltan.setText("Faltan: " + n));
        viewModel.sobrantes.observe(this,    n -> binding.tvSobrantes.setText("Para cambio: " + (n != null ? n : 0)));
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
            confirmarCerrarSesion();
            return true;
        } else if (id == R.id.action_usuario) {
            Toast.makeText(this,
                    "👤 " + session.getNombre() + "\n📧 " + session.getCorreo(),
                    Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_info) {
            Toast.makeText(this, "Álbum Mundial 2026 – Panini\n© Coca-Cola / FIFA",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ── Cerrar sesión con confirmación ────────────────────────────────────────

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Deseas cerrar la sesión de " + session.getNombre() + "?")
            .setPositiveButton("Sí, salir", (d, w) -> cerrarSesionEIrLogin())
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void cerrarSesionEIrLogin() {
        session.cerrarSesion();
        Intent intent = new Intent(this, LoginActivity.class);
        // Limpia el back stack: no se puede volver con el botón atrás del teléfono
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // ── Botón atrás del sistema ───────────────────────────────────────────────

    @Override
    public void onBackPressed() {
        // En lugar de salir, pregunta si quiere cambiar de usuario
        new AlertDialog.Builder(this)
            .setTitle("¿Cambiar de usuario?")
            .setMessage("¿Deseas cerrar sesión e ir al login?")
            .setPositiveButton("Sí, cambiar", (d, w) -> cerrarSesionEIrLogin())
            .setNegativeButton("No, quedarme", null)
            .show();
    }
}
