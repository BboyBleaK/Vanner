package com.example.vanner.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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

import com.bumptech.glide.Glide;
import com.example.vanner.R;
import com.example.vanner.models.Empleo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;
import java.util.Calendar;

public class EditarEmpleoActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etSalary, etVacancies, etExpirationDate;
    private RadioGroup etEmploymentMode;
    private Button btnSaveChanges;
    private ImageView imgEditarEmpleo;
    private ImageButton btnCambiarImg;
    private Uri urlImagen;
    private String empleoId, empresaId;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_empleo);

        empleoId = getIntent().getStringExtra("empleoId");
        empresaId = getIntent().getStringExtra("empresaId");

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etSalary = findViewById(R.id.etSalary);
        etVacancies = findViewById(R.id.etVacancies);
        etExpirationDate = findViewById(R.id.etExpirationDate);
        etEmploymentMode = findViewById(R.id.etEmploymentMode);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCambiarImg = findViewById(R.id.btnCambiarImg);
        imgEditarEmpleo = findViewById(R.id.imgEditarEmpleo);

        loadEmpleoData();

        etExpirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });
        btnCambiarImg.setOnClickListener(view -> abrirGaleria());

        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            urlImagen = data.getData();
            imgEditarEmpleo.setImageURI(urlImagen);
        }
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
                etExpirationDate.setText(fechaSeleccionada);
            }
        }, año, mes, dia);
        datePicker.show();
    }

    private void loadEmpleoData() {
        DatabaseReference empleoRef = FirebaseDatabase.getInstance().getReference("empleos").child(empleoId);

        empleoRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Empleo empleo = task.getResult().getValue(Empleo.class);
                if (empleo != null) {
                    etTitle.setText(empleo.getTitle());
                    etDescription.setText(empleo.getDescription());
                    etSalary.setText(String.valueOf(empleo.getSalary()));
                    etVacancies.setText(String.valueOf(empleo.getVacancies()));
                    etExpirationDate.setText(empleo.getExpirationDate());

                    for (int i = 0; i < etEmploymentMode.getChildCount(); i++) {
                        RadioButton radioButton = (RadioButton) etEmploymentMode.getChildAt(i);
                        if (radioButton.getText().toString().equals(empleo.getEmploymentMode())) {
                            radioButton.setChecked(true);
                            break;
                        }
                    }

                    empresaId = empleo.getEmpresaId();

                    String urlImagenString = empleo.getUrlImagen();
                    if (urlImagenString != null && !urlImagenString.isEmpty()) {
                        urlImagen = Uri.parse(urlImagenString);
                        Glide.with(this).load(urlImagen).into(imgEditarEmpleo);
                    }
                }
            } else {
                Toast.makeText(EditarEmpleoActivity.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveChanges() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String salaryStr = etSalary.getText().toString().trim();
        String vacanciesStr = etVacancies.getText().toString().trim();
        String expirationDate = etExpirationDate.getText().toString().trim();

        int selectedModeId = etEmploymentMode.getCheckedRadioButtonId();
        if (selectedModeId == -1) {
            Toast.makeText(this, "Por favor selecciona el modo de empleo", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedModeButton = findViewById(selectedModeId);
        String employmentMode = selectedModeButton.getText().toString();

        if (title.isEmpty() || description.isEmpty() || salaryStr.isEmpty() || vacanciesStr.isEmpty() || expirationDate.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int salary = Integer.parseInt(salaryStr);
            int vacancies = Integer.parseInt(vacanciesStr);

            String urlImagenString = urlImagen != null ? urlImagen.toString() : null;

            Empleo updatedEmpleo = new Empleo(title, description, salary, vacancies, expirationDate, employmentMode, empresaId, urlImagenString);
            updatedEmpleo.setEmpleoId(empleoId);

            DatabaseReference empleoRef = FirebaseDatabase.getInstance().getReference("empleos").child(empleoId);
            empleoRef.setValue(updatedEmpleo).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditarEmpleoActivity.this, "Empleo actualizado con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditarEmpleoActivity.this, "Error al actualizar el empleo", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El salario y las vacantes deben ser números válidos", Toast.LENGTH_SHORT).show();
        }
    }

}
