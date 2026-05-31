package com.example.alarmaapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmaapp.R;
import com.example.alarmaapp.model.Contacto;

import java.util.ArrayList;
import java.util.List;

// Adapter para la lista de contactos, igual al de categorias
public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {

    private List<Contacto> contactos = new ArrayList<>();
    private OnContactoClickListener onClickListener;
    private OnContactoClickListener onDeleteListener;

    public interface OnContactoClickListener {
        void onClick(Contacto contacto);
    }

    public ContactoAdapter(OnContactoClickListener onClickListener,
                           OnContactoClickListener onDeleteListener) {
        this.onClickListener  = onClickListener;
        this.onDeleteListener = onDeleteListener;
    }

    public void setContactos(List<Contacto> contactos) {
        this.contactos = contactos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Contacto contacto = contactos.get(position);

        holder.tvNombre.setText(contacto.getNombre());
        holder.tvTelefono.setText(contacto.getTelefono());

        holder.itemView.setOnClickListener(v -> onClickListener.onClick(contacto));
        holder.btnEliminar.setOnClickListener(v -> onDeleteListener.onClick(contacto));
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    static class ContactoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvTelefono;
        ImageButton btnEliminar;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre    = itemView.findViewById(R.id.tvNombre);
            tvTelefono  = itemView.findViewById(R.id.tvTelefono);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}