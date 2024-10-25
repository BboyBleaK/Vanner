package com.example.vanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText edtCorreo, edtPassword;
    private Button btnLogin, btnRegistro;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Auth y Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencias a los campos de texto y botones
        edtCorreo = findViewById(R.id.ETUsuario);
        edtPassword = findViewById(R.id.ETPass);
        btnLogin = findViewById(R.id.buttonInicioSesion);
        btnRegistro = findViewById(R.id.buttonRegistro);

        // Botón de Iniciar Sesión
        btnLogin.setOnClickListener(v -> {
            String correo = edtCorreo.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Iniciar sesión con Firebase Authentication
            mAuth.signInWithEmailAndPassword(correo, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                verificarCargoYRedirigir(userId, correo);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error en el inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Botón de Registro
        btnRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    // Verificar el cargo del usuario y redirigir a la actividad correspondiente
    private void verificarCargoYRedirigir(String userId, String correo) {
        mDatabase.child("usuarios").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                String cargo = snapshot.child("cargo").getValue(String.class);

                if ("empresa".equalsIgnoreCase(cargo)) {
                    Intent intent = new Intent(MainActivity.this, EmpresaActivity.class);
                    intent.putExtra("user_email", correo);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("user_email", correo);
                    startActivity(intent);
                }
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Error al obtener el cargo del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
