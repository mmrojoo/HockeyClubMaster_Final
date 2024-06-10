package com.example.agenda_online.ActualizarNota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agenda_online.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Actualizar_Nota extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    TextView Id_nota_A, Uid_Usuario_A, Correo_usuario_A, Fecha_registro_A, Fecha_A, Estado_A, Estado_nuevo;
    EditText Titulo_A, Descripcion_A;
    Button Btn_Calendario_A;
    String id_nota_R, uid_usuario_R, correo_usuario_R, fecha_registro_R, titulo_R, descripcion_R, fecha_R, estado_R;
    ImageView Tarea_Finalizada, Tarea_No_Finalizada;
    Spinner Spinner_estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_nota);

        // Configura la Toolbar
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Si deseas mostrar el botón de "atrás"
        }

        InicializarVistas();
        RecuperarDatos();
        SetearDatos();
        ComprobarEstadoNota();
        Spinner_Estado();
    }

    private void InicializarVistas() {
        Id_nota_A = findViewById(R.id.Id_nota_A);
        Uid_Usuario_A = findViewById(R.id.Uid_Usuario_A);
        Correo_usuario_A = findViewById(R.id.Correo_usuario_A);
        Fecha_registro_A = findViewById(R.id.Fecha_registro_A);
        Fecha_A = findViewById(R.id.Fecha_A);
        Estado_A = findViewById(R.id.Estado_A);
        Titulo_A = findViewById(R.id.Titulo_A);
        Descripcion_A = findViewById(R.id.Descripcion_A);
        Btn_Calendario_A = findViewById(R.id.Btn_Calendario_A);

        Tarea_Finalizada = findViewById(R.id.Tarea_Finalizada);
        Tarea_No_Finalizada = findViewById(R.id.Tarea_No_Finalizada);

        Spinner_estado = findViewById(R.id.Spinner_Estado);
        Estado_nuevo = findViewById(R.id.Estado_nuevo);

        // Configura el botón del calendario
        Btn_Calendario_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialog();
            }
        });
    }

    private void mostrarDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
        Fecha_A.setText(fechaSeleccionada);
    }

    private void RecuperarDatos() {
        Bundle intent = getIntent().getExtras();

        id_nota_R = intent.getString("id_nota");
        uid_usuario_R = intent.getString("uid_usuario");
        correo_usuario_R = intent.getString("correo_usuario");
        fecha_registro_R = intent.getString("fecha_registro");
        titulo_R = intent.getString("titulo");
        descripcion_R = intent.getString("descripcion");
        fecha_R = intent.getString("fecha_nota");
        estado_R = intent.getString("estado");
    }

    private void SetearDatos() {
        Id_nota_A.setText(id_nota_R);
        Uid_Usuario_A.setText(uid_usuario_R);
        Correo_usuario_A.setText(correo_usuario_R);
        Fecha_registro_A.setText(fecha_registro_R);
        Titulo_A.setText(titulo_R);
        Descripcion_A.setText(descripcion_R);
        Fecha_A.setText(fecha_R);
        Estado_A.setText(estado_R);
    }

    private void ComprobarEstadoNota() {
        String estado_nota = Estado_A.getText().toString();

        if (estado_nota.equals("No finalizado")) {
            Tarea_No_Finalizada.setVisibility(View.VISIBLE);
        } else if (estado_nota.equals("Finalizado")) {
            Tarea_Finalizada.setVisibility(View.VISIBLE);
        }
    }

    private void Spinner_Estado() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Estados_nota, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_estado.setAdapter(adapter);
        Spinner_estado.setOnItemSelectedListener(this);
    }

    private void ActualizarNotaBD() {
        String tituloActualizar = Titulo_A.getText().toString();
        String descripcionActualizar = Descripcion_A.getText().toString();
        String fechaActualizar = Fecha_A.getText().toString();
        String estadoActualizar = Estado_nuevo.getText().toString();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("convocatorias");
        //Consulta
        Query query = databaseReference.orderByChild("id_nota").equalTo(id_nota_R);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ds.getRef().child("titulo").setValue(tituloActualizar);
                    ds.getRef().child("descripcion").setValue(descripcionActualizar);
                    ds.getRef().child("fecha_nota").setValue(fechaActualizar);
                    ds.getRef().child("estado").setValue(estadoActualizar);
                }
                Toast.makeText(Actualizar_Nota.this, "Nota actualizada con éxito", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String ESTADO_ACTUAL = Estado_A.getText().toString();

        String Posicion_1 = adapterView.getItemAtPosition(1).toString();

        String estado_seleccionado = adapterView.getItemAtPosition(i).toString();
        Estado_nuevo.setText(estado_seleccionado);

        if (ESTADO_ACTUAL.equals("Finalizado")){
            Estado_nuevo.setText(Posicion_1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualizar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Actualizar_Nota_BD) {
            ActualizarNotaBD();
            Toast.makeText(this, "Nota actualizada", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
