package com.example.vanner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText edtCorreo, edtPass, edtRePass, edtNombre, edtRut, edtEdad;
    private EditText edtIdentificadorFiscal, edtRutEmpresa, edtNombreEmpresa, edtCorreoEmpresa, dtpContactoEmpresa, edtPassEmpresa, edtRePassEmpresa;
    private RelativeLayout RelativeUsuario, RelativeEmpresa;
    private LinearLayout linearRegresarAusuario, LinearIrAEmpresa, LinearRegistrarEmpresa, LinearRegistrarUsuario;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ImageView imgUsuario;
    private ImageButton btnCambioIMG;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        edtCorreo = findViewById(R.id.edtCorreo);
        edtPass = findViewById(R.id.edtUsuario);
        edtRePass = findViewById(R.id.edtRePass);
        edtNombre = findViewById(R.id.edtNombre);
        edtRut = findViewById(R.id.edtRut);
        edtEdad = findViewById(R.id.edtEdad);
        RelativeUsuario = findViewById(R.id.RelativeUsuario);
        LinearRegistrarUsuario = findViewById(R.id.LinearRegistrarUsuario);

        // Inicialización de vistas para empresa
        edtIdentificadorFiscal = findViewById(R.id.edtIdentificadorFiscal);
        edtRutEmpresa = findViewById(R.id.edtRutEmpresa);
        edtNombreEmpresa = findViewById(R.id.edtNombreEmpresa);
        edtCorreoEmpresa = findViewById(R.id.edtCorreoEmpresa);
        dtpContactoEmpresa = findViewById(R.id.dtpContactoEmpresa);
        edtPassEmpresa = findViewById(R.id.edtPassEmpresa);
        edtRePassEmpresa = findViewById(R.id.edtRePassEmpresa);
        RelativeEmpresa = findViewById(R.id.RelativeEmpresa);
        LinearRegistrarEmpresa = findViewById(R.id.LinearRegistrarEmpresa);

        // Botones de navegación entre usuario y empresa
        linearRegresarAusuario = findViewById(R.id.linearRegresarAusuario);
        LinearIrAEmpresa = findViewById(R.id.LinearIrAEmpresa);

        // Botón para cambiar imagen
        btnCambioIMG = findViewById(R.id.btnCambioimg);
        imgUsuario = findViewById(R.id.imgUsuario);

        // Alternar entre vistas de registro para usuario y empresa
        LinearIrAEmpresa.setOnClickListener(v -> {
            RelativeUsuario.setVisibility(View.GONE);
            RelativeEmpresa.setVisibility(View.VISIBLE);
        });

        linearRegresarAusuario.setOnClickListener(v -> {
            RelativeEmpresa.setVisibility(View.GONE);
            RelativeUsuario.setVisibility(View.VISIBLE);
        });

        // Solicitar permiso de almacenamiento si es necesario
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
        }

        // Cambio de imagen de usuario
        btnCambioIMG.setOnClickListener(view -> abrirGaleria());

        // Manejo de registro de usuario
        LinearRegistrarUsuario.setOnClickListener(v -> {
            if (verificarCamposUsuario()) registrarUsuario("usuario");
        });

        // Manejo de registro de empresa
        LinearRegistrarEmpresa.setOnClickListener(v -> {
            if (verificarCamposEmpresa()) registrarUsuario("empresa");
        });

        // Botón para regresar al MainActivity
        ImageButton btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                imgUsuario.setImageURI(imageUri);
            }
        }
    }

    private void registrarUsuario(String tipoUsuario) {
        String email, password, rePassword;

        if ("usuario".equals(tipoUsuario)) {
            email = edtCorreo.getText().toString().trim();
            password = edtPass.getText().toString().trim();
            rePassword = edtRePass.getText().toString().trim();
        } else {
            email = edtCorreoEmpresa.getText().toString().trim();
            password = edtPassEmpresa.getText().toString().trim();
            rePassword = edtRePassEmpresa.getText().toString().trim();
        }

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, completa todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(rePassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(RegistroActivity.this, "Correo de verificación enviado", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegistroActivity.this, "Error al enviar correo de verificación", Toast.LENGTH_SHORT).show();
                                }
                            });
                            if ("usuario".equals(tipoUsuario)) {
                                guardarDatosEnRealtimeDatabaseUsuario(user.getUid());
                            } else {
                                guardarDatosEnRealtimeDatabaseEmpresa(user.getUid());
                            }
                        }
                        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegistroActivity.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarDatosEnRealtimeDatabaseUsuario(String userId) {
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("nombre", edtNombre.getText().toString().trim());
        datosUsuario.put("rut", edtRut.getText().toString().trim());
        datosUsuario.put("correo", edtCorreo.getText().toString().trim());
        datosUsuario.put("edad", edtEdad.getText().toString().trim());
        datosUsuario.put("cargo", "usuario"); // Guardar el tipo de usuario

        mDatabase.child("usuarios").child(userId).setValue(datosUsuario).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(RegistroActivity.this, "Error al guardar los datos del usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarDatosEnRealtimeDatabaseEmpresa(String userId) {
        Map<String, Object> datosEmpresa = new HashMap<>();
        datosEmpresa.put("identificador_fiscal", edtIdentificadorFiscal.getText().toString().trim());
        datosEmpresa.put("nombre", edtNombreEmpresa.getText().toString().trim());
        datosEmpresa.put("rut", edtRutEmpresa.getText().toString().trim());
        datosEmpresa.put("correo", edtCorreoEmpresa.getText().toString().trim());
        datosEmpresa.put("contacto", dtpContactoEmpresa.getText().toString().trim());
        datosEmpresa.put("cargo", "empresa"); // Guardar el tipo de empresa

        mDatabase.child("empresas").child(userId).setValue(datosEmpresa).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(RegistroActivity.this, "Error al guardar los datos de la empresa: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegistroActivity.this, "Datos de la empresa guardados exitosamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean verificarCamposUsuario() {
        String[] campos = {edtCorreo.getText().toString().trim(), edtNombre.getText().toString().trim(),
                edtRut.getText().toString().trim(), edtEdad.getText().toString().trim(),
                edtPass.getText().toString().trim(), edtRePass.getText().toString().trim()};
        String[] mensajes = {"Por favor, ingresa un correo", "Por favor, ingresa tu nombre",
                "Por favor, ingresa tu RUT", "Por favor, ingresa tu edad",
                "Por favor, ingresa una contraseña", "Por favor, confirma tu contraseña"};

        for (int i = 0; i < campos.length; i++) {
            if (TextUtils.isEmpty(campos[i])) {
                Toast.makeText(this, mensajes[i], Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean verificarCamposEmpresa() {
        String[] campos = {edtIdentificadorFiscal.getText().toString().trim(), edtRutEmpresa.getText().toString().trim(),
                edtNombreEmpresa.getText().toString().trim(), edtCorreoEmpresa.getText().toString().trim(),
                dtpContactoEmpresa.getText().toString().trim(), edtPassEmpresa.getText().toString().trim(),
                edtRePassEmpresa.getText().toString().trim()};
        String[] mensajes = {"Por favor, ingresa el identificador fiscal", "Por favor, ingresa el RUT de la empresa",
                "Por favor, ingresa el nombre de la empresa", "Por favor, ingresa el correo de la empresa",
                "Por favor, ingresa el contacto de la empresa", "Por favor, ingresa una contraseña",
                "Por favor, confirma la contraseña"};

        for (int i = 0; i < campos.length; i++) {
            if (TextUtils.isEmpty(campos[i])) {
                Toast.makeText(this, mensajes[i], Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
