package com.example.vanner.models;

public class Experiencia {
    private String id;
    private String nombre_Empresa;
    private String cargo_Desempenado;
    private String fecha_Inicio;
    private String fecha_Termino;
    private String motivo_Salida;
    private String nombre_Contacto;
    private String cargo_Contacto;
    private String contacto_a_cargo;

    public Experiencia() {
    }

    public Experiencia(String id, String nombre_Empresa, String cargo_Desempenado, String fecha_Inicio, String fecha_Termino, String motivo_Salida, String nombre_Contacto, String cargo_Contacto, String contacto_a_cargo) {
        this.id = id;
        this.nombre_Empresa = nombre_Empresa;
        this.cargo_Desempenado = cargo_Desempenado;
        this.fecha_Inicio = fecha_Inicio;
        this.fecha_Termino = fecha_Termino;
        this.motivo_Salida = motivo_Salida;
        this.nombre_Contacto = nombre_Contacto;
        this.cargo_Contacto = cargo_Contacto;
        this.contacto_a_cargo = contacto_a_cargo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre_Empresa() {
        return nombre_Empresa;
    }

    public void setNombre_Empresa(String nombre_Empresa) {
        this.nombre_Empresa = nombre_Empresa;
    }

    public String getCargo_Desempenado() {
        return cargo_Desempenado;
    }

    public void setCargo_Desempenado(String cargo_Desempenado) {
        this.cargo_Desempenado = cargo_Desempenado;
    }

    public String getFecha_Inicio() {
        return fecha_Inicio;
    }

    public void setFecha_Inicio(String fecha_Inicio) {
        this.fecha_Inicio = fecha_Inicio;
    }

    public String getFecha_Termino() {
        return fecha_Termino;
    }

    public void setFecha_Termino(String fecha_Termino) {
        this.fecha_Termino = fecha_Termino;
    }

    public String getMotivo_Salida() {
        return motivo_Salida;
    }

    public void setMotivo_Salida(String motivo_Salida) {
        this.motivo_Salida = motivo_Salida;
    }

    public String getNombre_Contacto() {
        return nombre_Contacto;
    }

    public void setNombre_Contacto(String nombre_Contacto) {
        this.nombre_Contacto = nombre_Contacto;
    }

    public String getCargo_Contacto() {
        return cargo_Contacto;
    }

    public void setCargo_Contacto(String cargo_Contacto) {
        this.cargo_Contacto = cargo_Contacto;
    }

    public String getContacto_a_cargo() {
        return contacto_a_cargo;
    }

    public void setContacto_a_cargo(String contacto_a_cargo) {
        this.contacto_a_cargo = contacto_a_cargo;
    }
}
