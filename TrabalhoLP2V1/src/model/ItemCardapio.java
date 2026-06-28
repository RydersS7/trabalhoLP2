package model;

public abstract class ItemCardapio {
    private String nome;
    private double preco;

    public ItemCardapio() {}

    public ItemCardapio(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) {
        if (preco <= 0) throw new IllegalArgumentException("Preço deve ser maior que zero");
        this.preco = preco;
    }

    // Método abstrato — cada subclasse implementa sua descrição detalhada
    public abstract String getDescricaoCompleta();

    // Método abstrato — retorna a categoria do item
    public abstract String getCategoria();

    @Override
    public String toString() {
        return String.format("[%s] %s — R$ %.2f", getCategoria(), nome, preco);
    }
}
