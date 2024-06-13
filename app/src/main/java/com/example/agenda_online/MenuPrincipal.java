package com.example.agenda_online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agenda_online.Configuracion.Configuracion;
import com.example.agenda_online.Notas.Agregar_Nota;
import com.example.agenda_online.Contactos.Listar_Contactos;
import com.example.agenda_online.Notas.Listar_Notas;
import com.example.agenda_online.Notas.Notas_Importantes;
import com.example.agenda_online.Perfil.Perfil_Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {

    private Button CerrarSesion, ListarNotas, AgregarNotas, Importantes, Contactos, AcercaDe;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ImageView imagen_perfil;
    private TextView NombresPrincipal, CorreoPrincipal, UidPrincipal;
    private Button EstadoCuentaPrincipal;
    private ProgressBar progressBarDatos;
    private ProgressDialog progressDialog;
    private LinearLayoutCompat Linear_Nombres, Linear_Correo, Linear_Verificacion;
    private DatabaseReference Usuarios;
    private Dialog dialog_cuenta_verificada, dialog_informacion;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("HockeyClubMaster");
        }

        initializeUI();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        EstadoCuentaPrincipal.setOnClickListener(v -> handleAccountVerification());

        AgregarNotas.setOnClickListener(v -> navigateToAddNoteActivity());
        ListarNotas.setOnClickListener(v -> navigateToListNotesActivity());
        Importantes.setOnClickListener(v -> navigateToImportantNotesActivity());
        Contactos.setOnClickListener(v -> navigateToListContactsActivity());
        AcercaDe.setOnClickListener(v -> showInformationDialog());

        CerrarSesion.setOnClickListener(v -> logout());
    }

    private void initializeUI() {
        UidPrincipal = findViewById(R.id.UidPrincipal);
        NombresPrincipal = findViewById(R.id.NombrePrincipal);
        CorreoPrincipal = findViewById(R.id.CorreoPrincipal);
        EstadoCuentaPrincipal = findViewById(R.id.EstadoCuentaPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);
        imagen_perfil = findViewById(R.id.imagen_perfil);

        dialog_cuenta_verificada = new Dialog(this);
        dialog_informacion = new Dialog(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor...");
        progressDialog.setCanceledOnTouchOutside(false);

        Linear_Nombres = findViewById(R.id.Linear_Nombres);
        Linear_Correo = findViewById(R.id.Linear_Correo);
        Linear_Verificacion = findViewById(R.id.Linear_Verificacion);

        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        CerrarSesion = findViewById(R.id.CerrarSesion);
        AgregarNotas = findViewById(R.id.AgregarNotas);
        ListarNotas = findViewById(R.id.ListarNotas);
        Importantes = findViewById(R.id.Importantes);
        Contactos = findViewById(R.id.Contactos);
        AcercaDe = findViewById(R.id.AcercaDe);
    }

    private void handleAccountVerification() {
        if (user.isEmailVerified()) {
            showAccountVerifiedAnimation();
        } else {
            promptEmailVerification();
        }
    }

    private void promptEmailVerification() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verificar cuenta")
                .setMessage("¿Estas seguro(a) que quieres verificar su cuenta a través de su correo " + user.getEmail() + "?")
                .setPositiveButton("Enviar", (dialog, which) -> sendVerificationEmail())
                .setNegativeButton("Cancelar", (dialog, which) -> Toast.makeText(MenuPrincipal.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show())
                .show();
    }

    private void sendVerificationEmail() {
        progressDialog.setMessage("Enviando instrucciones de verificación a su correo electrónico " + user.getEmail());
        progressDialog.show();

        user.sendEmailVerification()
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(MenuPrincipal.this, "Revise su correo " + user.getEmail(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(MenuPrincipal.this, "Fallo debido a: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showAccountVerifiedAnimation() {
        dialog_cuenta_verificada.setContentView(R.layout.dialogo_cuenta_verificada);
        Button EntendidoVerificado = dialog_cuenta_verificada.findViewById(R.id.EntendidoVerificado);
        EntendidoVerificado.setOnClickListener(v -> dialog_cuenta_verificada.dismiss());
        dialog_cuenta_verificada.show();
        dialog_cuenta_verificada.setCanceledOnTouchOutside(false);
    }

    private void showInformationDialog() {
        dialog_informacion.setContentView(R.layout.cuadro_dialogo_informacion);
        Button EntendidoInfo = dialog_informacion.findViewById(R.id.EntendidoInfo);
        EntendidoInfo.setOnClickListener(v -> dialog_informacion.dismiss());
        dialog_informacion.show();
        dialog_informacion.setCanceledOnTouchOutside(false);
    }

    private void navigateToAddNoteActivity() {
        Intent intent = new Intent(MenuPrincipal.this, Agregar_Nota.class);
        intent.putExtra("Uid", UidPrincipal.getText().toString());
        intent.putExtra("Correo", CorreoPrincipal.getText().toString());
        startActivity(intent);
    }

    private void navigateToListNotesActivity() {
        startActivity(new Intent(MenuPrincipal.this, Listar_Notas.class));
        Toast.makeText(MenuPrincipal.this, "Listar Convocatorias", Toast.LENGTH_SHORT).show();
    }

    private void navigateToImportantNotesActivity() {
        startActivity(new Intent(MenuPrincipal.this, Notas_Importantes.class));
        Toast.makeText(MenuPrincipal.this, "Convocatorias Guardadas", Toast.LENGTH_SHORT).show();
    }

    private void navigateToListContactsActivity() {
        Intent intent = new Intent(MenuPrincipal.this, Listar_Contactos.class);
        intent.putExtra("Uid", UidPrincipal.getText().toString());
        startActivity(intent);
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void checkAccountStatus() {
        String Verificado = "Verificado";
        String No_Verificado = "No verificado";
        if (user.isEmailVerified()) {
            EstadoCuentaPrincipal.setText(Verificado);
            EstadoCuentaPrincipal.setBackgroundColor(Color.rgb(0, 0, 255));
        } else {
            EstadoCuentaPrincipal.setText(No_Verificado);
            EstadoCuentaPrincipal.setBackgroundColor(Color.rgb(231, 76, 60));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyUserLogin();
    }

    private void verifyUserLogin() {
        if (user != null) {
            loadUserData();
        } else {
            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
            finish();
        }
    }

    private void loadUserData() {
        checkAccountStatus();
        Usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !isFinishing() && !isDestroyed()) { // Check if Activity is not finishing or destroyed
                    progressBarDatos.setVisibility(View.GONE);

                    Linear_Nombres.setVisibility(View.VISIBLE);
                    Linear_Correo.setVisibility(View.VISIBLE);
                    Linear_Verificacion.setVisibility(View.VISIBLE);

                    String uid = snapshot.child("uid").getValue(String.class);
                    String nombres = snapshot.child("nombres").getValue(String.class);
                    String correo = snapshot.child("correo").getValue(String.class);
                    String imagenPerfilUrl = snapshot.child("imagen_perfil").getValue(String.class);

                    UidPrincipal.setText(uid);
                    NombresPrincipal.setText(nombres);
                    CorreoPrincipal.setText(correo);

                    if (!isFinishing() && !isDestroyed()) { // Ensure activity is still active
                        Glide.with(MenuPrincipal.this)
                                .load(imagenPerfilUrl)
                                .placeholder(R.drawable.imagen_perfil)
                                .into(imagen_perfil);
                    }

                    enableButtons();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuPrincipal.this, "Error al cargar datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableButtons() {
        AgregarNotas.setEnabled(true);
        ListarNotas.setEnabled(true);
        Importantes.setEnabled(true);
        Contactos.setEnabled(true);
        AcercaDe.setEnabled(true);
        CerrarSesion.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.Perfil_usuario) {
            startActivity(new Intent(MenuPrincipal.this, Perfil_Usuario.class));
        } else if (itemId == R.id.Configuracion) {
            Intent intent = new Intent(MenuPrincipal.this, Configuracion.class);
            intent.putExtra("Uid", UidPrincipal.getText().toString());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
