package com.example.vanner.models;

public class UsuarioLogin {
    public String email;
    public String cargo;

    // Constructor vacío requerido por Firebase
    public UsuarioLogin() {
    }

    public UsuarioLogin(String email, String cargo) {
        this.email = email;
        this.cargo = cargo;
    }

}
