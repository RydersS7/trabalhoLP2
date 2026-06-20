package model;

import java.util.ArrayList;

public class Pedido {
    private Cliente cliente;
    private Mesa mesa;
    private ArrayList<ItemPedido> itens; // Adicionado Generic <ItemPedido>
    private String status;

    // Construtores
    public Pedido() {
        this.itens = new ArrayList<>();
    }

    public Pedido(Cliente cliente, Mesa mesa, ArrayList<ItemPedido> itens, String status) {
        this.cliente = cliente;
        this.mesa = mesa;
        this.itens = itens;
        this.status = status;
    }

    // Getters e Setters
    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public ArrayList<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(ArrayList<ItemPedido> itens) {
        this.itens = itens;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    public int obterQuantidadeTotalItens() {
        return itens.stream().mapToInt(ItemPedido::getQuantidade).sum();
    }
     public boolean estaVazio() {
        return itens.isEmpty();
    }
}