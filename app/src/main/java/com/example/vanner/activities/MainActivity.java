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

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtCorreo = findViewById(R.id.ETUsuario);
        edtPassword = findViewById(R.id.ETPass);
        btnLogin = findViewById(R.id.buttonInicioSesion);
        btnRegistro = findViewById(R.id.buttonRegistro);

        btnLogin.setOnClickListener(v -> {
            String correo = edtCorreo.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

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

        btnRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private void verificarCargoYRedirigir(String userId, String correo) {
        mDatabase.child("usuarios").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                String cargo = snapshot.child("cargo").getValue(String.class);

                if ("empresa".equalsIgnoreCase(cargo)) {
                    Intent intent = new Intent(MainActivity.this, ControlEmpresa.class);
                    intent.putExtra("user_email", correo);
                    startActivity(intent);
                } else {
                    String rut = snapshot.child("rut").getValue(String.class);
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String materno = snapshot.child("materno").getValue(String.class);
                    String paterno = snapshot.child("paterno").getValue(String.class);
                    String nacimiento = snapshot.child("nacimiento").getValue(String.class);
                    String direccion = snapshot.child("direccion").getValue(String.class);
                    String fono = snapshot.child("fono").getValue(String.class);

                    Intent intent = new Intent(MainActivity.this, VistaPrueba.class);
                    intent.putExtra("user_email", correo);
                    intent.putExtra("user_rut", rut);
                    intent.putExtra("user_nombre", nombre);
                    intent.putExtra("user_materno", materno);
                    intent.putExtra("user_paterno", paterno);
                    intent.putExtra("user_nacimiento", nacimiento);
                    intent.putExtra("user_direccion", direccion);
                    intent.putExtra("user_fono", fono);
                    intent.putExtra("user_cargo", cargo);
                    startActivity(intent);
                }
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Error al obtener el cargo del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
