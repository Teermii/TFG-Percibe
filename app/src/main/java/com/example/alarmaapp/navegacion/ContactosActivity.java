package com.example.alarmaapp.navegacion;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmaapp.R;
import com.example.alarmaapp.model.Contacto;
import com.example.alarmaapp.view.ContactoAdapter;
import com.example.alarmaapp.viewmodel.ContactoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactosActivity extends AppCompatActivity {

    private ContactoViewModel viewModel;
    private ContactoAdapter adapter;
    private long alarmaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        // Recibimos el ID de la alarma desde NuevaAlarma/EditarAlarma
        alarmaId = getIntent().getLongExtra("alarmaId", -1);

        TextView tvTitulo            = findViewById(R.id.tvTitulo);
        RecyclerView recyclerView    = findViewById(R.id.recyclerView);
        TextView tvVacia             = findViewById(R.id.tvVacia);
        FloatingActionButton fabAdd  = findViewById(R.id.fabAnadir);

        String nombreAlarma = getIntent().getStringExtra("nombreAlarma");
        if (nombreAlarma != null) {
            tvTitulo.setText("Contactos de \"" + nombreAlarma + "\"");
        }

        viewModel = new ViewModelProvider(this).get(ContactoViewModel.class);

        adapter = new ContactoAdapter(
                contacto -> mostrarDialogoEditar(contacto),  // Click en contacto → editar
                contacto -> viewModel.eliminar(contacto)     // Click en eliminar → borrar
        );

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getContactosPorAlarma(alarmaId).observe(this, contactos -> {
            adapter.setContactos(contactos);
            tvVacia.setVisibility(contactos.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(contactos.isEmpty() ? View.GONE : View.VISIBLE);
        });

        fabAdd.setOnClickListener(v -> mostrarDialogoCrear());
    }

    private void mostrarDialogoCrear() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_contacto, null);

        EditText etNombre   = dialogView.findViewById(R.id.etNombre);
        EditText etTelefono = dialogView.findViewById(R.id.etTelefono);
        EditText etMensaje  = dialogView.findViewById(R.id.etMensaje);

        new AlertDialog.Builder(this)
                .setTitle("Nuevo contacto")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre   = etNombre.getText().toString().trim();
                    String telefono = etTelefono.getText().toString().trim();
                    String mensaje  = etMensaje.getText().toString().trim();

                    if (!nombre.isEmpty() && !telefono.isEmpty()) {
                        if (mensaje.isEmpty()) {
                            mensaje = "Estoy en la zona de la alarma.";
                        }
                        viewModel.insertar(new Contacto(alarmaId, nombre, telefono, mensaje));
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditar(Contacto contacto) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_contacto, null);

        EditText etNombre   = dialogView.findViewById(R.id.etNombre);
        EditText etTelefono = dialogView.findViewById(R.id.etTelefono);
        EditText etMensaje  = dialogView.findViewById(R.id.etMensaje);

        // Precargamos los datos actuales
        etNombre.setText(contacto.getNombre());
        etTelefono.setText(contacto.getTelefono());
        etMensaje.setText(contacto.getMensaje());

        new AlertDialog.Builder(this)
                .setTitle("Editar contacto")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre   = etNombre.getText().toString().trim();
                    String telefono = etTelefono.getText().toString().trim();
                    String mensaje  = etMensaje.getText().toString().trim();

                    if (!nombre.isEmpty() && !telefono.isEmpty()) {
                        contacto.setNombre(nombre);
                        contacto.setTelefono(telefono);
                        contacto.setMensaje(mensaje.isEmpty()
                                ? "Estoy en la zona de la alarma." : mensaje);
                        viewModel.actualizar(contacto);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}