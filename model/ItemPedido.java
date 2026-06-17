package model;

public class ItemPedido {
    private ItemCardapio item;
    private int quantidade;

    // Construtores
    public ItemPedido() {}

    public ItemPedido(ItemCardapio item, int quantidade) {
        this.item = item;
        this.quantidade = quantidade;
    }

    // Getters e Setters
    public ItemCardapio getItem() {
        return item;
    }

    public void setItem(ItemCardapio item) {
        this.item = item;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}