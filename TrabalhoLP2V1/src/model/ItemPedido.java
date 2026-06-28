package model;

public class ItemPedido {
    private ItemCardapio item;
    private int quantidade;
    private boolean entregue;

    public ItemPedido() {}

    public ItemPedido(ItemCardapio item, int quantidade) {
        this.item = item;
        this.quantidade = quantidade;
        this.entregue = false;
    }

    public ItemCardapio getItem() { return item; }
    public void setItem(ItemCardapio item) { this.item = item; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public boolean isEntregue() { return entregue; }
    public void setEntregue(boolean entregue) { this.entregue = entregue; }

    public double calcularSubtotal() { return item.getPreco() * quantidade; }

    @Override
    public String toString() {
        return String.format("%dx %s — R$ %.2f%s",
            quantidade, item.getNome(), calcularSubtotal(),
            entregue ? " [ENTREGUE]" : "");
    }
}
