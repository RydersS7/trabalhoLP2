package model;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Pedido {
    private Cliente cliente;
    private Mesa mesa;
    private ArrayList<ItemPedido> itens;
    private String status; // ABERTO, PRONTO, ENTREGUE, PAGO
    private LocalDateTime dataHoraAberta;
    private LocalDateTime dataHoraFechada;

    public Pedido() {
        this.itens = new ArrayList<>();
        this.dataHoraAberta = LocalDateTime.now();
        this.status = "ABERTO";
    }

    public Pedido(Cliente cliente, Mesa mesa, ArrayList<ItemPedido> itens, String status) {
        this.cliente = cliente;
        this.mesa = mesa;
        this.itens = itens != null ? itens : new ArrayList<>();
        this.status = status;
        this.dataHoraAberta = LocalDateTime.now();
    }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }

    public ArrayList<ItemPedido> getItens() { return itens; }
    public void setItens(ArrayList<ItemPedido> itens) { this.itens = itens; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataHoraAberta() { return dataHoraAberta; }
    public LocalDateTime getDataHoraFechada() { return dataHoraFechada; }
    public void setDataHoraFechada(LocalDateTime dataHora) { this.dataHoraFechada = dataHora; }

    public void adicionarItem(ItemPedido item) { itens.add(item); }

    public double calcularTotal() {
        double total = 0;
        for (ItemPedido i : itens) total += i.calcularSubtotal();
        return total;
    }

    public int obterQuantidadeTotalItens() {
        return itens.stream().mapToInt(ItemPedido::getQuantidade).sum();
    }

    public boolean estaVazio() { return itens.isEmpty(); }

    public boolean todosItensEntregues() {
        if (itens.isEmpty()) return false;
        for (ItemPedido ip : itens)
            if (!ip.isEntregue()) return false;
        return true;
    }

    public long obterMinutosDePermanencia() {
        if (dataHoraAberta == null) return 0;
        LocalDateTime fim = (dataHoraFechada != null) ? dataHoraFechada : LocalDateTime.now();
        return ChronoUnit.MINUTES.between(dataHoraAberta, fim);
    }

    public String obterTempoFormatado() {
        long minutos = obterMinutosDePermanencia();
        long horas = minutos / 60;
        long mins = minutos % 60;
        return horas > 0 ? horas + "h " + mins + "m" : mins + "m";
    }
}
