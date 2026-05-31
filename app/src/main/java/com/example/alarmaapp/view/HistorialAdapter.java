package com.example.alarmaapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmaapp.R;
import com.example.alarmaapp.model.HistorialActivacion;

import java.util.ArrayList;
import java.util.List;

// Adapter del historial de activacion, simplemente muestra fecha y hora
public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private List<HistorialActivacion> historial = new ArrayList<>();

    public void setHistorial(List<HistorialActivacion> historial) {
        this.historial = historial;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        HistorialActivacion activacion = historial.get(position);
        holder.tvFecha.setText(activacion.getFecha());
        holder.tvHora.setText(activacion.getHora());
    }

    @Override
    public int getItemCount() {
        return historial.size();
    }

    static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha;
        TextView tvHora;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora  = itemView.findViewById(R.id.tvHora);
        }
    }
}