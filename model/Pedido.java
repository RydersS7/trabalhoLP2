package model;

import java.util.ArrayList;
import java.time.LocalDateTime;

public class Pedido {
    private Cliente cliente;
    private Mesa mesa;
    private ArrayList<ItemPedido> itens;
    private String status;
    private LocalDateTime dataHoraAberta;
    private LocalDateTime dataHoraFechada;

    public Pedido() {
        this.itens = new ArrayList<>();
        this.dataHoraAberta = LocalDateTime.now();
    }

    public Pedido(Cliente cliente, Mesa mesa, ArrayList<ItemPedido> itens, String status) {
        this.cliente = cliente;
        this.mesa = mesa;
        this.itens = itens;
        this.status = status;
        this.dataHoraAberta = LocalDateTime.now();
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

    public LocalDateTime getDataHoraAberta() {
        return dataHoraAberta;
    }

    public LocalDateTime getDataHoraFechada() {
        return dataHoraFechada;
    }

    public void setDataHoraFechada(LocalDateTime dataHora) {
        this.dataHoraFechada = dataHora;
    }

    public long obterMinutosDePermanencia() {
        if (dataHoraAberta == null) return 0;
        LocalDateTime fim = (dataHoraFechada != null) ? dataHoraFechada : LocalDateTime.now();
        return java.time.temporal.ChronoUnit.MINUTES.between(dataHoraAberta, fim);
    }

    public String obterTempoFormatado() {
        long minutos = obterMinutosDePermanencia();
        long horas = minutos / 60;
        long mins = minutos % 60;
        if (horas > 0) {
            return horas + "h " + mins + "m";
        }
        return mins + "m";
    }
}