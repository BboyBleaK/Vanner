package com.example.vanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vanner.R;

public class Home_Empresa extends AppCompatActivity {

    private ImageButton btnHome, btnChat, btnNotificacion, btnPerfil, btnCancelar, btnPasar, btnMeGusta;
    private TextView txtNombreTrabajador, txtFonoTrabajador, txtCorreoTrabajador, txtCargoTrabajador, txtGeneroTrabajador;
    private View viewHome, viewChat, viewNotificacion, viewPerfil;
    private Button btnCrearEmpleo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_empresa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnHome = findViewById(R.id.btnHome);
        btnChat = findViewById(R.id.btnChat);
        btnNotificacion = findViewById(R.id.btnNotificacion);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnPasar = findViewById(R.id.btnPasar);
        btnMeGusta = findViewById(R.id.btnMeGusta);
        btnCrearEmpleo = findViewById(R.id.btnCrearEmpleo);

        txtNombreTrabajador = findViewById(R.id.txtNombreTrabajador);
        txtFonoTrabajador = findViewById(R.id.txtFonoTrabajador);
        txtCorreoTrabajador = findViewById(R.id.txtCorreoTrabajador);
        txtCargoTrabajador = findViewById(R.id.txtCargoTrabajador);
        txtGeneroTrabajador = findViewById(R.id.txtGeneroTrabajador);

        viewHome = findViewById(R.id.viewHome);
        viewChat = findViewById(R.id.viewChat);
        viewNotificacion = findViewById(R.id.viewNotificacion);
        viewPerfil = findViewById(R.id.viewPerfil);

        selectView(R.id.viewHome);

        btnHome.setOnClickListener(v -> selectView(R.id.viewHome));
        btnChat.setOnClickListener(v -> selectView(R.id.viewChat));
        btnNotificacion.setOnClickListener(v -> selectView(R.id.viewNotificacion));
        btnPerfil.setOnClickListener(v -> selectView(R.id.viewPerfil));
        btnCrearEmpleo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Home_Empresa.this, JobPostActivity.class);
                startActivity(intent);
            }
        });

        setButtonBorder(btnHome);
        setButtonBorder(btnChat);
        setButtonBorder(btnNotificacion);
        setButtonBorder(btnPerfil);
    }

    private void selectView(int viewId) {
        // Ocultar todas las vistas
        viewHome.setVisibility(View.GONE);
        viewChat.setVisibility(View.GONE);
        viewNotificacion.setVisibility(View.GONE);
        viewPerfil.setVisibility(View.GONE);

        btnHome.setSelected(false);
        btnChat.setSelected(false);
        btnNotificacion.setSelected(false);
        btnPerfil.setSelected(false);

        if (viewId == R.id.viewHome) {
            viewHome.setVisibility(View.VISIBLE);
            btnHome.setSelected(true);
        } else if (viewId == R.id.viewChat) {
            viewChat.setVisibility(View.VISIBLE);
            btnChat.setSelected(true);
        } else if (viewId == R.id.viewNotificacion) {
            viewNotificacion.setVisibility(View.VISIBLE);
            btnNotificacion.setSelected(true);
        } else if (viewId == R.id.viewPerfil) {
            viewPerfil.setVisibility(View.VISIBLE);
            btnPerfil.setSelected(true);
        }
        
        setButtonBorder(btnHome);
        setButtonBorder(btnChat);
        setButtonBorder(btnNotificacion);
        setButtonBorder(btnPerfil);
    }

    private void setButtonBorder(ImageButton button) {
        GradientDrawable border = new GradientDrawable();
        if (button.isSelected()) {
            // Color del borde cuando el botón está seleccionado
            border.setColor(getResources().getColor(android.R.color.transparent)); // Color de fondo transparente
            border.setStroke(4, getResources().getColor(R.color.border_color)); // Color del borde seleccionado
        } else {
            // Color del borde cuando el botón no está seleccionado
            border.setColor(getResources().getColor(android.R.color.transparent)); // Color de fondo transparente
            border.setStroke(4, getResources().getColor(R.color.noBorder_color)); // Color del borde no seleccionado
        }
        border.setCornerRadius(8f);
        button.setBackground(border);
    }
}