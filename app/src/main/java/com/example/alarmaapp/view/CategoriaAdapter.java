package com.example.alarmaapp.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmaapp.R;
import com.example.alarmaapp.model.Categoria;

import java.util.ArrayList;
import java.util.List;

// Adapter para la lista de categorias
public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private List<Categoria> categorias = new ArrayList<>();
    private OnCategoriaClickListener onClickListener;
    private OnCategoriaClickListener onDeleteListener;

    public interface OnCategoriaClickListener {
        void onClick(Categoria categoria);
    }

    public CategoriaAdapter(OnCategoriaClickListener onClickListener,
                            OnCategoriaClickListener onDeleteListener) {
        this.onClickListener  = onClickListener;
        this.onDeleteListener = onDeleteListener;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        Categoria categoria = categorias.get(position);

        holder.tvNombre.setText(categoria.getNombre());
        // Pintamos el circulo de color con el color de la categoria
        holder.vColor.setBackgroundColor(Color.parseColor(categoria.getColor()));

        holder.itemView.setOnClickListener(v -> onClickListener.onClick(categoria));
        holder.btnEliminar.setOnClickListener(v -> onDeleteListener.onClick(categoria));
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        View vColor;
        ImageButton btnEliminar;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre    = itemView.findViewById(R.id.tvNombre);
            vColor      = itemView.findViewById(R.id.vColor);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}