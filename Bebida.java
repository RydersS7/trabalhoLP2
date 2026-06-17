public class Bebida extends ItemCardapio {
    private String fornecedor;
    private String volume;

    public Bebida(String nome, double preco, String fornecedor, String volume) {
        super(nome, preco);
        this.fornecedor = fornecedor;
        this.volume = volume;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public String getVolume() {
        return volume;
    }
}