import java.util.ArrayList;

public class Cardapio {
    private ArrayList<ItemCardapio> itens;

    public Cardapio() {
        itens = new ArrayList<>();
    }

    public void adicionarItem(ItemCardapio item) {
        itens.add(item);
    }

    public void removerItem(ItemCardapio item) {
        itens.remove(item);
    }

    public ArrayList<ItemCardapio> listarItens() {
        return itens;
    }

    public ItemCardapio buscarItemPorNome(String nome) {
        for (ItemCardapio item : itens) {
            if (item.getNome().equalsIgnoreCase(nome)) {
                return item;
            }
        }
        return null;
    }
}