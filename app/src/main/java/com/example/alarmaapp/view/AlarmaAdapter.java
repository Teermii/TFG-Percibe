package com.example.alarmaapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmaapp.R;
import com.example.alarmaapp.model.Alarma;
import com.example.alarmaapp.model.Categoria;
import com.example.alarmaapp.viewmodel.AlarmaViewModel;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;


public class AlarmaAdapter extends RecyclerView.Adapter<AlarmaAdapter.AlarmaViewHolder> {

    /* Adapter para conectar la lista de alarmas con el RecyclerView (solo carga las alarmas
    * que se vean en pantalla)
     */
    private List<Alarma> alarmas = new ArrayList<>();
    private AlarmaViewModel viewModel;
    // Listener para cuando clickan en una alarma
    private OnAlarmaClickListener listener;
    private List<Categoria> categorias = new ArrayList<>();

    public interface OnAlarmaClickListener {
        void onAlarmaClick(Alarma alarma);
    }

    public AlarmaAdapter(AlarmaViewModel viewModel, OnAlarmaClickListener listener) {
        this.viewModel = viewModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlarmaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // convertimos el XML del item en una vista
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarma, parent, false);
        return new AlarmaViewHolder(vista);
    }

    // Rellenamos la vista con los datos de cada alarma, se llamara cada vez que un item tenga que mostrarse en pantalla
    @Override
    public void onBindViewHolder(@NonNull AlarmaViewHolder holder, int position) {
        Alarma alarma = alarmas.get(position);

        holder.tvNombre.setText(alarma.getNombre());
        holder.tvRadio.setText("Radio: " + alarma.getRadio() + "m");

        // Buscar la categoría de esta alarma
        if (alarma.getCategoriaId() != null && !categorias.isEmpty()) {
            Categoria categoria = null;
            for (Categoria c : categorias) {
                if (c.getId() == alarma.getCategoriaId()) {
                    categoria = c;
                    break;
                }
            }
            if (categoria != null) {
                holder.tvCategoria.setVisibility(View.VISIBLE);
                holder.tvCategoria.setText(categoria.getNombre());
                // Aplicamos el color de la categoría al fondo del chip
                GradientDrawable chip = new GradientDrawable();
                chip.setShape(GradientDrawable.RECTANGLE);
                chip.setCornerRadius(40f);
                chip.setColor(Color.parseColor(categoria.getColor()));
                holder.tvCategoria.setBackground(chip);
            } else {
                holder.tvCategoria.setVisibility(View.GONE);
            }
        } else {
            holder.tvCategoria.setVisibility(View.GONE);
        }

        /* Antes de llamar a setChecked(), quitamos el listener del switch.
        * Si no lo hiciéramos, setChecked() dispararía el listener, que llamaría a
        * actualizar(), que actualizaría Room, que avisaría a LiveData, que llamaría
        * a setAlarmas(), que llamaría a notifyDataSetChanged(), que llamaría a
        * onBindViewHolder() de nuevo → bucle infinito.
        */
        holder.switchActiva.setOnCheckedChangeListener(null);
        holder.switchActiva.setChecked(alarma.isActiva());

        // Ahora sí ponemos el listener
        holder.switchActiva.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarma.setActiva(isChecked);
            viewModel.actualizar(alarma);
        });
        // Al clickar en una alarma iremos al intent EditarAlarma.class
        holder.itemView.setOnClickListener(v -> listener.onAlarmaClick(alarma));
    }

    @Override
    public int getItemCount() {
        return alarmas.size();
    }

    // Actualiza la lista cuando Room avisa de cambios
    public void setAlarmas(List<Alarma> alarmas) {
        this.alarmas = alarmas;
        notifyDataSetChanged(); // avisa de que han habido cambios
    }

    // ViewHolder — representa cada item de la lista
    static class AlarmaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvRadio;
        TextView tvCategoria; // Añadir esto
        com.google.android.material.switchmaterial.SwitchMaterial switchActiva;

        public AlarmaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre     = itemView.findViewById(R.id.tvNombre);
            tvRadio      = itemView.findViewById(R.id.tvRadio);
            tvCategoria  = itemView.findViewById(R.id.tvCategoria); // Añadir esto
            switchActiva = itemView.findViewById(R.id.switchActiva);
        }
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
        notifyDataSetChanged();
    }
}
