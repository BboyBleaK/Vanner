package com.example.vanner.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vanner.R;
import com.example.vanner.models.Job;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JobEditActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, salaryInput, vacanciesInput, expirationDateInput;
    private RadioGroup modeGroup;
    private Button updateJobButton, cancelButton;
    private ProgressBar progressBar;
    private DatabaseReference jobsDatabase;
    private String jobId, companyEmail, mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_edit);

        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        salaryInput = findViewById(R.id.salaryInput);
        vacanciesInput = findViewById(R.id.vacanciesInput);
        expirationDateInput = findViewById(R.id.expirationDateInput);
        modeGroup = findViewById(R.id.modeGroup);
        updateJobButton = findViewById(R.id.updateJobButton);
        cancelButton = findViewById(R.id.cancelJobButton); // Renombrado a "cancelButton"
        progressBar = findViewById(R.id.progressBar);

        jobsDatabase = FirebaseDatabase.getInstance().getReference("jobs");

        // Obtener los datos pasados desde la actividad anterior
        jobId = getIntent().getStringExtra("jobId");
        companyEmail = getIntent().getStringExtra("companyEmail");
        titleInput.setText(getIntent().getStringExtra("title"));
        descriptionInput.setText(getIntent().getStringExtra("description"));
        salaryInput.setText(getIntent().getStringExtra("salary"));
        vacanciesInput.setText(getIntent().getStringExtra("vacancies"));
        expirationDateInput.setText(getIntent().getStringExtra("expirationDate"));
        mode = getIntent().getStringExtra("mode");

        // Establecer la opción seleccionada en el RadioGroup según el modo
        if (mode != null) {
            if (mode.equals("Full-time")) {
                modeGroup.check(R.id.radioFullTime);
            } else if (mode.equals("Part-time")) {
                modeGroup.check(R.id.radioPartTime);
            } else if (mode.equals("Por Horas")) {
                modeGroup.check(R.id.radioHoras);
            }
        }

        updateJobButton.setOnClickListener(view -> updateJob());
        cancelButton.setOnClickListener(view -> finish()); // Cierra la actividad al cancelar
    }

    private void updateJob() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String salary = salaryInput.getText().toString().trim();
        String vacancies = vacanciesInput.getText().toString().trim();
        String expirationDate = expirationDateInput.getText().toString().trim();
        int selectedModeId = modeGroup.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(salary) ||
                TextUtils.isEmpty(vacancies) || TextUtils.isEmpty(expirationDate) || selectedModeId == -1) {
            Toast.makeText(JobEditActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mode = ((RadioButton) findViewById(selectedModeId)).getText().toString();

        progressBar.setVisibility(View.VISIBLE);

        // Crear el objeto Job actualizado
        Job updatedJob = new Job(jobId, companyEmail, title, description, expirationDate, Integer.parseInt(vacancies), mode, salary);

        // Actualizar el empleo en la base de datos
        jobsDatabase.child(jobId).setValue(updatedJob).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(JobEditActivity.this, "Empleo actualizado con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(JobEditActivity.this, "Error al actualizar el empleo: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
