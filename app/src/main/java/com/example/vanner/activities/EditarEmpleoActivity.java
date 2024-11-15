package com.example.vanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vanner.R;
import com.example.vanner.models.Empleo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditarEmpleoActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etSalary, etVacancies, etExpirationDate, etEmploymentMode;
    private Button btnSaveChanges;
    private String empleoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_empleo);

        // Obtener el ID del empleo desde el Intent
        empleoId = getIntent().getStringExtra("empleoId");

        // Inicializar vistas
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etSalary = findViewById(R.id.etSalary);
        etVacancies = findViewById(R.id.etVacancies);
        etExpirationDate = findViewById(R.id.etExpirationDate);
        etEmploymentMode = findViewById(R.id.etEmploymentMode);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Cargar datos del empleo
        loadEmpleoData();

        // Configurar el botón de guardar cambios
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadEmpleoData() {
        // Aquí deberías cargar los datos del empleo desde Firebase usando empleoId
        DatabaseReference empleoRef = FirebaseDatabase.getInstance().getReference("empleos").child(empleoId);

        empleoRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Empleo empleo = task.getResult().getValue(Empleo.class);
                if (empleo != null) {
                    // Rellenar los campos con los datos actuales
                    etTitle.setText(empleo.getTitle());
                    etDescription.setText(empleo.getDescription());
                    etSalary.setText(String.valueOf(empleo.getSalary()));
                    etVacancies.setText(String.valueOf(empleo.getVacancies()));
                    etExpirationDate.setText(empleo.getExpirationDate());
                    etEmploymentMode.setText(empleo.getEmploymentMode());
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
        String employmentMode = etEmploymentMode.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || salaryStr.isEmpty() || vacanciesStr.isEmpty() || expirationDate.isEmpty() || employmentMode.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int salary = Integer.parseInt(salaryStr);
        int vacancies = Integer.parseInt(vacanciesStr);

        // Crear un objeto Empleo con los datos actualizados
        Empleo updatedEmpleo = new Empleo();
        updatedEmpleo.setTitle(title);
        updatedEmpleo.setDescription(description);
        updatedEmpleo.setSalary(salary);
        updatedEmpleo.setVacancies(vacancies);
        updatedEmpleo.setExpirationDate(expirationDate);
        updatedEmpleo.setEmploymentMode(employmentMode);

        // No modificamos el empleoId, ya que es el ID del nodo en Firebase
        updatedEmpleo.setEmpleoId(empleoId); // Mantener el mismo empleoId que cargamos

        // Guardar los datos actualizados en Firebase, sin modificar el empleoId
        DatabaseReference empleoRef = FirebaseDatabase.getInstance().getReference("empleos").child(empleoId);
        empleoRef.setValue(updatedEmpleo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditarEmpleoActivity.this, "Empleo actualizado con éxito", Toast.LENGTH_SHORT).show();
                finish(); // Regresar a la actividad anterior
            } else {
                Toast.makeText(EditarEmpleoActivity.this, "Error al actualizar el empleo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
