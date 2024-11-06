package com.example.vanner.models;

public class Job {
    private String jobId;
    private String companyEmail;
    private String title;
    private String description;
    private String expirationDate;
    private int vacancies;
    private String mode;
    private String salary;

    public Job() {
        // Constructor vacío requerido por Firebase
    }

    public Job(String jobId, String companyEmail, String title, String description, String expirationDate, int vacancies, String mode, String salary) {
        this.jobId = jobId;
        this.companyEmail = companyEmail;
        this.title = title;
        this.description = description;
        this.expirationDate = expirationDate;
        this.vacancies = vacancies;
        this.mode = mode;
        this.salary = salary;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getVacancies() {
        return vacancies;
    }

    public void setVacancies(int vacancies) {
        this.vacancies = vacancies;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
