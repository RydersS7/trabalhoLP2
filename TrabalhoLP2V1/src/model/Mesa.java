package model;

public class Mesa {
    private int numero;
    private StatusMesa status;
    private Pedido pedidoAtual;
    private Cliente clienteAtual;

    public Mesa() {}

    public Mesa(int numero, StatusMesa status) {
        this.numero = numero;
        this.status = status;
    }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public StatusMesa getStatus() { return status; }
    public void setStatus(StatusMesa status) { this.status = status; }

    public Pedido getPedidoAtual() { return pedidoAtual; }
    public void setPedidoAtual(Pedido pedidoAtual) { this.pedidoAtual = pedidoAtual; }

    public Cliente getClienteAtual() { return clienteAtual; }
    public void setClienteAtual(Cliente cliente) { this.clienteAtual = cliente; }

    public void ocuparMesa(Pedido pedido) {
        this.pedidoAtual = pedido;
        this.status = StatusMesa.OCUPADA;
    }

    public void liberarMesa() {
        this.pedidoAtual = null;
        this.status = StatusMesa.LIVRE;
        this.clienteAtual = null;
    }

    @Override
    public String toString() {
        return String.format("Mesa %d [%s]", numero, status);
    }
}
