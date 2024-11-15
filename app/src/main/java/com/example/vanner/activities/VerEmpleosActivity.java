package com.example.vanner.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vanner.R;
import com.example.vanner.adapters.EmpleoAdapter;
import com.example.vanner.models.Empleo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VerEmpleosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmpleoAdapter empleoAdapter;
    private List<Empleo> empleoList;
    private DatabaseReference databaseReference;
    private String empresaId;  // El ID de la empresa que creó los empleos
    private ImageButton btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_empleos);

        recyclerView = findViewById(R.id.recyclerViewEmpleos);
        btnRegresar = findViewById(R.id.btnRegresar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Obtener el ID de la empresa desde el usuario autenticado
        empresaId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Obtenemos el ID de la empresa autenticada
        empleoList = new ArrayList<>();

        // Referencia a la base de datos de Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("empleos");

        // Buscar empleos que correspondan a esta empresa
        Query query = databaseReference.orderByChild("empresaId").equalTo(empresaId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                empleoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Empleo empleo = snapshot.getValue(Empleo.class);
                    if (empleo != null) {
                        empleoList.add(empleo);
                    }
                }

                // Inicializar el adaptador con los datos filtrados
                empleoAdapter = new EmpleoAdapter(VerEmpleosActivity.this, empleoList, "empresa");
                recyclerView.setAdapter(empleoAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar posibles errores en la consulta
                Log.e("VerEmpleosActivity", "Error al cargar los empleos: " + databaseError.getMessage());
            }
        });
    }
}
