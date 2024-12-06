package com.example.vanner.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vanner.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Agregar_experiencia extends AppCompatActivity {

    private EditText edtForNombreEmpresa, edtForCargo, edtForOtros, edtForIdActualizar;
    private TextInputEditText dtmForInicioEmpleo, dtmForTerminoEmpleo, campoActivo;
    private RadioGroup grpForMotivoSalida;
    private RadioButton rdbForRenuncia, rdbForTerminoContrato, rdbForOtros;
    private EditText edtForNombreContacto, edtForCargoContacto, edtForContacto;
    private Button btnCompletarFormulario, btnActualizarFormulario;
    private ImageButton btnRegresar;
    private String experienciaId;

    private Calendar fechaInicio = Calendar.getInstance();
    private Calendar fechaTermino = Calendar.getInstance();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_experiencia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtForNombreEmpresa = findViewById(R.id.edtForNombreEmpresa);
        edtForCargo = findViewById(R.id.edtForCargo);
        dtmForInicioEmpleo = findViewById(R.id.dtmForInicioEmpleo);
        dtmForTerminoEmpleo = findViewById(R.id.dtmForTerminoEmpleo);

        grpForMotivoSalida = findViewById(R.id.grpForMotivoSalida);
        rdbForRenuncia = findViewById(R.id.rdbForRenuncia);
        rdbForTerminoContrato = findViewById(R.id.rdbForTerminoContrato);
        rdbForOtros = findViewById(R.id.rdbForOtros);
        edtForOtros = findViewById(R.id.edtForOtros);

        edtForNombreContacto = findViewById(R.id.edtForNombreContacto);
        edtForCargoContacto = findViewById(R.id.edtForCargoContacto);
        edtForContacto = findViewById(R.id.edtForContacto);

        edtForIdActualizar = findViewById(R.id.edtForIdActualizar);

        btnCompletarFormulario = findViewById(R.id.btnCompletarFormulario);

        btnActualizarFormulario = findViewById(R.id.btnActualizarFormulario);
        btnCompletarFormulario.setVisibility(View.VISIBLE);
        btnActualizarFormulario.setVisibility(View.GONE);

        btnRegresar = findViewById(R.id.btnRegresar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtForOtros.setEnabled(false);

        grpForMotivoSalida.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdbForOtros) {
                edtForOtros.setEnabled(true);
            } else {
                edtForOtros.setEnabled(false);
                edtForOtros.setText("");
            }
        });

        rdbForOtros.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtForOtros.setVisibility(View.VISIBLE);
            } else {
                edtForOtros.setVisibility(View.GONE);
            }
        });

        btnCompletarFormulario.setOnClickListener(v -> {
            if (validarCampos()) {
                CrearDatosFirebase();
            }
        });
        btnActualizarFormulario.setOnClickListener(v -> {
            if (validarCampos()) {
                actualizarDatosFirebase(experienciaId);
            }
        });


        recibirYActualizarCampos();

        btnRegresar.setOnClickListener(v -> {
            finish();
        });

        dtmForInicioEmpleo.setOnClickListener(v -> mostrarDatePicker(true));
        dtmForTerminoEmpleo.setOnClickListener(v -> mostrarDatePicker(false));

    }

    private void inicializarComponentes() {
        edtForNombreEmpresa = findViewById(R.id.edtForNombreEmpresa);
        edtForCargo = findViewById(R.id.edtForCargo);
        dtmForInicioEmpleo = findViewById(R.id.dtmForInicioEmpleo);
        dtmForTerminoEmpleo = findViewById(R.id.dtmForTerminoEmpleo);
        grpForMotivoSalida = findViewById(R.id.grpForMotivoSalida);
        rdbForRenuncia = findViewById(R.id.rdbForRenuncia);
        rdbForTerminoContrato = findViewById(R.id.rdbForTerminoContrato);
        rdbForOtros = findViewById(R.id.rdbForOtros);
        edtForOtros = findViewById(R.id.edtForOtros);
        edtForNombreContacto = findViewById(R.id.edtForNombreContacto);
        edtForCargoContacto = findViewById(R.id.edtForCargoContacto);
        edtForContacto = findViewById(R.id.edtForContacto);
        btnCompletarFormulario = findViewById(R.id.btnCompletarFormulario);
        btnRegresar = findViewById(R.id.btnRegresar);
    }

    private void mostrarDatePicker(boolean esFechaInicio) {
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar fechaSeleccionada = Calendar.getInstance();
            fechaSeleccionada.set(year, month, dayOfMonth);
            String fechaTexto = dayOfMonth + "/" + (month + 1) + "/" + year;

            if (esFechaInicio) {
                if (fechaTermino != null && fechaSeleccionada.after(fechaTermino)) {
                    Toast.makeText(this, "La fecha de inicio no puede ser mayor a la fecha de término", Toast.LENGTH_SHORT).show();
                } else {
                    fechaInicio.set(year, month, dayOfMonth);
                    dtmForInicioEmpleo.setText(fechaTexto);
                }
            } else {
                if (fechaInicio != null && fechaSeleccionada.before(fechaInicio)) {
                    Toast.makeText(this, "La fecha de término no puede ser menor a la fecha de inicio", Toast.LENGTH_SHORT).show();
                } else {
                    fechaTermino.set(year, month, dayOfMonth);
                    dtmForTerminoEmpleo.setText(fechaTexto);
                }
            }
        }, año, mes, dia);
        datePicker.show();
    }


    private void CrearDatosFirebase() {
        String userId = mAuth.getCurrentUser().getUid();
        String nombreEmpresa = edtForNombreEmpresa.getText().toString();
        String cargoDesempenado = edtForCargo.getText().toString();
        String fechaInicio = dtmForInicioEmpleo.getText().toString();
        String fechaTermino = dtmForTerminoEmpleo.getText().toString();

        String motivoSalida;
        if (rdbForRenuncia.isChecked()) {
            motivoSalida = "Renuncia voluntaria";
        } else if (rdbForTerminoContrato.isChecked()) {
            motivoSalida = "Término de contrato";
        } else {
            motivoSalida = edtForOtros.getText().toString();
        }

        String nombreContacto = edtForNombreContacto.getText().toString();
        String cargoContacto = edtForCargoContacto.getText().toString();
        String contacto = edtForContacto.getText().toString();

        DatabaseReference experienciaRef = mDatabase.child("usuarios").child(userId).child("experiencia").push();
        String experienciaId = experienciaRef.getKey();

        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("id", experienciaId);
        datosUsuario.put("nombre_Empresa", nombreEmpresa);
        datosUsuario.put("cargo_Desempenado", cargoDesempenado);
        datosUsuario.put("fecha_Inicio", fechaInicio);
        datosUsuario.put("fecha_Termino", fechaTermino);
        datosUsuario.put("motivo_Salida", motivoSalida);
        if (!nombreContacto.isEmpty()) datosUsuario.put("nombre_Contacto", nombreContacto);
        if (!cargoContacto.isEmpty()) datosUsuario.put("cargo_Contacto", cargoContacto);
        if (!contacto.isEmpty()) datosUsuario.put("contacto_a_cargo", contacto);

        mDatabase.child("usuarios").child(userId).child("experiencia")
                .push()
                .setValue(datosUsuario)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validarCampos() {
        if (edtForNombreEmpresa.getText().toString().isEmpty()) {
            edtForNombreEmpresa.setError("Ingrese el nombre de la empresa");
            return false;
        }
        if (edtForCargo.getText().toString().isEmpty()) {
            edtForCargo.setError("Ingrese el cargo desempeñado");
            return false;
        }
        if (dtmForInicioEmpleo.getText().toString().isEmpty()) {
            dtmForInicioEmpleo.setError("Seleccione la fecha de inicio");
            return false;
        }
        if (dtmForTerminoEmpleo.getText().toString().isEmpty()) {
            dtmForTerminoEmpleo.setError("Seleccione la fecha de término");
            return false;
        }
        if (grpForMotivoSalida.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Seleccione un motivo de salida", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (rdbForOtros.isChecked() && edtForOtros.getText().toString().isEmpty()) {
            edtForOtros.setError("Especifique el motivo de salida");
            return false;
        }
        return true;
    }

    private void recibirYActualizarCampos() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String modo = extras.getString("modo");

            if ("editar".equals(modo)) {
                edtForIdActualizar.setText(extras.getString("id", ""));
                edtForNombreEmpresa.setText(extras.getString("nombreEmpresa", ""));
                edtForCargo.setText(extras.getString("cargo", ""));
                dtmForInicioEmpleo.setText(extras.getString("fechaInicio", ""));
                dtmForTerminoEmpleo.setText(extras.getString("fechaTermino", ""));

                String motivoSalida = extras.getString("motivoSalida", "");
                if ("Renuncia voluntaria".equals(motivoSalida)) {
                    rdbForRenuncia.setChecked(true);
                } else if ("Término de contrato".equals(motivoSalida)) {
                    rdbForTerminoContrato.setChecked(true);
                } else {
                    rdbForOtros.setChecked(true);
                    edtForOtros.setVisibility(View.VISIBLE);
                    edtForOtros.setText(motivoSalida);
                }

                edtForNombreContacto.setText(extras.getString("nombreContacto", ""));
                edtForCargoContacto.setText(extras.getString("cargoContacto", ""));
                edtForContacto.setText(extras.getString("contacto", ""));

                btnCompletarFormulario.setVisibility(View.GONE);
                btnActualizarFormulario.setVisibility(View.VISIBLE);

                String experienciaId = extras.getString("id");
                if (experienciaId != null) {
                    btnActualizarFormulario.setOnClickListener(v -> {
                        actualizarDatosFirebase(experienciaId);
                    });
                } else {
                    Toast.makeText(this, "No se pudo encontrar la experiencia para editar.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void actualizarDatosFirebase(String experienciaId) {
        if (!validarCampos()) return;

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> datosActualizados = new HashMap<>();
        datosActualizados.put("nombre_Empresa", edtForNombreEmpresa.getText().toString());
        datosActualizados.put("cargo_Desempenado", edtForCargo.getText().toString());
        datosActualizados.put("fecha_Inicio", dtmForInicioEmpleo.getText().toString());
        datosActualizados.put("fecha_Termino", dtmForTerminoEmpleo.getText().toString());

        String motivoSalida;
        if (rdbForRenuncia.isChecked()) {
            motivoSalida = "Renuncia voluntaria";
        } else if (rdbForTerminoContrato.isChecked()) {
            motivoSalida = "Término de contrato";
        } else {
            motivoSalida = edtForOtros.getText().toString();
        }
        datosActualizados.put("motivo_Salida", motivoSalida);

        datosActualizados.put("nombre_Contacto", edtForNombreContacto.getText().toString());
        datosActualizados.put("cargo_Contacto", edtForCargoContacto.getText().toString());
        datosActualizados.put("contacto_a_cargo", edtForContacto.getText().toString());

        DatabaseReference experienciaRef = mDatabase.child("usuarios").child(userId).child("experiencia").child(experienciaId);
        experienciaRef.updateChildren(datosActualizados).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Experiencia actualizada correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar la experiencia", Toast.LENGTH_SHORT).show();
            }
        });
    }
}