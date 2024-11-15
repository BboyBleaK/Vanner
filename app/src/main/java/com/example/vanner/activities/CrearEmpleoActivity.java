package com.example.vanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton btnRegresar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_empleo);


        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        salaryInput = findViewById(R.id.salaryInput);
        vacanciesInput = findViewById(R.id.vacanciesInput);
        expirationDateInput = findViewById(R.id.expirationDateInput);
        modeGroup = findViewById(R.id.modeGroup);
        BtnPublicarEmpleo = findViewById(R.id.BtnPublicarEmpleo);
        btnRegresar = findViewById(R.id.btnRegresar);


        mDatabase = FirebaseDatabase.getInstance().getReference("empleos");
        mAuth = FirebaseAuth.getInstance();

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        BtnPublicarEmpleo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();
                String salaryString = salaryInput.getText().toString().trim();
                String vacanciesString = vacanciesInput.getText().toString().trim();
                String expirationDate = expirationDateInput.getText().toString().trim();


                if (title.isEmpty() || description.isEmpty() || salaryString.isEmpty() || vacanciesString.isEmpty() || expirationDate.isEmpty()) {
                    Toast.makeText(CrearEmpleoActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }


                int vacancies = 0;
                try {
                    vacancies = Integer.parseInt(vacanciesString);
                } catch (NumberFormatException e) {
                    Toast.makeText(CrearEmpleoActivity.this, "Vacantes debe ser un número válido", Toast.LENGTH_SHORT).show();
                    return;
                }


                int salaryValue = 0;
                try {
                    salaryValue = Integer.parseInt(salaryString); // Ahora es un valor entero
                } catch (NumberFormatException e) {
                    Toast.makeText(CrearEmpleoActivity.this, "Salario debe ser un valor numérico válido", Toast.LENGTH_SHORT).show();
                    return;
                }


                String empresaId = mAuth.getCurrentUser().getUid();


                int selectedModeId = modeGroup.getCheckedRadioButtonId();
                if (selectedModeId == -1) {
                    Toast.makeText(CrearEmpleoActivity.this, "Por favor selecciona el modo de empleo", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton selectedModeButton = findViewById(selectedModeId);
                String employmentMode = selectedModeButton.getText().toString();


                Empleo empleo = new Empleo(title, description, salaryValue, vacancies, expirationDate, employmentMode, empresaId);


                String empleoId = mDatabase.push().getKey();
                if (empleoId != null) {
                    empleo.setEmpleoId(empleoId);

                    mDatabase.child(empleoId).setValue(empleo);


                    Toast.makeText(CrearEmpleoActivity.this, "Oferta de empleo publicada", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CrearEmpleoActivity.this, "Hubo un error al publicar la oferta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
