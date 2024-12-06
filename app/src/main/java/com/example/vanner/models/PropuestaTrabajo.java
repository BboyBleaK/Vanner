package com.example.vanner.models;

public class PropuestaTrabajo {
    private String idTrabajador;
    private String idEmpresa;

    public PropuestaTrabajo() {
    }

    public PropuestaTrabajo(String idTrabajador, String idEmpresa) {
        this.idTrabajador = idTrabajador;
        this.idEmpresa = idEmpresa;
    }

    public String getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(String idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
}
