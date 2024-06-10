package com.example.agenda_online.NotasImportantes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.agenda_online.Detalle.Detalle_Nota;
import com.example.agenda_online.Objetos.Nota;
import com.example.agenda_online.R;
import com.example.agenda_online.ViewHolder.ViewHolder_Nota_Importante;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Notas_Importantes extends AppCompatActivity {

    RecyclerView RecyclerViewNotasImportantes;
    FirebaseDatabase firebaseDatabase;

    DatabaseReference Mis_Usuarios;
    DatabaseReference Notas_Importantes;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Importante> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Nota> firebaseRecyclerOptions;

    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas_archivadas);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Convocatorias importantes");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerViewNotasImportantes = findViewById(R.id.RecyclerViewNotasImportantes);
        RecyclerViewNotasImportantes.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        Mis_Usuarios = firebaseDatabase.getReference("Usuarios");

        ComprobarUsuario();
    }

    private void ComprobarUsuario() {
        if (user == null) {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        } else {
            ListarNotasImportantes();
        }
    }

    private void ListarNotasImportantes() {
        // Asegúrate de que el path en Firebase es correcto
        DatabaseReference convocatoriasImportantesRef = Mis_Usuarios.child(user.getUid()).child("Mis convocatorias importantes");

        // Verifica que esta referencia contiene datos en el formato correcto
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(convocatoriasImportantesRef, Nota.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Importante>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota_Importante viewHolder_nota_importante, int position, @NonNull Nota nota) {
                viewHolder_nota_importante.SetearDatos(
                        getApplicationContext(),
                        nota.getId_nota(),
                        nota.getUid_usuario(),
                        nota.getCorreo_usuario(),
                        nota.getFecha_hora_actual(),
                        nota.getTitulo(),
                        nota.getDescripcion(),
                        nota.getFecha_nota(),
                        nota.getEstado()
                );
            }

            @NonNull
            @Override
            public ViewHolder_Nota_Importante onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota_importante, parent, false);
                ViewHolder_Nota_Importante viewHolder_nota_importante = new ViewHolder_Nota_Importante(view);
                viewHolder_nota_importante.setOnClickListener(new ViewHolder_Nota_Importante.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Nota nota = getItem(position);

                        // Enviamos los datos a la siguiente actividad
                        Intent intent = new Intent(Notas_Importantes.this, Detalle_Nota.class);
                        intent.putExtra("id_nota", nota.getId_nota());
                        intent.putExtra("uid_usuario", nota.getUid_usuario());
                        intent.putExtra("correo_usuario", nota.getCorreo_usuario());
                        intent.putExtra("fecha_registro", nota.getFecha_hora_actual());
                        intent.putExtra("titulo", nota.getTitulo());
                        intent.putExtra("descripcion", nota.getDescripcion());
                        intent.putExtra("fecha_nota", nota.getFecha_nota());
                        intent.putExtra("estado", nota.getEstado());

                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        // Acción cuando se mantiene presionado un elemento
                    }
                });
                return viewHolder_nota_importante;
            }
        };

        linearLayoutManager = new LinearLayoutManager(Notas_Importantes.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        RecyclerViewNotasImportantes.setLayoutManager(linearLayoutManager);
        RecyclerViewNotasImportantes.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Esto manejará la navegación hacia atrás
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Esto manejará la navegación hacia atrás
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
