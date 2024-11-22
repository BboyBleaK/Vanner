package com.example.vanner.models;

public class Empleo {
    private String title;
    private String description;
    private int salary;
    private int vacancies;
    private String expirationDate;
    private String employmentMode;
    private String empresaId;
    private String empleoId;
    private String UrlImagen;


    public Empleo() {}


    public Empleo(String title, String description, int salary, int vacancies, String expirationDate, String employmentMode, String empresaId, String UrlImagen) {
        this.title = title;
        this.description = description;
        this.salary = salary;
        this.vacancies = vacancies;
        this.expirationDate = expirationDate;
        this.employmentMode = employmentMode;
        this.empresaId = empresaId;
        this.UrlImagen = UrlImagen;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getSalary() { return salary; }
    public void setSalary(int salary) { this.salary = salary; }
    public int getVacancies() { return vacancies; }
    public void setVacancies(int vacancies) { this.vacancies = vacancies; }
    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }
    public String getEmploymentMode() { return employmentMode; }
    public void setEmploymentMode(String employmentMode) { this.employmentMode = employmentMode; }
    public String getEmpresaId() { return empresaId; }
    public void setEmpresaId(String empresaId) { this.empresaId = empresaId; }
    public String getEmpleoId() { return empleoId; }
    public void setEmpleoId(String empleoId) { this.empleoId = empleoId; }
    public String getUrlImagen() { return UrlImagen; }
    public void setUrlImagen(String urlImagen) { UrlImagen = urlImagen; }
}
