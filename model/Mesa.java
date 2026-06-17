package model;

public class Mesa {
    private int numero;
    private String status;
    private Pedido pedidoAtual;

    // Construtores
    public Mesa() {}

    public Mesa(int numero, String status) {
        this.numero = numero;
        this.status = status;
    }

    // Getters e Setters
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Pedido getPedidoAtual() {
        return pedidoAtual;
    }

    public void setPedidoAtual(Pedido pedidoAtual) {
        this.pedidoAtual = pedidoAtual;
    }
}