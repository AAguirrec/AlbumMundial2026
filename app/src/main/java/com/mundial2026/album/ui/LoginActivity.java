package com.mundial2026.album.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mundial2026.album.data.UsuarioRepository;
import com.mundial2026.album.databinding.ActivityLoginBinding;
import com.mundial2026.album.model.Usuario;
import com.mundial2026.album.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UsuarioRepository    repository;
    private SessionManager       session;

    // Controla si estamos en modo LOGIN o REGISTRO
    private boolean modoRegistro = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding    = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new UsuarioRepository(getApplication());
        session    = new SessionManager(this);

        // Si ya hay sesión activa, ir directo al álbum
        if (session.estaLogueado()) {
            irAlbum();
            return;
        }

        configurarBotones();
    }

    // ── Configuración de botones ──────────────────────────────────────────────

    private void configurarBotones() {
        // Botón principal: Ingresar o Registrarse
        binding.btnAccion.setOnClickListener(v -> {
            if (modoRegistro) ejecutarRegistro();
            else              ejecutarLogin();
        });

        // Enlace para cambiar entre Login y Registro
        binding.tvCambiarModo.setOnClickListener(v -> cambiarModo());
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    private void ejecutarLogin() {
        String correo = binding.etCorreo.getText().toString().trim();
        String clave  = binding.etClave.getText().toString();

        if (correo.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarCargando(true);

        repository.login(correo, clave, new UsuarioRepository.LoginCallback() {
            @Override
            public void onExito(Usuario usuario) {
                runOnUiThread(() -> {
                    mostrarCargando(false);
                    session.guardarSesion(usuario.getId(),
                                          usuario.getNombre(),
                                          usuario.getCorreo());
                    Toast.makeText(LoginActivity.this,
                            "¡Bienvenido, " + usuario.getNombre() + "!",
                            Toast.LENGTH_SHORT).show();
                    irAlbum();
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    mostrarCargando(false);
                    Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // ── Registro ──────────────────────────────────────────────────────────────

    private void ejecutarRegistro() {
        String nombre        = binding.etNombre.getText().toString().trim();
        String correo        = binding.etCorreo.getText().toString().trim();
        String clave         = binding.etClave.getText().toString();
        String claveConfirm  = binding.etConfirmClave.getText().toString();

        if (nombre.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingresa un correo válido.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (clave.length() < 6) {
            Toast.makeText(this, "La clave debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!clave.equals(claveConfirm)) {
            Toast.makeText(this, "Las claves no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarCargando(true);

        repository.registrar(nombre, correo, clave, new UsuarioRepository.RegistroCallback() {
            @Override
            public void onExito(Usuario usuario) {
                runOnUiThread(() -> {
                    mostrarCargando(false);
                    session.guardarSesion(usuario.getId(),
                                          usuario.getNombre(),
                                          usuario.getCorreo());
                    Toast.makeText(LoginActivity.this,
                            "¡Cuenta creada! Bienvenido, " + usuario.getNombre() + "!",
                            Toast.LENGTH_SHORT).show();
                    irAlbum();
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    mostrarCargando(false);
                    Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void cambiarModo() {
        modoRegistro = !modoRegistro;
        if (modoRegistro) {
            binding.tvTitulo.setText("Crear cuenta");
            binding.tilNombre.setVisibility(View.VISIBLE);
            binding.tilConfirmClave.setVisibility(View.VISIBLE);
            binding.btnAccion.setText("Registrarse");
            binding.tvCambiarModo.setText("¿Ya tienes cuenta? Inicia sesión");
        } else {
            binding.tvTitulo.setText("Iniciar sesión");
            binding.tilNombre.setVisibility(View.GONE);
            binding.tilConfirmClave.setVisibility(View.GONE);
            binding.btnAccion.setText("Ingresar");
            binding.tvCambiarModo.setText("¿No tienes cuenta? Regístrate");
        }
        // Limpiar campos al cambiar de modo
        binding.etNombre.setText("");
        binding.etCorreo.setText("");
        binding.etClave.setText("");
        binding.etConfirmClave.setText("");
    }

    private void mostrarCargando(boolean cargando) {
        binding.progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
        binding.btnAccion.setEnabled(!cargando);
        binding.tvCambiarModo.setEnabled(!cargando);
    }

    private void irAlbum() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
