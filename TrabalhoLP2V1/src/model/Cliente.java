package model;

import java.util.ArrayList;

public class Cliente {
    private String nome;
    private String cpf;
    private String email;
    private double bonus;
    private ArrayList<Pedido> historicoPedidos;

    public Cliente() { this.historicoPedidos = new ArrayList<>(); }

    public Cliente(String nome, String cpf, String email, double bonus) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.bonus = bonus;
        this.historicoPedidos = new ArrayList<>();
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) {
        if (cpf == null || cpf.length() != 11)
            throw new IllegalArgumentException("CPF deve ter 11 dígitos numéricos");
        this.cpf = cpf;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getBonus() { return bonus; }
    public void setBonus(double bonus) {
        if (bonus < 0) throw new IllegalArgumentException("Bônus não pode ser negativo");
        this.bonus = bonus;
    }

    public void adicionarBonus(double valorPago) {
        this.bonus += valorPago * 0.10;
    }

    public void usarBonus(double valor) {
        if (valor > bonus) throw new IllegalArgumentException("Saldo de bônus insuficiente");
        bonus -= valor;
    }

    public boolean podeUsarBonus(double valor) { return bonus >= valor; }

    public String obterResumo() {
        return String.format("%s (CPF: %s) — Bônus: R$ %.2f", nome, cpf, bonus);
    }

    public void adicionarPedidoAoHistorico(Pedido pedido) { historicoPedidos.add(pedido); }

    public ArrayList<Pedido> getHistoricoPedidos() { return historicoPedidos; }

    public double getTotalGasto() {
        double total = 0;
        for (Pedido p : historicoPedidos)
            if (p.getStatus().equals("PAGO")) total += p.calcularTotal();
        return total;
    }

    @Override
    public String toString() { return obterResumo(); }
}
