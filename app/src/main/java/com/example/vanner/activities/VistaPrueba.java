package com.example.vanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vanner.R;

public class VistaPrueba extends AppCompatActivity {

    private TextView txtCorreo;

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

        txtCorreo = findViewById(R.id.txtCorreo);

        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("user_email");

        if (userEmail != null) {
            txtCorreo.setText("Correo: " + userEmail);
        } else {
            txtCorreo.setText("Correo no disponible");
        }

        Button buttonVolverHome = findViewById(R.id.buttonVolverHome);
        buttonVolverHome.setOnClickListener(v -> {
            Intent volverIntent = new Intent(VistaPrueba.this, HomeActivity.class);
            startActivity(volverIntent);

        });
    }
}
