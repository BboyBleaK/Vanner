package com.example.vanner.models;

public class Usuario {
    private String userId;
    private String nombre;
    private String paterno;
    private String materno;
    private String rut;
    private String direccion;
    private String contacto;
    private String fechaNacimiento;
    private String correo;
    private String cargo;
    private String genero;
    private String urlImagen;

    public Usuario() {
    }

    public Usuario(String userId, String nombre, String paterno, String materno, String rut, String direccion, String contacto, String fechaNacimiento, String correo, String cargo, String genero, String urlImagen) {
        this.userId = userId;
        this.nombre = nombre;
        this.paterno = paterno;
        this.materno = materno;
        this.rut = rut;
        this.direccion = direccion;
        this.contacto = contacto;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.cargo = cargo;
        this.genero = genero;
        this.urlImagen = urlImagen;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPaterno() {
        return paterno;
    }

    public void setPaterno(String paterno) {
        this.paterno = paterno;
    }

    public String getMaterno() {
        return materno;
    }

    public void setMaterno(String materno) {
        this.materno = materno;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
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

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
}
