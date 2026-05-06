public class ItemPedido {
    private ItemCardapio item;
    private int quantidade;

    public ItemPedido(ItemCardapio item, int quantidade) {
        this.item = item;
        this.quantidade = quantidade;
    }

    public ItemCardapio getItem() {
        return item;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double calcularSubtotal() {
        return item.getPreco() * quantidade;
    }
}