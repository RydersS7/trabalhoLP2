import java.util.ArrayList;

public class Comida extends ItemCardapio {
    private String descricao;
    private ArrayList<String> ingredientes;

    public Comida(String nome, double preco, String descricao) {
        super(nome, preco);
        this.descricao = descricao;
        this.ingredientes = new ArrayList<>();
    }

    public String getDescricao() {
        return descricao;
    }

    public ArrayList<String> getIngredientes() {
        return ingredientes;
    }

    public void adicionarIngrediente(String ingrediente) {
        ingredientes.add(ingrediente);
    }
}