package com.example.vanner.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vanner.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditarTrabajadorPer extends AppCompatActivity {

    private EditText edtNombreUsuario, edtRutUsuario, edtCorreoUsuario, edtEdadUsuario,edtForTelefonoUsuario;
    private TextInputEditText dtmNacimientoUsuario;
    private Button btnActualizarDatos;
    private ImageButton btnRegresar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_trabajador_per);


        edtNombreUsuario = findViewById(R.id.edtForNombreUsuario);
        edtRutUsuario = findViewById(R.id.edtForRutUsuario);
        edtCorreoUsuario = findViewById(R.id.edtForCorreoUsuario);
        edtForTelefonoUsuario = findViewById(R.id.edtForTelefonoUsuario);

        edtEdadUsuario = findViewById(R.id.edtForEdadUsuario);
        dtmNacimientoUsuario = findViewById(R.id.dtmForNacimientoUsuario);

        btnActualizarDatos = findViewById(R.id.btnActualizarDatos);
        btnRegresar = findViewById(R.id.btnRegresar);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();


        cargarDatosUsuario(userId);


        dtmNacimientoUsuario.setOnClickListener(v -> mostrarDatePicker());


        btnActualizarDatos.setOnClickListener(v -> actualizarDatosUsuario(userId));


        btnRegresar.setOnClickListener(v -> finish());
    }

    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
            dtmNacimientoUsuario.setText(fechaSeleccionada);
        }, anio, mes, dia);

        datePickerDialog.show();
    }

    private void cargarDatosUsuario(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    edtNombreUsuario.setText(snapshot.child("nombre").getValue(String.class));
                    edtRutUsuario.setText(snapshot.child("rut").getValue(String.class));
                    edtCorreoUsuario.setText(snapshot.child("correo").getValue(String.class));
                    edtEdadUsuario.setText(snapshot.child("edad").getValue(String.class));

                    DataSnapshot infoAdicionalSnapshot = snapshot.child("informacionAdicional");
                    edtForTelefonoUsuario.setText(infoAdicionalSnapshot.child("telefono").getValue(String.class));
                    dtmNacimientoUsuario.setText(infoAdicionalSnapshot.child("nacimiento").getValue(String.class));
                } else {
                    Toast.makeText(EditarTrabajadorPer.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditarTrabajadorPer.this, "Error al cargar datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarDatosUsuario(String userId) {
        String nombre = edtNombreUsuario.getText().toString().trim();
        String rut = edtRutUsuario.getText().toString().trim();
        String correo = edtCorreoUsuario.getText().toString().trim();
        String telefono = edtForTelefonoUsuario.getText().toString().trim(); // Cambiado a telefono
        String edad = edtEdadUsuario.getText().toString().trim();
        String nacimiento = dtmNacimientoUsuario.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(rut) || TextUtils.isEmpty(correo)) {
            Toast.makeText(this, "Por favor, completa todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios").child(userId);


        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("rut", rut);
        updates.put("correo", correo);
        updates.put("edad", edad);


        Map<String, Object> infoAdicionalUpdates = new HashMap<>();
        infoAdicionalUpdates.put("telefono", telefono);
        infoAdicionalUpdates.put("nacimiento", nacimiento);


        userRef.updateChildren(updates);
        userRef.child("informacionAdicional").updateChildren(infoAdicionalUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditarTrabajadorPer.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditarTrabajadorPer.this, "Error al actualizar datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
