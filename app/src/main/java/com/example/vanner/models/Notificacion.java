package com.example.vanner.models;

public class Notificacion {
    String nombre;
    String tipo;
    String correo;
    String cargo;
    String mensaje;

    public Notificacion() {
    }

    public Notificacion(String nombre, String tipo, String correo, String cargo, String mensaje) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.correo = correo;
        this.cargo = cargo;
        this.mensaje = mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
