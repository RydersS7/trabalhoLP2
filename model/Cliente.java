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
    if (cpf == null || cpf.length() != 11) {
        throw new IllegalArgumentException("CPF deve ter 11 dígitos");
    }
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
    if (bonus < 0) {
        throw new IllegalArgumentException("Bônus não pode ser negativo");
    }
    this.bonus = bonus;
    }
     public void adicionarBonus(double valor) {
        this.bonus += valor * 0.10;
    }

    public void usarBonus(double valor) {
            bonus -= valor;
    }
    public boolean podeUsarBonus(double valor) {
        return bonus >= valor;
    }
    public String obterResumo() {
        return String.format("%s (CPF: %s) - Bônus: R$ %.2f", 
                           nome, cpf, bonus);
    }

}