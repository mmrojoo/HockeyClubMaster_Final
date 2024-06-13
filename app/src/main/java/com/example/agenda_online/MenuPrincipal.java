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

    Button CerrarSesion,ListarNotas ,AgregarNotas, Importantes, Contactos, AcercaDe ;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ImageView imagen_perfil;

    TextView NombresPrincipal, CorreoPrincipal, UidPrincipal;
    Button EstadoCuentaPrincipal;
    ProgressBar progressBarDatos;
    ProgressDialog progressDialog;
    LinearLayoutCompat Linear_Nombres, Linear_Correo, Linear_Verificacion;

    DatabaseReference Usuarios;
    Dialog dialog_cuenta_verificada, dialog_informacion;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("HockeyClubMaster");

        UidPrincipal = findViewById(R.id.UidPrincipal);
        NombresPrincipal = findViewById(R.id.NombrePrincipal);
        CorreoPrincipal = findViewById(R.id.CorreoPrincipal);
        EstadoCuentaPrincipal = findViewById(R.id.EstadoCuentaPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);

        imagen_perfil = findViewById(R.id.imagen_perfil );


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

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        EstadoCuentaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.isEmailVerified()){
                    //Si la cuenta esta verificada
                    //Toast.makeText(MenuPrincipal.this, "Cuenta ya verificada", Toast.LENGTH_SHORT).show();
                    AnimacionCuentaVErificada();
                }else {
                    //Si la cuenta no esta verificada
                    VerificarCuentaCorreo();
                }
            }
        });

        AgregarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtenemos la informacion de los textview
                String uid_usuario = UidPrincipal.getText().toString();
                String correo_usuario = CorreoPrincipal.getText().toString();

                //Pasamos datos a la siguiente actividad
                Intent intent = new Intent(MenuPrincipal.this, Agregar_Nota.class);
                intent.putExtra("Uid", uid_usuario);
                intent.putExtra("Correo", correo_usuario);
                startActivity(intent);
            }
        });
        ListarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, Listar_Notas.class));
                Toast.makeText(MenuPrincipal.this, "Listar Convocatorias", Toast.LENGTH_SHORT).show();
            }
        });
        Importantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, Notas_Importantes.class));
                Toast.makeText(MenuPrincipal.this, "Convocatorias Guardadas", Toast.LENGTH_SHORT).show();
            }
        });
        Contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid_usuario = UidPrincipal.getText().toString();
                Intent intent =  new Intent(MenuPrincipal.this, Listar_Contactos.class);
                intent.putExtra("Uid", uid_usuario);
                startActivity(intent);
            }

        });
        AcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Informacion();
            }
        });



        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirAplicacion();
            }
        });


    }

    private void VerificarCuentaCorreo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("verificar cuenta")
                .setMessage("¿Estas seguro(a) que quieres verificar su cuenta a través de su correo?"
                + user.getEmail())
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EnviarCorreoVerificacion();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MenuPrincipal.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }
    private void AnimacionCuentaVErificada(){
        Button EntendidoVerificado;

        dialog_cuenta_verificada.setContentView(R.layout.dialogo_cuenta_verificada);

        EntendidoVerificado = dialog_cuenta_verificada.findViewById(R.id.EntendidoVerificado);

        EntendidoVerificado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_cuenta_verificada.dismiss();
            }
        });
        dialog_cuenta_verificada.show();
        dialog_cuenta_verificada.setCanceledOnTouchOutside(false);
    }
    private void Informacion(){
        Button EntendidoInfo;

        dialog_informacion.setContentView(R.layout.cuadro_dialogo_informacion);

        EntendidoInfo = dialog_informacion.findViewById(R.id.EntendidoInfo);

        EntendidoInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_informacion.dismiss();
            }
        });

        dialog_informacion.show();
        dialog_informacion.setCanceledOnTouchOutside(false);
    }

    private void EnviarCorreoVerificacion() {
        progressDialog.setMessage("Enviando instrucciones dde verificacion a su correo electronico" + user.getEmail());
        progressDialog.show();
        
        user.sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Envio correcto
                        progressDialog.dismiss();
                        Toast.makeText(MenuPrincipal.this, "Revise su correo " +user.getEmail(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Falla el envio
                        Toast.makeText(MenuPrincipal.this, "Fallo debido a: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void VerificarEstadoDeCuenta(){
        String Verificado = "Verificado";
        String No_Verificado = "No verificado";
        if (user.isEmailVerified()){
            EstadoCuentaPrincipal.setText(Verificado);
            EstadoCuentaPrincipal.setBackgroundColor(Color.rgb(0, 0, 255));
        }else {
            EstadoCuentaPrincipal.setText(No_Verificado);
            EstadoCuentaPrincipal.setBackgroundColor(Color.rgb(231, 76, 60));

        }
    }

    @Override
    protected void onStart() {
        ComprobarInicioSesion();
        super.onStart();
    }

    private void ComprobarInicioSesion(){
        if (user != null) {
            //el usuario ha iniciado sesion
            CargaDeDatos();
        } else {
            //Lo dirige al mainActivity
            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
            finish();
        }
    }
    private void CargaDeDatos() {
        VerificarEstadoDeCuenta();
        Usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Si el usuario existe
                if (snapshot.exists()) {
                    // Ocultar el progressbar
                    progressBarDatos.setVisibility(View.GONE);

                    // Mostrar los layouts
                    Linear_Nombres.setVisibility(View.VISIBLE);
                    Linear_Correo.setVisibility(View.VISIBLE);
                    Linear_Verificacion.setVisibility(View.VISIBLE);

                    // Obtener los datos
                    String uid = "" + snapshot.child("uid").getValue();
                    String nombres = "" + snapshot.child("nombres").getValue();
                    String correo = "" + snapshot.child("correo").getValue();
                    String imagenPerfilUrl = "" + snapshot.child("imagen_perfil").getValue(); // Obtener la URL de la imagen

                    // Setear los datos en los respectivos textview
                    UidPrincipal.setText(uid);
                    NombresPrincipal.setText(nombres);
                    CorreoPrincipal.setText(correo);

                    // Cargar la imagen de perfil usando Glide
                    Glide.with(MenuPrincipal.this)
                            .load(imagenPerfilUrl)
                            .placeholder(R.drawable.imagen_perfil) // Placeholder image
                            .into(imagen_perfil);

                    // Habilitar los botones del menú
                    AgregarNotas.setEnabled(true);
                    ListarNotas.setEnabled(true);
                    Importantes.setEnabled(true);
                    Contactos.setEnabled(true);
                    AcercaDe.setEnabled(true);
                    CerrarSesion.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Perfil_usuario){
            startActivity(new Intent(MenuPrincipal.this, Perfil_Usuario.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void SalirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Cerraste Sesión Correctamente", Toast.LENGTH_SHORT).show();
    }

}