public class Mesa {
    private int numero;
    private String status;
    private Pedido pedidoAtual;

    public Mesa(int numero) {
        this.numero = numero;
        this.status = StatusMesa.LIVRE;
    }

    public int getNumero() {
        return numero;
    }

    public String getStatus() {
        return status;
    }

    public Pedido getPedidoAtual() {
        return pedidoAtual;
    }

    public void ocuparMesa(Pedido pedido) {
        this.pedidoAtual = pedido;
        this.status = StatusMesa.OCUPADA;
    }

    public void liberarMesa() {
        this.pedidoAtual = null;
        this.status = StatusMesa.LIVRE;
    }
}