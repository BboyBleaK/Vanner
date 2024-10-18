package com.example.vanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistroActivity extends AppCompatActivity {

    // Campos de texto
    private EditText edtCorreo, edtPass, edtRePass, edtNombre, edtPaterno, edtMaterno, edtRut, edtDireccion, edtFono, dtpNacimiento;
    private Spinner edtCargo, edtGenero;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializa Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
        edtCargo = findViewById(R.id.edtCargo);
        edtGenero = findViewById(R.id.edtGenero);

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

    // Método para registrar usuario en Firebase Authentication
    private void registrarUsuario() {
        String email = edtCorreo.getText().toString().trim();
        String password = edtPass.getText().toString().trim();
        String rePassword = edtRePass.getText().toString().trim();

        // Verificación de que los campos no estén vacíos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, completa todos los campos requeridos", Toast.LENGTH_SHORT).show();
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

                            // Redirigir a la actividad principal después de registrar
                            Intent intent = new Intent(RegistroActivity.this, HomeActivity.class);
                            intent.putExtra("user_email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegistroActivity.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
