package com.example.vanner.ui;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class RegistroTactil implements View.OnFocusChangeListener {

    private String mensaje;

    // Constructor que recibe el mensaje
    public RegistroTactil(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            // Mostrar el mensaje personalizado
            Snackbar.make(v, mensaje, Snackbar.LENGTH_LONG).show();
        }
    }
}