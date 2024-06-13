package com.example.agenda_online.Contactos;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agenda_online.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Actualizar_Contacto extends AppCompatActivity {

    TextView Id_C_A, Uid_C_A;
    EditText Nombres_C_A, Apellidos_C_A, Correo_C_A;
    ImageView Imagen_C_A, Actualizar_imagen_C_A;
    Button Btn_Actualizar_C_A;

    String id_c, uid_usuario, nombres_c, apellidos_c, correo_c;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Uri imagenUri = null;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_contacto);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Mis contactos");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        InicializarVistas();
        RecuperarDatos();
        SetearDatosRecuperados();
        ObtenerImagen();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (user == null) {
            // Si el usuario no está autenticado, puedes redirigirlo a la pantalla de inicio de sesión
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            // Aquí podrías decidir cerrar esta actividad o realizar otra acción
            return;
        }

        Btn_Actualizar_C_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarInformacionContacto();
            }
        });
        Actualizar_imagen_C_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeleccionarImagenGaleria();
            }
        });
        progressDialog = new ProgressDialog(Actualizar_Contacto.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void InicializarVistas() {
        Id_C_A = findViewById(R.id.Id_C_A);
        Uid_C_A = findViewById(R.id.Uid_C_A);
        Nombres_C_A = findViewById(R.id.Nombres_C_A);
        Apellidos_C_A = findViewById(R.id.Apellidos_C_A);
        Correo_C_A = findViewById(R.id.Correo_C_A);
        Imagen_C_A = findViewById(R.id.Imagen_C_A);
        Btn_Actualizar_C_A = findViewById(R.id.Btn_Actualizar_C_A);
        Actualizar_imagen_C_A = findViewById(R.id.Actualizar_imagen_C_A);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void RecuperarDatos() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id_c = bundle.getString("id_c");
            uid_usuario = bundle.getString("uid_usuario");
            nombres_c = bundle.getString("nombres_c");
            apellidos_c = bundle.getString("apellidos_c");
            correo_c = bundle.getString("correo_c");
        }
    }

    private void SetearDatosRecuperados() {
        Id_C_A.setText(id_c);
        Uid_C_A.setText(uid_usuario);
        Nombres_C_A.setText(nombres_c);
        Apellidos_C_A.setText(apellidos_c);
        Correo_C_A.setText(correo_c);
    }

    private void ObtenerImagen() {
        String imagen_c = getIntent().getStringExtra("imagen_c");

        try {
            Glide.with(getApplicationContext()).load(imagen_c).placeholder(R.drawable.imagen_contacto).into(Imagen_C_A);
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void ActualizarInformacionContacto() {
        String NombresActualizar = Nombres_C_A.getText().toString().trim();
        String ApellidosActualizar = Apellidos_C_A.getText().toString().trim();
        String CorreoActualizar = Correo_C_A.getText().toString().trim();

        // Verificar que el usuario esté autenticado
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Usuarios");

        Query query = databaseReference.child(user.getUid()).child("Contactos").orderByChild("id_contacto").equalTo(id_c);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().child("nombres").setValue(NombresActualizar);
                    ds.getRef().child("apellidos").setValue(ApellidosActualizar);
                    ds.getRef().child("correo").setValue(CorreoActualizar);
                }

                Toast.makeText(Actualizar_Contacto.this, "Información actualizada", Toast.LENGTH_SHORT).show();
                // Finalizar la actividad actual y volver a la actividad anterior
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Actualizar_Contacto.this, "Error al actualizar la información: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void subirImagenStorage(){
        progressDialog.setMessage("Subiendo imagen");
        progressDialog.show();
        String id_c = getIntent().getStringExtra("id_c");

        String carpetaImagenesContactos = "ImagenesPerfilContactos/";
        String NombreImagen = carpetaImagenesContactos+id_c;
        StorageReference reference = FirebaseStorage.getInstance().getReference(NombreImagen);
        reference.putFile(imagenUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String UriIMAGEN = ""+uriTask.getResult();
                        ActualizarImagenBD(UriIMAGEN);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Actualizar_Contacto.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void ActualizarImagenBD(String uriIMAGEN) {
        progressDialog.setMessage("Actualizando la imagen");
        progressDialog.show();

        String id_c = getIntent().getStringExtra("id_c");

        HashMap<String, Object> hashMap = new HashMap<>();
        if (imagenUri != null){
            hashMap.put("imagen", ""+uriIMAGEN);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(user.getUid()).child("Contactos").child(id_c)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(Actualizar_Contacto.this, "Imagen actualizada con éxito", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Actualizar_Contacto.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
    private void SeleccionarImagenGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galeriaActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        imagenUri = data.getData();
                        cargarImagenDesdeUri(imagenUri);
                        subirImagenStorage();
                    } else {
                        Toast.makeText(Actualizar_Contacto.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void cargarImagenDesdeUri(Uri uri) {
        try {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.imagen_contacto) // Placeholder mientras se carga la imagen
                    .into(Imagen_C_A);
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
