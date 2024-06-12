package com.example.agenda_online.Perfil;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.agenda_online.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Editar_imagen_perfil extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_CAMERA = 113;

    ImageView ImagenPerfilActualizar;
    Button BtnElegirImagenDe, BtnActualizarImagen;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Dialog dialog_elegir_imagen;
    Uri imagenUri = null;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_imagen_perfil);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ImagenPerfilActualizar = findViewById(R.id.ImagenPerfilActualizar);
        BtnElegirImagenDe = findViewById(R.id.BtnElegirImagenDe);
        BtnActualizarImagen = findViewById(R.id.BtnActualizarImagen);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        dialog_elegir_imagen = new Dialog(Editar_imagen_perfil.this);

        BtnElegirImagenDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ElegirImagenDe();
            }
        });

        BtnActualizarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagenUri == null){
                    Toast.makeText(Editar_imagen_perfil.this, "Inserte una nueva imagen", Toast.LENGTH_SHORT).show();
                }else {
                    subirImagenStorage();
                }
            }
        });
        progressDialog = new ProgressDialog(Editar_imagen_perfil.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        LecturaDeImagen();
    }

    private void subirImagenStorage(){
        progressDialog.setMessage("Subiendo imagen");
        progressDialog.show();
        String carpetaImagenes = "ImagenesPerfil/";
        String NombreImagen = carpetaImagenes+firebaseAuth.getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference(NombreImagen);
        reference.putFile(imagenUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uriImagen = ""+uriTask.getResult();
                        ActualizarImagenBD(uriImagen);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Editar_imagen_perfil.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void ActualizarImagenBD(String uriImagen) {
        progressDialog.setMessage("Actualizando la imagen");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        if (imagenUri != null){
            hashMap.put("imagen_perfil", ""+uriImagen);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(user.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(Editar_imagen_perfil.this, "Imagen se ha actualizado con éxito", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Editar_imagen_perfil.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void LecturaDeImagen(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imagen_perfil = ""+snapshot.child("imagen_perfil").getValue();
                Glide.with(getApplicationContext())
                        .load(imagen_perfil)
                        .placeholder(R.drawable.imagen_perfil)
                        .into(ImagenPerfilActualizar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void ElegirImagenDe() {
        Button Btn_Elegir_Galeria, Btn_Elegir_Camara;

        dialog_elegir_imagen.setContentView(R.layout.cuadro_dialogo_elegir_imagen);

        Btn_Elegir_Galeria = dialog_elegir_imagen.findViewById(R.id.Btn_Elegir_Galeria);
        Btn_Elegir_Camara = dialog_elegir_imagen.findViewById(R.id.Btn_Elegir_Camara);

        Btn_Elegir_Galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeleccionarImagenGaleria();
                dialog_elegir_imagen.dismiss();
            }
        });

        Btn_Elegir_Camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Editar_imagen_perfil.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(Editar_imagen_perfil.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    SeleccionarImagenCamara();
                } else {
                    ActivityCompat.requestPermissions(Editar_imagen_perfil.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
                dialog_elegir_imagen.dismiss();
            }
        });

        dialog_elegir_imagen.show();
        dialog_elegir_imagen.setCanceledOnTouchOutside(true);
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
                        ajustarImagen(ImagenPerfilActualizar, imagenUri);
                    } else {
                        Toast.makeText(Editar_imagen_perfil.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void SeleccionarImagenCamara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva imagen");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripción de imagen");
        imagenUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri);
        camaraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> camaraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        ajustarImagen(ImagenPerfilActualizar, imagenUri);
                    } else {
                        Toast.makeText(Editar_imagen_perfil.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                SeleccionarImagenCamara();
            } else {
                Toast.makeText(this, "El permiso es necesario para acceder a la cámara y al almacenamiento externo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ajustarImagen(ImageView imageView, Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            int orientation = getImageOrientation(this, imageUri);
            if (orientation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getImageOrientation(Context context, Uri imageUri) {
        int orientation = 0;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                android.media.ExifInterface exif = new android.media.ExifInterface(inputStream);
                int exifOrientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_NORMAL);

                switch (exifOrientation) {
                    case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                        orientation = 90;
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                        orientation = 180;
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                        orientation = 270;
                        break;
                    default:
                        orientation = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orientation;
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
