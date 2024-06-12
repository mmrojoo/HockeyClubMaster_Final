package com.example.agenda_online.Contactos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agenda_online.Objetos.Contacto;
import com.example.agenda_online.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Agregar_Contacto extends AppCompatActivity {

    TextView Uid_Usuario_C;
    EditText Nombres_C, Apellidos_C, Correo_C, Edad_C, Direccion_C, Telefono_C;

    Button Btn_Guardar_Contacto;
    Dialog dialog_establecer_telefono;

    DatabaseReference BD_Usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        // Inicializar variables y obtener instancia de FirebaseAuth
        InicializarVariables();
        ObtenerUidUsuario();

        // Configurar ActionBar con botón de retroceso
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Mostrar el botón de retroceso
            actionBar.setTitle("Agregar Contacto"); // Título de la ActionBar
        }

        Btn_Guardar_Contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarContacto();
            }
        });
    }

    private void InicializarVariables() {
        Uid_Usuario_C = findViewById(R.id.Uid_Usuario_C);
        Nombres_C = findViewById(R.id.Nombres_C);
        Apellidos_C = findViewById(R.id.Apellidos_C);
        Correo_C = findViewById(R.id.Correo_C);
        Telefono_C = findViewById(R.id.Telefono_C);
        Edad_C = findViewById(R.id.Edad_C);
        Direccion_C = findViewById(R.id.Direccion_C);
        Btn_Guardar_Contacto = findViewById(R.id.Btn_Guardar_Contacto);

        dialog_establecer_telefono = new Dialog(Agregar_Contacto.this);
        BD_Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        dialog = new Dialog(Agregar_Contacto.this);
    }

    private void ObtenerUidUsuario() {
        String UidRecuperado = getIntent().getStringExtra("Uid");
        Uid_Usuario_C.setText(UidRecuperado);
    }

    private void AgregarContacto() {
        /* Obtener los datos */
        String uid = Uid_Usuario_C.getText().toString();
        String nombres = Nombres_C.getText().toString();
        String apellidos = Apellidos_C.getText().toString();
        String correo = Correo_C.getText().toString();
        String telefono = Telefono_C.getText().toString();
        String edad = Edad_C.getText().toString();
        String direccion = Direccion_C.getText().toString();

        /* Validar los datos */
        if (!uid.equals("") && !nombres.equals("")) {
            // Crear un ID único para el contacto
            String id_contacto = BD_Usuarios.child(user.getUid()).child("Contactos").push().getKey();

            // Crear objeto Contacto
            Contacto contacto = new Contacto(
                    id_contacto,
                    uid,
                    nombres,
                    apellidos,
                    correo,
                    telefono,
                    edad,
                    direccion,
                    "");

            // Guardar contacto en la base de datos
            BD_Usuarios.child(user.getUid()).child("Contactos").child(id_contacto).setValue(contacto);
            Toast.makeText(this, "Contacto agregado", Toast.LENGTH_SHORT).show();
            onBackPressed(); // Volver a la actividad anterior

        } else {
            ValidarRegistroContacto();
        }
    }

    private void ValidarRegistroContacto() {
        Button Btn_Validar_Registro_C;

        dialog.setContentView(R.layout.cuadro_dialogo_validar_registro_c);
        Btn_Validar_Registro_C = dialog.findViewById(R.id.Btn_Validar_Registro_C);

        Btn_Validar_Registro_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    // Manejar el evento de clic en el botón de retroceso de la ActionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Acción de retroceso predeterminada
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
