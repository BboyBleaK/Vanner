package com.example.vanner.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.vanner.MainActivity;
import com.example.vanner.R;
import com.example.vanner.RegistroActivity;
import com.example.vanner.models.Empleo;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CrearEmpleoActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, salaryInput, vacanciesInput, expirationDateInput;
    private ImageView imgCreaEmpleo;
    private RadioGroup modeGroup;
    private Button BtnPublicarEmpleo;
    private ImageButton btnRegresar, btnCambiarImg;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_empleo);

        FirebaseApp storageApp = FirebaseApp.getInstance("proyectoStorage");
        storageReference = FirebaseStorage.getInstance(storageApp).getReference("perfil_usuarios");


        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        salaryInput = findViewById(R.id.salaryInput);
        vacanciesInput = findViewById(R.id.vacanciesInput);
        expirationDateInput = findViewById(R.id.expirationDateInput);
        modeGroup = findViewById(R.id.modeGroup);

        BtnPublicarEmpleo = findViewById(R.id.BtnPublicarEmpleo);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnCambiarImg = findViewById(R.id.btnCambiarImg);

        imgCreaEmpleo = findViewById(R.id.imgCreaEmpleo);

        mDatabase = FirebaseDatabase.getInstance().getReference("empleos");
        mAuth = FirebaseAuth.getInstance();

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
        }

        btnCambiarImg.setOnClickListener(view -> abrirGaleria());

        expirationDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });

        BtnPublicarEmpleo.setOnClickListener(view -> publicarEmpleo());
    }

        private void mostrarDatePicker() {
            final Calendar calendario = Calendar.getInstance();
            int año = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int año, int mes, int dia) {
                    String fechaSeleccionada = dia + "/" + (mes + 1) + "/" + año;
                    expirationDateInput.setText(fechaSeleccionada);
                }
            }, año, mes, dia);
            datePicker.show();
        }


    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgCreaEmpleo.setImageURI(imageUri);
        }
    }

    private void publicarEmpleo() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String salaryString = salaryInput.getText().toString().trim();
        String vacanciesString = vacanciesInput.getText().toString().trim();
        String expirationDate = expirationDateInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || salaryString.isEmpty() || vacanciesString.isEmpty() || expirationDate.isEmpty() || imageUri == null) {
            Toast.makeText(CrearEmpleoActivity.this, "Por favor completa todos los campos e incluye una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        int salaryValue;
        int vacancies;
        try {
            salaryValue = Integer.parseInt(salaryString);
            vacancies = Integer.parseInt(vacanciesString);
        } catch (NumberFormatException e) {
            Toast.makeText(CrearEmpleoActivity.this, "Salario y vacantes deben ser números válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedModeId = modeGroup.getCheckedRadioButtonId();
        if (selectedModeId == -1) {
            Toast.makeText(CrearEmpleoActivity.this, "Por favor selecciona el modo de empleo", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedModeButton = findViewById(selectedModeId);
        String employmentMode = selectedModeButton.getText().toString();

        String empresaId = mAuth.getCurrentUser().getUid();
        String empleoId = mDatabase.push().getKey();

        if (empleoId != null) {
            subirImagenAStorage(empleoId, title, description, salaryValue, vacancies, expirationDate, employmentMode, empresaId);
        } else {
            Toast.makeText(CrearEmpleoActivity.this, "Error al generar ID de empleo", Toast.LENGTH_SHORT).show();
        }
    }

    private void subirImagenAStorage(String empleoId, String title, String description, int salary, int vacancies, String expirationDate, String employmentMode, String empresaId) {
        StorageReference fileReference = storageReference.child("empleos/" + empleoId + ".jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imgUrl = uri.toString();
                    guardarDatosEnRealtimeDatabase(empleoId, title, description, salary, vacancies, expirationDate, employmentMode, empresaId, imgUrl);
                })).addOnFailureListener(e -> Toast.makeText(this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void guardarDatosEnRealtimeDatabase(String empleoId, String title, String description, int salary, int vacancies, String expirationDate, String employmentMode, String empresaId, String imgUrl) {
        Empleo empleo = new Empleo(title, description, salary, vacancies, expirationDate, employmentMode, empresaId, imgUrl);
        empleo.setEmpleoId(empleoId);

        mDatabase.child(empleoId).setValue(empleo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CrearEmpleoActivity.this, "Oferta de empleo publicada", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CrearEmpleoActivity.this, "Error al guardar datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
