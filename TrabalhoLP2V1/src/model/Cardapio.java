package model;

import java.util.ArrayList;

public class Cardapio {
    private ArrayList<ItemCardapio> itens; // Adicionado Generic <ItemCardapio>

    // Construtores
    public Cardapio() {
        this.itens = new ArrayList<>();
    }

    public Cardapio(ArrayList<ItemCardapio> itens) {
        this.itens = itens;
    }

    // Getters e Setters
    public ArrayList<ItemCardapio> getItens() {
        return itens;
    }

    public void setItens(ArrayList<ItemCardapio> itens) {
        this.itens = itens;
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