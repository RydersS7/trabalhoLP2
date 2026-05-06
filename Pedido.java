import java.util.ArrayList;

public class Pedido {
    private Cliente cliente;
    private Mesa mesa;
    private ArrayList<ItemPedido> itens;
    private String status;

    public Pedido(Cliente cliente, Mesa mesa) {
        this.cliente = cliente;
        this.mesa = mesa;
        this.itens = new ArrayList<>();
        this.status = "ABERTO";
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Mesa getMesa() { 
        return mesa;
    }

    public String getStatus() {
        return status;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    public double calcularTotal() {
        double total = 0;
        for (ItemPedido i : itens) {
            total += i.calcularSubtotal();
        }
        return total;
    }

    public void pagarConta(String tipoPagamento, boolean usarBonus) {
        double total = calcularTotal();

        if (usarBonus) {
            double bonus = cliente.getBonus();
            if (bonus >= total) {
                total = 0;
                cliente.usarBonus(bonus);
            } else {
                total -= bonus;
                cliente.usarBonus(bonus);
            }
        }

        cliente.adicionarBonus(total);
        this.status = "PAGO";

        System.out.println("Mesa: " + mesa.getNumero());
        System.out.println("Pagamento realizado com " + tipoPagamento);
        System.out.println("Total pago: " + total);
    }
}