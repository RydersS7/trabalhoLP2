package model;

public class Cliente {
    private String nome;
    private String cpf;
    private String email;
    private double bonus;

    // Construtores
    public Cliente() {}

    public Cliente(String nome, String cpf, String email, double bonus) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.bonus = bonus;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }
}