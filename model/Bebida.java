package model;

public class Bebida extends ItemCardapio {
    private String fornecedor;
    private String volume;

    // Construtores
    public Bebida() {
        super();
    }

    public Bebida(String nome, double preco, String fornecedor, String volume) {
        super(nome, preco);
        this.fornecedor = fornecedor;
        this.volume = volume;
    }

    // Getters e Setters
    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}