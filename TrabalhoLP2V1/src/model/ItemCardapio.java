package model;

public abstract class ItemCardapio {
    private String nome;
    private double preco;

    // Construtores
    public ItemCardapio() {}

    public ItemCardapio(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
    if (preco <= 0) {
        throw new IllegalArgumentException("Preço deve ser maior que zero");
    }
    this.preco = preco;
}
}