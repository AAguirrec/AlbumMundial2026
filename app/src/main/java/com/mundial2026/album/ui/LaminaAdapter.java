package com.mundial2026.album.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.mundial2026.album.R;
import com.mundial2026.album.databinding.ItemLaminaBinding;
import com.mundial2026.album.model.EstadoLamina;
import com.mundial2026.album.model.Lamina;

public class LaminaAdapter extends ListAdapter<Lamina, LaminaAdapter.LaminaViewHolder> {

    public interface OnLaminaClickListener {
        void onMasClick(Lamina lamina);
        void onMenosClick(Lamina lamina);
    }

    private final OnLaminaClickListener listener;

    public LaminaAdapter(OnLaminaClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public LaminaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLaminaBinding binding = ItemLaminaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new LaminaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LaminaViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    class LaminaViewHolder extends RecyclerView.ViewHolder {

        private final ItemLaminaBinding binding;

        LaminaViewHolder(ItemLaminaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Lamina lamina) {
            binding.tvNumero.setText("#" + lamina.getNumero());
            binding.tvDescripcion.setText(lamina.getDescripcion());
            binding.tvSeccion.setText(lamina.getSeccion());
            binding.tvCantidad.setText("×" + lamina.getCantidad());

            // Color de fondo según estado
            int colorRes;
            int labelRes;
            if (lamina.getEstado() == EstadoLamina.FALTA) {
                colorRes = R.color.estado_falta;
                labelRes = R.string.estado_falta;
            } else if (lamina.getEstado() == EstadoLamina.TIENE) {
                colorRes = R.color.estado_tiene;
                labelRes = R.string.estado_tiene;
            } else {
                colorRes = R.color.estado_repetida;
                labelRes = R.string.estado_repetida;
            }

            binding.cardLamina.setCardBackgroundColor(
                    ContextCompat.getColor(binding.getRoot().getContext(), colorRes));
            binding.tvEstado.setText(labelRes);

            // Botones
            binding.btnMas.setOnClickListener(v -> listener.onMasClick(lamina));
            binding.btnMenos.setOnClickListener(v -> listener.onMenosClick(lamina));
        }
    }

    // ── DiffUtil ──────────────────────────────────────────────────────────────

    private static final DiffUtil.ItemCallback<Lamina> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Lamina>() {
                @Override
                public boolean areItemsTheSame(@NonNull Lamina a, @NonNull Lamina b) {
                    return a.getNumero() == b.getNumero();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Lamina a, @NonNull Lamina b) {
                    return a.getEstado() == b.getEstado() &&
                           a.getCantidad() == b.getCantidad() &&
                           a.getDescripcion().equals(b.getDescripcion());
                }
            };
}
