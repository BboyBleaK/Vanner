package com.example.vanner.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    // Campos de texto
    private EditText edtCorreo, edtPass, edtRePass, edtNombre, edtPaterno, edtMaterno, edtRut, edtDireccion, edtFono, dtpNacimiento;
    private Spinner spnCargo, spnGenero; // Spinners para cargo y género

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializa Firebase Auth y Firebase Realtime Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencias a los campos de texto
        edtCorreo = findViewById(R.id.edtCorreo);
        edtPass = findViewById(R.id.edtUsuario);  // Contraseña
        edtRePass = findViewById(R.id.edtRePass); // Repetir contraseña
        edtNombre = findViewById(R.id.edtNombre);
        edtPaterno = findViewById(R.id.edtPaterno);
        edtMaterno = findViewById(R.id.edtMaterno);
        edtRut = findViewById(R.id.edtRut);
        edtDireccion = findViewById(R.id.edtDireccion);
        edtFono = findViewById(R.id.edtFono);
        dtpNacimiento = findViewById(R.id.dtpNacimiento);
        spnCargo = findViewById(R.id.SpinnerCargo);
        spnGenero = findViewById(R.id.SpinnerGenero);

        // Configurar el adaptador para el Spinner de cargo
        ArrayAdapter<CharSequence> adapterCargo = ArrayAdapter.createFromResource(this,
                R.array.cargos_array, android.R.layout.simple_spinner_item);
        adapterCargo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCargo.setAdapter(adapterCargo);

        // Configurar el adaptador para el Spinner de género
        ArrayAdapter<CharSequence> adapterGenero = ArrayAdapter.createFromResource(this,
                R.array.generos_array, android.R.layout.simple_spinner_item);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGenero.setAdapter(adapterGenero);

        // Configurar el selector de fecha para el campo de fecha de nacimiento
        dtpNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });

        // Botón de Registro
        ImageButton btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        // Botón para volver a MainActivity
        ImageButton btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Método para mostrar el DatePickerDialog
    private void mostrarDatePicker() {
        // Obtener la fecha actual
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        // Crear el DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegistroActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int anio, int mes, int dia) {
                // Formatear la fecha seleccionada y mostrarla en el campo de texto
                String fechaSeleccionada = dia + "/" + (mes + 1) + "/" + anio;
                dtpNacimiento.setText(fechaSeleccionada);
            }
        }, anio, mes, dia);

        // Mostrar el diálogo
        datePickerDialog.show();
    }

    // Método para registrar usuario en Firebase Authentication y guardar datos en Firebase Realtime Database
    private void registrarUsuario() {
        String email = edtCorreo.getText().toString().trim();
        String password = edtPass.getText().toString().trim();
        String rePassword = edtRePass.getText().toString().trim();

        String nombre = edtNombre.getText().toString().trim();
        String paterno = edtPaterno.getText().toString().trim();
        String materno = edtMaterno.getText().toString().trim();
        String rut = edtRut.getText().toString().trim();
        String direccion = edtDireccion.getText().toString().trim();
        String fono = edtFono.getText().toString().trim();
        String fechaNacimiento = dtpNacimiento.getText().toString().trim();
        String cargo = spnCargo.getSelectedItem().toString();   // Obtener el valor seleccionado del spinner cargo
        String genero = spnGenero.getSelectedItem().toString(); // Obtener el valor seleccionado del spinner género

        // Verificación de que los campos no estén vacíos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, completa todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificación de longitud mínima de la contraseña
        if (password.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(rePassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Registro de usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                guardarDatosEnRealtimeDatabase(user.getUid(), nombre, paterno, materno, rut, direccion, fono, fechaNacimiento, email, cargo, genero);
                            }

                            // Enviar a MainActivity en vez de HomeActivity
                            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                            intent.putExtra("user_email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegistroActivity.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Método para guardar los datos en Firebase Realtime Database
    private void guardarDatosEnRealtimeDatabase(String userId, String nombre, String paterno, String materno, String rut, String direccion, String telefono, String fechaNacimiento, String email, String cargo, String genero) {
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("nombre", nombre);
        datosUsuario.put("paterno", paterno);
        datosUsuario.put("materno", materno);
        datosUsuario.put("rut", rut);
        datosUsuario.put("direccion", direccion);
        datosUsuario.put("telefono", telefono);
        datosUsuario.put("fechaNacimiento", fechaNacimiento);
        datosUsuario.put("correo", email);
        datosUsuario.put("cargo", cargo);   // Guardar el cargo en la base de datos
        datosUsuario.put("genero", genero); // Guardar el género en la base de datos

        mDatabase.child("usuarios").child(userId).setValue(datosUsuario)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistroActivity.this, "Datos guardados correctamente en Realtime Database", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistroActivity.this, "Error al guardar los datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
