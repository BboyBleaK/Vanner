package com.example.vanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vanner.R;
import com.example.vanner.models.Empleo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CrearEmpleoActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, salaryInput, vacanciesInput, expirationDateInput;
    private RadioGroup modeGroup;
    private Button BtnPublicarEmpleo;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_empleo);

        // Inicialización de los componentes de la UI
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        salaryInput = findViewById(R.id.salaryInput);
        vacanciesInput = findViewById(R.id.vacanciesInput);
        expirationDateInput = findViewById(R.id.expirationDateInput);
        modeGroup = findViewById(R.id.modeGroup);
        BtnPublicarEmpleo = findViewById(R.id.BtnPublicarEmpleo);

        // Inicialización de la base de datos y FirebaseAuth
        mDatabase = FirebaseDatabase.getInstance().getReference("empleos");
        mAuth = FirebaseAuth.getInstance();

        // Configuración del botón para publicar la oferta de empleo
        BtnPublicarEmpleo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();
                String salaryString = salaryInput.getText().toString().trim();
                String vacanciesString = vacanciesInput.getText().toString().trim();
                String expirationDate = expirationDateInput.getText().toString().trim();

                // Validación de campos vacíos
                if (title.isEmpty() || description.isEmpty() || salaryString.isEmpty() || vacanciesString.isEmpty() || expirationDate.isEmpty()) {
                    Toast.makeText(CrearEmpleoActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validación de valores numéricos para vacantes
                int vacancies = 0;
                try {
                    vacancies = Integer.parseInt(vacanciesString);
                } catch (NumberFormatException e) {
                    Toast.makeText(CrearEmpleoActivity.this, "Vacantes debe ser un número válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validación de valores numéricos para salario
                int salaryValue = 0;
                try {
                    salaryValue = Integer.parseInt(salaryString); // Ahora es un valor entero
                } catch (NumberFormatException e) {
                    Toast.makeText(CrearEmpleoActivity.this, "Salario debe ser un valor numérico válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Obtener el ID de la empresa (usuario logueado)
                String empresaId = mAuth.getCurrentUser().getUid();

                // Validación del modo de empleo
                int selectedModeId = modeGroup.getCheckedRadioButtonId();
                if (selectedModeId == -1) {
                    Toast.makeText(CrearEmpleoActivity.this, "Por favor selecciona el modo de empleo", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton selectedModeButton = findViewById(selectedModeId);
                String employmentMode = selectedModeButton.getText().toString();

                // Crear el objeto Empleo con salario como int
                Empleo empleo = new Empleo(title, description, salaryValue, vacancies, expirationDate, employmentMode, empresaId);

                // Obtener un ID único para el empleo
                String empleoId = mDatabase.push().getKey();
                if (empleoId != null) {
                    empleo.setEmpleoId(empleoId);  // Asociar el empleoId al objeto Empleo
                    // Guardar el empleo en Firebase
                    mDatabase.child(empleoId).setValue(empleo);

                    // Mensaje de éxito
                    Toast.makeText(CrearEmpleoActivity.this, "Oferta de empleo publicada", Toast.LENGTH_SHORT).show();
                    finish();  // Terminar la actividad
                } else {
                    Toast.makeText(CrearEmpleoActivity.this, "Hubo un error al publicar la oferta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
