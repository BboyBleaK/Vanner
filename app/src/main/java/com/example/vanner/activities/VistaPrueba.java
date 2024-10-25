package com.example.vanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VistaPrueba extends AppCompatActivity {

    private EditText edtRut, edtNombre, edtPaterno, edtMaterno, dtpNacimiento, edtDireccion, edtCorreo;
    private ImageButton btnRegresar, btnModificar, btnEliminar, btnRegistrar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vista_prueba);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        edtRut = findViewById(R.id.edtRut);
        edtNombre = findViewById(R.id.edtNombre);
        edtPaterno = findViewById(R.id.edtPaterno);
        edtMaterno = findViewById(R.id.edtMaterno);
        dtpNacimiento = findViewById(R.id.dtpNacimiento);
        edtDireccion = findViewById(R.id.edtDireccion);
        edtCorreo = findViewById(R.id.edtCorreo);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnModificar = findViewById(R.id.btnModificar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setClickable(false);


        Intent intent = getIntent();
        String userId = intent.getStringExtra("user_id");
        edtCorreo.setText(intent.getStringExtra("user_email"));
        edtRut.setText(intent.getStringExtra("user_rut"));
        edtNombre.setText(intent.getStringExtra("user_nombre"));
        edtPaterno.setText(intent.getStringExtra("user_paterno"));
        edtMaterno.setText(intent.getStringExtra("user_materno"));
        dtpNacimiento.setText(intent.getStringExtra("user_nacimiento"));
        edtDireccion.setText(intent.getStringExtra("user_direccion"));


        disableFields();


        btnRegresar.setOnClickListener(v -> {
            Intent volverIntent = new Intent(VistaPrueba.this, HomeActivity.class);
            startActivity(volverIntent);
        });


        btnModificar.setOnClickListener(v -> {
            enableFields();
            edtRut.requestFocus();
        });


        btnEliminar.setOnClickListener(v -> eliminarUsuario(userId));
    }

    private void disableFields() {
        edtCorreo.setEnabled(false);
        edtDireccion.setEnabled(false);
        edtMaterno.setEnabled(false);
        edtRut.setEnabled(false);
        edtNombre.setEnabled(false);
        edtPaterno.setEnabled(false);
        dtpNacimiento.setEnabled(false);
    }

    private void enableFields() {
        edtCorreo.setEnabled(true);
        edtDireccion.setEnabled(true);
        edtMaterno.setEnabled(true);
        edtRut.setEnabled(true);
        edtNombre.setEnabled(true);
        edtPaterno.setEnabled(true);
        dtpNacimiento.setEnabled(true);
    }

    private void eliminarUsuario(String userId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            currentUser.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    mDatabase.child("usuarios").child(userId).removeValue()
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(VistaPrueba.this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(VistaPrueba.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.e("VistaPrueba", "Error al eliminar de la base de datos", task1.getException());
                                    Toast.makeText(VistaPrueba.this, "Error al eliminar el usuario de la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Log.e("VistaPrueba", "Error al eliminar de Auth", task.getException());
                    Toast.makeText(VistaPrueba.this, "Error al eliminar el usuario de autenticación", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No se encontró el usuario autenticado", Toast.LENGTH_SHORT).show();
        }
    }
}
